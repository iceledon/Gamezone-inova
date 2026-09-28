package com.gamezone.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import com.gamezone.model.Console;
import com.gamezone.model.Customer;
import com.gamezone.model.ExtendedWarranty;
import com.gamezone.model.Product;
import com.gamezone.model.Promotion;
import com.gamezone.model.Sale;
import com.gamezone.model.Seller;
import com.gamezone.persistence.SaleRepository;

/**
 * Business operations available for sales.
 * <p>
 * A sale needs at least one item, every participant and item must exist,
 * and there must be enough stock for every unit requested. Once validated,
 * the service creates the sale, applies the best promotion, assigns warranties,
 * updates inventory and persists the result.
 */
public class SaleService {

    private final SaleRepository repository;
    private final ProductService productService;
    private final PersonService personService;
    private final PromotionService promotionService;
    private final AccessoryService accessoryService;
    private final List<Sale> sales;
    private WarrantyService warrantyService;

    /**
     * Creates the service and loads the sales history into memory, resolving
     * the entities each stored sale references through the other services.
     *
     * @param repository       the repository used to read and write sales
     * @param productService   the service that owns the product inventory
     * @param personService    the service that owns customers and sellers
     * @param promotionService the service used to find the best promotion for a sale
     * @param accessoryService the service that owns the accessory inventory
     */
    public SaleService(SaleRepository repository, ProductService productService,
                       PersonService personService, PromotionService promotionService,
                       AccessoryService accessoryService) {
        this.repository = repository;
        this.productService = productService;
        this.personService = personService;
        this.promotionService = promotionService;
        this.accessoryService = accessoryService;

        List<Product> catalog = new ArrayList<>(productService.listAll());
        catalog.addAll(accessoryService.listAllAccessories());

        this.sales = new ArrayList<>(repository.load(
                catalog,
                personService.listCustomers(),
                personService.listSellers()));
    }

    /**
     * Connects this service with the warranty module.
     * Called from {@code Main} after the required services are constructed.
     *
     * @param warrantyService the service used to assign warranties automatically
     */
    public void setWarrantyService(WarrantyService warrantyService) {
        this.warrantyService = warrantyService;
    }

    /**
     * Registers a new sale in the following order:
     * <ol>
     *   <li>Validate items, customer, seller and available stock.</li>
     *   <li>Create the sale.</li>
     *   <li>Apply the best active promotion to the item subtotal.</li>
     *   <li>Assign console warranties and add extended warranty costs.</li>
     *   <li>Update inventory.</li>
     *   <li>Persist the updated sales history.</li>
     * </ol>
     *
     * @param customerId                     the id of the customer making the purchase
     * @param sellerId                       the id of the seller handling the sale
     * @param productIds                     the ids of the products or accessories
     *                                       being sold, one entry per unit
     * @param productIdsWithExtendedWarranty ids of the consoles that should also
     *                                       receive an extended warranty; null or
     *                                       empty means no extended warranty is applied
     * @return the registered sale
     */
    public Sale registerSale(String customerId, String sellerId, List<String> productIds,
                             List<String> productIdsWithExtendedWarranty) {
        if (productIds == null || productIds.isEmpty()) {
            throw new IllegalArgumentException(
                    "La venta debe contener al menos un producto.");
        }

        Customer customer = personService.findCustomerById(customerId);
        if (customer == null) {
            throw new IllegalArgumentException(
                    "No existe un cliente con la identificacion " + customerId + ".");
        }

        Seller seller = personService.findSellerById(sellerId);
        if (seller == null) {
            throw new IllegalArgumentException(
                    "No existe un vendedor con la identificacion " + sellerId + ".");
        }

        List<Product> soldProducts = new ArrayList<>();
        for (String productId : productIds) {
            try {
                soldProducts.add(findItemById(productId));
            } catch (NoSuchElementException e) {
                throw new IllegalArgumentException(
                        "No existe un producto ni un accesorio con el codigo "
                                + productId + ".");
            }
        }

        Map<String, Integer> requestedUnits = countUnitsByProduct(soldProducts);
        for (Map.Entry<String, Integer> entry : requestedUnits.entrySet()) {
            Product product = findItemById(entry.getKey());
            if (product.getQuantity() < entry.getValue()) {
                throw new IllegalArgumentException(String.format(
                        "Stock insuficiente para %s. Disponible: %d, solicitado: %d.",
                        product.getTitle(), product.getQuantity(), entry.getValue()));
            }
        }

        Sale sale = new Sale(
                generateSaleId(), LocalDate.now(), customer, seller, soldProducts);

        // Apply the best active promotion before adding warranty costs.
        Promotion bestPromotion = promotionService.findBestPromotionFor(sale);
        if (bestPromotion != null) {
            sale.setAppliedPromotionName(bestPromotion.getName());
            sale.setDiscountAmount(bestPromotion.calculateDiscount(sale));
        }

        // Assign basic and optional extended warranties to consoles.
        if (warrantyService != null) {
            for (Product product : soldProducts) {
                if (product instanceof Console) {
                    warrantyService.assignBasicWarranty(
                            product, sale, sale.getDate());

                    if (productIdsWithExtendedWarranty != null
                            && productIdsWithExtendedWarranty.contains(product.getId())) {
                        ExtendedWarranty extendedWarranty =
                                warrantyService.assignExtendedWarranty(
                                        product, sale, sale.getDate());

                        sale.addExtraCost(extendedWarranty.getAdditionalCost());
                    }
                }
            }
        }

        for (Map.Entry<String, Integer> entry : requestedUnits.entrySet()) {
            updateStockOf(entry.getKey(), entry.getValue());
        }

        sales.add(sale);
        repository.save(sales);
        return sale;
    }

    /**
     * Registers a new sale without an extended warranty for any of its consoles.
     *
     * @param customerId the id of the customer making the purchase
     * @param sellerId   the id of the seller handling the sale
     * @param productIds the ids of the products or accessories being sold,
     *                   one entry per unit
     * @return the registered sale
     */
    public Sale registerSale(String customerId, String sellerId,
                             List<String> productIds) {
        return registerSale(
                customerId, sellerId, productIds, Collections.emptyList());
    }

    /**
     * Returns the complete sales history.
     *
     * @return an unmodifiable view of every registered sale
     */
    public List<Sale> getAllSales() {
        return Collections.unmodifiableList(sales);
    }

    /**
     * Returns the purchase history of a customer.
     * This is why {@code Customer} does not need to keep its own list of sales.
     *
     * @param customerId the id of the customer to filter by
     * @return the sales made by that customer
     */
    public List<Sale> getSalesByCustomer(String customerId) {
        List<Sale> result = new ArrayList<>();
        for (Sale sale : sales) {
            if (sale.getCustomer().getId().equals(customerId)) {
                result.add(sale);
            }
        }
        return result;
    }

    /**
     * Returns the sales handled by a seller.
     *
     * @param sellerId the id of the seller to filter by
     * @return the sales handled by that seller
     */
    public List<Sale> getSalesBySeller(String sellerId) {
        List<Sale> result = new ArrayList<>();
        for (Sale sale : sales) {
            if (sale.getSeller().getId().equals(sellerId)) {
                result.add(sale);
            }
        }
        return result;
    }

    /**
     * Finds a sale by its identifier.
     * The return module uses this method to reference an existing sale.
     *
     * @param id the identifier of the sale to find
     * @return the matching sale, or null if no sale has that identifier
     */
    public Sale findById(String id) {
        for (Sale sale : sales) {
            if (sale.getId().equals(id)) {
                return sale;
            }
        }
        return null;
    }

    /**
     * Looks up an item sold in the store by id, checking the product catalog
     * first and falling back to the accessory catalog.
     * Both catalogs contain objects that can be used as {@code Product} references.
     *
     * @param id the id of the product or accessory to find
     * @return the matching product or accessory
     * @throws NoSuchElementException if neither catalog has an item with that id
     */
    private Product findItemById(String id) {
        try {
            return productService.findById(id);
        } catch (NoSuchElementException e) {
            return accessoryService.findById(id);
        }
    }

    /**
     * Decreases the stock of a sold item through the service that owns it,
     * using the same catalog lookup order as {@link #findItemById(String)}.
     *
     * @param id     the id of the product or accessory whose stock was sold
     * @param amount the number of units to remove from inventory
     */
    private void updateStockOf(String id, int amount) {
        try {
            productService.updateStock(id, amount);
        } catch (NoSuchElementException e) {
            accessoryService.updateStock(id, amount);
        }
    }

    /**
     * Counts how many units of each product a sale requests, since the same
     * product id may appear more than once in the same sale.
     *
     * @param soldProducts the products of the sale, one entry per unit
     * @return the number of units requested per product id
     */
    private Map<String, Integer> countUnitsByProduct(List<Product> soldProducts) {
        Map<String, Integer> requestedUnits = new HashMap<>();
        for (Product product : soldProducts) {
            requestedUnits.merge(product.getId(), 1, Integer::sum);
        }
        return requestedUnits;
    }

    /**
     * Builds the identifier of the next sale, following the format {@code V001}.
     *
     * @return the id for the sale about to be created
     */
    private String generateSaleId() {
        return String.format("V%03d", sales.size() + 1);
    }
}