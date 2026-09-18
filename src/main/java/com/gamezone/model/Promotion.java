package com.gamezone.model;

import java.time.LocalDate;

/**
 * Represents a promotional campaign that can grant a discount to a sale.
 * <p>
 * Concrete subclasses define how the discount is calculated for a given sale, while this
 * class owns the data and behavior common to every promotion: its identity, its display
 * name, and the date range during which it is valid.
 */
public abstract class Promotion {

    private String id;
    private String name;
    private LocalDate startDate;
    private LocalDate endDate;

    /**
     * Creates a new promotion valid during the given date range.
     *
     * @param id        unique identifier of the promotion
     * @param name      display name of the promotion
     * @param startDate date the promotion becomes valid
     * @param endDate   date the promotion stops being valid
     */
    protected Promotion(String id, String name, LocalDate startDate, LocalDate endDate) {
        this.id = id;
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    /**
     * @return the unique identifier of the promotion
     */
    public String getId() {
        return id;
    }

    /**
     * @param id the new unique identifier of the promotion
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return the display name of the promotion
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the new display name of the promotion
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the date the promotion becomes valid
     */
    public LocalDate getStartDate() {
        return startDate;
    }

    /**
     * @param startDate the new date the promotion becomes valid
     */
    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    /**
     * @return the date the promotion stops being valid
     */
    public LocalDate getEndDate() {
        return endDate;
    }

    /**
     * @param endDate the new date the promotion stops being valid
     */
    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    /**
     * Checks whether this promotion is valid on the given date.
     *
     * @param date the date to check
     * @return true if date falls within [startDate, endDate], false otherwise
     */
    public boolean isActive(LocalDate date) {
        return !date.isBefore(startDate) && !date.isAfter(endDate);
    }
}
