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
    private String appliedPromotionName;
    private double discountAmount;

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
     * Returns the name of the promotion applied to this sale.
     *
     * @return the applied promotion's name, or null if no promotion was applied
     */
    public String getAppliedPromotionName() {
        return appliedPromotionName;
    }

    /**
     * Sets the name of the promotion applied to this sale.
     *
     * @param appliedPromotionName the name of the applied promotion
     */
    public void setAppliedPromotionName(String appliedPromotionName) {
        this.appliedPromotionName = appliedPromotionName;
    }

    /**
     * Returns the discount amount applied to this sale.
     *
     * @return the discount amount, or 0.0 if no promotion was applied
     */
    public double getDiscountAmount() {
        return discountAmount;
    }

    /**
     * Sets the discount amount applied to this sale.
     *
     * @param discountAmount the discount amount to set
     */
    public void setDiscountAmount(double discountAmount) {
        this.discountAmount = discountAmount;
    }

    /**
     * Calculates the subtotal of this sale by adding up the price of every unit it
     * contains, before any promotion discount.
     *
     * @return the subtotal of the sale
     */
    public double calculateSubtotal() {
        double subtotal = 0.0;
        for (Product product : products) {
            subtotal += product.getPrice();
        }
        return subtotal;
    }

    /**
     * Calculates the final amount of this sale: the subtotal minus the discount granted
     * by the promotion applied, if any.
     *
     * @return the total amount of the sale
     */
    public double calculateTotal() {
        return calculateSubtotal() - discountAmount;
    }

    /**
     * Revisa si esta venta todavia se puede devolver, es decir, si no han pasado mas de
     * 30 dias calendario desde que se hizo. Ese es el plazo maximo que da la tienda.
     *
     * @return true si todavia se puede devolver, false si ya paso el plazo
     */
    public boolean canBeReturned() {
        LocalDate limitDate = this.date.plusDays(30);
        LocalDate today = LocalDate.now();
        if (today.isAfter(limitDate)) {
            return false;
        }
        return true;
    }

    /**
     * Builds a receipt in Spanish showing the subtotal, the discount applied (if any) and
     * the final total, so the console interface can print it directly.
     *
     * @return the formatted receipt text
     */
    public String generateReceipt() {
        StringBuilder receipt = new StringBuilder();
        receipt.append("Recibo de venta ").append(id).append(" | Fecha: ").append(date).append("\n");
        receipt.append("  Cliente:  [").append(customer.getId()).append("] ").append(customer.getName()).append("\n");
        receipt.append("  Vendedor: [").append(seller.getId()).append("] ").append(seller.getName()).append("\n");
        receipt.append("  Productos:\n");
        for (Product product : products) {
            receipt.append("    - [").append(product.getId()).append("] ")
                    .append(product.getTitle()).append(" ($").append(product.getPrice()).append(")\n");
        }
        receipt.append(String.format("  Subtotal: $%.2f%n", calculateSubtotal()));
        if (discountAmount > 0) {
            receipt.append(String.format("  Descuento aplicado (%s): -$%.2f%n",
                    appliedPromotionName, discountAmount));
        }
        receipt.append(String.format("  Total: $%.2f", calculateTotal()));
        return receipt.toString();
    }
}
