package learn.position;

import learn.position.dto.Book;
import learn.position.dto.Currency;
import learn.position.dto.CurrencyPair;
import learn.position.dto.FXTransaction;
import learn.position.marketdata.MarketDataEntry;
import learn.position.marketdata.MarketDataListener;
import learn.position.marketdata.MarketDataSnapshot;
import learn.position.marketdata.PriceTier;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

public class FXPositionManager implements MarketDataListener {
    private final Map<UUID, FXTransaction> transactionHistory = new ConcurrentHashMap<>();
    private final Map<Book, Map<learn.position.dto.Currency, AtomicReference<Double>>> positionsByBook = new ConcurrentHashMap<>();
    private final learn.position.dto.Currency baseCurrency;
    private final Map<CurrencyPair, AtomicReference<MarketDataEntry>> marketDataEntries = new ConcurrentHashMap<>();
    private final List<PositionUpdateListener> positionUpdateListeners = new ArrayList<>();
    private final double deltaThreshold;

    public FXPositionManager(learn.position.dto.Currency baseCurrency, double deltaThreshold) {
        this.baseCurrency = baseCurrency;
        this.deltaThreshold = deltaThreshold;

        // Initialize positions for each book
        for (Book book : Book.values()) {
            positionsByBook.put(book, new ConcurrentHashMap<>());
        }
    }

    /**
     * Adds a listener to receive position updates.
     */
    public void addPositionUpdateListener(PositionUpdateListener listener) {
        positionUpdateListeners.add(listener);
    }

    /**
     * Notifies all listeners about a position update.
     */
    private void notifyPositionUpdate(Book book, learn.position.dto.Currency currency, double newPosition) {
        for (PositionUpdateListener listener : positionUpdateListeners) {
            listener.onPositionUpdate(book, currency, newPosition);
        }
    }

    /**
     * Adds a transaction to a specific book and updates positions.
     */
    public void addTransaction(Book book, FXTransaction transaction) {
        if (transaction == null) {
            throw new IllegalArgumentException("Transaction cannot be null.");
        }

        applyTransactionEffect(book, transaction);

        // Add the transaction to the history
        transactionHistory.put(transaction.getTransactionId(), transaction);
    }

    /**
     * Applies the effect of a transaction on a book's positions.
     */
    private void applyTransactionEffect(Book book, FXTransaction transaction) {
        CurrencyPair currencyPair = transaction.getCurrencyPair();
        learn.position.dto.Currency dealtCurrency = transaction.getDealtCurrency();
        double dealtAmount = transaction.getDealtAmount();
        double exchangeRate = transaction.getExchangeRate();

        // Determine the other currency in the pair
        learn.position.dto.Currency otherCurrency = currencyPair.getBaseCurrency().equals(dealtCurrency)
                ? currencyPair.getQuoteCurrency()
                : currencyPair.getBaseCurrency();

        // Compute the other amount based on the dealt currency
        double otherAmount = currencyPair.getQuoteCurrency().equals(dealtCurrency)
                ? dealtAmount / exchangeRate // If dealt currency is quote, invert the rate
                : dealtAmount * exchangeRate; // Otherwise, use the rate directly

        // Update the dealt currency position
        Map<learn.position.dto.Currency, AtomicReference<Double>> positions = positionsByBook.get(book);
        AtomicReference<Double> dealtPosition = positions.computeIfAbsent(dealtCurrency, k -> new AtomicReference<>(0.0));
        double newDealtPosition = dealtPosition.updateAndGet(current -> current + dealtAmount);
        notifyPositionUpdate(book, dealtCurrency, newDealtPosition);

        // Update the other currency position
        AtomicReference<Double> otherPosition = positions.computeIfAbsent(otherCurrency, k -> new AtomicReference<>(0.0));
        double newOtherPosition = otherPosition.updateAndGet(current -> current - otherAmount);
        notifyPositionUpdate(book, otherCurrency, newOtherPosition);

        // Propagate the transaction effect to parent books
        Book parent = book.getParent();
        while (parent != null) {
            Map<learn.position.dto.Currency, AtomicReference<Double>> parentPositions = positionsByBook.get(parent);
            AtomicReference<Double> parentDealtPosition = parentPositions.computeIfAbsent(dealtCurrency, k -> new AtomicReference<>(0.0));
            double newParentDealtPosition = parentDealtPosition.updateAndGet(current -> current + dealtAmount);
            notifyPositionUpdate(parent, dealtCurrency, newParentDealtPosition);

            AtomicReference<Double> parentOtherPosition = parentPositions.computeIfAbsent(otherCurrency, k -> new AtomicReference<>(0.0));
            double newParentOtherPosition = parentOtherPosition.updateAndGet(current -> current - otherAmount);
            notifyPositionUpdate(parent, otherCurrency, newParentOtherPosition);

            parent = parent.getParent();
        }
    }

    /**
     * Updates market data entries when a new market data snapshot is received.
     */
    @Override
    public void onMarketDataSnapshot(MarketDataSnapshot snapshot) {
        for (MarketDataEntry entry : snapshot.getEntries()) {
            CurrencyPair currencyPair = entry.getCurrencyPair();
            AtomicReference<MarketDataEntry> entryRef = marketDataEntries.computeIfAbsent(currencyPair, k -> new AtomicReference<>());
            MarketDataEntry previousEntry = entryRef.getAndSet(entry);

            // Skip if the market data hasn't changed significantly
            if (previousEntry != null && !isSignificantChange(previousEntry, entry)) {
                continue;
            }

            // Calculate position deltas for each book
            for (Book book : Book.values()) {
                Map<learn.position.dto.Currency, AtomicReference<Double>> positions = positionsByBook.get(book);
                for (Map.Entry<learn.position.dto.Currency, AtomicReference<Double>> positionEntry : positions.entrySet()) {
                    learn.position.dto.Currency currency = positionEntry.getKey();
                    double positionAmount = positionEntry.getValue().get();

                    if (!currency.equals(baseCurrency)) {
                        // Calculate the previous and new position values in the base currency
                        double previousRate = (previousEntry != null) ?
                                (positionAmount > 0 ? previousEntry.getBidPrice(positionAmount) : previousEntry.getAskPrice(positionAmount)) :
                                0.0;
                        double newRate = (positionAmount > 0) ? entry.getBidPrice(positionAmount) : entry.getAskPrice(positionAmount);

                        double previousValue = positionAmount / previousRate;
                        double newValue = positionAmount / newRate;

                        // Calculate the delta
                        double delta = Math.abs(newValue - previousValue);

                        // Notify listeners if the delta exceeds the threshold
                        if (delta > deltaThreshold) {
                            notifyPositionUpdate(book, currency, positionAmount);
                        }
                    }
                }
            }
        }
    }

    /**
     * Checks if the market data change is significant.
     */
    private boolean isSignificantChange(MarketDataEntry previousEntry, MarketDataEntry newEntry) {
        // Compare bid and ask prices for all tiers
        for (PriceTier prevTier : previousEntry.getBidTiers()) {
            for (PriceTier newTier : newEntry.getBidTiers()) {
                if (Math.abs(prevTier.getBidPrice() - newTier.getBidPrice()) > deltaThreshold) {
                    return true;
                }
            }
        }
        for (PriceTier prevTier : previousEntry.getAskTiers()) {
            for (PriceTier newTier : newEntry.getAskTiers()) {
                if (Math.abs(prevTier.getAskPrice() - newTier.getAskPrice()) > deltaThreshold) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Returns the net position for a currency in a specific book.
     */
    public double getNetPosition(Book book, learn.position.dto.Currency currency) {
        AtomicReference<Double> position = positionsByBook.get(book).get(currency);
        return (position != null) ? position.get() : 0.0;
    }

    /**
     * Prints all positions for a specific book and its children.
     */
    public void printPositions(Book book) {
        System.out.println("Positions for " + book + " Book:");
        Map<learn.position.dto.Currency, AtomicReference<Double>> positions = positionsByBook.get(book);
        for (Map.Entry<Currency, AtomicReference<Double>> entry : positions.entrySet()) {
            System.out.println(entry.getKey() + ": " + String.format("%.2f", entry.getValue().get()));
        }

        // Print positions for child books
        for (Book child : book.getChildren()) {
            printPositions(child);
        }
    }
}