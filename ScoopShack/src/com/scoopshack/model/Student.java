package com.scoopshack.model;

/** Student customer: 5% discount. */
public class Student extends Customer {
    public Student(String name) {
        super(name);
    }

    @Override
    public double getDiscountRate() {
        return 0.05;
    }
}
