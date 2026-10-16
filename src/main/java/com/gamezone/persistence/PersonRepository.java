package com.gamezone.persistence;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.gamezone.exceptions.PersistenceException;
import com.gamezone.model.Customer;
import com.gamezone.model.Seller;

/**
 * Handles persistence for customers and sellers.
 */
public class PersonRepository {

    private static final String CUSTOMERS_FILE = "data/customers.txt";
    private static final String SELLERS_FILE = "data/sellers.txt";
    private static final String DELIMITER = ";";

    /**
     * Saves customers, replacing the current file content.
     *
     * @param customers the customers to save
     * @throws PersistenceException if the customers file cannot be written
     */
    public void saveCustomers(List<Customer> customers) {
        try (BufferedWriter writer =
                new BufferedWriter(new FileWriter(CUSTOMERS_FILE))) {

            for (Customer customer : customers) {
                writer.write(customer.getId() + DELIMITER
                        + customer.getName() + DELIMITER
                        + customer.getPhone() + DELIMITER
                        + customer.getEmail());
                writer.newLine();
            }
        } catch (IOException e) {
            throw new PersistenceException(
                    "Error de escritura en el archivo '"
                            + CUSTOMERS_FILE + "'.", e);
        }
    }

    /**
     * Loads customers from the data file.
     *
     * @return stored customers, or an empty list if the file does not exist
     * @throws PersistenceException if the file cannot be read or contains corrupt data
     */
    public List<Customer> loadCustomers() {
        List<Customer> customers = new ArrayList<>();
        File file = new File(CUSTOMERS_FILE);

        if (!file.exists()) {
            return customers;
        }

        try (BufferedReader reader =
                new BufferedReader(new FileReader(file))) {

            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(DELIMITER);

                if (parts.length != 4) {
                    throw new PersistenceException(
                            "El archivo '" + CUSTOMERS_FILE
                                    + "' contiene una línea incompleta o inválida"
                                    + " (operación: lectura).",
                            new IllegalArgumentException(
                                    "Expected 4 customer fields, found "
                                            + parts.length));
                }

                customers.add(
                        new Customer(parts[0], parts[1], parts[2], parts[3]));
            }
        } catch (IOException e) {
            throw new PersistenceException(
                    "Error de lectura en el archivo '"
                            + CUSTOMERS_FILE + "'.", e);
        }

        return customers;
    }

    /**
     * Saves sellers, replacing the current file content.
     *
     * @param sellers the sellers to save
     * @throws PersistenceException if the sellers file cannot be written
     */
    public void saveSellers(List<Seller> sellers) {
        try (BufferedWriter writer =
                new BufferedWriter(new FileWriter(SELLERS_FILE))) {

            for (Seller seller : sellers) {
                writer.write(seller.getId() + DELIMITER
                        + seller.getName() + DELIMITER
                        + seller.getPhone() + DELIMITER
                        + seller.getEmployeeCode() + DELIMITER
                        + seller.getShift());
                writer.newLine();
            }
        } catch (IOException e) {
            throw new PersistenceException(
                    "Error de escritura en el archivo '"
                            + SELLERS_FILE + "'.", e);
        }
    }

    /**
     * Loads sellers from the data file.
     *
     * @return stored sellers, or an empty list if the file does not exist
     * @throws PersistenceException if the file cannot be read or contains corrupt data
     */
    public List<Seller> loadSellers() {
        List<Seller> sellers = new ArrayList<>();
        File file = new File(SELLERS_FILE);

        if (!file.exists()) {
            return sellers;
        }

        try (BufferedReader reader =
                new BufferedReader(new FileReader(file))) {

            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(DELIMITER);

                if (parts.length != 5) {
                    throw new PersistenceException(
                            "El archivo '" + SELLERS_FILE
                                    + "' contiene una línea incompleta o inválida"
                                    + " (operación: lectura).",
                            new IllegalArgumentException(
                                    "Expected 5 seller fields, found "
                                            + parts.length));
                }

                sellers.add(
                        new Seller(
                                parts[0], parts[1], parts[2],
                                parts[3], parts[4]));
            }
        } catch (IOException e) {
            throw new PersistenceException(
                    "Error de lectura en el archivo '"
                            + SELLERS_FILE + "'.", e);
        }

        return sellers;
    }
}