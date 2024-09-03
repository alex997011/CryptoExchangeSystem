package org.example.controller;

import org.example.model.User;
import org.example.model.Wallet;
import org.example.view.SystemView;

import java.io.*;
import java.util.ArrayList;
import java.util.Random;

public class UserController {
    private static ArrayList<User> users = new ArrayList<>();
    private static User currentUser;
    public UserController(){
        this.users = new ArrayList<>() ;
    }
    public static void StartUserSystem(){
        String filePath = "src/main/resources/users.txt";
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                User user = parseUser(line);
                if (user != null) {
                    users.add(user);
                }
            }
            System.out.println("Users loaded from file successfully.");
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }
    }
    public static Integer generate_id(){

        Random random = new Random();


        int id = 100000000 + random.nextInt(900000000);

        return id;
    }
    public static boolean isPasswordSecure(String password) {
        return password.length() >= 8 && password.matches(".*[A-Z].*") && password.matches(".*[a-z].*") && password.matches(".*\\d.*");
    }
    public static boolean registerUser(int id, String username, String password,String mail, String name, String lastname) {
        for (User user : users) {
            if (user.getUser().equals(username)) {
                System.out.println("The username is already in use. Try another one.");
                return false;
            }
        }
        User newUser = new User(id, username, password, mail, name, lastname);
        users.add(newUser);
        writeUser(newUser);
        Wallet newWallet = new Wallet(id, 0.00, new ArrayList<>());
        WalletController.addWallet(newWallet);
        WalletController.writeWallet(newWallet);

        System.out.println("User and wallet created successfully.");
        SystemView.reloadSystem();
        return true;
    }

    public static boolean loginUser(String username, String password) {
        for (User user : users) {
            if (user.getUser().equals(username) && user.getPassword().equals(password)) {
                currentUser = user;
                return true;
            }
        }
        return false;
    }

    public static void writeUser(User user) {
        String filePath = "src/main/resources/users.txt";

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) {
            String line = user.getId()+","+user.getUser()+","+user.getPassword()+","+user.getMail()+","+user.getName()+","+user.getLastname();
            writer.write(line);
            writer.newLine();

            System.out.println("Users saved to file successfully.");
        } catch (IOException e) {
            System.out.println("Error writing to file: " + e.getMessage());
        }
    }
    private static User parseUser(String line) {
        String[] parts = line.split(",");
        if (parts.length == 6) {
            try {
                int id = Integer.parseInt(parts[0].trim());
                String username = parts[1].trim();
                String password = parts[2].trim();
                String mail = parts[3].trim();
                String name = parts[4].trim();
                String lastname = parts[5].trim();
                return new User(id, username, password,mail, name, lastname);
            } catch (NumberFormatException e) {
                System.out.println("Error parsing user ID: " + e.getMessage());
            }
        } else {
            System.out.println("Invalid user line:" + line);
        }
        return null;

    }
    public static User getCurrentUser() {
        return currentUser;
    }
    public static void logoutUser() {
        currentUser = null;
    }

    public static ArrayList<User> getUsers() {
        return users;
    }

    public static void setUsers(ArrayList<User> users) {
        UserController.users = users;
    }
}
