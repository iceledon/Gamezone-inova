package com.gamezone.exceptions;

/**
 * Thrown when an operation breaks a business rule, such as insufficient stock or a duplicated id.
 */
public class BusinessRuleException extends GameZoneException {

    private static final String ERROR_CODE = "BUSINESS_RULE";

    /**
     * @param message description of the business rule that was broken
     */
    public BusinessRuleException(String message) {
        super(message, ERROR_CODE);
    }
}
