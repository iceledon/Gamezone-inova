package com.gamezone.model;

/**
 * Represents a customer of the store.
 * Extends the Person class with customer-specific information.
 */
public class Customer extends Person {
    private String email;

    /**
     * Constructs a new Customer.
     *
     * @param id    the unique identifier
     * @param name  the full name
     * @param phone the contact phone number
     * @param email the contact email address
     */
    public Customer(String id, String name, String phone, String email) {
        super(id, name, phone);
        this.email = email;
    }

    /**
     * Gets the customer's email.
     *
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the customer's email.
     *
     * @param email the email to set
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Returns the role description for a customer.
     *
     * @return the role description
     */
    @Override
    public String getRoleDescription() {
        return "Cliente";
    }
}
