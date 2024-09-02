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
            System.out.println("wallet cargada con éxito.");
        } catch (IOException e) {
            System.out.println("Error al leer el archivo: " + e.getMessage());
        }
    }
    public static void Deposit() {
        User currentUser = UserController.getCurrentUser();

        if (currentUser != null) {

            Wallet userWallet = findWalletByUserId(currentUser.getId());

            if (userWallet != null) {
                Scanner scanner = new Scanner(System.in);
                System.out.println("Ingrese el monto a depositar:");
                double amount = scanner.nextDouble();

                if (amount > 0) {

                    double newBalance = userWallet.getFiatBalance() + amount;
                    userWallet.setFiatBalance(newBalance);

                    System.out.println("Depósito exitoso. Nuevo saldo: " + String.format("%.2f", newBalance) + " USD");

                    updateWalletFile();
                } else {
                    System.out.println("El monto a depositar debe ser positivo.");
                }
            } else {
                System.out.println("No se encontró una billetera asociada con este usuario.");
            }
        } else {
            System.out.println("No hay un usuario autenticado. Por favor, inicie sesión.");
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

            System.out.println("Archivo de billeteras actualizado con éxito.");
        } catch (IOException e) {
            System.out.println("Error al actualizar el archivo de billeteras: " + e.getMessage());
        }
    }
    public static void funds(){
        User currentUser = UserController.getCurrentUser();
        if (currentUser != null) {


            Wallet userWallet = findWalletByUserId(currentUser.getId());

            if (userWallet != null) {
                System.out.println("=== Información de Fondos ===");
                System.out.println("Saldo disponible: " + userWallet.getFiatBalance() + " USD");
            } else {
                System.out.println("No se encontró una billetera asociada con este usuario.");
            }
        } else {
            System.out.println("No hay un usuario autenticado. Por favor, inicie sesión.");
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
                System.out.println("=== Información de Criptomonedas en la Cuenta ===");

                ArrayList<Cryptocurrency> listCrypto = userWallet.getListCrypto();

                if (!listCrypto.isEmpty()) {
                    for (Cryptocurrency crypto : listCrypto) {
                        System.out.println("Criptomoneda: " + crypto.getMarketCoin().getFullName() +
                                " (" + crypto.getMarketCoin() + "), Cantidad: " + crypto.getValue());
                    }
                } else {
                    System.out.println("No hay criptomonedas en la cuenta.");
                }
            } else {
                System.out.println("No se encontró una billetera asociada con este usuario.");
            }
        } else {
            System.out.println("No hay un usuario autenticado. Por favor, inicie sesión.");
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
                System.out.println("Error al parsear el ID de usuario: " + e.getMessage());
            }
        } else {
            System.out.println("Línea de usuario inválida: " + line);
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
                    System.out.println("Error al parsear la criptomoneda: " + e.getMessage());
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
            writer.newLine();  // Asegurarse de que cada billetera se escriba en una línea nueva
            System.out.println("Billetera guardada en el archivo con éxito.");
        } catch (IOException e) {
            System.out.println("Error al escribir en el archivo de billeteras: " + e.getMessage());
        }
    }

    public static ArrayList<Wallet> getWallets() {
        return wallets;
    }

    public static void setWallets(ArrayList<Wallet> wallets) {
        WalletController.wallets = wallets;
    }
}
