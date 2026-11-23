package com.scoopshack.ui;

import com.scoopshack.exceptions.InsufficientStockException;
import com.scoopshack.exceptions.MenuItemNotFoundException;
import com.scoopshack.model.Customer;
import com.scoopshack.model.ItemStack;
import com.scoopshack.model.MenuItem;
import com.scoopshack.model.Order;
import com.scoopshack.model.OrderLine;
import com.scoopshack.model.Regular;
import com.scoopshack.model.Student;
import com.scoopshack.service.ShopService;
import com.scoopshack.util.InputHelper;
import com.scoopshack.util.Money;

/** The "frontend": everything the server sees and types. All logic is in ShopService. */
public class ConsoleUI {
    private final ShopService service;
    private final InputHelper input;

    public ConsoleUI(ShopService service, InputHelper input) {
        this.service = service;
        this.input = input;
    }

    public void run() {
        System.out.println("*************************************");
        System.out.println("*   SCOOPSHACK  -  Ordering System  *");
        System.out.println("*************************************");
        int choice;
        do {
            printMainMenu();
            choice = input.readInt("Choose an option (1-5): ");
            switch (choice) {
                case 1:
                    showMenu();
                    break;
                case 2:
                    takeOrder();
                    break;
                case 3:
                    viewBill();
                    break;
                case 4:
                    showReport();
                    break;
                case 5:
                    System.out.println("Closing the shop. Thank you, see you tomorrow!");
                    break;
                default:
                    System.out.println("  ! Invalid option. Please choose 1 to 5.");
            }
        } while (choice != 5);
    }

    private void printMainMenu() {
        System.out.println();
        System.out.println("---------- MAIN MENU ----------");
        System.out.println(" 1  View menu");
        System.out.println(" 2  New order");
        System.out.println(" 3  View bill");
        System.out.println(" 4  Sales report");
        System.out.println(" 5  Exit");
    }

    // ---------------------------------------------------------------- 1
    private void showMenu() {
        System.out.println();
        System.out.printf("%-5s %-24s %-9s %9s %6s  %s%n",
                "Id", "Name", "Category", "Price(Rs)", "Stock", "Status");
        System.out.println("-".repeat(64));
        for (MenuItem item : service.getMenu()) {
            System.out.printf("%-5d %-24s %-9s %9.2f %6d  %s%n",
                    item.getId(), item.getName(), item.getCategory(),
                    item.getPrice(), item.getStock(), item.getStatus());
        }
    }

    // ---------------------------------------------------------------- 2
    private void takeOrder() {
        String name = input.readName("Customer name: ");
        char type = input.readCustomerType("Customer type - (R)egular or (S)tudent: ");
        Customer customer = (type == 'S') ? new Student(name) : new Regular(name);
        System.out.println("  -> " + customer);

        showMenu();
        System.out.println("Enter item id and quantity.  -1 = undo last item,  0 = finish order.");

        ItemStack cart = new ItemStack();
        Order order = null;
        try {                                              // outer try: whole order session
            boolean finished = false;
            while (!finished) {
                int id = input.readInt("Item id (-1 undo, 0 finish): ");
                if (id == 0) {
                    finished = true;
                } else if (id == -1) {
                    OrderLine removed = service.undoLast(cart);
                    if (removed == null) {
                        System.out.println("  ! Nothing to undo - the order is empty.");
                    } else {
                        System.out.println("  Removed: " + removed);
                        printCart(cart);
                    }
                } else {
                    int qty = input.readInt("Quantity: ");
                    try {                                  // inner try: one line at a time
                        OrderLine line = service.addToCart(cart, id, qty);
                        System.out.println("  Added: " + line);
                        printCart(cart);
                    } catch (MenuItemNotFoundException e) {
                        System.out.println("  ! " + e.getMessage());
                    } catch (InsufficientStockException e) {
                        System.out.println("  ! " + e.getMessage() + " Line rejected, stock unchanged.");
                    } catch (IllegalArgumentException e) {
                        System.out.println("  ! " + e.getMessage());
                    }
                }
            }

            if (cart.isEmpty()) {
                System.out.println("Order is empty - cancelled. No stock changed.");
            } else {
                try {                                      // nested try: final stock check
                    order = service.checkout(customer, cart);
                } catch (InsufficientStockException e) {
                    System.out.println("  ! Order rejected: " + e.getMessage() + " Stock unchanged.");
                }
            }
        } finally {                                        // runs no matter what
            System.out.println("(Order session for " + name + " closed.)");
        }

        if (order != null) {
            System.out.println();
            System.out.println(order.toBill());
        }
    }

    private void printCart(ItemStack cart) {
        System.out.println("  Current order:");
        for (int i = 0; i < cart.size(); i++) {
            OrderLine l = cart.get(i);
            System.out.printf("    %d. %-24s x%-3d %s%n", i + 1, l.getItem().getName(),
                    l.getQuantity(), Money.fmt(l.getLineTotal()));
        }
    }

    // ---------------------------------------------------------------- 3
    private void viewBill() {
        String text = input.readLine("Order id (e.g. ICE1): ");
        Order order = service.findOrder(text);
        if (order == null) {
            System.out.println("  ! No bill found for \"" + text + "\".");
        } else {
            System.out.println();
            System.out.println(order.toBill());
        }
    }

    // ---------------------------------------------------------------- 4
    private void showReport() {
        System.out.println();
        System.out.println("========== END-OF-DAY SALES REPORT ==========");
        int orders = service.getOrderCount();
        System.out.println("Total orders  : " + orders);
        System.out.println("Total revenue : " + Money.fmt(service.getTotalRevenue()));
        MenuItem best = service.getMostPopularItem();
        if (best == null) {
            System.out.println("Most popular  : (no sales yet)");
        } else {
            System.out.println("Most popular  : " + best.getName() + " (" + best.getUnitsSold() + " sold)");
        }
        if (orders > 0) {
            double avg = service.getTotalRevenue() / (double) orders;   // type casting
            System.out.println("Average bill  : " + Money.fmt(avg));
        }
        System.out.println("Window 1 orders: " + service.countOrdersAtWindow(1)
                + "   Window 2 orders: " + service.countOrdersAtWindow(2));
        System.out.println("Units sold    : " + service.getTotalUnitsSold());
        boolean anyLow = false;
        for (MenuItem item : service.getMenu()) {
            if (item.getStock() < MenuItem.LOW_STOCK_LIMIT) {
                if (!anyLow) {
                    System.out.println("Restock alert :");
                    anyLow = true;
                }
                System.out.println("   - " + item.getName() + " (only " + item.getStock() + " left)");
            }
        }
        System.out.println("=============================================");
    }
}
