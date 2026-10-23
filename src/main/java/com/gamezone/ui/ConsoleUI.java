package com.gamezone.ui;

import java.util.Scanner;

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

    private final Scanner scanner;
    private final ConsoleSupport support;
    private final ProductMenu productMenu;
    private final PersonMenu personMenu;
    private final SaleMenu saleMenu;
    private final ReturnMenu returnMenu;
    private final WarrantyMenu warrantyMenu;
    private final PromotionMenu promotionMenu;
    private final AccessoryMenu accessoryMenu;

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
        this.scanner = new Scanner(System.in);
        this.support = new ConsoleSupport(scanner);
        this.productMenu = new ProductMenu(productService, support);
        this.personMenu = new PersonMenu(personService, support);
        this.saleMenu = new SaleMenu(saleService, productService, accessoryService, support);
        this.returnMenu = new ReturnMenu(returnService, support);
        this.warrantyMenu = new WarrantyMenu(warrantyService, support);
        this.promotionMenu = new PromotionMenu(promotionService, support);
        this.accessoryMenu = new AccessoryMenu(accessoryService, support);
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
                    case "6" -> promotionMenu.show();
                    case "7" -> accessoryMenu.show();
                    case "8" -> {
                        running = false;
                        System.out.println("Gracias por usar GameZone Inova.");
                    }
                    default -> System.out.println("Opcion invalida.");
                }
            } catch (RuntimeException e) {
                support.showError(e);
            }
        }
    }

}
