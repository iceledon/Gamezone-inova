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
     * Verifica que la venta tenga productos y no contenga elementos nulos.
     *
     * @param products productos o identificadores solicitados
     * @throws BusinessRuleException si la lista es nula o está vacía
     * @throws InvalidDataException si algún elemento es nulo
     */
    public static void validateSaleProducts(List<?> products) {
        if (products == null || products.isEmpty()) {
            throw new BusinessRuleException(
                    "La venta debe contener al menos un producto.");
        }

        for (Object product : products) {
            if (product == null) {
                throw new InvalidDataException(
                        "productos",
                        "no se permiten elementos nulos en la venta");
            }
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
