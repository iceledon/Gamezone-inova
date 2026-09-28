package com.gamezone.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a video game accessory, a specific type of product.
 */
public abstract class Accessory extends Product {

    private List<String> compatibleConsoleIds;

    /**
     * Creates a new accessory with its basic information.
     *
     * @param id       unique identifier of the product
     * @param title    name of the product
     * @param price    unit price of the product
     * @param quantity units currently available in inventory
     */
    public Accessory(String id, String title, double price, int quantity) {
        super(id, title, price, quantity);
        this.compatibleConsoleIds = new ArrayList<>();
    }

    /**
     * @return the ids of the consoles this accessory is compatible with
     */
    public List<String> getCompatibleConsoleIds() {
        return new ArrayList<>(compatibleConsoleIds);
    }

    /**
     * Registers a console as compatible with this accessory, if it is not already.
     *
     * @param consoleId id of the compatible console
     */
    public void addCompatibleConsole(String consoleId) {
        if (!compatibleConsoleIds.contains(consoleId)) {
            compatibleConsoleIds.add(consoleId);
        }
    }

    /**
     * Removes a console from the list of consoles compatible with this accessory.
     *
     * @param consoleId id of the console to remove
     */
    public void removeCompatibleConsole(String consoleId) {
        compatibleConsoleIds.remove(consoleId);
    }

    /**
     * @param consoleId id of the console to check
     * @return true if this accessory is compatible with the given console
     */
    public boolean isCompatibleWith(String consoleId) {
        return compatibleConsoleIds.contains(consoleId);
    }
}
