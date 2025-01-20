package learn.position.marketdata;

import java.util.*;

public class MarketDataProvider {
    private List<MarketDataListener> listeners = new ArrayList<>();

    /**
     * Registers a listener to receive market data updates.
     */
    public void addListener(MarketDataListener listener) {
        listeners.add(listener);
    }

    /**
     * Publishes a market data snapshot to all listeners.
     */
    public void publishMarketDataSnapshot(MarketDataSnapshot snapshot) {
        for (MarketDataListener listener : listeners) {
            listener.onMarketDataSnapshot(snapshot);
        }
    }
}