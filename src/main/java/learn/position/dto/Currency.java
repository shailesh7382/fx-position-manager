package learn.position.dto;

public class Currency {
    private String code; // e.g., "USD", "EUR", "GBP"

    public Currency(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    @Override
    public String toString() {
        return code;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Currency currency = (Currency) obj;
        return code.equals(currency.code);
    }

    @Override
    public int hashCode() {
        return code.hashCode();
    }
}