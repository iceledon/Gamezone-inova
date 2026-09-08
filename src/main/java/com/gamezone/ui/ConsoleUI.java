package com.gamezone.ui;

import com.gamezone.model.Customer;
import com.gamezone.model.Product;
import com.gamezone.model.Sale;
import com.gamezone.model.Seller;
import com.gamezone.service.PersonService;
import com.gamezone.service.ProductService;
import com.gamezone.service.SaleService;

import java.util.ArrayList;
import java.util.List;
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
    private final Scanner scanner;

    /**
     * Creates the console interface with the services it delegates to.
     *
     * @param productService the service that handles products
     * @param personService  the service that handles customers and sellers
     * @param saleService    the service that handles sales
     */
    public ConsoleUI(ProductService productService, PersonService personService, SaleService saleService) {
        this.productService = productService;
        this.personService = personService;
        this.saleService = saleService;
        this.scanner = new Scanner(System.in);
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
            System.out.println("4. Salir");
            System.out.print("Seleccione una opcion: ");
            switch (scanner.nextLine().trim()) {
                case "1" -> showProductMenu();
                case "2" -> showPersonMenu();
                case "3" -> showSaleMenu();
                case "4" -> {
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
     * Asks for the data of a sale and registers it.
     */
    private void registerSale() {
        try {
            String customerId = ask("Identificacion del cliente: ");
            String sellerId = ask("Identificacion del vendedor: ");
            int units = askInt("Cantidad de productos a vender: ");
            List<String> productIds = new ArrayList<>();
            for (int i = 1; i <= units; i++) {
                productIds.add(ask("Codigo del producto " + i + ": "));
            }
            Sale sale = saleService.registerSale(customerId, sellerId, productIds);
            System.out.println("Venta registrada correctamente.");
            printSale(sale);
        } catch (RuntimeException e) {
            System.out.println("No se pudo registrar la venta: " + e.getMessage());
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
     * Prints the detail of a single sale, including its products and total.
     *
     * @param sale the sale to print
     */
    private void printSale(Sale sale) {
        System.out.printf("%nVenta %s | Fecha: %s%n", sale.getId(), sale.getDate());
        System.out.printf("  Cliente:  [%s] %s%n", sale.getCustomer().getId(), sale.getCustomer().getName());
        System.out.printf("  Vendedor: [%s] %s%n", sale.getSeller().getId(), sale.getSeller().getName());
        System.out.println("  Productos:");
        for (Product product : sale.getProducts()) {
            System.out.printf("    - [%s] %s ($%.2f)%n", product.getId(), product.getTitle(), product.getPrice());
        }
        System.out.printf("  Total: $%.2f%n", sale.calculateTotal());
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
            throw new IllegalArgumentException("Se esperaba un numero entero y se recibio '" + value + "'.");
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
            throw new IllegalArgumentException("Se esperaba un numero y se recibio '" + value + "'.");
        }
    }
}
