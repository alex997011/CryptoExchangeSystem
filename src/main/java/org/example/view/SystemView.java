package org.example.view;

import org.example.view.*;
import org.example.controller.*;
public class SystemView {
    public static void initSystem(){
        TradeController.StartTradeSystem();
        UserController.StartUserSystem();
        WalletController.StartWalletSystem();
        UserView.MenuUser();
    }
    public static void reloadSystem(){
        WalletController.StartWalletSystem();
        TradeController.StartTradeSystem();
    }
}
