package com.gamezone.persistence;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.gamezone.model.Product;
import com.gamezone.model.Return;
import com.gamezone.model.Sale;

/**
 * Guarda y lee las devoluciones del archivo data/returns.csv.
 * <p>
 * En el archivo solo se guardan los ids de la venta y de los productos,
 * no los objetos completos. Recibe los modelos ya cargados para resolver esos ids.
 */
public class ReturnRepository {

    private static final Path FILE_PATH = Path.of("data", "returns.csv");
    private static final String SEPARATOR = ";";
    private static final String PRODUCT_SEPARATOR = ",";

    /**
     * Guarda toda la lista de devoluciones en el archivo.
     *
     * @param returns lista completa para guardar
     */
    public void saveAll(List<Return> returns) {
        try {
            Files.createDirectories(FILE_PATH.getParent());
            try (BufferedWriter writer = Files.newBufferedWriter(FILE_PATH, StandardCharsets.UTF_8)) {
                for (Return oneReturn : returns) {
                    writer.write(toLine(oneReturn));
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("No se pudo guardar el archivo de devoluciones.", e);
        }
    }

    /**
     * Lee las devoluciones guardadas. Si no hay archivo devuelve lista vacia.
     *
     * @return lista de devoluciones
     */
    public List<Return> loadAll(List<Sale> sales, List<? extends Product> products,
                                List<? extends Product> accessories) {
        List<Return> returns = new ArrayList<>();
        if (!Files.exists(FILE_PATH)) {
            return returns;
        }
        try (BufferedReader reader = Files.newBufferedReader(FILE_PATH, StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.isBlank()) {
                    returns.add(fromLine(line, sales, products, accessories));
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("No se pudo leer el archivo de devoluciones.", e);
        }
        return returns;
    }

    /**
     * Pasa una devolucion a texto para guardarla.
     *
     * @param oneReturn devolucion a convertir
     * @return linea de texto
     */
    private String toLine(Return oneReturn) {
        String productIds = "";
        List<Product> products = oneReturn.getReturnedProducts();
        for (int i = 0; i < products.size(); i++) {
            if (i > 0) {
                productIds = productIds + PRODUCT_SEPARATOR;
            }
            productIds = productIds + products.get(i).getId();
        }
        return oneReturn.getId() + SEPARATOR
                + oneReturn.getDate().toString() + SEPARATOR
                + oneReturn.getOriginalSale().getId() + SEPARATOR
                + productIds + SEPARATOR
                + oneReturn.getReason() + SEPARATOR
                + oneReturn.getRefundAmount() + SEPARATOR
                + oneReturn.getWarrantyRefund();
    }

    /**
     * Arma una devolucion desde una linea de texto.
     *
     * @param line linea del archivo
     * @return devolucion armada
     */
    private Return fromLine(String line, List<Sale> sales,
                            List<? extends Product> products,
                            List<? extends Product> accessories) {
        String[] fields = line.split(SEPARATOR, -1);
        String id = fields[0];
        LocalDate date = LocalDate.parse(fields[1]);
        Sale sale = findSale(sales, fields[2]);
        String reason = fields[4];
        double warrantyRefund = fields.length > 6 ? Double.parseDouble(fields[6]) : 0.0;
        List<Product> found = new ArrayList<>();
        String[] ids = fields[3].split(PRODUCT_SEPARATOR);
               for (int i = 0; i < ids.length; i++) {
            Product item = findInList(products, ids[i]);
            if (item == null) {
                item = findInList(accessories, ids[i]);
            }
            if (item != null) {
                found.add(item);
            }
        }
        return new Return(id, date, sale, found, reason, warrantyRefund);
    }
    private Sale findSale(List<Sale> sales, String id) {
        for (Sale sale : sales) {
            if (sale.getId().equals(id)) {
                return sale;
            }
        }
        return null;
    }
    private Product findInList(List<? extends Product> items, String id) {
        for (Product item : items) {
            if (item.getId().equals(id)) {
                return item;
            }
        }
        return null;
    }
}
