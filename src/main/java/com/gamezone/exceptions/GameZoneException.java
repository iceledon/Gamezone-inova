package com.gamezone.exceptions;

/**
 * Root of every custom exception in the system.
 * Unchecked, so services and repositories do not need {@code throws} clauses.
 */
public abstract class GameZoneException extends RuntimeException {

    private final String errorCode;

    /**
     * Creates the exception with a message and no error code.
     *
     * @param message description of the error
     */
    protected GameZoneException(String message) {
        this(message, null);
    }

    /**
     * Creates the exception with a message and an error code.
     *
     * @param message   description of the error
     * @param errorCode short code that identifies the error category
     */
    protected GameZoneException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    /**
     * @return the code that identifies the error category, or null if none was given
     */
    public String getErrorCode() {
        return errorCode;
    }
}
