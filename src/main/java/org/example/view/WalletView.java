package org.example.view;

import org.example.controller.TradeController;
import org.example.controller.UserController;
import org.example.controller.WalletController;
import org.example.model.User;

import java.util.Scanner;

public class WalletView {
    // Scanner for user input
    public static Scanner scanner = new Scanner(System.in);

    // Menu for depositing money into the wallet
    public static void menuDeposit() {
        WalletController.Deposit(); // Perform the deposit action
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
                case "1" -> menuUserWallet();
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

    // Menu for checking the available funds
    public static void menuFunds() {
        WalletController.funds(); // Show available funds
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
                case "1" -> menuUserWallet();
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

    // Menu for checking cryptocurrency funds
    public static void menuCrypto() {
        WalletController.Crypofunds(); // Show available cryptocurrencies
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
                case "1" -> menuUserWallet();
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

    // Main menu for user wallet actions
    public static void menuUserWallet() {
        User currentUser = UserController.getCurrentUser(); // Get the current authenticated user
        System.out.println();
        boolean start = true;
        while (start) {
            System.out.println("""
                    === Welcome to the system ===\s
                    1. Deposit money\s
                    2. Account funds\s
                    3. Account cryptocurrencies\s
                    4. Go back to the main menu\s
                    5. Transaction history\s
                    6. Log out\s
                    Select an option:"""
            );
            String option = scanner.nextLine();
            switch (option) {
                case "1" -> menuDeposit();
                case "2" -> menuFunds();
                case "3" -> menuCrypto();
                case "4" -> UserView.menuProgram();
                case "5" -> TradeController.historic(currentUser.getId()); // Show transaction history
                case "6" -> {
                    UserView.menuUser();
                    SystemView.reloadSystem();
                }
                default -> System.out.println("Invalid option. Please try again.");
            }
            System.out.println();
        }
        scanner.close();
    }
}
