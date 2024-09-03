package org.example.view;

import org.example.controller.UserController;
import org.example.model.User;

import java.util.Scanner;

public class UserView {

    // Scanner for user input
    public static Scanner scanner = new Scanner(System.in);

    // Menu for User Registration
    public static void menuRegister() {
        System.out.println("=== User Registration Menu ===");

        // Generate unique ID for the new user
        Integer id = UserController.generate_id();

        // Username input with validation
        String username;
        while (true) {
            System.out.println("Enter your username:");
            username = scanner.nextLine();
            if (username.isEmpty()) {
                System.out.println("Username cannot be empty. Please try again.");
            } else {
                break;
            }
        }

        // Password input with validation
        String password;
        while (true) {
            System.out.println("Enter your password (minimum 8 characters, at least one uppercase letter, one lowercase letter, and one number):");
            password = scanner.nextLine();
            if (password.isEmpty() || !UserController.isPasswordSecure(password)) {
                System.out.println("Password is not secure. Ensure it has at least 8 characters, one uppercase letter, one lowercase letter, and one number.");
            } else {
                break;
            }
        }

        // Email input with validation
        String mail;
        while (true) {
            System.out.println("Enter your email:");
            mail = scanner.nextLine();
            if (mail.isEmpty()) {
                System.out.println("Email cannot be empty. Please try again.");
            } else {
                break;
            }
        }

        // First name input with validation
        String name;
        while (true) {
            System.out.println("Enter your first name:");
            name = scanner.nextLine();
            if (name.isEmpty()) {
                System.out.println("First name cannot be empty. Please try again.");
            } else {
                break;
            }
        }

        // Last name input with validation
        String lastname;
        while (true) {
            System.out.println("Enter your last name:");
            lastname = scanner.nextLine();
            if (lastname.isEmpty()) {
                System.out.println("Last name cannot be empty. Please try again.");
            } else {
                break;
            }
        }

        // Register the user and check if the registration was successful
        if (UserController.registerUser(id, username, password, mail, name, lastname)) {
            System.out.println("User registered successfully.");
        } else {
            System.out.println("Error registering user. Please try again.");
        }
    }

    // Menu for User Login
    public static void menuLogin() {
        System.out.println("=== Login Menu ===");

        String username;
        String password;
        boolean success = false;
        int attempts = 0;

        while (attempts < 3 && !success) {

            // Username input with validation
            while (true) {
                System.out.println("Enter your username:");
                username = scanner.nextLine();
                if (username.isEmpty()) {
                    System.out.println("Username cannot be empty. Please try again.");
                } else {
                    break;
                }
            }

            // Password input with validation
            while (true) {
                System.out.println("Enter your password:");
                password = scanner.nextLine();
                if (password.isEmpty()) {
                    System.out.println("Password cannot be empty. Please try again.");
                } else {
                    break;
                }
            }

            // Attempt to log in the user
            success = UserController.loginUser(username, password);
            if (success) {
                System.out.println("Login successful.");
                menuProgram(); // Redirect to the main program menu
            } else {
                attempts++;
                System.out.println("Incorrect username or password. Attempt " + attempts + " of 3.");
            }

            // If the maximum number of attempts is reached
            if (attempts == 3 && !success) {
                System.out.println("Maximum attempts reached. Please try again later.");
            }
        }
    }

    // Main Menu for User actions
    public static void menuUser() {
        boolean start = true;
        while (start) {
            System.out.println("""
                    === User System Menu ===\s
                    1. Log In\s
                    2. Register\s
                    3. Exit\s
                    Select an option:"""
            );
            String option = scanner.nextLine();
            switch (option) {
                case "1" -> menuLogin();
                case "2" -> menuRegister();
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

    // Program Menu after logging in
    public static void menuProgram() {
        boolean start = true;
        System.out.println();
        while (start) {
            System.out.println("""
                    === Welcome to the system ===\s
                    1. Digital Wallet Information\s
                    2. Place Purchase Order\s
                    3. Place Sales Order\s
                    4. Log Out\s
                    Select an option:"""
            );
            String option = scanner.nextLine();
            switch (option) {
                case "1" -> WalletView.menuUserWallet();
                case "2" -> TradeView.menuPurchaseOrder();
                case "3" -> TradeView.menuSalesOrder();
                case "4" -> {
                    menuUser();
                    SystemView.reloadSystem(); // Assume this reloads or restarts the system
                }
                default -> System.out.println("Invalid option. Please try again.");
            }
            System.out.println();
        }
        scanner.close();
    }

    // Display current user's information
    public static void showCurrentUserInfo() {
        User current = UserController.getCurrentUser();
        if (current != null) {
            System.out.println("Current user: " + current.toString());
        } else {
            System.out.println("No user is authenticated.");
        }
    }
}
