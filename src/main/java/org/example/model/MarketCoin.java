package org.example.model;

public enum MarketCoin {
    BTC("Bitcoin"),
    ETH("Ethereum"),
    USDT("Tether"),
    BNB("Binance Coin"),
    SOL("Solana"),
    XPR("XRP"),
    DOGE("Dogecoin"),
    TRX("TRON"),
    TON("Toncoin");

    private final String fullName;


    MarketCoin(String fullName) {
        this.fullName = fullName;
    }


    public String getFullName() {
        return fullName;
    }


    public static MarketCoin fromFullName(String fullName) {
        for (MarketCoin coin : MarketCoin.values()) {
            if (coin.getFullName().equalsIgnoreCase(fullName.trim())) {
                return coin;
            }
        }
        throw new IllegalArgumentException("No enum constant for fullName: " + fullName);
    }

    @Override
    public String toString() {
        return this.name() + " (" + fullName + ")";
    }
}