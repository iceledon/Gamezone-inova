package com.gamezone.model;

/**
 * Represents a console, a specific type of product.
 */
public class Console extends Product {

    private String brand;
    private String model;
    private int generation;

    /**
     * Creates a new console with its basic and specific information.
     *
     * @param id         unique identifier of the product
     * @param title      name of the product
     * @param price      unit price of the product
     * @param quantity   units currently available in inventory
     * @param brand      manufacturer of the console
     * @param model      model name of the console
     * @param generation generation number of the console
     */
    public Console(String id, String title, double price, int quantity,
                   String brand, String model, int generation) {
        super(id, title, price, quantity);
        this.brand = brand;
        this.model = model;
        this.generation = generation;
    }

    @Override
    public String getDescription() {
        return getTitle() + " - Brand: " + brand + ", Model: " + model + ", Generation: " + generation;
    }

    /**
     * @return the manufacturer of the console
     */
    public String getBrand() {
        return brand;
    }

    /**
     * @return the model name of the console
     */
    public String getModel() {
        return model;
    }

    /**
     * @return the generation number of the console
     */
    public int getGeneration() {
        return generation;
    }
}
