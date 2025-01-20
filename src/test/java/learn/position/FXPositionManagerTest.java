package learn.position;

import learn.position.dto.Book;
import learn.position.dto.Currency;
import learn.position.dto.CurrencyPair;
import learn.position.dto.FXTransaction;
import learn.position.marketdata.MarketDataEntry;
import learn.position.marketdata.MarketDataSnapshot;
import learn.position.marketdata.PriceTier;

import java.time.LocalDateTime;

public class FXPositionManagerTest {
    public static void main(String[] args) {
        Currency usd = new Currency("USD");
        Currency eur = new Currency("EUR");
        Currency gbp = new Currency("GBP");

        double deltaThreshold = 1000.0; // Threshold for significant position changes
        FXPositionManager manager = new FXPositionManager(usd, deltaThreshold);

        // Register a position update listener
        PositionUpdateLogger logger = new PositionUpdateLogger();
        manager.addPositionUpdateListener(logger);

        // Define currency pairs
        CurrencyPair eurusd = new CurrencyPair(eur, usd);
        CurrencyPair gbpusd = new CurrencyPair(gbp, usd);

        // Transaction 1: Buy 100,000 EUR @ 1.18 USD/EUR in Warehouse book
        FXTransaction transaction1 = new FXTransaction(eurusd, eur, 100000, 1.18);
        manager.addTransaction(Book.WAREHOUSE, transaction1);

        // Transaction 2: Buy 50,000 GBP @ 1.35 USD/GBP in Manual book
        FXTransaction transaction2 = new FXTransaction(gbpusd, gbp, 50000, 1.35);
        manager.addTransaction(Book.MANUAL, transaction2);

        // Create market data entries with price tiers
        MarketDataEntry eurusdEntry = new MarketDataEntry(eurusd, LocalDateTime.now());
        eurusdEntry.addBidTier(new PriceTier(0, 50000, 1.19, 1.20));
        eurusdEntry.addBidTier(new PriceTier(50000, 100000, 1.18, 1.19));
        eurusdEntry.addAskTier(new PriceTier(0, 50000, 1.21, 1.22));
        eurusdEntry.addAskTier(new PriceTier(50000, 100000, 1.20, 1.21));

        MarketDataEntry gbpusdEntry = new MarketDataEntry(gbpusd, LocalDateTime.now());
        gbpusdEntry.addBidTier(new PriceTier(0, 25000, 1.34, 1.35));
        gbpusdEntry.addBidTier(new PriceTier(25000, 50000, 1.33, 1.34));
        gbpusdEntry.addAskTier(new PriceTier(0, 25000, 1.36, 1.37));
        gbpusdEntry.addAskTier(new PriceTier(25000, 50000, 1.35, 1.36));

        // Create market data snapshots
        MarketDataSnapshot snapshot = new MarketDataSnapshot();
        snapshot.addEntry(eurusdEntry);
        snapshot.addEntry(gbpusdEntry);

        // Publish the market data snapshot
        manager.onMarketDataSnapshot(snapshot);

        // Print all positions and transaction history
        System.out.println("After adding transactions:");
        manager.printAllPositions();
        manager.printTransactionHistory();
    }
}