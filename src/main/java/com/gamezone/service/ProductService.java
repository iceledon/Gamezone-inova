package com.gamezone.service;

import java.util.ArrayList;
import java.util.List;

import com.gamezone.exceptions.BusinessRuleException;
import com.gamezone.exceptions.InvalidDataException;
import com.gamezone.exceptions.PersistenceException;
import com.gamezone.exceptions.ResourceNotFoundException;
import com.gamezone.model.Console;
import com.gamezone.model.Product;
import com.gamezone.model.VideoGame;
import com.gamezone.persistence.ProductRepository;
import com.gamezone.validation.ProductValidator;

/**
 * Business rules for managing products.
 */
public class ProductService {

    private final ProductRepository repository;
    private final List<Product> products;

    /**
     * Loads the inventory using the given repository.
     *
     * @param repository repository used to persist products
     * @throws PersistenceException if the inventory cannot be loaded
     */
    public ProductService(ProductRepository repository) {
        this.repository = repository;
        this.products = repository.load();
    }

    /**
     * Registers a video game and persists the updated inventory.
     * The in-memory inventory changes only after saving succeeds.
     *
     * @param id unique identifier of the product
     * @param title name of the product
     * @param price unit price of the product
     * @param quantity units currently available in inventory
     * @param platform platform the game was developed for
     * @param genre genre of the game
     * @param ageRating recommended age rating of the game
     * @throws BusinessRuleException if the product id already exists
     * @throws InvalidDataException if the common product data is invalid
     * @throws PersistenceException if the inventory cannot be saved
     */
    public void registerVideoGame(
            String id, String title, double price, int quantity,
            String platform, String genre, String ageRating) {

        rejectIfIdExists(id);
        ProductValidator.validateProductData(id, title, price, quantity);

        Product product = new VideoGame(
                id,
                title,
                price,
                quantity,
                platform,
                genre,
                ageRating
        );

        List<Product> updatedProducts = new ArrayList<>(products);
        updatedProducts.add(product);

        repository.save(updatedProducts);

        products.add(product);
    }

    /**
     * Registers a console and persists the updated inventory.
     * The in-memory inventory changes only after saving succeeds.
     *
     * @param id unique identifier of the product
     * @param title name of the product
     * @param price unit price of the product
     * @param quantity units currently available in inventory
     * @param brand manufacturer of the console
     * @param model model name of the console
     * @param generation generation number of the console
     * @throws BusinessRuleException if the product id already exists
     * @throws InvalidDataException if the common product data is invalid
     * @throws PersistenceException if the inventory cannot be saved
     */
    public void registerConsole(
            String id, String title, double price, int quantity,
            String brand, String model, int generation) {

        rejectIfIdExists(id);
        ProductValidator.validateProductData(id, title, price, quantity);

        Product product = new Console(
                id,
                title,
                price,
                quantity,
                brand,
                model,
                generation
        );

        List<Product> updatedProducts = new ArrayList<>(products);
        updatedProducts.add(product);

        repository.save(updatedProducts);

        products.add(product);
    }

    private void rejectIfIdExists(String id) {
        if (find(id) != null) {
            throw new BusinessRuleException(
                    "Ya existe un producto con el identificador " + id + "."
            );
        }
    }

    private Product find(String id) {
        for (Product product : products) {
            if (product.getId().equals(id)) {
                return product;
            }
        }
        return null;
    }

    /**
     * Returns a copy of the inventory list.
     *
     * @return every product currently registered
     */
    public List<Product> listAll() {
        return new ArrayList<>(products);
    }

    /**
     * Finds a product that must exist.
     *
     * @param id unique identifier of the product to find
     * @return the product with the given id
     * @throws ResourceNotFoundException if no product has that id
     */
    public Product findById(String id) {
        Product product = find(id);
        ProductValidator.validateProductExists(product, id);
        return product;
    }

    /**
     * Decreases the product stock and persists the change.
     *
     * @param productId unique identifier of the product
     * @param amount units to remove from inventory
     * @throws ResourceNotFoundException if the product does not exist
     * @throws BusinessRuleException if the stock is insufficient
     * @throws PersistenceException if the inventory cannot be saved
     */
    public void updateStock(String productId, int amount) {
        Product product = findById(productId);
        ProductValidator.validateStockAvailability(product, amount);

        product.decreaseStock(amount);
        repository.save(products);
    }

    /**
     * Restores returned units to the inventory and persists the change.
     *
     * @param productId identifier of the returned product
     * @param amount units to restore to the inventory
     * @throws ResourceNotFoundException if the product does not exist
     * @throws PersistenceException if the inventory cannot be saved
     */
    public void restoreStock(String productId, int amount) {
        Product product = findById(productId);
        product.increaseStock(amount);
        repository.save(products);
    }
}