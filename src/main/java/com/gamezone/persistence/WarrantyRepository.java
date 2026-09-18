package com.gamezone.persistence;

import com.gamezone.model.BasicWarranty;
import com.gamezone.model.ExtendedWarranty;
import com.gamezone.model.Product;
import com.gamezone.model.Sale;
import com.gamezone.model.Warranty;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class WarrantyRepository {

    private static final Path FILE_PATH = Path.of("data", "warranties.csv");
    private static final String SEPARATOR = ";";
    private static final String BASIC = "BASIC";
    private static final String EXTENDED = "EXTENDED";

    public void saveAll(List<Warranty> warranties) {
        try {
            Files.createDirectories(FILE_PATH.getParent());
            try (BufferedWriter writer = Files.newBufferedWriter(FILE_PATH, StandardCharsets.UTF_8)) {
                for (Warranty warranty : warranties) {
                    writer.write(toLine(warranty));
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("No se pudo guardar el archivo de garantias.", e);
        }
    }

    public List<Warranty> loadAll(List<Product> products, List<Sale> sales) {
        List<Warranty> warranties = new ArrayList<>();
        if (!Files.exists(FILE_PATH)) {
            return warranties;
        }
        try (BufferedReader reader = Files.newBufferedReader(FILE_PATH, StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.isBlank()) {
                    warranties.add(fromLine(line, products, sales));
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("No se pudo leer el archivo de garantias.", e);
        }
        return warranties;
    }

    private String toLine(Warranty warranty) {
        String discriminator = warranty instanceof ExtendedWarranty ? EXTENDED : BASIC;
        return String.join(SEPARATOR,
                discriminator,
                warranty.getId(),
                warranty.getProduct().getId(),
                warranty.getSale().getId(),
                warranty.getStartDate().toString());
    }

    private Warranty fromLine(String line, List<Product> products, List<Sale> sales) {
        String[] fields = line.split(SEPARATOR, -1);
        String discriminator = fields[0];
        String id = fields[1];
        String productId = fields[2];
        String saleId = fields[3];
        LocalDate startDate = LocalDate.parse(fields[4]);

        Product product = findProduct(products, productId);
        Sale sale = findSale(sales, saleId);

        if (product == null || sale == null) {
            throw new RuntimeException(
                    "La garantia " + id + " referencia un producto o venta inexistente.");
        }

        if (discriminator.equals(EXTENDED)) {
            return new ExtendedWarranty(id, product, sale, startDate);
        } else if (discriminator.equals(BASIC)) {
            return new BasicWarranty(id, product, sale, startDate);
        }
        throw new RuntimeException("Tipo de garantia desconocido: " + discriminator);
    }

    private Product findProduct(List<Product> products, String productId) {
        for (Product candidate : products) {
            if (candidate.getId().equals(productId)) {
                return candidate;
            }
        }
        return null;
    }

    private Sale findSale(List<Sale> sales, String saleId) {
        for (Sale candidate : sales) {
            if (candidate.getId().equals(saleId)) {
                return candidate;
            }
        }
        return null;
    }
}
