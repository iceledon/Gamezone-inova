package com.gamezone.model;

/**
 * Represents a game controller, a specific type of accessory.
 */
public class Controller extends Accessory {

    private String connectionType;

    /**
     * Creates a new controller with its basic and specific information.
     *
     * @param id             unique identifier of the product
     * @param title          name of the product
     * @param price          unit price of the product
     * @param quantity       units currently available in inventory
     * @param connectionType connection type of the controller (e.g. wireless, wired)
     */
    public Controller(String id, String title, double price, int quantity, String connectionType) {
        super(id, title, price, quantity);
        this.connectionType = connectionType;
    }

    @Override
    public String getDescription() {
        return getTitle() + " - Connection: " + connectionType;
    }

    /**
     * @return the connection type of the controller
     */
    public String getConnectionType() {
        return connectionType;
    }

    /**
     * @param connectionType the new connection type of the controller
     */
    public void setConnectionType(String connectionType) {
        this.connectionType = connectionType;
    }
}
