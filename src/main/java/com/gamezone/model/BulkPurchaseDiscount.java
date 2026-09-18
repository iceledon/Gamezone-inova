package com.gamezone.model;

import java.time.LocalDate;

/**
 * Promotion that grants a percentage discount over the total of a sale, but only when the
 * sale includes at least a minimum quantity of products.
 */
public class BulkPurchaseDiscount extends Promotion {

    private int minimumQuantity;
    private double percentage;

    /**
     * Creates a new bulk purchase discount.
     *
     * @param id              unique identifier of the promotion
     * @param name            display name of the promotion
     * @param startDate       date the promotion becomes valid
     * @param endDate         date the promotion stops being valid
     * @param minimumQuantity minimum number of products a sale must include to qualify
     * @param percentage      discount percentage to apply, between 0 and 100
     */
    public BulkPurchaseDiscount(String id, String name, LocalDate startDate, LocalDate endDate,
                                 int minimumQuantity, double percentage) {
        super(id, name, startDate, endDate);
        this.minimumQuantity = minimumQuantity;
        this.percentage = percentage;
    }

    /**
     * @return the minimum number of products a sale must include to qualify
     */
    public int getMinimumQuantity() {
        return minimumQuantity;
    }

    /**
     * @param minimumQuantity the new minimum number of products a sale must include
     */
    public void setMinimumQuantity(int minimumQuantity) {
        this.minimumQuantity = minimumQuantity;
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
        if (sale.getProducts().size() < minimumQuantity) {
            return 0.0;
        }
        return sale.calculateTotal() * (percentage / 100);
    }
}
