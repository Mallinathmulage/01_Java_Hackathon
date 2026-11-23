package com.scoopshack.model;

/** One line of an order: a menu item and how many of it. */
public class OrderLine {
    private final MenuItem item;
    private final int quantity;

    public OrderLine(MenuItem item, int quantity) {
        this.item = item;
        this.quantity = quantity;
    }

    public MenuItem getItem() { return item; }
    public int getQuantity() { return quantity; }

    public double getLineTotal() {
        return item.getPrice() * quantity;
    }

    @Override
    public String toString() {
        return quantity + " x " + item.getName();
    }
}
