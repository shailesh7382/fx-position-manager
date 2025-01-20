package learn.position.disruptor;

import learn.position.marketdata.MarketDataSnapshot;

public class MarketDataEvent {
    private MarketDataSnapshot snapshot;

    // Getters and setters

    public MarketDataSnapshot getSnapshot() {
        return snapshot;
    }

    public void setSnapshot(MarketDataSnapshot snapshot) {
        this.snapshot = snapshot;
    }
}