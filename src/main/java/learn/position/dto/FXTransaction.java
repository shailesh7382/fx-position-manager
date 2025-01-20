package learn.position.dto;

import java.util.UUID;

public class FXTransaction {
    private UUID transactionId; // Unique identifier for the transaction
    private CurrencyPair currencyPair; // Currency pair (e.g., EUR/USD)
    private Currency dealtCurrency; // Currency being bought or sold
    private double dealtAmount; // Amount of the dealt currency
    private double exchangeRate; // Exchange rate (quote currency per base currency)

    public FXTransaction(CurrencyPair currencyPair, Currency dealtCurrency, double dealtAmount, double exchangeRate) {
        if (dealtAmount == 0 || exchangeRate <= 0) {
            throw new IllegalArgumentException("Dealt amount cannot be zero, and exchange rate must be positive.");
        }
        if (!dealtCurrency.equals(currencyPair.getBaseCurrency()) && 
            !dealtCurrency.equals(currencyPair.getQuoteCurrency())) {
            throw new IllegalArgumentException("Dealt currency must be part of the currency pair.");
        }

        this.transactionId = UUID.randomUUID(); // Generate a unique ID
        this.currencyPair = currencyPair;
        this.dealtCurrency = dealtCurrency;
        this.dealtAmount = dealtAmount;
        this.exchangeRate = exchangeRate;
    }

    public UUID getTransactionId() {
        return transactionId;
    }

    public CurrencyPair getCurrencyPair() {
        return currencyPair;
    }

    public Currency getDealtCurrency() {
        return dealtCurrency;
    }

    public double getDealtAmount() {
        return dealtAmount;
    }

    public double getExchangeRate() {
        return exchangeRate;
    }

    @Override
    public String toString() {
        return String.format("Transaction %s: Dealt %s %.2f @ %.4f %s",
                transactionId, dealtCurrency, dealtAmount, exchangeRate, currencyPair);
    }
}