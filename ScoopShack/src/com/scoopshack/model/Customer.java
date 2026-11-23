package com.scoopshack.model;

/** Abstract customer: every customer type must define its own discount rate. */
public abstract class Customer extends Person {

    public Customer(String name) {
        super(name);            // pass the name up to Person
    }

    /** Fraction between 0 and 1, e.g. 0.05 means 5% off. */
    public abstract double getDiscountRate();

    /** The one final method: subclasses may NOT change how the label is built. */
    public final String getTypeLabel() {
        return getClass().getSimpleName();
    }

    @Override
    public String toString() {
        return getTypeLabel() + " customer " + getName()
                + " (discount " + (int) Math.round(getDiscountRate() * 100) + "%)";
    }
}
