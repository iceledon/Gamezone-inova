package com.gamezone.ui;

import java.util.List;
import com.gamezone.model.Warranty;
import com.gamezone.service.WarrantyService;

/** Console interaction for the WarrantyMenu module. */
public final class WarrantyMenu {
    private final ConsoleSupport support;
    private final WarrantyService warrantyService;

    public WarrantyMenu(WarrantyService warrantyService, ConsoleSupport support) {
        this.support = support;
        this.warrantyService = warrantyService;
    }

    /**
     * Shows the warranty submenu until the user goes back.
     */
    public void show() {
        if (warrantyService == null) {
            System.out.println("Modulo de garantias no disponible.");
            return;
        }
        boolean back = false;
        while (!back) {
            System.out.println();
            System.out.println("--- Gestion de garantias ---");
            System.out.println("1. Consultar garantia de un producto en una venta");
            System.out.println("2. Listar todas las garantias");
            System.out.println("3. Listar garantias vigentes");
            System.out.println("4. Listar garantias proximas a vencer");
            System.out.println("0. Volver");
            System.out.print("Seleccione una opcion: ");
            try {
                switch (support.ask("")) {
                    case "1" -> consultWarranty();
                    case "2" -> listAllWarranties();
                    case "3" -> listActiveWarranties();
                    case "4" -> listWarrantiesExpiringSoon();
                    case "0" -> back = true;
                    default -> System.out.println("Opcion invalida.");
                }
            } catch (RuntimeException e) {
                support.showError(e);
            }
        }
    }

    /**
     * Prints the warranty certificate of one product inside one sale.
     */
    private void consultWarranty() {
        String productId = support.ask("Codigo del producto: ");
        String saleId = support.ask("Identificacion de la venta: ");
        Warranty warranty = warrantyService.findWarrantyByProduct(productId, saleId);
        if (warranty == null) {
            System.out.println("Este producto no tiene una garantia registrada en esa venta.");
        } else {
            System.out.println(warranty.generateWarrantyCertificate());
        }
    }

    /**
     * Prints every registered warranty.
     */
    private void listAllWarranties() {
        printWarranties(warrantyService.listAllWarranties(), "No hay garantias registradas.");
    }

    /**
     * Prints only the warranties active on the current date.
     */
    private void listActiveWarranties() {
        printWarranties(warrantyService.listActiveWarranties(), "No hay garantias vigentes.");
    }

    /**
     * Prints the warranties about to expire within a number of days the user provides.
     */
    private void listWarrantiesExpiringSoon() {
        int days = support.askInt("Dias de anticipacion: ");
        printWarranties(warrantyService.listWarrantiesExpiringSoon(days),
                "No hay garantias proximas a vencer en ese rango.");
    }

    /**
     * Prints a list of warranties, or a message when the list is empty.
     *
     * @param warranties   the warranties to print
     * @param emptyMessage the message shown when there is nothing to print
     */
    private void printWarranties(List<Warranty> warranties, String emptyMessage) {
        if (warranties.isEmpty()) {
            System.out.println(emptyMessage);
            return;
        }
        for (Warranty warranty : warranties) {
            System.out.println();
            System.out.println(warranty.generateWarrantyCertificate());
        }
    }
}
