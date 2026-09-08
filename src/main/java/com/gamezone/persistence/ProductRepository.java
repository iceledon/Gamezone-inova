package com.gamezone.persistence;

import com.gamezone.model.Console;
import com.gamezone.model.Product;
import com.gamezone.model.VideoGame;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Saves and loads products from a text file.
 */
public class ProductRepository {

    private static final String FILE_PATH = "data/products.txt";
    private static final String SEPARATOR = ";";

    /**
     * Saves the given products to the data file, replacing its current content.
     *
     * @param products products to persist
     */
    public void save(List<Product> products) {
        List<String> lines = new ArrayList<>();
        for (Product product : products) {
            lines.add(toLine(product));
        }
        try {
            Files.write(Path.of(FILE_PATH), lines);
        } catch (IOException e) {
            throw new UncheckedIOException("Error writing products file", e);
        }
    }

    /**
     * @return the products currently stored in the data file, or an empty list if it does not exist yet
     */
    public List<Product> load() {
        Path path = Path.of(FILE_PATH);
        List<Product> products = new ArrayList<>();
        if (!Files.exists(path)) {
            return products;
        }
        try {
            for (String line : Files.readAllLines(path)) {
                products.add(fromLine(line));
            }
        } catch (IOException e) {
            throw new UncheckedIOException("Error reading products file", e);
        }
        return products;
    }

    private String toLine(Product product) {
        if (product instanceof VideoGame videoGame) {
            return "VIDEOGAME" + SEPARATOR + videoGame.getId() + SEPARATOR + videoGame.getTitle() + SEPARATOR
                    + videoGame.getPrice() + SEPARATOR + videoGame.getQuantity() + SEPARATOR
                    + videoGame.getPlatform() + SEPARATOR + videoGame.getGenre() + SEPARATOR
                    + videoGame.getAgeRating();
        } else if (product instanceof Console console) {
            return "CONSOLE" + SEPARATOR + console.getId() + SEPARATOR + console.getTitle() + SEPARATOR
                    + console.getPrice() + SEPARATOR + console.getQuantity() + SEPARATOR
                    + console.getBrand() + SEPARATOR + console.getModel() + SEPARATOR
                    + console.getGeneration();
        }
        throw new IllegalArgumentException("Unknown product type: " + product.getClass());
    }

    private Product fromLine(String line) {
        String[] fields = line.split(SEPARATOR);
        String type = fields[0];
        String id = fields[1];
        String title = fields[2];
        double price = Double.parseDouble(fields[3]);
        int quantity = Integer.parseInt(fields[4]);

        if (type.equals("VIDEOGAME")) {
            return new VideoGame(id, title, price, quantity, fields[5], fields[6], fields[7]);
        } else if (type.equals("CONSOLE")) {
            return new Console(id, title, price, quantity, fields[5], fields[6], Integer.parseInt(fields[7]));
        }
        throw new IllegalArgumentException("Unknown product type: " + type);
    }
}
