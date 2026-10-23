package com.gamezone.ui;

import java.util.List;
import com.gamezone.model.Accessory;
import com.gamezone.service.AccessoryService;

/**
 * Console submenu for the accessory module, extracted from ConsoleUI (V-08).
 */
public class AccessoryMenu {

    private final AccessoryService accessoryService;
    private final ConsoleSupport support;

    /**
     * Creates the accessory submenu.
     *
     * @param accessoryService service that manages the accessories
     * @param support          shared console input and error helper
     */
    public AccessoryMenu(AccessoryService accessoryService, ConsoleSupport support) {
        this.accessoryService = accessoryService;
        this.support = support;
    }

    /**
     * Shows the accessory submenu until the user goes back.
     */
    public void show() {
        boolean back = false;
        while (!back) {
            System.out.println();
            System.out.println("--- Gestion de accesorios ---");
            System.out.println("1. Registrar control");
            System.out.println("2. Registrar cable");
            System.out.println("3. Registrar memoria");
            System.out.println("4. Listar todos los accesorios");
            System.out.println("5. Listar accesorios por tipo");
            System.out.println("6. Consultar accesorios compatibles con una consola");
            System.out.println("0. Volver");
            System.out.print("Seleccione una opcion: ");
            try {
                switch (support.ask("")) {
                    case "1" -> registerController();
                    case "2" -> registerCable();
                    case "3" -> registerMemory();
                    case "4" -> listAllAccessories();
                    case "5" -> listAccessoriesByType();
                    case "6" -> listCompatibleAccessories();
                    case "0" -> back = true;
                    default -> System.out.println("Opcion invalida.");
                }
            } catch (RuntimeException e) {
                support.showError(e);
            }
        }
    }

    /**
     * Asks for the data of a controller and registers it.
     */
    private void registerController() {
        try {
            String id = support.ask("Codigo del accesorio: ");
            String title = support.ask("Titulo: ");
            double price = support.askDouble("Precio: ");
            int quantity = support.askInt("Cantidad en stock: ");
            String connectionType = support.ask("Tipo de conexion (inalambrico/alambrico): ");
            accessoryService.registerController(id, title, price, quantity, connectionType);
            System.out.println("Control registrado correctamente.");
        } catch (RuntimeException e) {
            support.showError(e);
        }
    }

    /**
     * Asks for the data of a cable and registers it.
     */
    private void registerCable() {
        try {
            String id = support.ask("Codigo del accesorio: ");
            String title = support.ask("Titulo: ");
            double price = support.askDouble("Precio: ");
            int quantity = support.askInt("Cantidad en stock: ");
            double lengthMeters = support.askDouble("Longitud (metros): ");
            String connectorType = support.ask("Tipo de conector (HDMI/USB/optico/etc): ");
            accessoryService.registerCable(id, title, price, quantity, lengthMeters, connectorType);
            System.out.println("Cable registrado correctamente.");
        } catch (RuntimeException e) {
            support.showError(e);
        }
    }

    /**
     * Asks for the data of a memory card and registers it.
     */
    private void registerMemory() {
        try {
            String id = support.ask("Codigo del accesorio: ");
            String title = support.ask("Titulo: ");
            double price = support.askDouble("Precio: ");
            int quantity = support.askInt("Cantidad en stock: ");
            int capacityGb = support.askInt("Capacidad (GB): ");
            String memoryType = support.ask("Tipo de memoria (SD/microSD/tarjeta interna): ");
            accessoryService.registerMemory(id, title, price, quantity, capacityGb, memoryType);
            System.out.println("Memoria registrada correctamente.");
        } catch (RuntimeException e) {
            support.showError(e);
        }
    }

    /**
     * Prints every accessory in the inventory, with its price and available stock.
     */
    private void listAllAccessories() {
        printAccessories(accessoryService.listAllAccessories(), "No hay accesorios registrados.");
    }

    /**
     * Asks for a type and prints the accessories that match it.
     */
    private void listAccessoriesByType() {
        String type = support.ask("Tipo (control/cable/memoria): ");
        String normalizedType = switch (type.toLowerCase()) {
            case "control" -> "CONTROLLER";
            case "cable" -> "CABLE";
            case "memoria" -> "MEMORY";
            default -> type;
        };
        printAccessories(accessoryService.listAccessoriesByType(normalizedType),
                "No hay accesorios de ese tipo.");
    }

    /**
     * Asks for a console id and prints the accessories compatible with it.
     */
    private void listCompatibleAccessories() {
        String consoleId = support.ask("Codigo de la consola: ");
        printAccessories(accessoryService.findAccessoriesCompatibleWith(consoleId),
                "No hay accesorios compatibles con esa consola.");
    }

    /**
     * Prints a list of accessories, or a message when the list is empty.
     *
     * @param accessories  the accessories to print
     * @param emptyMessage the message shown when there is nothing to print
     */
    private void printAccessories(List<Accessory> accessories, String emptyMessage) {
        if (accessories.isEmpty()) {
            System.out.println(emptyMessage);
            return;
        }
        for (Accessory accessory : accessories) {
            System.out.printf("  [%s] %s | Precio: $%.2f | Stock: %d%n",
                    accessory.getId(), accessory.getDescription(), accessory.getPrice(), accessory.getQuantity());
        }
    }
}
