package com.gamezone.service;

import com.gamezone.model.Accessory;
import com.gamezone.model.Cable;
import com.gamezone.model.Controller;
import com.gamezone.model.Memory;
import com.gamezone.persistence.AccessoryRepository;

import java.util.List;

/**
 * Business rules for managing accessories.
 */
public class AccessoryService {

    private final AccessoryRepository repository;
    private final List<Accessory> accessories;

    /**
     * @param repository repository used to persist accessories
     */
    public AccessoryService(AccessoryRepository repository) {
        this.repository = repository;
        this.accessories = repository.loadAll();
    }

    /**
     * Registers a new controller and persists the updated inventory.
     *
     * @param id             unique identifier of the product
     * @param title          name of the product
     * @param price          unit price of the product
     * @param quantity       units currently available in inventory
     * @param connectionType connection type of the controller
     */
    public void registerController(String id, String title, double price, int quantity, String connectionType) {
        rejectIfIdExists(id);
        accessories.add(new Controller(id, title, price, quantity, connectionType));
        repository.saveAll(accessories);
    }

    /**
     * Registers a new cable and persists the updated inventory.
     *
     * @param id            unique identifier of the product
     * @param title         name of the product
     * @param price         unit price of the product
     * @param quantity      units currently available in inventory
     * @param lengthMeters  length of the cable, in meters
     * @param connectorType type of connector
     */
    public void registerCable(String id, String title, double price, int quantity,
                               double lengthMeters, String connectorType) {
        rejectIfIdExists(id);
        accessories.add(new Cable(id, title, price, quantity, lengthMeters, connectorType));
        repository.saveAll(accessories);
    }

    /**
     * Registers a new memory card and persists the updated inventory.
     *
     * @param id         unique identifier of the product
     * @param title      name of the product
     * @param price      unit price of the product
     * @param quantity   units currently available in inventory
     * @param capacityGb storage capacity, in gigabytes
     * @param memoryType type of memory
     */
    public void registerMemory(String id, String title, double price, int quantity,
                                int capacityGb, String memoryType) {
        rejectIfIdExists(id);
        accessories.add(new Memory(id, title, price, quantity, capacityGb, memoryType));
        repository.saveAll(accessories);
    }

    private void rejectIfIdExists(String id) {
        if (find(id) != null) {
            throw new IllegalArgumentException("Accessory id already exists: " + id);
        }
    }

    private Accessory find(String id) {
        for (Accessory accessory : accessories) {
            if (accessory.getId().equals(id)) {
                return accessory;
            }
        }
        return null;
    }
}
