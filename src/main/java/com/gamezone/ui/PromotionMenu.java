package com.gamezone.ui;

import java.time.LocalDate;
import java.util.List;
import com.gamezone.model.BulkPurchaseDiscount;
import com.gamezone.model.CategoryDiscount;
import com.gamezone.model.PercentageDiscount;
import com.gamezone.model.Promotion;
import com.gamezone.service.PromotionService;

/**
 * Console submenu for the promotion module, extracted from ConsoleUI (V-08).
 */
public class PromotionMenu {

    private final PromotionService promotionService;
    private final ConsoleSupport support;

    /**
     * Creates the promotion submenu.
     *
     * @param promotionService service that manages the promotions
     * @param support          shared console input and error helper
     */
    public PromotionMenu(PromotionService promotionService, ConsoleSupport support) {
        this.promotionService = promotionService;
        this.support = support;
    }

    /**
     * Shows the promotion submenu until the user goes back.
     */
    public void show() {
        boolean back = false;
        while (!back) {
            System.out.println();
            System.out.println("--- Gestion de promociones ---");
            System.out.println("1. Registrar promocion por porcentaje");
            System.out.println("2. Registrar promocion por categoria");
            System.out.println("3. Registrar promocion por volumen de compra");
            System.out.println("4. Listar todas las promociones");
            System.out.println("5. Listar promociones vigentes");
            System.out.println("0. Volver");
            System.out.print("Seleccione una opcion: ");
            try {
                switch (support.ask("")) {
                    case "1" -> registerPercentageDiscount();
                    case "2" -> registerCategoryDiscount();
                    case "3" -> registerBulkPurchaseDiscount();
                    case "4" -> listAllPromotions();
                    case "5" -> listActivePromotions();
                    case "0" -> back = true;
                    default -> System.out.println("Opcion invalida.");
                }
            } catch (RuntimeException e) {
                support.showError(e);
            }
        }
    }

    /**
     * Asks for the data of a percentage discount and registers it.
     */
    private void registerPercentageDiscount() {
        try {
            String id = support.ask("Codigo de la promocion: ");
            String name = support.ask("Nombre de la promocion: ");
            LocalDate startDate = support.askDate("Fecha de inicio (aaaa-mm-dd): ");
            LocalDate endDate = support.askDate("Fecha de fin (aaaa-mm-dd): ");
            double percentage = support.askDouble("Porcentaje de descuento: ");
            promotionService.registerPercentageDiscount(id, name, startDate, endDate, percentage);
            System.out.println("Promocion registrada correctamente.");
        } catch (RuntimeException e) {
            support.showError(e);
        }
    }

    /**
     * Asks for the data of a category discount and registers it.
     */
    private void registerCategoryDiscount() {
        try {
            String id = support.ask("Codigo de la promocion: ");
            String name = support.ask("Nombre de la promocion: ");
            LocalDate startDate = support.askDate("Fecha de inicio (aaaa-mm-dd): ");
            LocalDate endDate = support.askDate("Fecha de fin (aaaa-mm-dd): ");
            double percentage = support.askDouble("Porcentaje de descuento: ");
            String targetCategory = support.ask("Categoria objetivo (VIDEOGAME/CONSOLE/ACCESSORY): ");
            promotionService.registerCategoryDiscount(id, name, startDate, endDate, percentage, targetCategory);
            System.out.println("Promocion registrada correctamente.");
        } catch (RuntimeException e) {
            support.showError(e);
        }
    }

    /**
     * Asks for the data of a bulk purchase discount and registers it.
     */
    private void registerBulkPurchaseDiscount() {
        try {
            String id = support.ask("Codigo de la promocion: ");
            String name = support.ask("Nombre de la promocion: ");
            LocalDate startDate = support.askDate("Fecha de inicio (aaaa-mm-dd): ");
            LocalDate endDate = support.askDate("Fecha de fin (aaaa-mm-dd): ");
            int minimumQuantity = support.askInt("Cantidad minima de productos: ");
            double percentage = support.askDouble("Porcentaje de descuento: ");
            promotionService.registerBulkPurchaseDiscount(id, name, startDate, endDate, minimumQuantity, percentage);
            System.out.println("Promocion registrada correctamente.");
        } catch (RuntimeException e) {
            support.showError(e);
        }
    }

    /**
     * Prints every registered promotion.
     */
    private void listAllPromotions() {
        printPromotions(promotionService.listAllPromotions(), "No hay promociones registradas.");
    }

    /**
     * Prints only the promotions valid on the current date.
     */
    private void listActivePromotions() {
        printPromotions(promotionService.listActivePromotions(), "No hay promociones vigentes.");
    }

    /**
     * Prints a list of promotions, or a message when the list is empty.
     *
     * @param promotions   the promotions to print
     * @param emptyMessage the message shown when there is nothing to print
     */
    private void printPromotions(List<Promotion> promotions, String emptyMessage) {
        if (promotions.isEmpty()) {
            System.out.println(emptyMessage);
            return;
        }
        for (Promotion promotion : promotions) {
            System.out.printf("  [%s] %s | Vigencia: %s al %s | %s%n",
                    promotion.getId(), promotion.getName(), promotion.getStartDate(), promotion.getEndDate(),
                    describePromotion(promotion));
        }
    }

    /**
     * Builds a short description of a promotion's type-specific rule.
     *
     * @param promotion the promotion to describe
     * @return the description text
     */
    private String describePromotion(Promotion promotion) {
        if (promotion instanceof PercentageDiscount percentageDiscount) {
            return String.format("%.0f%% sobre el total", percentageDiscount.getPercentage());
        }
        if (promotion instanceof CategoryDiscount categoryDiscount) {
            return String.format("%.0f%% sobre %s", categoryDiscount.getPercentage(), categoryDiscount.getTargetCategory());
        }
        if (promotion instanceof BulkPurchaseDiscount bulkPurchaseDiscount) {
            return String.format("%.0f%% desde %d productos", bulkPurchaseDiscount.getPercentage(),
                    bulkPurchaseDiscount.getMinimumQuantity());
        }
        return "";
    }
}
