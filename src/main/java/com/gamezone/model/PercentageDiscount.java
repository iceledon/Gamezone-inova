package com.gamezone.model;

import java.time.LocalDate;

/**
 * Promotion that grants a flat percentage discount over the total of a sale, regardless
 * of what products it contains.
 */
public class PercentageDiscount extends Promotion {

    private double percentage;

    /**
     * Creates a new percentage discount.
     *
     * @param id         unique identifier of the promotion
     * @param name       display name of the promotion
     * @param startDate  date the promotion becomes valid
     * @param endDate    date the promotion stops being valid
     * @param percentage discount percentage to apply, between 0 and 100
     */
    public PercentageDiscount(String id, String name, LocalDate startDate, LocalDate endDate, double percentage) {
        super(id, name, startDate, endDate);
        this.percentage = percentage;
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

    @Override
    public double calculateDiscount(Sale sale) {
        return sale.calculateTotal() * (percentage / 100);
    }
}
