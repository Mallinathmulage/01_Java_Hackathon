package com.scoopshack.app;

import com.scoopshack.service.ShopService;
import com.scoopshack.ui.ConsoleUI;
import com.scoopshack.util.InputClosedException;
import com.scoopshack.util.InputHelper;

import java.util.Scanner;

/** Entry point. */
public class ScoopShackApp {
    public static void main(String[] args) {
        // try-with-resources closes the Scanner automatically
        try (Scanner scanner = new Scanner(System.in)) {
            new ConsoleUI(new ShopService(), new InputHelper(scanner)).run();
        } catch (InputClosedException e) {
            System.out.println();
            System.out.println("Input closed. Goodbye!");
        }
    }
}
