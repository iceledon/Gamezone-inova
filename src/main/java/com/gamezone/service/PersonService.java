package com.gamezone.service;

import java.util.List;

import com.gamezone.exceptions.BusinessRuleException;
import com.gamezone.exceptions.InvalidDataException;
import com.gamezone.exceptions.PersistenceException;
import com.gamezone.exceptions.ResourceNotFoundException;
import com.gamezone.model.Customer;
import com.gamezone.model.Seller;
import com.gamezone.persistence.PersonRepository;
import com.gamezone.validation.PersonValidator;

/**
 * Manages customers and seller lookups.
 */
public class PersonService {

    private final PersonRepository repository;

    /**
     * Creates the service using the given repository.
     *
     * @param repository repository used to persist people
     */
    public PersonService(PersonRepository repository) {
        this.repository = repository;
    }

    /**
     * Validates and registers a customer.
     *
     * @param customer customer to register
     * @throws InvalidDataException if the customer or its data is invalid
     * @throws BusinessRuleException if the customer id already exists
     * @throws PersistenceException if the customers cannot be loaded or saved
     */
    public void registerCustomer(Customer customer) {
        if (customer == null) {
            throw new InvalidDataException("cliente", "es obligatorio");
        }

        PersonValidator.validatePersonData(
                customer.getId(),
                customer.getName(),
                customer.getEmail(),
                customer.getPhone());

        List<Customer> customers = repository.loadCustomers();
        PersonValidator.validateUniquePerson(customers, customer.getId());

        customers.add(customer);
        repository.saveCustomers(customers);
    }

    /**
     * Loads all registered customers.
     *
     * @return registered customers
     * @throws PersistenceException if the customers cannot be loaded
     */
    public List<Customer> listCustomers() {
        return repository.loadCustomers();
    }

    /**
     * Loads all registered sellers.
     *
     * @return registered sellers
     * @throws PersistenceException if the sellers cannot be loaded
     */
    public List<Seller> listSellers() {
        return repository.loadSellers();
    }

    /**
     * Searches for a customer without requiring its existence.
     *
     * @param id customer identifier
     * @return the matching customer, or null if it does not exist
     * @throws PersistenceException if the customers cannot be loaded
     */
    public Customer findCustomerById(String id) {
        return repository.loadCustomers().stream()
                .filter(customer -> customer.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    /**
     * Searches for a seller without requiring its existence.
     *
     * @param id seller identifier
     * @return the matching seller, or null if it does not exist
     * @throws PersistenceException if the sellers cannot be loaded
     */
    public Seller findSellerById(String id) {
        return repository.loadSellers().stream()
                .filter(seller -> seller.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    /**
     * Finds a customer that must exist.
     *
     * @param id customer identifier
     * @return the matching customer
     * @throws ResourceNotFoundException if the customer does not exist
     * @throws PersistenceException if the customers cannot be loaded
     */
    public Customer getCustomerById(String id) {
        Customer customer = findCustomerById(id);
        PersonValidator.validatePersonExists(customer, id);
        return customer;
    }

    /**
     * Finds a seller that must exist.
     *
     * @param id seller identifier
     * @return the matching seller
     * @throws ResourceNotFoundException if the seller does not exist
     * @throws PersistenceException if the sellers cannot be loaded
     */
    public Seller getSellerById(String id) {
        Seller seller = findSellerById(id);
        PersonValidator.validatePersonExists(seller, id);
        return seller;
    }
}