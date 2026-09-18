package com.gamezone.ui;

import com.gamezone.model.Accessory;
import com.gamezone.model.BulkPurchaseDiscount;
import com.gamezone.model.CategoryDiscount;
import com.gamezone.model.Console;
import com.gamezone.model.Customer;
import com.gamezone.model.PercentageDiscount;
import com.gamezone.model.Product;
import com.gamezone.model.Promotion;
import com.gamezone.model.Return;
import com.gamezone.model.Sale;
import com.gamezone.model.Seller;
import com.gamezone.model.Warranty;
import com.gamezone.service.AccessoryService;
import com.gamezone.service.PersonService;
import com.gamezone.service.ProductService;
import com.gamezone.service.PromotionService;
import com.gamezone.service.ReturnService;
import com.gamezone.service.SaleService;
import com.gamezone.service.WarrantyService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * Console user interface of the application.
 * <p>
 * Shows the menus in Spanish and delegates every operation to the service layer. This
 * class never touches a repository and never applies a business rule on its own: it only
 * reads what the user types, calls a service and prints the result or the error message.
 */
public class ConsoleUI {

    private final ProductService productService;
    private final PersonService personService;
    private final SaleService saleService;
    private final ReturnService returnService;
    private final WarrantyService warrantyService;
    private final PromotionService promotionService;
    private final AccessoryService accessoryService;
    private final Scanner scanner;

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
        this.productService = productService;
        this.personService = personService;
        this.saleService = saleService;
        this.returnService = returnService;
        this.warrantyService = warrantyService;
        this.promotionService = promotionService;
        this.accessoryService = accessoryService;
        this.scanner = new Scanner(System.in);
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
            switch (scanner.nextLine().trim()) {
                case "1" -> showProductMenu();
                case "2" -> showPersonMenu();
                case "3" -> showSaleMenu();
                case "4" -> showReturnMenu();
                case "5" -> showWarrantyMenu();
                case "6" -> showPromotionMenu();
                case "7" -> showAccessoryMenu();
                case "8" -> {
                    running = false;
                    System.out.println("Gracias por usar GameZone Inova.");
                }
                default -> System.out.println("Opcion invalida.");
            }
        }
    }

    /**
     * Shows the product submenu until the user goes back.
     */
    private void showProductMenu() {
        boolean back = false;
        while (!back) {
            System.out.println();
            System.out.println("--- Gestion de productos ---");
            System.out.println("1. Registrar videojuego");
            System.out.println("2. Registrar consola");
            System.out.println("3. Listar productos");
            System.out.println("0. Volver");
            System.out.print("Seleccione una opcion: ");
            switch (scanner.nextLine().trim()) {
                case "1" -> registerVideoGame();
                case "2" -> registerConsole();
                case "3" -> listProducts();
                case "0" -> back = true;
                default -> System.out.println("Opcion invalida.");
            }
        }
    }

    /**
     * Asks for the data of a video game and registers it.
     */
    private void registerVideoGame() {
        try {
            String id = ask("Codigo del producto: ");
            String title = ask("Titulo: ");
            double price = askDouble("Precio: ");
            int quantity = askInt("Cantidad en stock: ");
            String platform = ask("Plataforma: ");
            String genre = ask("Genero: ");
            String ageRating = ask("Clasificacion por edad: ");
            productService.registerVideoGame(id, title, price, quantity, platform, genre, ageRating);
            System.out.println("Videojuego registrado correctamente.");
        } catch (RuntimeException e) {
            System.out.println("No se pudo registrar el videojuego: " + e.getMessage());
        }
    }

    /**
     * Asks for the data of a console and registers it.
     */
    private void registerConsole() {
        try {
            String id = ask("Codigo del producto: ");
            String title = ask("Titulo: ");
            double price = askDouble("Precio: ");
            int quantity = askInt("Cantidad en stock: ");
            String brand = ask("Marca: ");
            String model = ask("Modelo: ");
            int generation = askInt("Generacion: ");
            productService.registerConsole(id, title, price, quantity, brand, model, generation);
            System.out.println("Consola registrada correctamente.");
        } catch (RuntimeException e) {
            System.out.println("No se pudo registrar la consola: " + e.getMessage());
        }
    }

    /**
     * Prints every product in the inventory, with its price and available stock.
     */
    private void listProducts() {
        List<Product> products = productService.listAll();
        if (products.isEmpty()) {
            System.out.println("No hay productos registrados.");
            return;
        }
        System.out.println("Productos en inventario:");
        for (Product product : products) {
            System.out.printf("  [%s] %s | Precio: $%.2f | Stock: %d%n",
                    product.getId(), product.getDescription(), product.getPrice(), product.getQuantity());
        }
    }

    /**
     * Shows the people submenu until the user goes back.
     */
    private void showPersonMenu() {
        boolean back = false;
        while (!back) {
            System.out.println();
            System.out.println("--- Gestion de personas ---");
            System.out.println("1. Registrar cliente");
            System.out.println("2. Listar clientes");
            System.out.println("3. Listar vendedores");
            System.out.println("0. Volver");
            System.out.print("Seleccione una opcion: ");
            switch (scanner.nextLine().trim()) {
                case "1" -> registerCustomer();
                case "2" -> listCustomers();
                case "3" -> listSellers();
                case "0" -> back = true;
                default -> System.out.println("Opcion invalida.");
            }
        }
    }

    /**
     * Asks for the data of a customer and registers it.
     */
    private void registerCustomer() {
        try {
            String id = ask("Identificacion: ");
            String name = ask("Nombre completo: ");
            String phone = ask("Telefono: ");
            String email = ask("Correo electronico: ");
            personService.registerCustomer(new Customer(id, name, phone, email));
            System.out.println("Cliente registrado correctamente.");
        } catch (RuntimeException e) {
            System.out.println("No se pudo registrar el cliente: " + e.getMessage());
        }
    }

    /**
     * Prints every registered customer.
     */
    private void listCustomers() {
        List<Customer> customers = personService.listCustomers();
        if (customers.isEmpty()) {
            System.out.println("No hay clientes registrados.");
            return;
        }
        System.out.println("Clientes registrados:");
        for (Customer customer : customers) {
            System.out.printf("  [%s] %s | %s | Telefono: %s | Correo: %s%n",
                    customer.getId(), customer.getName(), customer.getRoleDescription(),
                    customer.getPhone(), customer.getEmail());
        }
    }

    /**
     * Prints every registered seller.
     */
    private void listSellers() {
        List<Seller> sellers = personService.listSellers();
        if (sellers.isEmpty()) {
            System.out.println("No hay vendedores registrados.");
            return;
        }
        System.out.println("Vendedores registrados:");
        for (Seller seller : sellers) {
            System.out.printf("  [%s] %s | %s | Codigo: %s | Turno: %s%n",
                    seller.getId(), seller.getName(), seller.getRoleDescription(),
                    seller.getEmployeeCode(), seller.getShift());
        }
    }

    /**
     * Shows the sales submenu until the user goes back.
     */
    private void showSaleMenu() {
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
            switch (scanner.nextLine().trim()) {
                case "1" -> registerSale();
                case "2" -> showAllSales();
                case "3" -> showSalesByCustomer();
                case "4" -> showSalesBySeller();
                case "0" -> back = true;
                default -> System.out.println("Opcion invalida.");
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
            String customerId = ask("Identificacion del cliente: ");
            String sellerId = ask("Identificacion del vendedor: ");
            int units = askInt("Cantidad de items a vender (productos o accesorios): ");
            List<String> productIds = new ArrayList<>();
            List<String> productIdsWithExtendedWarranty = new ArrayList<>();
            for (int i = 1; i <= units; i++) {
                String productId = ask("Codigo del producto o accesorio " + i + ": ");
                productIds.add(productId);
                Product product = findAnyItemById(productId);
                if (product instanceof Console) {
                    String answer = ask("Desea agregar garantia extendida a la consola "
                            + product.getTitle() + "? (S/N): ");
                    if (answer.equalsIgnoreCase("S")) {
                        productIdsWithExtendedWarranty.add(productId);
                    }
                }
            }
            Sale sale = saleService.registerSale(customerId, sellerId, productIds,
                    productIdsWithExtendedWarranty);
            System.out.println("Venta registrada correctamente.");
            printSale(sale);
        } catch (RuntimeException e) {
            System.out.println("No se pudo registrar la venta: " + e.getMessage());
        }
    }

    /**
     * Looks up a sold item by id in either catalog, only to decide whether to offer an
     * extended warranty before the sale is actually registered. Returns null if the id
     * does not match anything yet; {@link SaleService#registerSale} is the one that gives
     * the real "item not found" error once the sale is actually submitted.
     *
     * @param id the id typed by the user
     * @return the matching product or accessory, or null if none was found
     */
    private Product findAnyItemById(String id) {
        try {
            return productService.findById(id);
        } catch (NoSuchElementException e) {
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
        printSales(saleService.getAllSales(), "No hay ventas registradas.");
    }

    /**
     * Prints the sales history of one customer.
     */
    private void showSalesByCustomer() {
        String customerId = ask("Identificacion del cliente: ");
        printSales(saleService.getSalesByCustomer(customerId), "Este cliente no tiene compras registradas.");
    }

    /**
     * Prints the sales history of one seller.
     */
    private void showSalesBySeller() {
        String sellerId = ask("Identificacion del vendedor: ");
        printSales(saleService.getSalesBySeller(sellerId), "Este vendedor no tiene ventas registradas.");
    }

    /**
     * Prints a list of sales, or a message when the list is empty.
     *
     * @param sales        the sales to print
     * @param emptyMessage the message shown when there is nothing to print
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

    /**
     * Prints a prompt and reads a line of text.
     *
     * @param prompt the text shown to the user
     * @return what the user typed, without leading or trailing spaces
     */
    private String ask(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    /**
     * Prints a prompt and reads a whole number.
     *
     * @param prompt the text shown to the user
     * @return the number typed by the user
     * @throws IllegalArgumentException if what the user typed is not a whole number
     */
    private int askInt(String prompt) {
        String value = ask(prompt);
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Se esperaba un numero entero y se recibio " + value + ".");
        }
    }

    /**
     * Prints a prompt and reads a decimal number.
     *
     * @param prompt the text shown to the user
     * @return the number typed by the user
     * @throws IllegalArgumentException if what the user typed is not a number
     */
    private double askDouble(String prompt) {
        String value = ask(prompt);
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Se esperaba un numero y se recibio " + value + ".");
        }
    }

    /**
     * Prints a prompt and reads a date in ISO format (yyyy-MM-dd).
     *
     * @param prompt the text shown to the user
     * @return the date typed by the user
     * @throws IllegalArgumentException if what the user typed is not a valid date
     */
    private LocalDate askDate(String prompt) {
        String value = ask(prompt);
        try {
            return LocalDate.parse(value);
        } catch (java.time.format.DateTimeParseException e) {
            throw new IllegalArgumentException("Se esperaba una fecha en formato aaaa-mm-dd y se recibio " + value + ".");
        }
    }

    /**
     * Muestra el submenu de devoluciones hasta que el usuario decide volver.
     */
    private void showReturnMenu() {
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
            switch (scanner.nextLine().trim()) {
                case "1" -> registerReturn();
                case "2" -> showAllReturns();
                case "3" -> showReturnsByCustomer();
                case "4" -> showReturnsBySale();
                case "5" -> showMonthlyBalance();
                case "0" -> back = true;
                default -> System.out.println("Opcion invalida.");
            }
        }
    }

    /**
     * Pide los datos de una devolucion y la registra.
     */
    private void registerReturn() {
        try {
            String saleId = ask("Identificacion de la venta original: ");
            int units = askInt("Cantidad de productos a devolver: ");
            List<String> productIds = new ArrayList<>();
            for (int i = 1; i <= units; i++) {
                productIds.add(ask("Codigo del producto " + i + " a devolver: "));
            }
            String reason = ask("Motivo de la devolucion: ");
            Return newReturn = returnService.registerReturn(saleId, productIds, reason);
            System.out.println("Devolucion registrada correctamente.");
            System.out.println(newReturn.generateReturnReceipt());
        } catch (RuntimeException e) {
            System.out.println("No se pudo registrar la devolucion: " + e.getMessage());
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
        String customerId = ask("Identificacion del cliente: ");
        printReturns(returnService.viewReturnsByCustomer(customerId), "Este cliente no tiene devoluciones registradas.");
    }

    /**
     * Imprime las devoluciones de una venta.
     */
    private void showReturnsBySale() {
        String saleId = ask("Identificacion de la venta: ");
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
            int month = askInt("Mes a consultar (1-12): ");
            int year = askInt("Anio a consultar: ");
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
            System.out.println("No se pudo calcular el balance: " + e.getMessage());
        }
    }

    /**
     * Shows the warranty submenu until the user goes back.
     */
    private void showWarrantyMenu() {
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
            switch (scanner.nextLine().trim()) {
                case "1" -> consultWarranty();
                case "2" -> listAllWarranties();
                case "3" -> listActiveWarranties();
                case "4" -> listWarrantiesExpiringSoon();
                case "0" -> back = true;
                default -> System.out.println("Opcion invalida.");
            }
        }
    }

    /**
     * Prints the warranty certificate of one product inside one sale.
     */
    private void consultWarranty() {
        String productId = ask("Codigo del producto: ");
        String saleId = ask("Identificacion de la venta: ");
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
        int days = askInt("Dias de anticipacion: ");
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
            switch (scanner.nextLine().trim()) {
                case "1" -> registerPercentageDiscount();
                case "2" -> registerCategoryDiscount();
                case "3" -> registerBulkPurchaseDiscount();
                case "4" -> listAllPromotions();
                case "5" -> listActivePromotions();
                case "0" -> back = true;
                default -> System.out.println("Opcion invalida.");
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
            System.out.println("No se pudo registrar la promocion: " + e.getMessage());
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
            String targetCategory = ask("Categoria objetivo (VIDEOGAME/CONSOLE): ");
            promotionService.registerCategoryDiscount(id, name, startDate, endDate, percentage, targetCategory);
            System.out.println("Promocion registrada correctamente.");
        } catch (RuntimeException e) {
            System.out.println("No se pudo registrar la promocion: " + e.getMessage());
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
            System.out.println("No se pudo registrar la promocion: " + e.getMessage());
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
            System.out.println("No se pudo registrar el control: " + e.getMessage());
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
            System.out.println("No se pudo registrar el cable: " + e.getMessage());
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
            System.out.println("No se pudo registrar la memoria: " + e.getMessage());
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
}
