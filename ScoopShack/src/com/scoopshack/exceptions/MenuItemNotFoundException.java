package com.scoopshack.exceptions;

/** Checked exception: thrown when a menu id is not on the menu. */
public class MenuItemNotFoundException extends Exception {
    private final int itemId;

    public MenuItemNotFoundException(int itemId) {
        super("Menu item id " + itemId + " does not exist. Please choose an id from the menu.");
        this.itemId = itemId;
    }

    public MenuItemNotFoundException(String name) {
        super("No menu item named \"" + name + "\" exists.");
        this.itemId = -1;
    }

    public int getItemId() {
        return itemId;
    }
}
