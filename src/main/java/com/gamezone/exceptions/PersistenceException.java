package com.gamezone.exceptions;

/**
 * Thrown when a data file cannot be read or written.
 */
public class PersistenceException extends GameZoneException {

    private static final String ERROR_CODE = "PERSISTENCE";

    /**
     * Keeps the original exception as the cause so its stack trace is not lost.
     *
     * @param message what failed, including the file and the operation
     * @param cause   original exception, usually an IOException
     */
    public PersistenceException(String message, Throwable cause) {
        super(message, ERROR_CODE);
        initCause(cause);
    }
}
