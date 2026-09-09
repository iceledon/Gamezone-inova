package com.gamezone.model;

/**
 * Represents a video game, a specific type of product.
 */
public class VideoGame extends Product {

    private String platform;
    private String genre;
    private String ageRating;

    /**
     * Creates a new video game with its basic and specific information.
     *
     * @param id        unique identifier of the product
     * @param title     name of the product
     * @param price     unit price of the product
     * @param quantity  units currently available in inventory
     * @param platform  platform the game was developed for
     * @param genre     genre of the game
     * @param ageRating recommended age rating of the game
     */
    public VideoGame(String id, String title, double price, int quantity,
                      String platform, String genre, String ageRating) {
        super(id, title, price, quantity);
        this.platform = platform;
        this.genre = genre;
        this.ageRating = ageRating;
    }

    @Override
    public String getDescription() {
        return getTitle() + " - Platform: " + platform + ", Genre: " + genre + ", Age Rating: " + ageRating;
    }

    /**
     * @return the platform the game was developed for
     */
    public String getPlatform() {
        return platform;
    }

    /**
     * @return the genre of the game
     */
    public String getGenre() {
        return genre;
    }

    /**
     * @return the recommended age rating of the game
     */
    public String getAgeRating() {
        return ageRating;
    }
}
