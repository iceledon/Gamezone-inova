package com.gamezone.ui;

import java.util.ArrayList;
import java.util.List;
import com.gamezone.model.Return;
import com.gamezone.service.ReturnService;

/** Console interaction for the ReturnMenu module. */
public final class ReturnMenu {
    private final ConsoleSupport support;
    private final ReturnService returnService;

    public ReturnMenu(ReturnService returnService, ConsoleSupport support) {
        this.support = support;
        this.returnService = returnService;
    }

    /**
     * Muestra el submenu de devoluciones hasta que el usuario decide volver.
     */
    public void show() {
        boolean back = false;
        while (!back) {
            System.out.println();
            System.out.println("--- Gestion de devoluciones ---");
            System.out.println("1. Registrar devolucion");
            System.out.println("2. Listar todas las devoluciones");
            System.out.println("3. Devoluciones por cliente");
            System.out.println("4. Devoluciones por venta");
            System.out.println("5. Balance mensual");
            System.out.println("0. Volver");
            System.out.print("Seleccione una opcion: ");
            try {
                switch (support.ask("")) {
                    case "1" -> registerReturn();
                    case "2" -> showAllReturns();
                    case "3" -> showReturnsByCustomer();
                    case "4" -> showReturnsBySale();
                    case "5" -> showMonthlyBalance();
                    case "0" -> back = true;
                    default -> System.out.println("Opcion invalida.");
                }
            } catch (RuntimeException e) {
                support.showError(e);
            }
        }
    }

    /**
     * Pide los datos de una devolucion y la registra.
     */
    private void registerReturn() {
        try {
            String saleId = support.ask("Identificacion de la venta original: ");
            int units = support.askInt("Cantidad de productos a devolver: ");
            List<String> productIds = new ArrayList<>();
            for (int i = 1; i <= units; i++) {
                productIds.add(support.ask("Codigo del producto " + i + " a devolver: "));
            }
            String reason = support.ask("Motivo de la devolucion: ");
            Return newReturn = returnService.registerReturn(saleId, productIds, reason);
            System.out.println("Devolucion registrada correctamente.");
            System.out.println(newReturn.generateReturnReceipt());
        } catch (RuntimeException e) {
            support.showError(e);
        }
    }

    /**
     * Imprime todas las devoluciones registradas.
     */
    private void showAllReturns() {
        printReturns(returnService.viewAllReturns(), "No hay devoluciones registradas.");
    }

    /**
     * Imprime las devoluciones de un cliente.
     */
    private void showReturnsByCustomer() {
        String customerId = support.ask("Identificacion del cliente: ");
        printReturns(returnService.viewReturnsByCustomer(customerId), "Este cliente no tiene devoluciones registradas.");
    }

    /**
     * Imprime las devoluciones de una venta.
     */
    private void showReturnsBySale() {
        String saleId = support.ask("Identificacion de la venta: ");
        printReturns(returnService.viewReturnsBySale(saleId), "Esta venta no tiene devoluciones registradas.");
    }

    /**
     * Imprime una lista de devoluciones, o un mensaje cuando la lista esta vacia.
     *
     * @param returns      las devoluciones a imprimir
     * @param emptyMessage el mensaje que se muestra cuando no hay nada
     */
    private void printReturns(List<Return> returns, String emptyMessage) {
        if (returns.isEmpty()) {
            System.out.println(emptyMessage);
            return;
        }
        for (Return oneReturn : returns) {
            System.out.println();
            System.out.println(oneReturn.generateReturnReceipt());
        }
    }

    /**
     * Pide un mes y un año y muestra el total de ventas, el total de devoluciones y el
     * balance neto de ese periodo, que es lo que le interesa al dueño del negocio.
     */
    private void showMonthlyBalance() {
        try {
            int month = support.askInt("Mes a consultar (1-12): ");
            int year = support.askInt("Anio a consultar: ");
            if (month < 1 || month > 12) {
                System.out.println("El mes debe estar entre 1 y 12.");
                return;
            }
            double totalSales = returnService.calculateMonthlySales(month, year);
            double totalReturns = returnService.calculateMonthlyReturns(month, year);
            double balance = returnService.generateMonthlyBalance(month, year);
            System.out.printf("%nBalance de %d/%d%n", month, year);
            System.out.printf("  Total ventas:       $%.2f%n", totalSales);
            System.out.printf("  Total devoluciones: $%.2f%n", totalReturns);
            System.out.printf("  Balance neto:       $%.2f%n", balance);
        } catch (RuntimeException e) {
            support.showError(e);
        }
    }
}
