package com.gamezone.model;

import java.time.LocalDate;

/**
 * Promotion that grants a percentage discount, but only over the products of a sale that
 * belong to a specific category ({@code "VIDEOGAME"} or {@code "CONSOLE"}).
 */
public class CategoryDiscount extends Promotion {

    private double percentage;
    private String targetCategory;

    /**
     * Creates a new category discount.
     *
     * @param id             unique identifier of the promotion
     * @param name           display name of the promotion
     * @param startDate      date the promotion becomes valid
     * @param endDate        date the promotion stops being valid
     * @param percentage     discount percentage to apply, between 0 and 100
     * @param targetCategory category the discount applies to, {@code "VIDEOGAME"} or {@code "CONSOLE"}
     */
    public CategoryDiscount(String id, String name, LocalDate startDate, LocalDate endDate,
                             double percentage, String targetCategory) {
        super(id, name, startDate, endDate);
        this.percentage = percentage;
        this.targetCategory = targetCategory;
    }

    /**
     * @return the discount percentage this promotion applies
     */
    public double getPercentage() {
        return percentage;
    }

    /**
     * @param percentage the new discount percentage this promotion applies
     */
    public void setPercentage(double percentage) {
        this.percentage = percentage;
    }

    /**
     * @return the category this promotion applies to
     */
    public String getTargetCategory() {
        return targetCategory;
    }

    /**
     * @param targetCategory the new category this promotion applies to
     */
    public void setTargetCategory(String targetCategory) {
        this.targetCategory = targetCategory;
    }

    @Override
    public double calculateDiscount(Sale sale) {
        double eligibleTotal = 0.0;
        for (Product product : sale.getProducts()) {
            if (matchesTargetCategory(product)) {
                eligibleTotal += product.getPrice();
            }
        }
        return eligibleTotal * (percentage / 100);
    }

    /**
     * Checks whether a product belongs to this promotion's target category, using the
     * product's concrete type instead of adding a category flag to {@link Product}.
     *
     * @param product the product to check
     * @return true if the product belongs to the target category
     */
    private boolean matchesTargetCategory(Product product) {
        if ("VIDEOGAME".equals(targetCategory)) {
            return product instanceof VideoGame;
        }
        if ("CONSOLE".equals(targetCategory)) {
            return product instanceof Console;
        }
        return false;
    }
}
