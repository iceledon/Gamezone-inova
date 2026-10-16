package com.gamezone.validation;

import com.gamezone.exceptions.BusinessRuleException;
import com.gamezone.exceptions.InvalidDataException;
import com.gamezone.exceptions.ResourceNotFoundException;
import com.gamezone.model.Product;

/**
 * Validation rules for products, called by the services before any business operation.
 */
public final class ProductValidator {

    private ProductValidator() {
    }

    /**
     * Checks the common data of a product before it is registered.
     *
     * @param id    unique identifier of the product
     * @param title name of the product
     * @param price unit price of the product
     * @param stock units available in inventory
     * @throws InvalidDataException if a text is empty, the price is not positive or the stock is negative
     */
    public static void validateProductData(String id, String title, double price, int stock) {
        requireText(id, "codigo");
        requireText(title, "titulo");
        if (price <= 0) {
            throw new InvalidDataException("precio", "debe ser mayor que cero");
        }
        if (stock < 0) {
            throw new InvalidDataException("cantidad en stock", "no puede ser negativo");
        }
    }

    /**
     * Checks that the product has enough units for the requested quantity.
     *
     * @param product           product to check
     * @param requestedQuantity units the operation needs
     * @throws BusinessRuleException if the stock is lower than the requested quantity
     */
    public static void validateStockAvailability(Product product, int requestedQuantity) {
        if (product.getQuantity() < requestedQuantity) {
            throw new BusinessRuleException(String.format(
                    "Stock insuficiente para %s. Disponible: %d, solicitado: %d.",
                    product.getTitle(), product.getQuantity(), requestedQuantity));
        }
    }

    /**
     * Checks that a product lookup found something.
     *
     * @param product   result of the lookup, may be null
     * @param productId identifier that was searched
     * @throws ResourceNotFoundException if the product is null
     */
    public static void validateProductExists(Product product, String productId) {
        if (product == null) {
            throw new ResourceNotFoundException("un producto", productId);
        }
    }

    private static void requireText(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new InvalidDataException(fieldName, "no puede estar vacio");
        }
    }
}
