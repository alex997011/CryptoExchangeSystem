package org.example.model;

import org.example.controller.TradeController;
import org.example.controller.UserController;

import java.security.PrivateKey;
import java.util.Date;

public class OrderSale extends Order{

    public OrderSale(int id, int id_proprietary, Date date, Cryptocurrency crypto, int amount, float price, String stage) {
        super(id, id_proprietary, date, crypto, amount, price, stage);
    }

    public static void execute() {
        User currentUser = UserController.getCurrentUser();
        TradeController.salesOrder(currentUser.getId());
    }
}
