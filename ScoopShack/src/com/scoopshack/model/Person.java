package com.scoopshack.model;

/** Base class for any person in the shop. */
public class Person {
    private final String name;

    public Person(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "Person[" + name + "]";
    }
}
