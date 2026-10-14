package com.gamezone.validation;

import java.util.List;

import com.gamezone.exceptions.BusinessRuleException;
import com.gamezone.exceptions.InvalidDataException;
import com.gamezone.exceptions.ResourceNotFoundException;
import com.gamezone.model.Customer;
import com.gamezone.model.Product;
import com.gamezone.model.Sale;
import com.gamezone.model.Seller;

/**
 * Validation rules for sales, called by the services before any business operation.
 */
public final class SaleValidator {

    private SaleValidator() {
    }

  /**
 * Checks that a sale requests at least one product.
 *
 * @param products requested products or product identifiers
 * @throws BusinessRuleException if the list is null or empty
 */
public static void validateSaleProducts(List<?> products) {
    if (products == null || products.isEmpty()) {
        throw new BusinessRuleException(
                "La venta debe contener al menos un producto.");
    }
}
    public static void validateSaleData(Customer customer, Seller seller, List<? extends Product> products) {
       validateSaleProducts(products);
        if (customer == null) {
            throw new InvalidDataException("cliente", "es obligatorio");
        }
        if (seller == null) {
            throw new InvalidDataException("vendedor", "es obligatorio");
        }
    }

    /**
     * Checks that a sale lookup found something.
     *
     * @param sale   result of the lookup, may be null
     * @param saleId identifier that was searched
     * @throws ResourceNotFoundException if the sale is null
     */
    public static void validateSaleExists(Sale sale, String saleId) {
        if (sale == null) {
            throw new ResourceNotFoundException("una venta", saleId);
        }
    }
}
