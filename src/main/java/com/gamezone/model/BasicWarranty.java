package com.gamezone.model;

import java.time.LocalDate;

/**
 * Automatic factory-defect warranty generated for every console sold. It has a fixed
 * six-month duration and never adds a cost to the sale.
 */
public class BasicWarranty extends Warranty {

    private static final int DURATION_MONTHS = 6;

    /**
     * Creates a new basic warranty.
     *
     * @param id unique identifier of the warranty
     * @param product product covered by this warranty
     * @param sale sale that generated this warranty
     * @param startDate date the coverage begins
     */
    public BasicWarranty(String id, Product product, Sale sale, LocalDate startDate) {
        super(id, product, sale, startDate);
    }

    @Override
    public int getDurationInMonths() {
        return DURATION_MONTHS;
    }

    @Override
    public String getWarrantyType() {
        return "Garantia Basica";
    }

    @Override
    public double getAdditionalCost() {
        return 0.0;
    }
}
