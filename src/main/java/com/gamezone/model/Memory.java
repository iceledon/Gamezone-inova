package com.gamezone.model;

/**
 * Represents a memory card, a specific type of accessory.
 */
public class Memory extends Accessory {

    private int capacityGb;
    private String memoryType;

    /**
     * Creates a new memory card with its basic and specific information.
     *
     * @param id         unique identifier of the product
     * @param title      name of the product
     * @param price      unit price of the product
     * @param quantity   units currently available in inventory
     * @param capacityGb storage capacity, in gigabytes
     * @param memoryType type of memory (e.g. SD, microSD, internal card)
     */
    public Memory(String id, String title, double price, int quantity, int capacityGb, String memoryType) {
        super(id, title, price, quantity);
        this.capacityGb = capacityGb;
        this.memoryType = memoryType;
    }

    @Override
    public String getDescription() {
        return getTitle() + " - Capacity: " + capacityGb + "GB, Type: " + memoryType;
    }

    /**
     * @return the storage capacity, in gigabytes
     */
    public int getCapacityGb() {
        return capacityGb;
    }

    /**
     * @param capacityGb the new storage capacity, in gigabytes
     */
    public void setCapacityGb(int capacityGb) {
        this.capacityGb = capacityGb;
    }

    /**
     * @return the type of memory
     */
    public String getMemoryType() {
        return memoryType;
    }

    /**
     * @param memoryType the new type of memory
     */
    public void setMemoryType(String memoryType) {
        this.memoryType = memoryType;
    }
}
