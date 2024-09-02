package org.example.view;

import org.example.controller.TradeController;
import org.example.controller.UserController;
import org.example.controller.WalletController;
import org.example.model.User;

import static org.example.view.UserView.MenuUser;

import java.util.Scanner;
public class WalletView {
    public static Scanner scanner = new Scanner(System.in);
    public static void MenuDeposit(){
        WalletController.Deposit();
    }
    public static void MenuFunds(){
        WalletController.funds();
    }
    public static void MenuCrypto(){
        WalletController.Crypofunds();
    }
    public static void MenuUserWallet(){
        User currentUser = UserController.getCurrentUser();
        boolean start= true;
        while(start){
            System.out.println("""
                    === Bienvenido al sistema===\s
                    1. Depositar dinero\s
                    2. Fondos de la cuenta\s
                    3. Cryptos de la cuenta\s
                    4. Regresar al menu principal\s
                    5. Historial \s
                    6. Cerrar sesion\s
                    Seleccione una opcion:"""
            );
            String option = scanner.nextLine();
            switch (option) {
                case "1" -> MenuDeposit();
                case "2" -> MenuFunds();
                case "3" -> MenuCrypto();
                case "4" -> UserView.MenuProgram();
                case "5" -> TradeController.historic(currentUser.getId());
                case "6" -> UserView.MenuUser();
                default -> System.out.println("Opcion no valida. Intentelo de nuevo");
            }
            System.out.println();
        }
        scanner.close();
    }
}
