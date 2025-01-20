package learn.position.risk;

import learn.position.dto.Book;
import learn.position.dto.Currency;
import learn.position.PositionUpdateListener;

import java.util.HashMap;
import java.util.Map;

public class HFTRiskManager implements PositionUpdateListener {
    private final Map<Currency, Double> exposureLimits = new HashMap<>();

    public HFTRiskManager() {
        exposureLimits.put(new Currency("EUR"), 1_000_000.0);
        exposureLimits.put(new Currency("USD"), 2_000_000.0);
    }

    @Override
    public void onPositionUpdate(Book book, Currency currency, double newPosition) {
        double limit = exposureLimits.getOrDefault(currency, Double.MAX_VALUE);
        if (Math.abs(newPosition) > limit) {
            System.out.println("Exposure limit exceeded for " + currency + ": " + newPosition);
            // Trigger corrective action (e.g., stop trading, hedge positions)
        }
    }
}