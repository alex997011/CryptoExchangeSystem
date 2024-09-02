package org.example.model;

import java.util.Date;

public abstract class Order {
    private int id;
    private int id_proprietary;
    private Date date;
    private Cryptocurrency crypto;
    private int amount;
    private float price;
    private String stage;

    public Order(int id, int id_proprietary, Date date, Cryptocurrency crypto, int amount, float price, String stage) {
        this.id = id;
        this.id_proprietary = id_proprietary;
        this.date = date;
        this.crypto = crypto;
        this.amount = amount;
        this.price = price;
        this.stage = stage;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId_proprietary() {
        return id_proprietary;
    }

    public void setId_proprietary(int id_proprietary) {
        this.id_proprietary = id_proprietary;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Cryptocurrency getCrypto() {
        return crypto;
    }

    public void setCrypto(Cryptocurrency crypto) {
        this.crypto = crypto;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public String getStage() {
        return stage;
    }

    public void setStage(String stage) {
        this.stage = stage;
    }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", id_proprietary=" + id_proprietary +
                ", date=" + date +
                ", crypto=" + crypto +
                ", amount=" + amount +
                ", price=" + price +
                ", stage='" + stage + '\'' +
                '}';
    }

    public static void execute() {
    }
}
