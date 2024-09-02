package org.example.view;

import org.example.controller.TradeController;
import org.example.model.OrderBuy;
import org.example.model.OrderSale;

public class TradeView {
    public static void menuPurchaseOrder(){
        OrderBuy.execute();
    }
    public static void menuSalesOrder(){
        OrderSale.execute();
    }
}
