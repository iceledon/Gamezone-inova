package com.gamezone.exceptions;

/**
 * Thrown when an input value does not meet its format, range or type rules.
 */
public class InvalidDataException extends GameZoneException {

    private static final String ERROR_CODE = "INVALID_DATA";

    /**
     * Builds a Spanish message from the invalid field and the problem found.
     *
     * @param fieldName name of the invalid field, for example "precio"
     * @param problem   what is wrong with the value, for example "debe ser mayor que cero"
     */
    public InvalidDataException(String fieldName, String problem) {
        super("El campo " + fieldName + " " + problem + ".", ERROR_CODE);
    }
}
