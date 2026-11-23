package com.scoopshack.exceptions;

/** Checked exception: thrown when an order asks for more than the stock available. */
public class InsufficientStockException extends Exception {
    private final String itemName;
    private final long requested;
    private final int available;

    public InsufficientStockException(String itemName, long requested, int available) {
        super("Not enough stock for " + itemName + ": requested " + requested
                + ", only " + available + " available.");
        this.itemName = itemName;
        this.requested = requested;
        this.available = available;
    }

    public String getItemName() { return itemName; }
    public long getRequested() { return requested; }
    public int getAvailable() { return available; }
}
