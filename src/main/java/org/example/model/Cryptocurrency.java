package org.example.model;

public class Cryptocurrency {
    private MarketCoin marketCoin;
    private double value;

    public Cryptocurrency(MarketCoin marketCoin, double value) {
        this.marketCoin = marketCoin;
        this.value = value;
    }

    public MarketCoin getMarketCoin() {
        return marketCoin;
    }

    public void setMarketCoin(MarketCoin marketCoin) {
        this.marketCoin = marketCoin;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "Cryptocurrency{" +
                "marketCoin=" + marketCoin +
                ", value=" + value +
                '}';
    }
}
