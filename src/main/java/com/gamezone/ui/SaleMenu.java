package com.gamezone.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import com.gamezone.exceptions.ResourceNotFoundException;
import com.gamezone.model.Console;
import com.gamezone.model.Product;
import com.gamezone.model.Sale;
import com.gamezone.service.AccessoryService;
import com.gamezone.service.ProductService;
import com.gamezone.service.SaleService;

/** Console interaction for the SaleMenu module. */
public final class SaleMenu {
    private final ConsoleSupport support;
    private final SaleService saleService;
    private final ProductService productService;
    private final AccessoryService accessoryService;

    public SaleMenu(SaleService saleService, ProductService productService, AccessoryService accessoryService, ConsoleSupport support) {
        this.support = support;
        this.saleService = saleService;
        this.productService = productService;
        this.accessoryService = accessoryService;
    }

    /**
     * Shows the sales submenu until the user goes back.
     */
    public void show() {
        boolean back = false;
        while (!back) {
            System.out.println();
            System.out.println("--- Gestion de ventas ---");
            System.out.println("1. Registrar venta");
            System.out.println("2. Historial completo de ventas");
            System.out.println("3. Historial por cliente");
            System.out.println("4. Historial por vendedor");
            System.out.println("0. Volver");
            System.out.print("Seleccione una opcion: ");
            try {
                switch (support.ask("")) {
                    case "1" -> registerSale();
                    case "2" -> showAllSales();
                    case "3" -> showSalesByCustomer();
                    case "4" -> showSalesBySeller();
                    case "0" -> back = true;
                    default -> System.out.println("Opcion invalida.");
                }
            } catch (RuntimeException e) {
                support.showError(e);
            }
        }
    }

    /**
     * Asks for the data of a sale and registers it, offering an extended warranty for
     * every console sold before sending the sale to the service layer. Each item can be
     * a regular product or an accessory, resolved through {@link #findAnyItemById(String)}.
     */
   private void registerSale() {
    try {
        String customerId = support.ask("Identificacion del cliente: ");
        String sellerId = support.ask("Identificacion del vendedor: ");
        int units = support.askInt(
                "Cantidad de items a vender (productos o accesorios): ");

        List<String> productIds = new ArrayList<>();
        List<String> productIdsWithExtendedWarranty = new ArrayList<>();

        for (int i = 1; i <= units; i++) {
            String productId = support.ask(
                    "Codigo del producto o accesorio " + i + ": ");
            productIds.add(productId);

            Product product = findAnyItemById(productId);
            if (product instanceof Console) {
                String answer = support.ask(
                        "Desea garantia extendida para esta consola? (s/n): ");

                if (answer.equalsIgnoreCase("s")
                        || answer.equalsIgnoreCase("si")) {
                    productIdsWithExtendedWarranty.add(productId);
                }
            }
        }

        Sale sale = saleService.registerSale(
                customerId,
                sellerId,
                productIds,
                productIdsWithExtendedWarranty);

        System.out.println("Venta registrada correctamente.");
        printSale(sale);
    } catch (RuntimeException e) {
            support.showError(e);
        }
}

    /**
 * Looks up an item in both catalogs to offer an extended warranty.
 *
 * @param id the product or accessory identifier
 * @return the matching item, or null if neither catalog contains it
 */
private Product findAnyItemById(String id) {
    try {
        return productService.findById(id);
    } catch (ResourceNotFoundException e) {
        if (accessoryService == null) {
            return null;
        }

        try {
            return accessoryService.findById(id);
        } catch (NoSuchElementException e2) {
            return null;
        }
    }
}

    /**
 * Prints the complete sales history.
 */
private void showAllSales() {
    printSales(
            saleService.getAllSales(),
            "No hay ventas registradas.");
}

    /**
 * Prints the sales history of one customer.
 */
private void showSalesByCustomer() {
    String customerId = support.ask("Identificacion del cliente: ");
    printSales(
            saleService.getSalesByCustomer(customerId),
            "Este cliente no tiene compras registradas.");
}

    /**
 * Prints the sales history of one seller.
 */
private void showSalesBySeller() {
    String sellerId = support.ask("Identificacion del vendedor: ");
    printSales(
            saleService.getSalesBySeller(sellerId),
            "Este vendedor no tiene ventas registradas.");
}

    /**
 * Prints sales, or a message when the list is empty.
 *
 * @param sales the sales to print
 * @param emptyMessage the message shown when there are no sales
 */
private void printSales(List<Sale> sales, String emptyMessage) {
    if (sales.isEmpty()) {
        System.out.println(emptyMessage);
        return;
    }

    for (Sale sale : sales) {
        printSale(sale);
    }
}

    /**
     * Prints the receipt of a single sale, delegating the formatting to
     * {@link Sale#generateReceipt()} so the same text is used whether this is called
     * right after registering a sale or while browsing the sales history.
     *
     * @param sale the sale to print
     */
    private void printSale(Sale sale) {
        System.out.println();
        System.out.println(sale.generateReceipt());
    }
}
