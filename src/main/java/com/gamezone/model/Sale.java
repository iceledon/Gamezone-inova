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
    private String appliedPromotionName;
    private double discountAmount;

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
     * contains, before extended warranties or any promotion discount.
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
     * Calculates the final amount of the sale: the subtotal, plus the extra cost of any
     * extended warranties, minus the discount granted by the promotion applied, if any.
     *
     * @return the total amount of the sale
     */
    public double calculateTotal() {
        return calculateSubtotal() + extraCost - discountAmount;
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
     * Builds a receipt in Spanish showing the subtotal, the extended warranty cost (if
     * any), the discount applied (if any) and the final total, so the console interface
     * can print it directly.
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
        if (extraCost > 0) {
            receipt.append(String.format("  Garantias extendidas: +$%.2f%n", extraCost));
        }
        if (discountAmount > 0) {
            receipt.append(String.format("  Descuento aplicado (%s): -$%.2f%n",
                    appliedPromotionName, discountAmount));
        }
        receipt.append(String.format("  Total: $%.2f", calculateTotal()));
        return receipt.toString();
    }
}