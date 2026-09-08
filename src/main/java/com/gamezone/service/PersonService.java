package com.gamezone.service;

import com.gamezone.model.Customer;
import com.gamezone.model.Seller;
import com.gamezone.persistence.PersonRepository;
import java.util.List;

/**
 * Service to manage person operations.
 */
public class PersonService {
    private final PersonRepository repository;

    public PersonService(PersonRepository repository) {
        this.repository = repository;
    }

    public void registerCustomer(Customer customer) {
        List<Customer> customers = repository.loadCustomers();
        customers.add(customer);
        repository.saveCustomers(customers);
    }

    public List<Customer> listCustomers() {
        return repository.loadCustomers();
    }

    public List<Seller> listSellers() {
        return repository.loadSellers();
    }

    public Customer findCustomerById(String id) {
        return repository.loadCustomers().stream()
                .filter(c -> c.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public Seller findSellerById(String id) {
        return repository.loadSellers().stream()
                .filter(s -> s.getId().equals(id))
                .findFirst()
                .orElse(null);
    }
}
