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
            System.out.println("Las tendencias del mercado se cargaron con éxito.");
        } catch (IOException e) {
            System.out.println("Error al leer el archivo: " + e.getMessage());
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
            System.out.println("Órdenes de compra cargadas con éxito.");
        } catch (IOException e) {
            System.out.println("Error al leer el archivo: " + e.getMessage());
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
            System.out.println("Órdenes de venta cargadas con éxito.");
        } catch (IOException e) {
            System.out.println("Error al leer el archivo: " + e.getMessage());
        }

        // Print loaded data
        System.out.println("Precios de criptomonedas cargados:");
        for (Cryptocurrency crypto : cryptocurrencies) {
            System.out.println(crypto);
        }

        System.out.println("Órdenes de compra cargadas:");
        for (OrderBuy order : buyOrders) {
            System.out.println(order);
        }

        System.out.println("Órdenes de venta cargadas:");
        for (OrderSale order : saleOrders) {
            System.out.println(order);
        }
    }

    public static void purchaseOrder(int userId) {
        Scanner scanner = new Scanner(System.in);

        Wallet userWallet = findWalletByUserId(userId);

        if (userWallet == null) {
            System.out.println("No se encontró la Wallet para el usuario con ID: " + userId);
            return;
        }

        // Mostrar criptomonedas disponibles y sus precios actuales
        System.out.println("Seleccione la criptomoneda que desea comprar:");
        for (int i = 0; i < cryptocurrencies.size(); i++) {
            System.out.println(i + ". " + cryptocurrencies.get(i));
        }

        int cryptoIndex = scanner.nextInt();
        Cryptocurrency selectedCrypto = cryptocurrencies.get(cryptoIndex);

        System.out.println("Ingrese la cantidad que desea comprar:");
        int amount = scanner.nextInt();

        // Validar el precio de compra
        float purchasePrice;
        while (true) {
            System.out.println("Ingrese el precio al que desea comprar por unidad:");
            purchasePrice = scanner.nextFloat();

            double marketPrice = selectedCrypto.getValue();
            double priceDifference = Math.abs(marketPrice - purchasePrice);

            if (purchasePrice > marketPrice && priceDifference > 300) {
                System.out.println("El precio de compra ingresado es demasiado alto en comparación con el precio de mercado.");
                System.out.println("La diferencia es de " + priceDifference + " USD. Ingrese un precio más cercano al precio de mercado.");
            } else {
                break; // El precio es aceptable
            }
        }

        if (userWallet.getFiatBalance() >= (purchasePrice * amount)) {
            // Crear una nueva orden de compra
            OrderBuy newBuyOrder = new OrderBuy(generateOrderId(), userId, new Date(), selectedCrypto, amount, purchasePrice, "NOREALIZADO");

            // Guardar la orden de compra antes de modificarla
            buyOrders.add(newBuyOrder);
            saveBuyOrdersToFile();

            boolean orderExecuted = false;
            for (OrderSale saleOrder : saleOrders) {

                // Verificación para asegurarse de que el usuario no está comprando su propia orden de venta
                if (saleOrder.getId_proprietary() == userId) {
                    System.out.println("No puedes comprar a tu propia orden de venta.");
                    continue;
                }

                if (saleOrder.getCrypto().getMarketCoin().equals(selectedCrypto.getMarketCoin())
                        && saleOrder.getAmount() == amount
                        && saleOrder.getStage().equals("NOREALIZADO")
                        && saleOrder.getPrice() <= purchasePrice) {

                    System.out.println("Orden de compra ejecutada al precio de: " + purchasePrice);

                    userWallet.setFiatBalance(userWallet.getFiatBalance() - (purchasePrice * amount));
                    addCryptocurrencyToWallet(userWallet, selectedCrypto, amount);

                    // Actualizar el estado de la orden
                    newBuyOrder.setStage("REALIZADO");
                    saleOrder.setStage("REALIZADO");

                    orderExecuted = true;
                    break;
                }
            }

            if (!orderExecuted) {
                System.out.println("Orden de compra agregada al libro de órdenes.");
            }

            // Guardar las órdenes actualizadas
            saveBuyOrdersToFile();
            saveSaleOrdersToFile(); // Guardar la actualización en las órdenes de venta
            saveWalletToFile(userWallet);
        } else {
            System.out.println("No tiene suficiente saldo en fiat para realizar la compra.");
        }
    }



    public static void salesOrder(int userId) {
        Scanner scanner = new Scanner(System.in);

        Wallet userWallet = findWalletByUserId(userId);

        if (userWallet == null) {
            System.out.println("No se encontró la Wallet para el usuario con ID: " + userId);
            return;
        }

        // Mostrar criptomonedas disponibles y sus precios actuales
        System.out.println("Seleccione la criptomoneda que desea vender:");
        for (int i = 0; i < cryptocurrencies.size(); i++) {
            System.out.println(i + ". " + cryptocurrencies.get(i));
        }

        int cryptoIndex = scanner.nextInt();
        Cryptocurrency selectedCrypto = cryptocurrencies.get(cryptoIndex);

        System.out.println("Ingrese la cantidad que desea vender:");
        int amount = scanner.nextInt();

        // Validar el precio de venta
        float sellPrice;
        while (true) {
            System.out.println("Ingrese el precio al que desea vender por unidad:");
            sellPrice = scanner.nextFloat();

            double marketPrice = selectedCrypto.getValue();
            double priceDifference = Math.abs(marketPrice - sellPrice);

            if (sellPrice < marketPrice && priceDifference > 300) {
                System.out.println("El precio de venta ingresado es demasiado bajo en comparación con el precio de mercado.");
                System.out.println("La diferencia es de " + priceDifference + " USD. Ingrese un precio más cercano al precio de mercado.");
            } else {
                break; // El precio es aceptable
            }
        }

        if (hasEnoughCryptocurrency(userWallet, selectedCrypto, amount)) {
            // Crear una nueva orden de venta
            OrderSale newSaleOrder = new OrderSale(generateOrderId(), userId, new Date(), selectedCrypto, amount, sellPrice, "NOREALIZADO");

            // Guardar la orden de venta antes de modificarla
            saleOrders.add(newSaleOrder);
            saveSaleOrdersToFile();

            boolean orderExecuted = false;
            for (OrderBuy buyOrder : buyOrders) {

                // Verificación para asegurarse de que el usuario no está vendiendo a su propia orden de compra
                if (buyOrder.getId_proprietary() == userId) {
                    System.out.println("No puedes vender a tu propia orden de compra.");
                    continue;
                }

                if (buyOrder.getCrypto().getMarketCoin().equals(selectedCrypto.getMarketCoin())
                        && buyOrder.getAmount() == amount
                        && buyOrder.getStage().equals("NOREALIZADO")
                        && buyOrder.getPrice() >= sellPrice) {

                    System.out.println("Orden de venta ejecutada al precio de: " + sellPrice);

                    userWallet.setFiatBalance(userWallet.getFiatBalance() + (sellPrice * amount));
                    removeCryptocurrencyFromWallet(userWallet, selectedCrypto, amount);

                    // Actualizar el estado de la orden
                    newSaleOrder.setStage("REALIZADO");
                    buyOrder.setStage("REALIZADO");

                    orderExecuted = true;
                    break;
                }
            }

            if (!orderExecuted) {
                System.out.println("Orden de venta agregada al libro de órdenes.");
            }

            // Guardar las órdenes actualizadas
            saveSaleOrdersToFile();
            saveBuyOrdersToFile(); // Guardar la actualización en las órdenes de compra
            saveWalletToFile(userWallet);
        } else {
            System.out.println("No tiene suficiente cantidad de criptomonedas para vender.");
        }
    }


    private static Cryptocurrency parseCryptocurrency(String line) {
        try {
            String[] parts = line.split(",");
            MarketCoin marketCoin = MarketCoin.valueOf(parts[0].trim());
            float value = Float.parseFloat(parts[1].trim());
            return new Cryptocurrency(marketCoin, value);
        } catch (Exception e) {
            System.out.println("Error al parsear criptomoneda: " + e.getMessage());
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
            System.out.println("Error al parsear orden de compra: " + e.getMessage());
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
            System.out.println("Error al parsear orden de venta: " + e.getMessage());
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
            System.out.println("Error al guardar la billetera: " + e.getMessage());
        }
    }
    private static String formatWallet(Wallet wallet) {
        StringBuilder sb = new StringBuilder();
        sb.append(wallet.getId()).append(",")
                .append(String.format("%.2f", wallet.getFiatBalance())).append(",");

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
            System.out.println("Error al leer el archivo de billeteras: " + e.getMessage());
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
            System.out.println("Error al parsear la billetera: " + e.getMessage());
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
                    System.out.println("No tiene suficiente criptomoneda para vender.");
                }
            }
        }
    }
    public static void historic(){

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
}
