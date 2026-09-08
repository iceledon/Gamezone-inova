package com.gamezone.model;

/**
 * Represents a store seller.
 * Extends the Person class with seller-specific information.
 */
public class Seller extends Person {
    private String employeeCode;
    private String shift;

    /**
     * Constructs a new Seller.
     *
     * @param id           the unique identifier
     * @param name         the full name
     * @param phone        the contact phone number
     * @param employeeCode the employee identification code
     * @param shift        the work shift (e.g., Manana, Tarde, Noche)
     */
    public Seller(String id, String name, String phone, String employeeCode, String shift) {
        super(id, name, phone);
        this.employeeCode = employeeCode;
        this.shift = shift;
    }

    /**
     * Gets the employee identification code.
     *
     * @return the employee code
     */
    public String getEmployeeCode() {
        return employeeCode;
    }

    /**
     * Sets the employee identification code.
     *
     * @param employeeCode the employee code to set
     */
    public void setEmployeeCode(String employeeCode) {
        this.employeeCode = employeeCode;
    }

    /**
     * Gets the work shift.
     *
     * @return the shift
     */
    public String getShift() {
        return shift;
    }

    /**
     * Sets the work shift.
     *
     * @param shift the shift to set
     */
    public void setShift(String shift) {
        this.shift = shift;
    }

    /**
     * Returns the role description for a seller.
     *
     * @return the role description
     */
    @Override
    public String getRoleDescription() {
        return "Vendedor";
    }
}
