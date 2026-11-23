package com.scoopshack.model;

/** One product on the ScoopShack menu. */
public class MenuItem {
    public static final int LOW_STOCK_LIMIT = 10;

    private final int id;
    private final String name;
    private final String category;
    private final double price;
    private int stock;
    private int unitsSold;

    public MenuItem(int id, String name, String category, double price, int stock) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.price = price;
        this.stock = stock;
        this.unitsSold = 0;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getCategory() { return category; }
    public double getPrice() { return price; }
    public int getStock() { return stock; }
    public int getUnitsSold() { return unitsSold; }

    /** Uses the ?: operator to show LOW (below 10) or OK. */
    public String getStatus() {
        return stock < LOW_STOCK_LIMIT ? "LOW" : "OK";
    }

    /** Called only after an order is fully validated. */
    public void reduceStock(int quantity) {
        this.stock -= quantity;
        this.unitsSold += quantity;
    }

    @Override
    public String toString() {
        return String.format("%d - %s (%s) Rs.%.2f, stock %d [%s]",
                id, name, category, price, stock, getStatus());
    }
}
