package com.gamezone.persistence;

import com.gamezone.model.Product;
import com.gamezone.model.Sale;
import com.gamezone.model.Warranty;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class WarrantyRepository {

    private static final Path FILE_PATH = Path.of("data", "warranties.csv");
    private static final String SEPARATOR = ";";

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
        return String.join(SEPARATOR,
                warranty.getId(),
                warranty.getProduct().getId(),
                warranty.getSale().getId(),
                warranty.getStartDate().toString());
    }

    private Warranty fromLine(String line, List<Product> products, List<Sale> sales) {
        return null;
    }
}
