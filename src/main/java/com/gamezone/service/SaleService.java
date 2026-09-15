package com.gamezone.service;

import com.gamezone.model.Customer;
import com.gamezone.model.Product;
import com.gamezone.model.Sale;
import com.gamezone.model.Seller;
import com.gamezone.persistence.SaleRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * Business operations available for sales.
 * <p>
 * This is the class that owns the business rules of the workshop: a sale needs at least
 * one product, every participant and product must exist, and there must be enough stock
 * for every unit requested. Only when all of them hold does the sale get created, the
 * inventory get updated and the result persisted.
 */
public class SaleService {

    private final SaleRepository repository;
    private final ProductService productService;
    private final PersonService personService;
    private final AccessoryService accessoryService;
    private final List<Sale> sales;

    /**
     * Creates the service and loads the sales history into memory, resolving the entities
     * each stored sale references through the other services.
     *
     * @param repository       the repository used to read and write sales
     * @param productService   the service that owns the product inventory
     * @param personService    the service that owns customers and sellers
     * @param accessoryService the service that owns the accessory inventory
     */
    public SaleService(SaleRepository repository, ProductService productService,
                        PersonService personService, AccessoryService accessoryService) {
        this.repository = repository;
        this.productService = productService;
        this.personService = personService;
        this.accessoryService = accessoryService;
        List<Product> catalog = new ArrayList<>(productService.listAll());
        catalog.addAll(accessoryService.listAllAccessories());
        this.sales = new ArrayList<>(repository.load(
                catalog,
                personService.listCustomers(),
                personService.listSellers()));
    }

    /**
     * Registers a new sale after validating every business rule, then discounts the sold
     * units from the inventory and persists the updated history.
     *
     * @param customerId the id of the customer making the purchase
     * @param sellerId   the id of the seller handling the sale
     * @param productIds the ids of the products being sold, one entry per unit
     * @return the registered sale
     * @throws IllegalArgumentException if there is no product, if the customer, the seller
     *                                  or a product does not exist, or if the available
     *                                  stock is not enough for the units requested
     */
    public Sale registerSale(String customerId, String sellerId, List<String> productIds) {
        if (productIds == null || productIds.isEmpty()) {
            throw new IllegalArgumentException("La venta debe contener al menos un producto.");
        }

        Customer customer = personService.findCustomerById(customerId);
        if (customer == null) {
            throw new IllegalArgumentException("No existe un cliente con la identificacion " + customerId + ".");
        }

        Seller seller = personService.findSellerById(sellerId);
        if (seller == null) {
            throw new IllegalArgumentException("No existe un vendedor con la identificacion " + sellerId + ".");
        }

        List<Product> soldProducts = new ArrayList<>();
        for (String productId : productIds) {
            try {
                soldProducts.add(findItemById(productId));
            } catch (NoSuchElementException e) {
                throw new IllegalArgumentException("No existe un producto ni un accesorio con el codigo " + productId + ".");
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

        Sale sale = new Sale(generateSaleId(), LocalDate.now(), customer, seller, soldProducts);

        for (Map.Entry<String, Integer> entry : requestedUnits.entrySet()) {
            updateStockOf(entry.getKey(), entry.getValue());
        }

        sales.add(sale);
        repository.save(sales);
        return sale;
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
     * Returns the purchase history of a customer. This is why {@code Customer} does not
     * need to keep its own list of sales.
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
     * Busca una venta por su identificador. Este metodo lo necesita el modulo de
     * devoluciones, que siempre debe referenciar una venta que ya existe.
     *
     * @param id identificador de la venta a buscar
     * @return la venta encontrada, o null si no existe ninguna con ese id
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
     * Looks up an item sold in the store by id, checking the product catalog first and
     * falling back to the accessory catalog, since a sale can include either. This is the
     * one place that needs to know both catalogs exist; everything else in this class just
     * works with {@code Product} references, because {@code Accessory extends Product}.
     *
     * @param id id of the product or accessory to find
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
     * Decreases the stock of a sold item, delegating to whichever service actually owns
     * it, the same way {@link #findItemById(String)} resolves it for lookups.
     *
     * @param id     id of the product or accessory whose stock was sold
     * @param amount units to remove from inventory
     */
    private void updateStockOf(String id, int amount) {
        try {
            productService.updateStock(id, amount);
        } catch (NoSuchElementException e) {
            accessoryService.updateStock(id, amount);
        }
    }

    /**
     * Counts how many units of each product a sale requests, since the same product id
     * may appear more than once in the same sale.
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
