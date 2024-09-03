package org.example.controller;

import org.example.model.Cryptocurrency;
import org.example.model.MarketCoin;
import org.example.model.User;
import org.example.model.Wallet;

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class WalletController {

    private static ArrayList<Wallet> wallets = new ArrayList<>();
    public WalletController(){this.wallets = new ArrayList<>();}
    public static void StartWalletSystem(){
        String filePath = "src/main/resources/wallet.txt";
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Wallet wallet = parseWallet(line);
                if (wallet != null) {
                    wallets.add(wallet);
                }
            }
            System.out.println("wallet loaded successfully.");
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }
    }
    public static void Deposit() {
        User currentUser = UserController.getCurrentUser();

        if (currentUser != null) {

            Wallet userWallet = findWalletByUserId(currentUser.getId());

            if (userWallet != null) {
                Scanner scanner = new Scanner(System.in);
                System.out.println("Enter the amount to deposit:");
                double amount = scanner.nextDouble();

                if (amount > 0) {

                    double newBalance = userWallet.getFiatBalance() + amount;
                    userWallet.setFiatBalance(newBalance);

                    System.out.println("Successful deposit. New balance:" + String.format("%.2f", newBalance) + " USD");

                    updateWalletFile();
                } else {
                    System.out.println("The amount to be deposited must be positive.");
                }
            } else {
                System.out.println("No wallet associated with this user was found.");
            }
        } else {
            System.out.println("There is no authenticated user. Please login.");
        }
    }
    private static void updateWalletFile() {
        String filePath = "src/main/resources/wallet.txt";

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (Wallet wallet : wallets) {

                StringBuilder line = new StringBuilder();
                line.append(wallet.getId()).append(",")
                        .append(String.format("%.2f",wallet.getFiatBalance())).append(",()");


                ArrayList<Cryptocurrency> listCrypto = wallet.getListCrypto();
                for (int i = 0; i < listCrypto.size(); i++) {
                    Cryptocurrency crypto = listCrypto.get(i);
                    line.append("(")
                            .append(crypto.getMarketCoin().getFullName())
                            .append("/")
                            .append(crypto.getValue())
                            .append(")");
                    if (i < listCrypto.size() - 1) {
                        line.append(";");
                    }
                }


                writer.write(line.toString());
                writer.newLine();
            }

            System.out.println("Wallets file updated successfully.");
        } catch (IOException e) {
            System.out.println("Error updating wallets file:" + e.getMessage());
        }
    }
    public static void funds(){
        User currentUser = UserController.getCurrentUser();
        if (currentUser != null) {


            Wallet userWallet = findWalletByUserId(currentUser.getId());

            if (userWallet != null) {
                System.out.println("=== Fund Information ===");
                System.out.println("Available balance:" + userWallet.getFiatBalance() + " USD");
            } else {
                System.out.println("No wallet associated with this user was found.");
            }
        } else {
            System.out.println("There is no authenticated user. Please login.");
        }
    }
    private static Wallet findWalletByUserId(int userId) {
        for (Wallet wallet : wallets) {
            if (wallet.getId() == userId) {
                return wallet;
            }
        }
        return null;
    }
    public static void Crypofunds() {
        User currentUser = UserController.getCurrentUser();
        if (currentUser != null) {

            Wallet userWallet = findWalletByUserId(currentUser.getId());

            if (userWallet != null) {
                System.out.println("===Cryptocurrency Information in the Account===");

                ArrayList<Cryptocurrency> listCrypto = userWallet.getListCrypto();

                if (!listCrypto.isEmpty()) {
                    for (Cryptocurrency crypto : listCrypto) {
                        System.out.println("Cryptocurrency: " + crypto.getMarketCoin().getFullName() +
                                " (" + crypto.getMarketCoin() + "), Amount: " + crypto.getValue());
                    }
                } else {
                    System.out.println("There are no cryptocurrencies in the account.");
                }
            } else {
                System.out.println("No wallet associated with this user was found.");
            }
        } else {
            System.out.println("There is no authenticated user. Please login.");
        }
    }
    private static Wallet parseWallet(String line){
        String[] parts = line.split(",");
        if (parts.length == 3) {
            try {
                int id = Integer.parseInt(parts[0].trim());
                double fiatbalance = Double.parseDouble(parts[1].trim());
                ArrayList<Cryptocurrency> listCrypto = parseCryptocurrencies(parts[2]);
                return new Wallet(id,fiatbalance, listCrypto);
            } catch (NumberFormatException e) {
                System.out.println("Error parsing user ID:" + e.getMessage());
            }
        } else {
            System.out.println("Invalid user line:" + line);
        }
        return null;
    }
    private static ArrayList<Cryptocurrency> parseCryptocurrencies(String cryptosString) {
        ArrayList<Cryptocurrency> listCrypto = new ArrayList<>();

        String[] cryptoParts = cryptosString.split(";");

        for (String crypto : cryptoParts) {
            String[] cryptoDetails = crypto.replace("(", "").replace(")", "").split("/");
            if (cryptoDetails.length == 2) {
                try {
                    MarketCoin marketCoin = MarketCoin.fromFullName(cryptoDetails[0].trim());
                    double value = Double.parseDouble(cryptoDetails[1].trim());
                    listCrypto.add(new Cryptocurrency(marketCoin, value));
                } catch (IllegalArgumentException e) {
                    System.out.println("Error parsing cryptocurrency: " + e.getMessage());
                }
            }
        }

        return listCrypto;
    }
    public static void addWallet(Wallet wallet) {
        wallets.add(wallet);
    }

    public static void writeWallet(Wallet wallet) {
        String filePath = "src/main/resources/wallet.txt";

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) {
            String line = wallet.getId() + "," + wallet.getFiatBalance() + ",()";
            writer.write(line);
            writer.newLine();
            System.out.println("Wallet saved to file successfully.");
        } catch (IOException e) {
            System.out.println("Error writing to wallets file: " + e.getMessage());
        }
    }

    public static ArrayList<Wallet> getWallets() {
        return wallets;
    }

    public static void setWallets(ArrayList<Wallet> wallets) {
        WalletController.wallets = wallets;
    }
}
