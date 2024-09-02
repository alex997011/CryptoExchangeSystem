package org.example.model;

import org.example.controller.TradeController;
import org.example.controller.UserController;

import java.util.Date;

public class OrderBuy extends Order{

    public OrderBuy(int id, int id_proprietary, Date date, Cryptocurrency crypto, int amount, float price, String stage) {
        super(id, id_proprietary, date, crypto, amount, price, stage);
    }


    public static void execute() {
        User currentUser = UserController.getCurrentUser();
        TradeController.purchaseOrder(currentUser.getId());
    }
}
