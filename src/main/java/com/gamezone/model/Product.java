package com.gamezone.model;

/**
 * Represents a generic product sold by the store.
 * Abstract because every product sold is always a specific type.
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

    /**
     * @return the unique identifier of the product
     */
    public String getId() {
        return id;
    }

    /**
     * @param id the new unique identifier of the product
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return the name of the product
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title the new name of the product
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return the unit price of the product
     */
    public double getPrice() {
        return price;
    }

    /**
     * @param price the new unit price of the product
     */
    public void setPrice(double price) {
        this.price = price;
    }

    /**
     * @return the units currently available in inventory
     */
    public int getQuantity() {
        return quantity;
    }

    /**
     * @param quantity the new quantity available in inventory
     */
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
