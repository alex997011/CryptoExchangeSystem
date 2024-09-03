package org.example.controller;

import org.example.model.*;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class TradeController {
    static User currentUser = UserController.getCurrentUser();
    private static ArrayList<Cryptocurrency> cryptocurrencies = new ArrayList<>();
    private static ArrayList<OrderBuy> buyOrders = new ArrayList<>();
    private static ArrayList<OrderSale> saleOrders = new ArrayList<>();
    private static final String WALLET_FILE_PATH = "src/main/resources/wallet.txt";
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");
    public static void StartTradeSystem(){
        String marketFilePath = "src/main/resources/cryptomarket.txt";
        String buyOrderFilePath = "src/main/resources/orderBuy.txt";
        String saleOrderFilePath = "src/main/resources/orderSale.txt";

        // Load cryptocurrencies
        try (BufferedReader reader = new BufferedReader(new FileReader(marketFilePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Cryptocurrency cryptocurrency = parseCryptocurrency(line);
                if (cryptocurrency != null) {
                    cryptocurrencies.add(cryptocurrency);
                }
            }
            System.out.println("Market trends loaded successfully.");
        } catch (IOException e) {
            System.out.println("Error reading the file:" + e.getMessage());
        }

        // Load buy orders
        try (BufferedReader reader = new BufferedReader(new FileReader(buyOrderFilePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                OrderBuy orderBuy = parseOrderBuy(line);
                if (orderBuy != null) {
                    buyOrders.add(orderBuy);
                }
            }
            System.out.println("Buy orders loaded successfully.");
        } catch (IOException e) {
            System.out.println("Error reading the file: " + e.getMessage());
        }

        // Load sale orders
        try (BufferedReader reader = new BufferedReader(new FileReader(saleOrderFilePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                OrderSale orderSale = parseOrderSale(line);
                if (orderSale != null) {
                    saleOrders.add(orderSale);
                }
            }
            System.out.println("Sale orders loaded successfully.");
        } catch (IOException e) {
            System.out.println("Error reading the file:" + e.getMessage());
        }
    }

    public static void purchaseOrder(int userId) {
        Scanner scanner = new Scanner(System.in);

        Wallet userWallet = findWalletByUserId(userId);

        if (userWallet == null) {
            System.out.println("Wallet not found for user with ID: " + userId);
            return;
        }


        System.out.println("Select the cryptocurrency you want to buy:");
        for (int i = 0; i < cryptocurrencies.size(); i++) {
            System.out.println(i + ". " + cryptocurrencies.get(i));
        }

        int cryptoIndex = scanner.nextInt();
        Cryptocurrency selectedCrypto = cryptocurrencies.get(cryptoIndex);

        System.out.println("Enter the amount you want to buy:");
        int amount = scanner.nextInt();


        float purchasePrice;
        while (true) {
            System.out.println("Enter the price at which you want to buy per unit:");
            purchasePrice = scanner.nextFloat();

            double marketPrice = selectedCrypto.getValue();
            double priceDifference = Math.abs(marketPrice - purchasePrice);

            if (purchasePrice > marketPrice && priceDifference > 300) {
                System.out.println("The entered purchase price is too high compared to the market price.");
                System.out.println("The difference is  " + priceDifference + " USD. Enter a price closer to the market price.");
            } else {
                break;
            }
        }

        if (userWallet.getFiatBalance() >= (purchasePrice * amount)) {

            OrderBuy newBuyOrder = new OrderBuy(generateOrderId(), userId, new Date(), selectedCrypto, amount, purchasePrice, "NOTEXECUTED");


            buyOrders.add(newBuyOrder);
            saveBuyOrdersToFile();

            boolean orderExecuted = false;
            for (OrderSale saleOrder : saleOrders) {


                if (saleOrder.getId_proprietary() == userId) {
                    System.out.println("");
                    continue;
                }

                if (saleOrder.getCrypto().getMarketCoin().equals(selectedCrypto.getMarketCoin())
                        && saleOrder.getAmount() == amount
                        && saleOrder.getStage().equals("NOTEXECUTED")
                        && saleOrder.getPrice() <= purchasePrice
                        && (purchasePrice - saleOrder.getPrice()) <= 500) {

                    System.out.println("Purchase order executed at the sale price of: " + saleOrder.getPrice());

                    userWallet.setFiatBalance(userWallet.getFiatBalance() - (saleOrder.getPrice() * amount));
                    addCryptocurrencyToWallet(userWallet, selectedCrypto, amount);


                    newBuyOrder.setStage("EXECUTED");
                    saleOrder.setStage("EXECUTED");

                    orderExecuted = true;
                    break;
                }
            }

            if (!orderExecuted) {
                System.out.println("Purchase order added to the order book.");
            }


            saveBuyOrdersToFile();
            saveSaleOrdersToFile();
            saveWalletToFile(userWallet);
        } else {
            System.out.println("You don't have enough fiat balance to make the purchase.");
        }
    }


    public static void salesOrder(int userId) {
        Scanner scanner = new Scanner(System.in);

        Wallet userWallet = findWalletByUserId(userId);

        if (userWallet == null) {
            System.out.println("Wallet not found for user with ID: " + userId);
            return;
        }


        System.out.println("Select the cryptocurrency you want to sell:");
        for (int i = 0; i < cryptocurrencies.size(); i++) {
            System.out.println(i + ". " + cryptocurrencies.get(i));
        }

        int cryptoIndex = scanner.nextInt();
        Cryptocurrency selectedCrypto = cryptocurrencies.get(cryptoIndex);

        System.out.println("Enter the amount you want to sell:");
        int amount = scanner.nextInt();

        // Validar el precio de venta
        float sellPrice;
        while (true) {
            System.out.println("Enter the price at which you want to sell per unit:");
            sellPrice = scanner.nextFloat();

            double marketPrice = selectedCrypto.getValue();
            double priceDifference = Math.abs(marketPrice - sellPrice);

            if (sellPrice < marketPrice && priceDifference > 300) {
                System.out.println("The entered sale price is too low compared to the market price.");
                System.out.println("The difference is " + priceDifference + " USD. Enter a price closer to the market price.");
            } else {
                break;
            }
        }

        if (hasEnoughCryptocurrency(userWallet, selectedCrypto, amount)) {

            OrderSale newSaleOrder = new OrderSale(generateOrderId(), userId, new Date(), selectedCrypto, amount, sellPrice, "NOREALIZADO");


            saleOrders.add(newSaleOrder);
            saveSaleOrdersToFile();

            boolean orderExecuted = false;
            for (OrderBuy buyOrder : buyOrders) {


                if (buyOrder.getId_proprietary() == userId) {
                    System.out.println("");
                    continue;
                }

                if (buyOrder.getCrypto().getMarketCoin().equals(selectedCrypto.getMarketCoin())
                        && buyOrder.getAmount() == amount
                        && buyOrder.getStage().equals("NOTEXECUTED")
                        && buyOrder.getPrice() >= sellPrice
                        && (buyOrder.getPrice() - sellPrice) <= 500) {

                    System.out.println("Sale order executed at the sale price of: " + sellPrice);

                    userWallet.setFiatBalance(userWallet.getFiatBalance() + (sellPrice * amount));
                    removeCryptocurrencyFromWallet(userWallet, selectedCrypto, amount);


                    newSaleOrder.setStage("EXECUTED");
                    buyOrder.setStage("EXECUTED");

                    orderExecuted = true;
                    break;
                }
            }

            if (!orderExecuted) {
                System.out.println("Sale order added to the order book.");
            }


            saveSaleOrdersToFile();
            saveBuyOrdersToFile();
            saveWalletToFile(userWallet);
        } else {
            System.out.println("You don't have enough cryptocurrency to sell.");
        }
    }



    private static Cryptocurrency parseCryptocurrency(String line) {
        try {
            String[] parts = line.split(",");
            MarketCoin marketCoin = MarketCoin.valueOf(parts[0].trim());
            float value = Float.parseFloat(parts[1].trim());
            return new Cryptocurrency(marketCoin, value);
        } catch (Exception e) {
            System.out.println("Error parsing cryptocurrency: " + e.getMessage());
            return null;
        }
    }

    private static OrderBuy parseOrderBuy(String line) {
        try {
            String[] parts = line.split(",");
            int id = Integer.parseInt(parts[0].trim());
            int userId = Integer.parseInt(parts[1].trim());
            Date date = DATE_FORMAT.parse(parts[2].trim());
            MarketCoin marketCoin = MarketCoin.fromFullName(parts[3].trim());
            Cryptocurrency crypto = new Cryptocurrency(marketCoin, Double.parseDouble(parts[4].trim()));;
            int amount = Integer.parseInt(parts[4].trim());
            float price = Float.parseFloat(parts[5].trim());
            String stage = parts[6].trim();
            return new OrderBuy(id, userId, date, crypto, amount, price, stage);
        } catch (ParseException | NumberFormatException e) {
            System.out.println("Error parsing a: " + e.getMessage());
            return null;
        }
    }

    private static OrderSale parseOrderSale(String line) {
        try {
            String[] parts = line.split(",");
            int id = Integer.parseInt(parts[0].trim());
            int userId = Integer.parseInt(parts[1].trim());
            Date date = DATE_FORMAT.parse(parts[2].trim());
            MarketCoin marketCoin = MarketCoin.fromFullName(parts[3].trim());
            Cryptocurrency crypto = new Cryptocurrency(marketCoin, Double.parseDouble(parts[4].trim()));;
            int amount = Integer.parseInt(parts[4].trim());
            float price = Float.parseFloat(parts[5].trim());
            String stage = parts[6].trim();
            return new OrderSale(id, userId, date, crypto, amount, price, stage);
        } catch (ParseException | NumberFormatException e) {
            System.out.println("Error parsing a: " + e.getMessage());
            return null;
        }
    }

    private static void saveBuyOrdersToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("src/main/resources/orderBuy.txt"))) {
            for (OrderBuy order : buyOrders) {
                writer.write(order.getId() + "," + order.getId_proprietary() + "," + new SimpleDateFormat("dd/MM/yyyy").format(order.getDate()) + "," +
                        order.getCrypto().getMarketCoin().getFullName() + "," + order.getAmount() + "," + order.getPrice() + "," + order.getStage());
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void saveSaleOrdersToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("src/main/resources/orderSale.txt"))) {
            for (OrderSale order : saleOrders) {
                writer.write(order.getId() + "," + order.getId_proprietary() + "," + new SimpleDateFormat("dd/MM/yyyy").format(order.getDate()) + "," +
                        order.getCrypto().getMarketCoin().getFullName() + "," + order.getAmount() + "," + order.getPrice() + "," + order.getStage());
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void saveWalletToFile(Wallet wallet) {
        List<Wallet> wallets = loadWalletsFromFile();
        boolean updated = false;

        // Update or add wallet entry
        for (int i = 0; i < wallets.size(); i++) {
            if (wallets.get(i).getId() == wallet.getId()) {
                wallets.set(i, wallet);
                updated = true;
                break;
            }
        }
        if (!updated) {
            wallets.add(wallet);
        }

        // Write all wallets to file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(WALLET_FILE_PATH))) {
            for (Wallet w : wallets) {
                writer.write(formatWallet(w));
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error saving the wallet: " + e.getMessage());
        }
    }
    private static String formatWallet(Wallet wallet) {
        StringBuilder sb = new StringBuilder();
        sb.append(wallet.getId()).append(",")
                .append(wallet.getFiatBalance()).append(",");

        List<Cryptocurrency> cryptoList = wallet.getListCrypto();
        if (cryptoList.isEmpty()) {
            sb.append("()");
        } else {
            sb.append("(");
            for (int i = 0; i < cryptoList.size(); i++) {
                Cryptocurrency crypto = cryptoList.get(i);
                sb.append(crypto.getMarketCoin().getFullName()).append("/")
                        .append(crypto.getValue());
                if (i < cryptoList.size() - 1) {
                    sb.append(");(");
                }
            }
            sb.append(")");
        }

        return sb.toString();
    }
    private static List<Wallet> loadWalletsFromFile() {
        List<Wallet> wallets = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(WALLET_FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Wallet wallet = parseWallet(line);
                if (wallet != null) {
                    wallets.add(wallet);
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading the wallet file: " + e.getMessage());
        }
        return wallets;
    }

    private static Wallet parseWallet(String line) {
        try {
            String[] parts = line.split(",");
            int userId = Integer.parseInt(parts[0].trim());
            float fiatBalance = Float.parseFloat(parts[1].trim());

            String[] cryptoParts = parts[2].trim().split("\\);\\(");
            ArrayList<Cryptocurrency> cryptocurrencies = new ArrayList<>();
            for (String cryptoPart : cryptoParts) {
                cryptoPart = cryptoPart.replaceAll("[()]", "");
                String[] cryptoInfo = cryptoPart.split("/");
                if (cryptoInfo.length == 2) {
                    MarketCoin marketCoin = MarketCoin.fromFullName(cryptoInfo[0].trim());
                    float amount = Float.parseFloat(cryptoInfo[1].trim());
                    cryptocurrencies.add(new Cryptocurrency(marketCoin, amount)); // Assuming name as marketCoin
                }
            }

            return new Wallet(userId, fiatBalance,cryptocurrencies);
        } catch (Exception e) {
            System.out.println("Error parsing a number: " + e.getMessage());
            return null;
        }
    }

    private static Wallet findWalletByUserId(int userId) {
        List<Wallet> wallets = loadWalletsFromFile();
        for (Wallet wallet : wallets) {
            if (wallet.getId() == userId) {
                return wallet;
            }
        }
        return null;
    }

    private static boolean hasEnoughCryptocurrency(Wallet wallet, Cryptocurrency crypto, int amount) {
        for (Cryptocurrency c : wallet.getListCrypto()) {
            if (c.getMarketCoin().equals(crypto.getMarketCoin()) && c.getValue() >= amount) {
                return true;
            }
        }
        return false;
    }

    private static void addCryptocurrencyToWallet(Wallet wallet, Cryptocurrency crypto, int amount) {
        for (Cryptocurrency c : wallet.getListCrypto()) {
            if (c.getMarketCoin().equals(crypto.getMarketCoin())) {
                c.setValue(c.getValue() + amount);
                return;
            }
        }
        wallet.getListCrypto().add(new Cryptocurrency(crypto.getMarketCoin(), amount));
    }

    private static void removeCryptocurrencyFromWallet(Wallet wallet, Cryptocurrency crypto, int amount) {
        for (Cryptocurrency c : wallet.getListCrypto()) {
            if (c.getMarketCoin().equals(crypto.getMarketCoin())) {
                if (c.getValue() >= amount) {
                    c.setValue(c.getValue() - amount);
                    if (c.getValue() == 0) {
                        wallet.getListCrypto().remove(c);
                    }
                    return;
                } else {
                    System.out.println("You don't have enough cryptocurrency to sell.");
                }
            }
        }
    }
    public static void historic(int userId) {
        System.out.println("Transaction history for user with ID: " + userId);


        System.out.println("\nPurchase Orders Placed:");
        for (OrderBuy buyOrder : buyOrders) {
            if (buyOrder.getId_proprietary() == userId && buyOrder.getStage().equals("EXECUTED")) {
                System.out.println("Purchase - Order ID: " + buyOrder.getId());
                System.out.println("Date:" + buyOrder.getDate());
                System.out.println("Cryptocurrency: " + buyOrder.getCrypto().getMarketCoin());
                System.out.println("Amount: " + buyOrder.getAmount());
                System.out.println("Price per unit: " + buyOrder.getPrice());
                System.out.println("Total: " + (buyOrder.getPrice() * buyOrder.getAmount()));
                System.out.println("-------------------------------------");
            }
        }


        System.out.println("\nÓrdenes de Venta Realizadas:");
        for (OrderSale saleOrder : saleOrders) {
            if (saleOrder.getId_proprietary() == userId && saleOrder.getStage().equals("EXECUTED")) {
                System.out.println("Sale - Order ID: " + saleOrder.getId());
                System.out.println("Date: " + saleOrder.getDate());
                System.out.println("Cryptocurrency: " + saleOrder.getCrypto().getMarketCoin());
                System.out.println("Amount: " + saleOrder.getAmount());
                System.out.println("Price per unit: " + saleOrder.getPrice());
                System.out.println("Total: " + (saleOrder.getPrice() * saleOrder.getAmount()));
                System.out.println("-------------------------------------");
            }
        }
    }
    private static int generateOrderId() {
        // Implement logic to generate a unique order ID
        return (int) (Math.random() * 10000); // Placeholder, replace with actual implementation
    }

    public static ArrayList<OrderBuy> getBuyOrders() {
        return buyOrders;
    }

    public static void setBuyOrders(ArrayList<OrderBuy> buyOrders) {
        TradeController.buyOrders = buyOrders;
    }

    public static ArrayList<OrderSale> getSaleOrders() {
        return saleOrders;
    }

    public static void setSaleOrders(ArrayList<OrderSale> saleOrders) {
        TradeController.saleOrders = saleOrders;
    }

    public static ArrayList<Cryptocurrency> getCryptocurrencies() {
        return cryptocurrencies;
    }

    public static void setCryptocurrencies(ArrayList<Cryptocurrency> cryptocurrencies) {
        TradeController.cryptocurrencies = cryptocurrencies;
    }
}
