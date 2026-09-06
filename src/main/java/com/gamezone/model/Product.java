package com.gamezone.model;

/**
 * Represents a generic product sold by the store.
 * <p>
 * Holds the attributes shared by every product type (video games, consoles).
 * Declared abstract because a plain, unspecialized product should never be
 * created directly — every product sold is always a specific type.
 */
public abstract class Product {

    private String id;
    private String title;
    private double price;
    private int quantity;

    /**
     * Creates a new product with its basic information.
     *
     * @param id       unique identifier of the product
     * @param title    name of the product
     * @param price    unit price of the product
     * @param quantity units currently available in inventory
     */
    public Product(String id, String title, double price, int quantity) {
        this.id = id;
        this.title = title;
        this.price = price;
        this.quantity = quantity;
    }
}
