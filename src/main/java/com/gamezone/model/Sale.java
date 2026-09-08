package com.gamezone.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a sale of one or more products, made by a {@link Customer} and handled by
 * a {@link Seller}.
 * <p>
 * The sale is associated with exactly one customer and one seller, and aggregates the
 * products it sold: those products keep existing in the inventory independently of the
 * sale. The sale computes its own total because the data needed for that calculation is
 * data it already owns.
 */
public class Sale {

    private String id;
    private LocalDate date;
    private Customer customer;
    private Seller seller;
    private List<Product> products;

    /**
     * Creates a new sale with the given participants and products.
     * <p>
     * The constructor rejects an empty product list to protect the invariant of the
     * class: a sale without products cannot exist. The business rule itself is verified
     * earlier, in the service layer, so the end user gets a clear message instead of an
     * exception; this check is the last line of defense.
     *
     * @param id       the unique identifier of the sale
     * @param date     the date the sale was made
     * @param customer the customer who made the purchase
     * @param seller   the seller who handled the sale
     * @param products the products included in the sale, one entry per unit sold
     * @throws IllegalArgumentException if the product list is null or empty
     */
    public Sale(String id, LocalDate date, Customer customer, Seller seller, List<Product> products) {
        if (products == null || products.isEmpty()) {
            throw new IllegalArgumentException("La venta debe contener al menos un producto.");
        }
        this.id = id;
        this.date = date;
        this.customer = customer;
        this.seller = seller;
        this.products = new ArrayList<>(products);
    }

    /**
     * Returns the unique identifier of this sale.
     *
     * @return the sale id
     */
    public String getId() {
        return id;
    }

    /**
     * Returns the date this sale was made.
     *
     * @return the sale date
     */
    public LocalDate getDate() {
        return date;
    }

    /**
     * Returns the customer who made this purchase.
     *
     * @return the customer
     */
    public Customer getCustomer() {
        return customer;
    }

    /**
     * Returns the seller who handled this sale.
     *
     * @return the seller
     */
    public Seller getSeller() {
        return seller;
    }

    /**
     * Returns the products included in this sale, one entry per unit sold.
     *
     * @return the list of sold products
     */
    public List<Product> getProducts() {
        return products;
    }

    /**
     * Calculates the total amount of this sale by adding up the price of every unit it
     * contains.
     *
     * @return the total amount of the sale
     */
    public double calculateTotal() {
        double total = 0.0;
        for (Product product : products) {
            total += product.getPrice();
        }
        return total;
    }
}
