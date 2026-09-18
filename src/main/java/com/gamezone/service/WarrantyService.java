package com.gamezone.service;

import com.gamezone.model.BasicWarranty;
import com.gamezone.model.ExtendedWarranty;
import com.gamezone.model.Product;
import com.gamezone.model.Sale;
import com.gamezone.model.Warranty;
import com.gamezone.persistence.WarrantyRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Business operations available for warranties.
 * <p>
 * Owns the in-memory list of warranties, loading it at construction time and persisting
 * it back to disk every time a new warranty is assigned. Assignment methods are meant to
 * be called from {@code SaleService} when a sale is registered; the consultation methods
 * back the "Gestion de garantias" submenu of the console interface.
 */
public class WarrantyService {

    private final WarrantyRepository repository;
    private final List<Warranty> warranties;

    /**
     * Creates the service and loads the warranty history into memory, resolving the
     * products and sales each stored warranty references through the given services.
     * <p>
     * {@code saleService} must already have its own sales loaded when this constructor
     * runs (which is always true, since {@code SaleService} loads its history in its own
     * constructor), so the warranty-to-sale references can be resolved on the first read.
     *
     * @param repository the repository used to read and write warranties
     * @param productService the service that owns the product inventory
     * @param saleService the service that owns the sales history
     */
    public WarrantyService(WarrantyRepository repository, ProductService productService,
                           SaleService saleService) {
        this.repository = repository;
        this.warranties = new ArrayList<>(
                repository.loadAll(productService.listAll(), saleService.getAllSales()));
    }

    /**
     * Creates, persists and returns the automatic basic warranty for a product sold.
     *
     * @param product the product being covered
     * @param sale the sale that generated the warranty
     * @param startDate the date the coverage begins
     * @return the newly created basic warranty
     */
    public BasicWarranty assignBasicWarranty(Product product, Sale sale, LocalDate startDate) {
        BasicWarranty warranty = new BasicWarranty(generateWarrantyId(), product, sale, startDate);
        warranties.add(warranty);
        repository.saveAll(warranties);
        return warranty;
    }

    /**
     * Creates, persists and returns an extended warranty requested for a product.
     *
     * @param product the product being covered
     * @param sale the sale that generated the warranty
     * @param startDate the date the coverage begins
     * @return the newly created extended warranty
     */
    public ExtendedWarranty assignExtendedWarranty(Product product, Sale sale, LocalDate startDate) {
        ExtendedWarranty warranty = new ExtendedWarranty(generateWarrantyId(), product, sale, startDate);
        warranties.add(warranty);
        repository.saveAll(warranties);
        return warranty;
    }

    /**
     * Finds the warranty associated with a specific product inside a specific sale.
     *
     * @param productId the id of the product to look for
     * @param saleId the id of the sale to look for
     * @return the matching warranty, or null if none exists
     */
    public Warranty findWarrantyByProduct(String productId, String saleId) {
        for (Warranty warranty : warranties) {
            if (warranty.getProduct().getId().equals(productId)
                    && warranty.getSale().getId().equals(saleId)) {
                return warranty;
            }
        }
        return null;
    }

    /**
     * @return every warranty registered in the system
     */
    public List<Warranty> listAllWarranties() {
        return Collections.unmodifiableList(warranties);
    }

    /**
     * @return the warranties that are active on the current date
     */
    public List<Warranty> listActiveWarranties() {
        List<Warranty> result = new ArrayList<>();
        LocalDate today = LocalDate.now();
        for (Warranty warranty : warranties) {
            if (warranty.isActive(today)) {
                result.add(warranty);
            }
        }
        return result;
    }

    /**
     * Lists the warranties whose end date falls within the next {@code daysAhead} days
     * from today, inclusive, and that have not already expired.
     *
     * @param daysAhead how many days ahead to look
     * @return the warranties about to expire
     */
    public List<Warranty> listWarrantiesExpiringSoon(int daysAhead) {
        List<Warranty> result = new ArrayList<>();
        LocalDate today = LocalDate.now();
        LocalDate limit = today.plusDays(daysAhead);
        for (Warranty warranty : warranties) {
            LocalDate endDate = warranty.getEndDate();
            if (!endDate.isBefore(today) && !endDate.isAfter(limit)) {
                result.add(warranty);
            }
        }
        return result;
    }

    /**
     * Builds the identifier of the next warranty, following the format {@code G001}.
     *
     * @return the id for the warranty about to be created
     */
    private String generateWarrantyId() {
        return String.format("G%03d", warranties.size() + 1);
    }
}
