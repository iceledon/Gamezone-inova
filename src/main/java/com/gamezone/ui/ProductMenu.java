package com.gamezone.ui;

import java.util.List;
import com.gamezone.model.Product;
import com.gamezone.service.ProductService;

/** Console interaction for the ProductMenu module. */
public final class ProductMenu {
    private final ConsoleSupport support;
    private final ProductService productService;

    public ProductMenu(ProductService productService, ConsoleSupport support) {
        this.support = support;
        this.productService = productService;
    }

    /**
     * Shows the product submenu until the user goes back.
     */
    public void show() {
        boolean back = false;
        while (!back) {
            System.out.println();
            System.out.println("--- Gestion de productos ---");
            System.out.println("1. Registrar videojuego");
            System.out.println("2. Registrar consola");
            System.out.println("3. Listar productos");
            System.out.println("0. Volver");
            System.out.print("Seleccione una opcion: ");
            try {
                switch (support.ask("")) {
                    case "1" -> registerVideoGame();
                    case "2" -> registerConsole();
                    case "3" -> listProducts();
                    case "0" -> back = true;
                    default -> System.out.println("Opcion invalida.");
                }
            } catch (RuntimeException e) {
                support.showError(e);
            }
        }
    }

    /**
     * Asks for the data of a video game and registers it.
     */
    private void registerVideoGame() {
        try {
            String id = support.ask("Codigo del producto: ");
            String title = support.ask("Titulo: ");
            double price = support.askDouble("Precio: ");
            int quantity = support.askInt("Cantidad en stock: ");
            String platform = support.ask("Plataforma: ");
            String genre = support.ask("Genero: ");
            String ageRating = support.ask("Clasificacion por edad: ");
            productService.registerVideoGame(id, title, price, quantity, platform, genre, ageRating);
            System.out.println("Videojuego registrado correctamente.");
        } catch (RuntimeException e) {
            support.showError(e);
        }
    }

    /**
     * Asks for the data of a console and registers it.
     */
    private void registerConsole() {
        try {
            String id = support.ask("Codigo del producto: ");
            String title = support.ask("Titulo: ");
            double price = support.askDouble("Precio: ");
            int quantity = support.askInt("Cantidad en stock: ");
            String brand = support.ask("Marca: ");
            String model = support.ask("Modelo: ");
            int generation = support.askInt("Generacion: ");
            productService.registerConsole(id, title, price, quantity, brand, model, generation);
            System.out.println("Consola registrada correctamente.");
        } catch (RuntimeException e) {
            support.showError(e);
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
}
