package com.gamezone.ui;

import com.gamezone.service.PersonService;
import com.gamezone.service.ProductService;
import com.gamezone.service.SaleService;

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
                case "4" -> {
                    running = false;
                    System.out.println("Gracias por usar GameZone Inova.");
                }
                default -> System.out.println("Opcion invalida.");
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
