package com.gamezone.ui;

import java.util.List;
import com.gamezone.model.Customer;
import com.gamezone.model.Seller;
import com.gamezone.service.PersonService;

/** Console interaction for the PersonMenu module. */
public final class PersonMenu {
    private final ConsoleSupport support;
    private final PersonService personService;

    public PersonMenu(PersonService personService, ConsoleSupport support) {
        this.support = support;
        this.personService = personService;
    }

    /**
     * Shows the people submenu until the user goes back.
     */
    public void show() {
        boolean back = false;
        while (!back) {
            System.out.println();
            System.out.println("--- Gestion de personas ---");
            System.out.println("1. Registrar cliente");
            System.out.println("2. Listar clientes");
            System.out.println("3. Listar vendedores");
            System.out.println("0. Volver");
            System.out.print("Seleccione una opcion: ");
            try {
                switch (support.ask("")) {
                    case "1" -> registerCustomer();
                    case "2" -> listCustomers();
                    case "3" -> listSellers();
                    case "0" -> back = true;
                    default -> System.out.println("Opcion invalida.");
                }
            } catch (RuntimeException e) {
                support.showError(e);
            }
        }
    }

    /**
     * Asks for the data of a customer and registers it.
     */
    private void registerCustomer() {
        try {
            String id = support.ask("Identificacion: ");
            String name = support.ask("Nombre completo: ");
            String phone = support.ask("Telefono: ");
            String email = support.ask("Correo electronico: ");
            personService.registerCustomer(new Customer(id, name, phone, email));
            System.out.println("Cliente registrado correctamente.");
        } catch (RuntimeException e) {
            support.showError(e);
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
}
