package com.gamezone.exceptions;

/**
 * Thrown when a resource (product, customer, seller, sale) is searched by id and does not exist.
 */
public class ResourceNotFoundException extends GameZoneException {

    private static final String ERROR_CODE = "NOT_FOUND";

    /**
     * Builds a Spanish message from the resource type and the id that was searched.
     *
     * @param resourceType type of the resource with its article, for example "un producto"
     * @param resourceId   identifier that was searched and not found
     */
    public ResourceNotFoundException(String resourceType, String resourceId) {
        super("No existe " + resourceType + " con el identificador " + resourceId + ".", ERROR_CODE);
    }
}
