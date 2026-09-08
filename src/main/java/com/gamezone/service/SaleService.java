package com.gamezone.service;

import com.gamezone.model.Customer;
import com.gamezone.model.Product;
import com.gamezone.model.Sale;
import com.gamezone.model.Seller;
import com.gamezone.persistence.SaleRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private final List<Sale> sales;

    /**
     * Creates the service and loads the sales history into memory, resolving the entities
     * each stored sale references through the other two services.
     *
     * @param repository     the repository used to read and write sales
     * @param productService the service that owns the product inventory
     * @param personService  the service that owns customers and sellers
     */
    public SaleService(SaleRepository repository, ProductService productService, PersonService personService) {
        this.repository = repository;
        this.productService = productService;
        this.personService = personService;
        this.sales = new ArrayList<>(repository.load(
                productService.listAll(),
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
            Product product = productService.findById(productId);
            if (product == null) {
                throw new IllegalArgumentException("No existe un producto con el codigo " + productId + ".");
            }
            soldProducts.add(product);
        }

        Map<String, Integer> requestedUnits = countUnitsByProduct(soldProducts);
        for (Map.Entry<String, Integer> entry : requestedUnits.entrySet()) {
            Product product = productService.findById(entry.getKey());
            if (product.getQuantity() < entry.getValue()) {
                throw new IllegalArgumentException(String.format(
                        "Stock insuficiente para %s. Disponible: %d, solicitado: %d.",
                        product.getTitle(), product.getQuantity(), entry.getValue()));
            }
        }

        Sale sale = new Sale(generateSaleId(), LocalDate.now(), customer, seller, soldProducts);

        for (Map.Entry<String, Integer> entry : requestedUnits.entrySet()) {
            productService.updateStock(entry.getKey(), entry.getValue());
        }

        sales.add(sale);
        repository.save(sales);
        return sale;
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
