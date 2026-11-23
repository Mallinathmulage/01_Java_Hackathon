package com.scoopshack.util;

import java.util.Scanner;

/** Safe keyboard input: never crashes, keeps asking until the input is valid. */
public class InputHelper {
    private final Scanner scanner;

    public InputHelper(Scanner scanner) {
        this.scanner = scanner;
    }

    public String readLine(String prompt) {
        System.out.print(prompt);
        if (!scanner.hasNextLine()) {
            throw new InputClosedException();
        }
        return scanner.nextLine().trim();
    }

    /** Keeps asking until the user types a whole number. */
    public int readInt(String prompt) {
        while (true) {
            String text = readLine(prompt);
            try {
                return Integer.parseInt(text);
            } catch (NumberFormatException e) {
                System.out.println("  ! Please type a whole number (digits only).");
            }
        }
    }

    public String readName(String prompt) {
        while (true) {
            String text = readLine(prompt);
            if (!text.isEmpty()) {
                return text;
            }
            System.out.println("  ! Name cannot be empty.");
        }
    }

    /** Returns 'R' or 'S'. */
    public char readCustomerType(String prompt) {
        while (true) {
            String text = readLine(prompt);
            if (!text.isEmpty()) {
                char c = Character.toUpperCase(text.charAt(0));
                if (c == 'R' || c == 'S') {
                    return c;
                }
            }
            System.out.println("  ! Type R for Regular or S for Student.");
        }
    }
}
