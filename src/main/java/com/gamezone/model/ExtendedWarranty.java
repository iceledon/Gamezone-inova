package com.gamezone.model;

import java.time.LocalDate;

/**
 * Optional warranty a seller can add to a console at the moment of sale. It covers
 * factory defects and accidental damage for twelve months and costs an extra 10% of the
 * covered product's price.
 */
public class ExtendedWarranty extends Warranty {

    private static final int DURATION_MONTHS = 12;
    private static final double EXTRA_COST_RATE = 0.10;

    /**
     * Creates a new extended warranty.
     *
     * @param id unique identifier of the warranty
     * @param product product covered by this warranty
     * @param sale sale that generated this warranty
     * @param startDate date the coverage begins
     */
    public ExtendedWarranty(String id, Product product, Sale sale, LocalDate startDate) {
        super(id, product, sale, startDate);
    }

    @Override
    public int getDurationInMonths() {
        return DURATION_MONTHS;
    }

    @Override
    public String getWarrantyType() {
        return "Garantia Extendida";
    }

    @Override
    public double getAdditionalCost() {
        return getProduct().getPrice() * EXTRA_COST_RATE;
    }
}
