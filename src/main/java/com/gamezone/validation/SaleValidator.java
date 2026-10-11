package com.gamezone.validation;

import com.gamezone.exceptions.BusinessRuleException;
import com.gamezone.exceptions.InvalidDataException;
import com.gamezone.exceptions.ResourceNotFoundException;
import com.gamezone.model.Customer;
import com.gamezone.model.Product;
import com.gamezone.model.Sale;
import com.gamezone.model.Seller;

import java.util.List;

/**
 * Validation rules for sales, called by the services before any business operation.
 */
public final class SaleValidator {

    private SaleValidator() {
    }

    /**
     * Checks that a sale has its participants and at least one product.
     * The products are checked first, in the same order the sale service already uses.
     *
     * @param customer customer making the purchase
     * @param seller   seller handling the sale
     * @param products products of the sale, one entry per unit
     * @throws BusinessRuleException if the sale has no products
     * @throws InvalidDataException  if the customer or the seller is missing
     */
    public static void validateSaleData(Customer customer, Seller seller, List<? extends Product> products) {
        if (products == null || products.isEmpty()) {
            throw new BusinessRuleException("La venta debe contener al menos un producto.");
        }
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
