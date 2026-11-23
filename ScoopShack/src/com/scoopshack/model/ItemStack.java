package com.scoopshack.model;

/**
 * Our own array-based stack (LIFO) of order lines.
 * push = add a line, pop = undo the last line added.
 */
public class ItemStack {
    private OrderLine[] data;
    private int top;            // number of items stored; also the next free index

    public ItemStack() {
        this(8);                // constructor chaining with this(...)
    }

    public ItemStack(int capacity) {
        this.data = new OrderLine[capacity < 1 ? 1 : capacity];
        this.top = 0;
    }

    public void push(OrderLine line) {
        if (top == data.length) {
            OrderLine[] bigger = new OrderLine[data.length * 2];   // grow the array when full
            for (int i = 0; i < data.length; i++) {
                bigger[i] = data[i];
            }
            data = bigger;
        }
        data[top++] = line;
    }

    /** Removes and returns the last line, or null if the stack is empty. */
    public OrderLine pop() {
        if (isEmpty()) {
            return null;
        }
        OrderLine line = data[--top];
        data[top] = null;
        return line;
    }

    public OrderLine peek() {
        return isEmpty() ? null : data[top - 1];
    }

    public boolean isEmpty() { return top == 0; }
    public int size() { return top; }

    /** Index 0 is the first line added. */
    public OrderLine get(int index) {
        if (index < 0 || index >= top) {
            throw new IndexOutOfBoundsException("No line at position " + index);
        }
        return data[index];
    }

    /** Total quantity of one menu item currently in the stack. */
    public int quantityOf(int itemId) {
        int sum = 0;
        for (int i = 0; i < top; i++) {
            if (data[i].getItem().getId() == itemId) {
                sum += data[i].getQuantity();
            }
        }
        return sum;
    }
}
