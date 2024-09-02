package org.example.model;

import java.util.ArrayList;
public class Wallet {
    private int id;
    private double fiatBalance;
    private ArrayList<Cryptocurrency> listCrypto;

    public Wallet(int id, double fiatBalance, ArrayList<Cryptocurrency> listCrypto) {
        this.id = id;
        this.fiatBalance = fiatBalance;
        this.listCrypto = listCrypto;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getFiatBalance() {
        return fiatBalance;
    }

    public void setFiatBalance(double fiatBalance) {
        this.fiatBalance = fiatBalance;
    }

    public ArrayList<Cryptocurrency> getListCrypto() {
        return listCrypto;
    }

    public void setListCrypto(ArrayList<Cryptocurrency> listCrypto) {
        this.listCrypto = listCrypto;
    }

    @Override
    public String toString() {
        return "Wallet{" +
                "id=" + id +
                ", fiatBalance=" + fiatBalance +
                ", listCrypto=" + listCrypto +
                '}';
    }
}
