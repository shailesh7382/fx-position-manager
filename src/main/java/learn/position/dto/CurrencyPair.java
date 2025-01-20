package learn.position.dto;

public class CurrencyPair {
    private Currency baseCurrency; // Base currency (e.g., EUR in EUR/USD)
    private Currency quoteCurrency; // Quote currency (e.g., USD in EUR/USD)

    public CurrencyPair(Currency baseCurrency, Currency quoteCurrency) {
        this.baseCurrency = baseCurrency;
        this.quoteCurrency = quoteCurrency;
    }

    public Currency getBaseCurrency() {
        return baseCurrency;
    }

    public Currency getQuoteCurrency() {
        return quoteCurrency;
    }

    @Override
    public String toString() {
        return baseCurrency + "/" + quoteCurrency;
    }
}