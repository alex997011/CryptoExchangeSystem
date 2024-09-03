package org.example.view;

import org.example.controller.TradeController;
import org.example.controller.UserController;
import org.example.controller.WalletController;
import org.example.model.*;

import java.util.ArrayList;

public class SystemView {
    public static void initSystem(){
        TradeController.StartTradeSystem();
        UserController.StartUserSystem();
        WalletController.StartWalletSystem();
        UserView.menuUser();

    }
    public static void reloadSystem(){
        ArrayList<User> listUsers = UserController.getUsers();
        listUsers.clear();
        ArrayList<Wallet> listWallets = WalletController.getWallets();
        listWallets.clear();
        ArrayList<Cryptocurrency> listCrypto = TradeController.getCryptocurrencies();
        ArrayList<OrderBuy> listaOrderBuy = TradeController.getBuyOrders();
        ArrayList<OrderSale> listaOrderSale = TradeController.getSaleOrders();
        listCrypto.clear();
        listaOrderBuy.clear();
        listaOrderSale.clear();
        WalletController.StartWalletSystem();
        TradeController.StartTradeSystem();
        UserController.StartUserSystem();
    }

}
