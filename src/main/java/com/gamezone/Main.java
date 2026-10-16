package com.gamezone;

import com.gamezone.exceptions.PersistenceException;
import com.gamezone.persistence.AccessoryRepository;
import com.gamezone.persistence.PersonRepository;
import com.gamezone.persistence.ProductRepository;
import com.gamezone.persistence.PromotionRepository;
import com.gamezone.persistence.ReturnRepository;
import com.gamezone.persistence.SaleRepository;
import com.gamezone.persistence.WarrantyRepository;
import com.gamezone.service.AccessoryService;
import com.gamezone.service.PersonService;
import com.gamezone.service.ProductService;
import com.gamezone.service.PromotionService;
import com.gamezone.service.ReturnService;
import com.gamezone.service.SaleService;
import com.gamezone.service.WarrantyService;
import com.gamezone.ui.ConsoleUI;

/**
 * Application entry point for GameZone Inova.
 *
 * Builds repositories, services and the console interface.
 * Startup failures are reported without exposing technical details.
 */
public class Main {

    /**
     * Initializes the application and starts the console interface.
     *
     * @param args command-line arguments, not used
     */
    public static void main(String[] args) {
        try {
            ProductRepository productRepository = new ProductRepository();
            PersonRepository personRepository = new PersonRepository();
            SaleRepository saleRepository = new SaleRepository();
            WarrantyRepository warrantyRepository = new WarrantyRepository();
            PromotionRepository promotionRepository = new PromotionRepository();
            AccessoryRepository accessoryRepository = new AccessoryRepository();

            ProductService productService =
                    new ProductService(productRepository);

            PersonService personService =
                    new PersonService(personRepository);

            PromotionService promotionService =
                    new PromotionService(promotionRepository);

            AccessoryService accessoryService =
                    new AccessoryService(accessoryRepository);

            WarrantyService warrantyService = new WarrantyService(
                    warrantyRepository,
                    saleRepository,
                    productService,
                    personService,
                    accessoryService
            );

            SaleService saleService = new SaleService(
                    saleRepository,
                    productService,
                    personService,
                    promotionService,
                    accessoryService
            );

            saleService.setWarrantyService(warrantyService);

            ReturnRepository returnRepository = new ReturnRepository(
                    saleService,
                    productService,
                    accessoryService
            );

            ReturnService returnService = new ReturnService(
                    returnRepository,
                    saleService,
                    productService,
                    accessoryService,
                    warrantyService
            );

            ConsoleUI consoleUI = new ConsoleUI(
                    productService,
                    personService,
                    saleService,
                    returnService,
                    warrantyService,
                    promotionService,
                    accessoryService
            );

            consoleUI.start();

        } catch (PersistenceException e) {
            System.err.println(
                    "Ocurrio un problema al acceder a los datos del sistema. "
                    + "Contacte al administrador."
            );
            System.exit(1);

        } catch (RuntimeException e) {
            System.err.println(
                    "No se pudo iniciar o continuar el sistema. "
                    + "Contacte al administrador."
            );
            System.exit(1);
        }
    }
}