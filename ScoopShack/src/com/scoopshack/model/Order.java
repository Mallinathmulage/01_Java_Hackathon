package com.scoopshack.model;

import java.util.Locale;

/** A confirmed order. Gets its id (ICE1, ICE2...) and token from static counters. */
public class Order implements Billable {
    public static final double GST = 0.18;

    private static int orderCounter = 0;   // shared by ALL orders
    private static int tokenCounter = 0;

    private final String orderId;
    private final int tokenNo;
    private final Customer customer;
    private final ItemStack lines;

    public Order(Customer customer, ItemStack lines) {
        this.customer = customer;
        this.lines = lines;
        orderCounter++;
        tokenCounter++;
        this.orderId = "ICE" + orderCounter;
        this.tokenNo = tokenCounter;
    }

    public String getOrderId() { return orderId; }
    public int getTokenNo() { return tokenNo; }
    public Customer getCustomer() { return customer; }
    public ItemStack getLines() { return lines; }

    /** Odd token -> Window 1, even token -> Window 2 (token & 1 is the last bit). */
    public int getWindow() {
        return (tokenNo & 1) == 1 ? 1 : 2;
    }

    // ----- overloaded method: same name, different parameters -----
    public double getSubtotal() {
        return getSubtotal(0);
    }

    /** Recursive: this line's total + the subtotal of all lines after it. */
    public double getSubtotal(int index) {
        if (index >= lines.size()) {
            return 0.0;                                   // base case
        }
        return lines.get(index).getLineTotal() + getSubtotal(index + 1);
    }

    private static double round2(double v) {
        return Math.round(v * 100.0) / 100.0;
    }

    @Override
    public double getDiscountAmount() {
        return round2(getSubtotal() * customer.getDiscountRate());
    }

    @Override
    public double getGstAmount() {
        return round2((getSubtotal() - getDiscountAmount()) * GST);
    }

    @Override
    public double getTotal() {
        return round2(getSubtotal() - getDiscountAmount() + getGstAmount());
    }

    /** The printable bill. */
    public String toBill() {
        StringBuilder sb = new StringBuilder();
        String line = "=".repeat(52);
        sb.append(line).append('\n');
        sb.append("                 SCOOPSHACK BILL\n");
        sb.append(line).append('\n');
        sb.append(String.format("Order id : %s      Token : %d\n", orderId, tokenNo));
        sb.append(String.format("Customer : %s (%s)\n", customer.getName(), customer.getTypeLabel()));
        sb.append(String.format("Collect at WINDOW %d\n", getWindow()));
        sb.append("-".repeat(52)).append('\n');
        sb.append(String.format("%-24s %4s %10s %10s\n", "Item", "Qty", "Price", "Total"));
        for (int i = 0; i < lines.size(); i++) {
            OrderLine l = lines.get(i);
            sb.append(String.format(Locale.US, "%-24s %4d %10.2f %10.2f\n",
                    l.getItem().getName(), l.getQuantity(), l.getItem().getPrice(), l.getLineTotal()));
        }
        sb.append("-".repeat(52)).append('\n');
        sb.append(String.format(Locale.US, "%-37s %14.2f\n", "Subtotal", getSubtotal()));
        sb.append(String.format(Locale.US, "%-37s %14.2f\n",
                "Discount (" + (int) Math.round(customer.getDiscountRate() * 100) + "%)",
                0.0 - getDiscountAmount()));
        sb.append(String.format(Locale.US, "%-37s %14.2f\n",
                "GST (" + (int) Math.round(GST * 100) + "%)", getGstAmount()));
        sb.append(line).append('\n');
        sb.append(String.format(Locale.US, "%-37s %14.2f\n", "FINAL TOTAL (Rs.)", getTotal()));
        sb.append(line);
        return sb.toString();
    }

    @Override
    public String toString() {
        return orderId + " | token " + tokenNo + " | window " + getWindow()
                + " | " + customer.getName() + " | Rs." + String.format(Locale.US, "%.2f", getTotal());
    }
}
