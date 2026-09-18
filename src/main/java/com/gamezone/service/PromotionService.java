package com.gamezone.service;

import com.gamezone.model.BulkPurchaseDiscount;
import com.gamezone.model.CategoryDiscount;
import com.gamezone.model.PercentageDiscount;
import com.gamezone.model.Promotion;
import com.gamezone.model.Sale;
import com.gamezone.persistence.PromotionRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Business rules for managing promotions.
 * <p>
 * Owns the in-memory list of promotions, loading it at construction time and persisting
 * it back to disk every time a new promotion is registered. This is also where the "which
 * promotion applies to this sale" decision lives: {@link #findBestPromotionFor(Sale)}
 * compares every currently valid promotion and picks the one that grants the highest
 * discount, so neither {@code Sale} nor the console menu need to know how that choice is
 * made.
 */
public class PromotionService {

    private final PromotionRepository repository;
    private final List<Promotion> promotions;

    /**
     * @param repository repository used to persist promotions
     */
    public PromotionService(PromotionRepository repository) {
        this.repository = repository;
        this.promotions = repository.loadAll();
    }

    /**
     * Registers a new percentage discount and persists the updated list.
     *
     * @param id         unique identifier of the promotion
     * @param name       display name of the promotion
     * @param startDate  date the promotion becomes valid
     * @param endDate    date the promotion stops being valid
     * @param percentage discount percentage to apply, between 0 and 100
     */
    public void registerPercentageDiscount(String id, String name, LocalDate startDate, LocalDate endDate,
                                            double percentage) {
        rejectIfIdExists(id);
        promotions.add(new PercentageDiscount(id, name, startDate, endDate, percentage));
        repository.saveAll(promotions);
    }

    /**
     * Registers a new category discount and persists the updated list.
     *
     * @param id             unique identifier of the promotion
     * @param name           display name of the promotion
     * @param startDate      date the promotion becomes valid
     * @param endDate        date the promotion stops being valid
     * @param percentage     discount percentage to apply, between 0 and 100
     * @param targetCategory category the discount applies to, {@code "VIDEOGAME"} or {@code "CONSOLE"}
     */
    public void registerCategoryDiscount(String id, String name, LocalDate startDate, LocalDate endDate,
                                          double percentage, String targetCategory) {
        rejectIfIdExists(id);
        promotions.add(new CategoryDiscount(id, name, startDate, endDate, percentage, targetCategory));
        repository.saveAll(promotions);
    }

    /**
     * Registers a new bulk purchase discount and persists the updated list.
     *
     * @param id              unique identifier of the promotion
     * @param name            display name of the promotion
     * @param startDate       date the promotion becomes valid
     * @param endDate         date the promotion stops being valid
     * @param minimumQuantity minimum number of products a sale must include to qualify
     * @param percentage      discount percentage to apply, between 0 and 100
     */
    public void registerBulkPurchaseDiscount(String id, String name, LocalDate startDate, LocalDate endDate,
                                              int minimumQuantity, double percentage) {
        rejectIfIdExists(id);
        promotions.add(new BulkPurchaseDiscount(id, name, startDate, endDate, minimumQuantity, percentage));
        repository.saveAll(promotions);
    }

    /**
     * @return every promotion currently registered
     */
    public List<Promotion> listAllPromotions() {
        return new ArrayList<>(promotions);
    }

    /**
     * @return the promotions that are valid on the current date
     */
    public List<Promotion> listActivePromotions() {
        List<Promotion> active = new ArrayList<>();
        LocalDate today = LocalDate.now();
        for (Promotion promotion : promotions) {
            if (promotion.isActive(today)) {
                active.add(promotion);
            }
        }
        return active;
    }

    /**
     * Finds the promotion that grants the highest discount to the given sale, among the
     * promotions currently valid. Promotions are not combined: only one, the best one,
     * ever applies to a sale.
     *
     * @param sale the sale to evaluate
     * @return the best applicable promotion, or null if none applies or the best discount is zero
     */
    public Promotion findBestPromotionFor(Sale sale) {
        Promotion bestPromotion = null;
        double bestDiscount = 0.0;
        for (Promotion promotion : listActivePromotions()) {
            double discount = promotion.calculateDiscount(sale);
            if (discount > bestDiscount) {
                bestDiscount = discount;
                bestPromotion = promotion;
            }
        }
        return bestPromotion;
    }

    /**
     * Finds a promotion by its identifier.
     *
     * @param id the id of the promotion to look for
     * @return the matching promotion, or null if none exists
     */
    public Promotion findById(String id) {
        for (Promotion promotion : promotions) {
            if (promotion.getId().equals(id)) {
                return promotion;
            }
        }
        return null;
    }

    private void rejectIfIdExists(String id) {
        if (findById(id) != null) {
            throw new IllegalArgumentException("Ya existe una promocion con el codigo " + id + ".");
        }
    }
}
