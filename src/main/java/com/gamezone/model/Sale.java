package com.gamezone.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents a sale registered in the store.
 * A sale is associated with one customer, one seller
 * and one or more products.
 */
public class Sale {

    private String id;
    private LocalDate date;
    private Customer customer;
    private Seller seller;
    private List<Product> products;
    private double extraCost;

    /**
     * Creates a new sale.
     *
     * @param id unique identifier of the sale
     * @param date date when the sale was registered
     * @param customer customer who made the purchase
     * @param seller seller who registered the sale
     * @param products products included in the sale
     */
    public Sale(String id, LocalDate date, Customer customer,
                Seller seller, List<Product> products) {

        this.id = id;
        this.date = date;
        this.customer = customer;
        this.seller = seller;
        this.products = new ArrayList<>(products);
        this.extraCost = 0.0;
    }

    /**
     * @return the unique identifier of the sale
     */
    public String getId() {
        return id;
    }

    /**
     * @return the date when the sale was registered
     */
    public LocalDate getDate() {
        return date;
    }

    /**
     * @return the customer associated with the sale
     */
    public Customer getCustomer() {
        return customer;
    }

    /**
     * @return the seller associated with the sale
     */
    public Seller getSeller() {
        return seller;
    }

    /**
     * @return the products included in the sale
     */
    public List<Product> getProducts() {
        return Collections.unmodifiableList(products);
    }

    /**
     * @return the extra amount added to this sale by extended warranties
     */
    public double getExtraCost() {
        return extraCost;
    }

    /**
     * Adds an extra amount to this sale, used when an extended warranty
     * is assigned to one of its products.
     *
     * @param amount the extra amount to add
     */
    public void addExtraCost(double amount) {
        this.extraCost += amount;
    }

    /**
     * Calculates the total amount of the sale, including
     * the additional cost of extended warranties.
     *
     * @return the total amount of the sale
     */
    public double calculateTotal() {
        double total = 0.0;

        for (Product product : products) {
            total += product.getPrice();
        }

        return total + extraCost;
    }
}