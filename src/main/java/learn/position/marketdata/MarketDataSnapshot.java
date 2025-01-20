package learn.position.marketdata;

import java.util.*;

public class MarketDataSnapshot {
    private List<MarketDataEntry> entries; // List of market data entries

    public MarketDataSnapshot() {
        this.entries = new ArrayList<>();
    }

    public void addEntry(MarketDataEntry entry) {
        entries.add(entry);
    }

    public List<MarketDataEntry> getEntries() {
        return entries;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("MarketDataSnapshot:\n");
        for (MarketDataEntry entry : entries) {
            sb.append(entry).append("\n");
        }
        return sb.toString();
    }
}