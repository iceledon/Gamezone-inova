package com.gamezone.ui;

import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

import com.gamezone.model.Accessory;
import com.gamezone.model.BulkPurchaseDiscount;
import com.gamezone.model.CategoryDiscount;
import com.gamezone.model.PercentageDiscount;
import com.gamezone.model.Promotion;
import com.gamezone.service.AccessoryService;
import com.gamezone.service.PersonService;
import com.gamezone.service.ProductService;
import com.gamezone.service.PromotionService;
import com.gamezone.service.ReturnService;
import com.gamezone.service.SaleService;
import com.gamezone.service.WarrantyService;

/**
 * Console user interface of the application.
 * <p>
 * Shows the menus in Spanish and delegates every operation to the service layer. This
 * class never touches a repository and never applies a business rule on its own: it only
 * reads what the user types, calls a service and prints the result or the error message.
 */
public class ConsoleUI {

    private final PromotionService promotionService;
    private final AccessoryService accessoryService;
    private final Scanner scanner;
    private final ConsoleSupport support;
    private final ProductMenu productMenu;
    private final PersonMenu personMenu;
    private final SaleMenu saleMenu;
    private final ReturnMenu returnMenu;
    private final WarrantyMenu warrantyMenu;

    /**
     * Crea la interfaz de consola con los servicios a los que delega.
     *
     * @param productService   servicio que maneja los productos
     * @param personService    servicio que maneja clientes y vendedores
     * @param saleService      servicio que maneja las ventas
     * @param returnService    servicio que maneja las devoluciones
     * @param warrantyService  servicio que maneja las garantias
     * @param promotionService servicio que maneja las promociones
     * @param accessoryService servicio que maneja los accesorios
     */
    public ConsoleUI(ProductService productService, PersonService personService, SaleService saleService,
                     ReturnService returnService, WarrantyService warrantyService, PromotionService promotionService,
                     AccessoryService accessoryService) {
        this.promotionService = promotionService;
        this.accessoryService = accessoryService;
        this.scanner = new Scanner(System.in);
        this.support = new ConsoleSupport(scanner);
        this.productMenu = new ProductMenu(productService, support);
        this.personMenu = new PersonMenu(personService, support);
        this.saleMenu = new SaleMenu(saleService, productService, accessoryService, support);
        this.returnMenu = new ReturnMenu(returnService, support);
        this.warrantyMenu = new WarrantyMenu(warrantyService, support);
    }

    /**
     * Convenience constructor used by callers that only wired the warranty module.
     */
    public ConsoleUI(ProductService productService, PersonService personService,
                     SaleService saleService, WarrantyService warrantyService) {
        this(productService, personService, saleService, null, warrantyService, null, null);
    }

    /**
     * Convenience constructor used by callers that only wired the returns module.
     */
    public ConsoleUI(ProductService productService, PersonService personService,
                     SaleService saleService, ReturnService returnService) {
        this(productService, personService, saleService, returnService, null, null, null);
    }

    /**
     * Runs the main menu loop until the user chooses to exit.
     */
    public void start() {
        boolean running = true;
        while (running) {
            System.out.println();
            System.out.println("=== GameZone Inova ===");
            System.out.println("1. Gestion de productos");
            System.out.println("2. Gestion de personas");
            System.out.println("3. Gestion de ventas");
            System.out.println("4. Gestion de devoluciones");
            System.out.println("5. Gestion de garantias");
            System.out.println("6. Gestion de promociones");
            System.out.println("7. Gestion de accesorios");
            System.out.println("8. Salir");
            System.out.print("Seleccione una opcion: ");
            try {
                switch (scanner.nextLine().trim()) {
                    case "1" -> productMenu.show();
                    case "2" -> personMenu.show();
                    case "3" -> saleMenu.show();
                    case "4" -> returnMenu.show();
                    case "5" -> warrantyMenu.show();
                    case "6" -> showPromotionMenu();
                    case "7" -> showAccessoryMenu();
                    case "8" -> {
                        running = false;
                        System.out.println("Gracias por usar GameZone Inova.");
                    }
                    default -> System.out.println("Opcion invalida.");
                }
            } catch (RuntimeException e) {
                showError(e);
            }
        }
    }

    /**
     * Prints a prompt and reads a line of text.
     *
     * @param prompt the text shown to the user
     * @return what the user typed, without leading or trailing spaces
     */
    private String ask(String prompt) {
        return support.ask(prompt);
    }

    /**
     * Reads a whole number from the console.
     *
     * @param prompt the text shown to the user
     * @return the entered integer
     * @throws InvalidDataException if the input is not a valid integer
     */
    private int askInt(String prompt) {
        return support.askInt(prompt);
    }

    /**
     * Reads a decimal number from the console.
     *
     * @param prompt the text shown to the user
     * @return the entered decimal number
     * @throws InvalidDataException if the input is not a finite number
     */
    private double askDouble(String prompt) {
        return support.askDouble(prompt);
    }

    /**
     * Reads a date in ISO format.
     *
     * @param prompt the text shown to the user
     * @return the entered date
     * @throws InvalidDataException if the input is not a valid ISO date
     */
    private LocalDate askDate(String prompt) {
        return support.askDate(prompt);
    }

    /**
     * Shows the promotion submenu until the user goes back.
     */
    private void showPromotionMenu() {
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
                switch (scanner.nextLine().trim()) {
                    case "1" -> registerPercentageDiscount();
                    case "2" -> registerCategoryDiscount();
                    case "3" -> registerBulkPurchaseDiscount();
                    case "4" -> listAllPromotions();
                    case "5" -> listActivePromotions();
                    case "0" -> back = true;
                    default -> System.out.println("Opcion invalida.");
                }
            } catch (RuntimeException e) {
                showError(e);
            }
        }
    }

    /**
     * Asks for the data of a percentage discount and registers it.
     */
    private void registerPercentageDiscount() {
        try {
            String id = ask("Codigo de la promocion: ");
            String name = ask("Nombre de la promocion: ");
            LocalDate startDate = askDate("Fecha de inicio (aaaa-mm-dd): ");
            LocalDate endDate = askDate("Fecha de fin (aaaa-mm-dd): ");
            double percentage = askDouble("Porcentaje de descuento: ");
            promotionService.registerPercentageDiscount(id, name, startDate, endDate, percentage);
            System.out.println("Promocion registrada correctamente.");
        } catch (RuntimeException e) {
            showError(e);
        }
    }

    /**
     * Asks for the data of a category discount and registers it.
     */
    private void registerCategoryDiscount() {
        try {
            String id = ask("Codigo de la promocion: ");
            String name = ask("Nombre de la promocion: ");
            LocalDate startDate = askDate("Fecha de inicio (aaaa-mm-dd): ");
            LocalDate endDate = askDate("Fecha de fin (aaaa-mm-dd): ");
            double percentage = askDouble("Porcentaje de descuento: ");
            String targetCategory = ask("Categoria objetivo (VIDEOGAME/CONSOLE/ACCESSORY): ");
            promotionService.registerCategoryDiscount(id, name, startDate, endDate, percentage, targetCategory);
            System.out.println("Promocion registrada correctamente.");
        } catch (RuntimeException e) {
            showError(e);
        }
    }

    /**
     * Asks for the data of a bulk purchase discount and registers it.
     */
    private void registerBulkPurchaseDiscount() {
        try {
            String id = ask("Codigo de la promocion: ");
            String name = ask("Nombre de la promocion: ");
            LocalDate startDate = askDate("Fecha de inicio (aaaa-mm-dd): ");
            LocalDate endDate = askDate("Fecha de fin (aaaa-mm-dd): ");
            int minimumQuantity = askInt("Cantidad minima de productos: ");
            double percentage = askDouble("Porcentaje de descuento: ");
            promotionService.registerBulkPurchaseDiscount(id, name, startDate, endDate, minimumQuantity, percentage);
            System.out.println("Promocion registrada correctamente.");
        } catch (RuntimeException e) {
            showError(e);
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

    /**
     * Shows the accessory submenu until the user goes back.
     */
    private void showAccessoryMenu() {
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
                switch (scanner.nextLine().trim()) {
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
                showError(e);
            }
        }
    }

    /**
     * Asks for the data of a controller and registers it.
     */
    private void registerController() {
        try {
            String id = ask("Codigo del accesorio: ");
            String title = ask("Titulo: ");
            double price = askDouble("Precio: ");
            int quantity = askInt("Cantidad en stock: ");
            String connectionType = ask("Tipo de conexion (inalambrico/alambrico): ");
            accessoryService.registerController(id, title, price, quantity, connectionType);
            System.out.println("Control registrado correctamente.");
        } catch (RuntimeException e) {
            showError(e);
        }
    }

    /**
     * Asks for the data of a cable and registers it.
     */
    private void registerCable() {
        try {
            String id = ask("Codigo del accesorio: ");
            String title = ask("Titulo: ");
            double price = askDouble("Precio: ");
            int quantity = askInt("Cantidad en stock: ");
            double lengthMeters = askDouble("Longitud (metros): ");
            String connectorType = ask("Tipo de conector (HDMI/USB/optico/etc): ");
            accessoryService.registerCable(id, title, price, quantity, lengthMeters, connectorType);
            System.out.println("Cable registrado correctamente.");
        } catch (RuntimeException e) {
            showError(e);
        }
    }

    /**
     * Asks for the data of a memory card and registers it.
     */
    private void registerMemory() {
        try {
            String id = ask("Codigo del accesorio: ");
            String title = ask("Titulo: ");
            double price = askDouble("Precio: ");
            int quantity = askInt("Cantidad en stock: ");
            int capacityGb = askInt("Capacidad (GB): ");
            String memoryType = ask("Tipo de memoria (SD/microSD/tarjeta interna): ");
            accessoryService.registerMemory(id, title, price, quantity, capacityGb, memoryType);
            System.out.println("Memoria registrada correctamente.");
        } catch (RuntimeException e) {
            showError(e);
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
        String type = ask("Tipo (control/cable/memoria): ");
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
        String consoleId = ask("Codigo de la consola: ");
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
        /**
     * Displays a Spanish message according to the failure category.
     *
     * @param error the failure raised by an operation
     */
    private void showError(RuntimeException error) {
        support.showError(error);
    }
}
