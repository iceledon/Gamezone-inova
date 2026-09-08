package com.gamezone.model;

/**
 * Abstract class representing a person in the system.
 */
public abstract class Person {
    private String id;
    private String name;
    private String phone;

    public Person(String id, String name, String phone) {
        this.id = id;
        this.name = name;
        this.phone = phone;
    }
}
