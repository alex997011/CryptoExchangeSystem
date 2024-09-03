package org.example.view;

import org.example.model.OrderBuy;
import org.example.model.OrderSale;

import java.util.Scanner;

public class TradeView {
    // Scanner for user input
    public static Scanner scanner = new Scanner(System.in);

    // Menu for executing a purchase order
    public static void menuPurchaseOrder() {
        OrderBuy.execute(); // Execute the purchase order

        boolean start = true;
        while (start) {
            System.out.println("""
                    1. Go back\s
                    2. Log out\s
                    3. Exit the program\s
                    Select an option:"""
            );
            String option = scanner.nextLine();
            switch (option) {
                case "1" -> UserView.menuProgram();
                case "2" -> {
                    UserView.menuUser();
                    SystemView.reloadSystem();
                }
                case "3" -> {
                    System.out.println("Exiting the system...");
                    start = false;
                }
                default -> System.out.println("Invalid option. Please try again.");
            }
            System.out.println();
        }
        scanner.close();
    }

    // Menu for executing a sales order
    public static void menuSalesOrder() {
        OrderSale.execute(); // Execute the sales order

        boolean start = true;
        while (start) {
            System.out.println("""
                    1. Go back\s
                    2. Log out\s
                    3. Exit the program\s
                    Select an option:"""
            );
            String option = scanner.nextLine();
            switch (option) {
                case "1" -> UserView.menuProgram();
                case "2" -> {
                    UserView.menuUser();
                    SystemView.reloadSystem();
                }
                case "3" -> {
                    System.out.println("Exiting the system...");
                    start = false;
                }
                default -> System.out.println("Invalid option. Please try again.");
            }
            System.out.println();
        }
        scanner.close();
    }
}