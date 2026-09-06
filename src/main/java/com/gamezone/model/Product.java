package com.gamezone.model;


public abstract class Product {

    private String id;
    private String title;
    private double price;
    private int quantity;

    /**
     * 
     *
     * @param id       
     * @param title    
     * @param price    
     * @param quantity 
     */
    public Product(String id, String title, double price, int quantity) {
        this.id = id;
        this.title = title;
        this.price = price;
        this.quantity = quantity;
    }

    /**
     * @return 
     */
    public String getId() {
        return id;
    }

    /**
     * @param id 
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return 
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title 
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return 
     */
    public double getPrice() {
        return price;
    }

    /**
     * @param price 
     */
    public void setPrice(double price) {
        this.price = price;
    }

    /**
     * @return 
     */
    public int getQuantity() {
        return quantity;
    }

    /**
     * @param quantity 
     */
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
