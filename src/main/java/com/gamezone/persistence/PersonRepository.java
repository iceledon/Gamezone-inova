package com.gamezone.persistence;

import com.gamezone.model.Customer;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Repository to handle persistence for Person entities.
 */
public class PersonRepository {
    private static final String CUSTOMERS_FILE = "data/customers.txt";
    private static final String DELIMITER = ";";

    /**
     * Saves a list of customers to the customers file.
     *
     * @param customers the list of customers to save
     */
    public void saveCustomers(List<Customer> customers) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(CUSTOMERS_FILE))) {
            for (Customer customer : customers) {
                writer.println(customer.getId() + DELIMITER +
                               customer.getName() + DELIMITER +
                               customer.getPhone() + DELIMITER +
                               customer.getEmail());
            }
        } catch (IOException e) {
            System.err.println("Error saving customers: " + e.getMessage());
        }
    }

    /**
     * Loads the list of customers from the customers file.
     *
     * @return a list of customers
     */
    public List<Customer> loadCustomers() {
        List<Customer> customers = new ArrayList<>();
        File file = new File(CUSTOMERS_FILE);
        if (!file.exists()) {
            return customers;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(DELIMITER);
                if (parts.length == 4) {
                    customers.add(new Customer(parts[0], parts[1], parts[2], parts[3]));
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading customers: " + e.getMessage());
        }
        return customers;
    }
}
