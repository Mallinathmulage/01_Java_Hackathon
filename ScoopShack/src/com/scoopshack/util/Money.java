package com.scoopshack.util;

import java.util.Locale;

/** Formats money the same way everywhere: Rs.1,289.15 */
public final class Money {
    private Money() { }

    public static String fmt(double amount) {
        return String.format(Locale.US, "Rs.%,.2f", amount);
    }
}
