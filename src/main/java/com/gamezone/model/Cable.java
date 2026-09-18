package com.gamezone.model;

/**
 * Represents a cable, a specific type of accessory.
 */
public class Cable extends Accessory {

    private double lengthMeters;
    private String connectorType;

    /**
     * Creates a new cable with its basic and specific information.
     *
     * @param id            unique identifier of the product
     * @param title         name of the product
     * @param price         unit price of the product
     * @param quantity      units currently available in inventory
     * @param lengthMeters  length of the cable, in meters
     * @param connectorType type of connector (e.g. HDMI, USB, optical)
     */
    public Cable(String id, String title, double price, int quantity, double lengthMeters, String connectorType) {
        super(id, title, price, quantity);
        this.lengthMeters = lengthMeters;
        this.connectorType = connectorType;
    }

    @Override
    public String getDescription() {
        return getTitle() + " - Length: " + lengthMeters + "m, Connector: " + connectorType;
    }

    /**
     * @return the length of the cable, in meters
     */
    public double getLengthMeters() {
        return lengthMeters;
    }

    /**
     * @param lengthMeters the new length of the cable, in meters
     */
    public void setLengthMeters(double lengthMeters) {
        this.lengthMeters = lengthMeters;
    }

    /**
     * @return the type of connector
     */
    public String getConnectorType() {
        return connectorType;
    }

    /**
     * @param connectorType the new type of connector
     */
    public void setConnectorType(String connectorType) {
        this.connectorType = connectorType;
    }
}
