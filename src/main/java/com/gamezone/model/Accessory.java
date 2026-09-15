package com.gamezone.model;

/**
 * Represents a video game accessory, a specific type of product.
 */
public abstract class Accessory extends Product {

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
    }
}
