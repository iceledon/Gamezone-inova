package com.gamezone.service;

import com.gamezone.model.Console;
import com.gamezone.model.Product;
import com.gamezone.model.VideoGame;
import com.gamezone.persistence.ProductRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * Business rules for managing products.
 */
public class ProductService {

    private final ProductRepository repository;
    private final List<Product> products;

    /**
     * @param repository repository used to persist products
     */
    public ProductService(ProductRepository repository) {
        this.repository = repository;
        this.products = repository.load();
    }

    /**
     * Registers a new video game and persists the updated inventory.
     *
     * @param id        unique identifier of the product
     * @param title     name of the product
     * @param price     unit price of the product
     * @param quantity  units currently available in inventory
     * @param platform  platform the game was developed for
     * @param genre     genre of the game
     * @param ageRating recommended age rating of the game
     */
    public void registerVideoGame(String id, String title, double price, int quantity,
                                   String platform, String genre, String ageRating) {
        products.add(new VideoGame(id, title, price, quantity, platform, genre, ageRating));
        repository.save(products);
    }

    /**
     * Registers a new console and persists the updated inventory.
     *
     * @param id         unique identifier of the product
     * @param title      name of the product
     * @param price      unit price of the product
     * @param quantity   units currently available in inventory
     * @param brand      manufacturer of the console
     * @param model      model name of the console
     * @param generation generation number of the console
     */
    public void registerConsole(String id, String title, double price, int quantity,
                                 String brand, String model, int generation) {
        products.add(new Console(id, title, price, quantity, brand, model, generation));
        repository.save(products);
    }

    /**
     * @return every product currently registered
     */
    public List<Product> listAll() {
        return new ArrayList<>(products);
    }
}
