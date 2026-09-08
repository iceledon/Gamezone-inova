package com.gamezone.persistence;

import com.gamezone.model.Customer;
import com.gamezone.model.Product;
import com.gamezone.model.Sale;
import com.gamezone.model.Seller;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Reads and writes {@link Sale} records to {@code data/sales.txt}.
 * <p>
 * A sale references a customer, a seller and several products, so the file stores only
 * their ids and the sale is rebuilt against the entities already loaded in memory:
 * <pre>
 * id;date;customerId;sellerId;productId1,productId2,...
 * </pre>
 * The lists needed to resolve those ids are received as parameters instead of being
 * pulled from the service layer, so this class keeps depending only on {@code model} and
 * the layer direction {@code persistence -> model} is never inverted.
 */
public class SaleRepository {

    private static final Path FILE_PATH = Path.of("data", "sales.txt");
    private static final String SEPARATOR = ";";
    private static final String PRODUCT_SEPARATOR = ",";

    /**
     * Overwrites the sales file with the given list.
     *
     * @param sales the complete list of sales to persist
     * @throws RuntimeException if the file cannot be written
     */
    public void save(List<Sale> sales) {
        try {
            Files.createDirectories(FILE_PATH.getParent());
            try (BufferedWriter writer = Files.newBufferedWriter(FILE_PATH, StandardCharsets.UTF_8)) {
                for (Sale sale : sales) {
                    writer.write(toLine(sale));
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("No se pudo guardar el archivo de ventas.", e);
        }
    }

    /**
     * Reads every sale stored in the sales file, resolving the customer, the seller and
     * the products of each one against the given lists.
     *
     * @param products  the products currently loaded in the inventory
     * @param customers the customers currently registered
     * @param sellers   the sellers currently registered
     * @return the list of stored sales, or an empty list if the file does not exist yet
     * @throws RuntimeException if the file cannot be read or a referenced id is missing
     */
    public List<Sale> load(List<Product> products, List<Customer> customers, List<Seller> sellers) {
        List<Sale> sales = new ArrayList<>();
        if (!Files.exists(FILE_PATH)) {
            return sales;
        }
        try (BufferedReader reader = Files.newBufferedReader(FILE_PATH, StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.isBlank()) {
                    sales.add(fromLine(line, products, customers, sellers));
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("No se pudo leer el archivo de ventas.", e);
        }
        return sales;
    }

    /**
     * Converts a sale into its delimited text representation, storing only the ids of the
     * entities it references.
     *
     * @param sale the sale to convert
     * @return the text line representing the sale
     */
    private String toLine(Sale sale) {
        StringBuilder productIds = new StringBuilder();
        List<Product> products = sale.getProducts();
        for (int i = 0; i < products.size(); i++) {
            if (i > 0) {
                productIds.append(PRODUCT_SEPARATOR);
            }
            productIds.append(products.get(i).getId());
        }
        return String.join(SEPARATOR,
                sale.getId(),
                sale.getDate().toString(),
                sale.getCustomer().getId(),
                sale.getSeller().getId(),
                productIds.toString());
    }

    /**
     * Rebuilds a sale from its delimited text representation.
     *
     * @param line      the text line to parse
     * @param products  the products available to resolve product ids
     * @param customers the customers available to resolve the customer id
     * @param sellers   the sellers available to resolve the seller id
     * @return the reconstructed sale
     * @throws RuntimeException if any referenced id cannot be resolved
     */
    private Sale fromLine(String line, List<Product> products, List<Customer> customers, List<Seller> sellers) {
        String[] fields = line.split(SEPARATOR, -1);
        String id = fields[0];
        LocalDate date = LocalDate.parse(fields[1]);

        Customer customer = null;
        for (Customer candidate : customers) {
            if (candidate.getId().equals(fields[2])) {
                customer = candidate;
            }
        }
        Seller seller = null;
        for (Seller candidate : sellers) {
            if (candidate.getId().equals(fields[3])) {
                seller = candidate;
            }
        }
        if (customer == null || seller == null) {
            throw new RuntimeException("La venta " + id + " referencia un cliente o vendedor inexistente.");
        }

        List<Product> soldProducts = new ArrayList<>();
        for (String productId : fields[4].split(PRODUCT_SEPARATOR)) {
            Product found = null;
            for (Product candidate : products) {
                if (candidate.getId().equals(productId)) {
                    found = candidate;
                }
            }
            if (found == null) {
                throw new RuntimeException("La venta " + id + " referencia el producto inexistente " + productId + ".");
            }
            soldProducts.add(found);
        }
        return new Sale(id, date, customer, seller, soldProducts);
    }
}
