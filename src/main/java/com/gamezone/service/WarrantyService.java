package com.gamezone.service;

import com.gamezone.model.BasicWarranty;
import com.gamezone.model.ExtendedWarranty;
import com.gamezone.model.Product;
import com.gamezone.model.Sale;
import com.gamezone.model.Warranty;
import com.gamezone.persistence.WarrantyRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class WarrantyService {

    private final WarrantyRepository repository;
    private final List<Warranty> warranties;

    public WarrantyService(WarrantyRepository repository, ProductService productService,
                           SaleService saleService) {
        this.repository = repository;
        this.warranties = new ArrayList<>(
                repository.loadAll(productService.listAll(), saleService.getAllSales()));
    }

    public BasicWarranty assignBasicWarranty(Product product, Sale sale, LocalDate startDate) {
        BasicWarranty warranty = new BasicWarranty(generateWarrantyId(), product, sale, startDate);
        warranties.add(warranty);
        repository.saveAll(warranties);
        return warranty;
    }

    public ExtendedWarranty assignExtendedWarranty(Product product, Sale sale, LocalDate startDate) {
        ExtendedWarranty warranty = new ExtendedWarranty(generateWarrantyId(), product, sale, startDate);
        warranties.add(warranty);
        repository.saveAll(warranties);
        return warranty;
    }

    private String generateWarrantyId() {
        return String.format("G%03d", warranties.size() + 1);
    }
}
