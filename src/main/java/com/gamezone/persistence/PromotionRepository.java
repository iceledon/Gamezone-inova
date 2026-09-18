package com.gamezone.persistence;

import com.gamezone.model.BulkPurchaseDiscount;
import com.gamezone.model.CategoryDiscount;
import com.gamezone.model.PercentageDiscount;
import com.gamezone.model.Promotion;

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
 * Reads and writes {@link Promotion} records to {@code data/promotions.csv}.
 * <p>
 * Unlike other repositories in the project, a promotion does not reference any product or
 * sale by id, so it does not need any already-loaded entity to be rebuilt: every field it
 * needs lives in the same line. A discriminator column tells the three concrete subclasses
 * apart when the file is read back, the same way {@code ProductRepository} distinguishes
 * CONSOLE from VIDEOGAME.
 */
public class PromotionRepository {

    private static final Path FILE_PATH = Path.of("data", "promotions.csv");
    private static final String SEPARATOR = ";";
    private static final String PERCENTAGE = "PERCENTAGE";
    private static final String CATEGORY = "CATEGORY";
    private static final String BULK = "BULK";

    /**
     * Overwrites the promotions file with the given list.
     *
     * @param promotions the complete list of promotions to persist
     * @throws RuntimeException if the file cannot be written
     */
    public void saveAll(List<Promotion> promotions) {
        try {
            Files.createDirectories(FILE_PATH.getParent());
            try (BufferedWriter writer = Files.newBufferedWriter(FILE_PATH, StandardCharsets.UTF_8)) {
                for (Promotion promotion : promotions) {
                    writer.write(toLine(promotion));
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("No se pudo guardar el archivo de promociones.", e);
        }
    }

    /**
     * Reads every promotion stored in the promotions file. Returns an empty list if the
     * file does not exist yet, the same convention used by every other repository in the
     * project.
     *
     * @return the list of stored promotions
     * @throws RuntimeException if the file cannot be read or contains an unknown type
     */
    public List<Promotion> loadAll() {
        List<Promotion> promotions = new ArrayList<>();
        if (!Files.exists(FILE_PATH)) {
            return promotions;
        }
        try (BufferedReader reader = Files.newBufferedReader(FILE_PATH, StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.isBlank()) {
                    promotions.add(fromLine(line));
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("No se pudo leer el archivo de promociones.", e);
        }
        return promotions;
    }

    private String toLine(Promotion promotion) {
        if (promotion instanceof PercentageDiscount percentageDiscount) {
            return String.join(SEPARATOR,
                    PERCENTAGE,
                    percentageDiscount.getId(),
                    percentageDiscount.getName(),
                    percentageDiscount.getStartDate().toString(),
                    percentageDiscount.getEndDate().toString(),
                    String.valueOf(percentageDiscount.getPercentage()));
        }
        if (promotion instanceof CategoryDiscount categoryDiscount) {
            return String.join(SEPARATOR,
                    CATEGORY,
                    categoryDiscount.getId(),
                    categoryDiscount.getName(),
                    categoryDiscount.getStartDate().toString(),
                    categoryDiscount.getEndDate().toString(),
                    String.valueOf(categoryDiscount.getPercentage()),
                    categoryDiscount.getTargetCategory());
        }
        if (promotion instanceof BulkPurchaseDiscount bulkPurchaseDiscount) {
            return String.join(SEPARATOR,
                    BULK,
                    bulkPurchaseDiscount.getId(),
                    bulkPurchaseDiscount.getName(),
                    bulkPurchaseDiscount.getStartDate().toString(),
                    bulkPurchaseDiscount.getEndDate().toString(),
                    String.valueOf(bulkPurchaseDiscount.getMinimumQuantity()),
                    String.valueOf(bulkPurchaseDiscount.getPercentage()));
        }
        throw new RuntimeException("Tipo de promocion desconocido: " + promotion.getClass().getSimpleName());
    }

    private Promotion fromLine(String line) {
        String[] fields = line.split(SEPARATOR, -1);
        String discriminator = fields[0];
        String id = fields[1];
        String name = fields[2];
        LocalDate startDate = LocalDate.parse(fields[3]);
        LocalDate endDate = LocalDate.parse(fields[4]);

        if (discriminator.equals(PERCENTAGE)) {
            double percentage = Double.parseDouble(fields[5]);
            return new PercentageDiscount(id, name, startDate, endDate, percentage);
        }
        if (discriminator.equals(CATEGORY)) {
            double percentage = Double.parseDouble(fields[5]);
            String targetCategory = fields[6];
            return new CategoryDiscount(id, name, startDate, endDate, percentage, targetCategory);
        }
        if (discriminator.equals(BULK)) {
            int minimumQuantity = Integer.parseInt(fields[5]);
            double percentage = Double.parseDouble(fields[6]);
            return new BulkPurchaseDiscount(id, name, startDate, endDate, minimumQuantity, percentage);
        }
        throw new RuntimeException("Tipo de promocion desconocido: " + discriminator);
    }
}
