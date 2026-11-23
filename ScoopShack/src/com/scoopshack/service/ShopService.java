package com.scoopshack.service;

import com.scoopshack.exceptions.InsufficientStockException;
import com.scoopshack.exceptions.MenuItemNotFoundException;
import com.scoopshack.model.Customer;
import com.scoopshack.model.ItemStack;
import com.scoopshack.model.MenuItem;
import com.scoopshack.model.Order;
import com.scoopshack.model.OrderLine;

/** The "backend": menu, stock rules, order storage and reports. No printing here. */
public class ShopService {
    private final MenuItem[] menu;
    private Order[] orders;
    private int orderCount;

    public ShopService() {
        menu = new MenuItem[] {
            new MenuItem(101, "Vanilla Scoop",          "Scoop",   60,  40),
            new MenuItem(102, "Chocolate Sundae",       "Sundae", 150,  20),
            new MenuItem(103, "Mango Shake",            "Shake",  120,  25),
            new MenuItem(104, "Kulfi Stick",            "Kulfi",   50,  30),
            new MenuItem(105, "Brownie with Ice Cream", "Dessert",180,  15),
            new MenuItem(106, "Family Pack 1 L",        "Pack",   350,  10)
        };
        orders = new Order[20];
        orderCount = 0;
    }

    public MenuItem[] getMenu() {
        return menu;
    }

    // ----- overloaded method #1: find by id -----
    public MenuItem findItem(int id) throws MenuItemNotFoundException {
        for (MenuItem item : menu) {
            if (item.getId() == id) {
                return item;
            }
        }
        throw new MenuItemNotFoundException(id);
    }

    // ----- overloaded method #2: find by name -----
    public MenuItem findItem(String name) throws MenuItemNotFoundException {
        for (MenuItem item : menu) {
            if (item.getName().equalsIgnoreCase(name)) {
                return item;
            }
        }
        throw new MenuItemNotFoundException(name);
    }

    /**
     * Adds a line to the pending cart. Stock is only CHECKED here, never reduced,
     * so a rejected order can never change stock.
     */
    public OrderLine addToCart(ItemStack cart, int id, int quantity)
            throws MenuItemNotFoundException, InsufficientStockException {
        MenuItem item = findItem(id);                      // may throw MenuItemNotFoundException
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be at least 1 (you typed " + quantity + ").");
        }
        int alreadyInCart = cart.quantityOf(id);
        long wanted = (long) alreadyInCart + quantity;     // long avoids int overflow
        if (wanted > item.getStock()) {
            throw new InsufficientStockException(item.getName(), wanted, item.getStock());
        }
        OrderLine line = new OrderLine(item, quantity);
        cart.push(line);
        return line;
    }

    /** Undo: removes the last line added (null if the cart is empty). */
    public OrderLine undoLast(ItemStack cart) {
        return cart.pop();
    }

    /** Final check, then reduce stock, then create the order. All-or-nothing. */
    public Order checkout(Customer customer, ItemStack cart) throws InsufficientStockException {
        // Pass 1: validate everything. Nothing is changed in this loop.
        for (int i = 0; i < cart.size(); i++) {
            MenuItem item = cart.get(i).getItem();
            int need = cart.quantityOf(item.getId());
            if (need > item.getStock()) {
                throw new InsufficientStockException(item.getName(), need, item.getStock());
            }
        }
        // Pass 2: safe to reduce the stock now.
        for (int i = 0; i < cart.size(); i++) {
            OrderLine line = cart.get(i);
            line.getItem().reduceStock(line.getQuantity());
        }
        Order order = new Order(customer, cart);
        if (orderCount == orders.length) {
            Order[] bigger = new Order[orders.length * 2];
            for (int i = 0; i < orders.length; i++) {
                bigger[i] = orders[i];
            }
            orders = bigger;
        }
        orders[orderCount++] = order;
        return order;
    }

    /** Accepts "ICE3", "ice3" or just "3". Returns null if not found. */
    public Order findOrder(String text) {
        String key = text.trim().toUpperCase();
        if (!key.isEmpty() && Character.isDigit(key.charAt(0))) {
            key = "ICE" + key;
        }
        for (int i = 0; i < orderCount; i++) {
            if (orders[i].getOrderId().equals(key)) {
                return orders[i];
            }
        }
        return null;
    }

    public int getOrderCount() { return orderCount; }

    public double getTotalRevenue() {
        double sum = 0;
        for (int i = 0; i < orderCount; i++) {
            sum += orders[i].getTotal();
        }
        return sum;
    }

    public int getTotalUnitsSold() {
        int sum = 0;
        for (MenuItem item : menu) {
            sum += item.getUnitsSold();
        }
        return sum;
    }

    /** Highest units sold; returns null if nothing has been sold. */
    public MenuItem getMostPopularItem() {
        MenuItem best = null;
        for (MenuItem item : menu) {
            if (item.getUnitsSold() > 0 && (best == null || item.getUnitsSold() > best.getUnitsSold())) {
                best = item;
            }
        }
        return best;
    }

    public int countOrdersAtWindow(int window) {
        int count = 0;
        for (int i = 0; i < orderCount; i++) {
            if (orders[i].getWindow() == window) {
                count++;
            }
        }
        return count;
    }
}
