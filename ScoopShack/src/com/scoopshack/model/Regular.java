package com.scoopshack.model;

/** Regular customer: no discount. */
public class Regular extends Customer {
    public Regular(String name) {
        super(name);
    }

    @Override
    public double getDiscountRate() {
        return 0.0;
    }
}
