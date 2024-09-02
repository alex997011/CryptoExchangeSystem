package org.example.view;

import org.example.controller.*;
import org.example.model.User;

import java.sql.SQLOutput;
import java.util.Scanner;
public class UserView {
    public static Scanner scanner = new Scanner(System.in);

    public static void MenuRegister() {
        System.out.println("=== Menú de Registro de Usuario ===");

        Integer id = UserController.generate_id();

        String username;
        while (true) {
            System.out.println("Ingrese el nombre de usuario:");
            username = scanner.nextLine();
            if (username.isEmpty()) {
                System.out.println("El nombre de usuario no puede estar vacío. Inténtelo de nuevo.");
            } else {
                break;
            }
        }

        String password;
        while (true) {
            System.out.println("Ingrese la contraseña (mínimo 8 caracteres, al menos una letra mayúscula, una minúscula y un número):");
            password = scanner.nextLine();
            if (password.isEmpty() || !UserController.isPasswordSecure(password)) {
                System.out.println("Contraseña no segura. Asegúrese de que tenga al menos 8 caracteres, una letra mayúscula, una minúscula y un número.");
            } else {
                break;
            }
        }

        String mail;
        while (true) {
            System.out.println("Ingrese el email:");
            mail = scanner.nextLine();
            if (mail.isEmpty()) {
                System.out.println("El email no puede estar vacío. Inténtelo de nuevo.");
            } else {
                break;
            }
        }

        String name;
        while (true) {
            System.out.println("Ingrese el nombre:");
            name = scanner.nextLine();
            if (name.isEmpty()) {
                System.out.println("El nombre no puede estar vacío. Inténtelo de nuevo.");
            } else {
                break;
            }
        }

        // Validar apellido
        String lastname;
        while (true) {
            System.out.println("Ingrese el apellido:");
            lastname = scanner.nextLine();
            if (lastname.isEmpty()) {
                System.out.println("El apellido no puede estar vacío. Inténtelo de nuevo.");
            } else {
                break;
            }
        }

        if (UserController.registerUser(id, username, password,mail, name, lastname)) {
            System.out.println("Usuario registrado con éxito.");
        } else {
            System.out.println("Error al registrar el usuario. Inténtelo de nuevo.");
        }
    }
    public static void MenuLogin() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("=== Menú de Inicio de Sesión ===");

        String username;
        String password;
        boolean success = false;
        int attempts = 0;

        while (attempts < 3 && !success) {

            while (true) {
                System.out.println("Ingrese su nombre de usuario:");
                username = scanner.nextLine();
                if (username.isEmpty()) {
                    System.out.println("El nombre de usuario no puede estar vacío. Inténtelo de nuevo.");
                } else {
                    break;
                }
            }

            // Validar contraseña
            while (true) {
                System.out.println("Ingrese su contraseña:");
                password = scanner.nextLine();
                if (password.isEmpty()) {
                    System.out.println("La contraseña no puede estar vacía. Inténtelo de nuevo.");
                } else {
                    break;
                }
            }


            success = UserController.loginUser(username, password);
            if (success) {
                System.out.println("Inicio de sesión exitoso.");
                MenuProgram();
            } else {
                attempts++;
                System.out.println("Nombre de usuario o contraseña incorrectos. Intento " + attempts + " de 3.");
            }

            if (attempts == 3 && !success) {
                System.out.println("Ha alcanzado el número máximo de intentos. Por favor, intente más tarde.");
            }
        }
    }

    public static void MenuUser(){
        boolean start= true;
        while(start){
            System.out.println("""
                    === Menu del sistema de Usuario ===\s
                    1. Iniciar Sesion\s
                    2. Registrase\s
                    3.salir\s
                    Seleccione una opcion:"""
            );
            String option = scanner.nextLine();
            switch (option) {
                case "1" -> MenuLogin();
                case "2" -> MenuRegister();
                case "3" -> {
                    System.out.println("Saliendo del sistema...");
                    start = false;
                }
                default -> System.out.println("Opcion no valida. Intentelo de nuevo");
            }
            System.out.println();
        }
        scanner.close();
    }
    public static void  MenuProgram(){
        boolean start= true;
        while(start){
            System.out.println("""
                    === Bienvenido al sistema===\s
                    1. Informacion de su billeteradigital\s
                    2. Realizar Pedido de compra\s
                    3. Realizar Perdido de venta\s
                    4. Cerrar sesion\s
                    Seleccione una opcion:"""
            );
            String option = scanner.nextLine();
            switch (option) {
                case "1" -> WalletView.MenuUserWallet();
                case "2" -> TradeView.menuPurchaseOrder();
                case "3" -> TradeView.menuSalesOrder();
                case "4" -> MenuUser();
                default -> System.out.println("Opcion no valida. Intentelo de nuevo");
            }
            System.out.println();
        }
        scanner.close();
    }
    public static void showCurrentUserInfo() {
        User current = UserController.getCurrentUser();
        if (current != null) {
            System.out.println("Usuario actual: " + current.toString());
        } else {
            System.out.println("No hay usuario autenticado.");
        }
    }
}
