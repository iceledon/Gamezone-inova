package com.gamezone;

import com.gamezone.persistence.PersonRepository;
import com.gamezone.persistence.ProductRepository;
import com.gamezone.persistence.ReturnRepository;
import com.gamezone.persistence.SaleRepository;
import com.gamezone.service.PersonService;
import com.gamezone.service.ProductService;
import com.gamezone.service.ReturnService;
import com.gamezone.service.SaleService;
import com.gamezone.ui.ConsoleUI;

/**
 * Application entry point for GameZone Inova.
 * <p>
 * Builds the object graph from the inside out — repositories first, then the services
 * that use them, then the console interface — and launches the menu. Each service loads
 * its own data when it is created, so the application starts with the state left by the
 * previous run.
 */
public class Main {

    /**
     * Wires the four layers together and starts the console interface.
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
        try {
            ProductRepository productRepository = new ProductRepository();
            PersonRepository personRepository = new PersonRepository();
            SaleRepository saleRepository = new SaleRepository();

            ProductService productService = new ProductService(productRepository);
            PersonService personService = new PersonService(personRepository);
            SaleService saleService = new SaleService(saleRepository, productService, personService);

            ReturnRepository returnRepository = new ReturnRepository(saleService, productService);
            ReturnService returnService = new ReturnService(returnRepository, saleService, productService);

            ConsoleUI consoleUI = new ConsoleUI(productService, personService, saleService, returnService);
            consoleUI.start();
        } catch (RuntimeException e) {
            System.err.println("Error fatal: " + e.getMessage());
            System.exit(1);
        }
    }
}
