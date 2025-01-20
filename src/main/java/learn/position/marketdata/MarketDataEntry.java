package learn.position.marketdata;

import learn.position.dto.CurrencyPair;

import java.time.LocalDateTime;
import java.util.*;

public class MarketDataEntry {
    private CurrencyPair currencyPair; // Currency pair (e.g., EUR/USD)
    private List<PriceTier> bidTiers;  // Bid price tiers
    private List<PriceTier> askTiers;  // Ask price tiers
    private LocalDateTime timestamp;   // Timestamp of the update

    public MarketDataEntry(CurrencyPair currencyPair, LocalDateTime timestamp) {
        this.currencyPair = currencyPair;
        this.bidTiers = new ArrayList<>();
        this.askTiers = new ArrayList<>();
        this.timestamp = timestamp;
    }

    public CurrencyPair getCurrencyPair() {
        return currencyPair;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    /**
     * Adds a bid price tier.
     */
    public void addBidTier(PriceTier bidTier) {
        bidTiers.add(bidTier);
    }

    /**
     * Adds an ask price tier.
     */
    public void addAskTier(PriceTier askTier) {
        askTiers.add(askTier);
    }

    /**
     * Returns the bid price for a given quantity.
     */
    public double getBidPrice(double quantity) {
        for (PriceTier tier : bidTiers) {
            if (quantity >= tier.getMinQuantity() && quantity <= tier.getMaxQuantity()) {
                return tier.getBidPrice();
            }
        }
        throw new IllegalArgumentException("No bid price tier found for quantity: " + quantity);
    }

    /**
     * Returns the ask price for a given quantity.
     */
    public double getAskPrice(double quantity) {
        for (PriceTier tier : askTiers) {
            if (quantity >= tier.getMinQuantity() && quantity <= tier.getMaxQuantity()) {
                return tier.getAskPrice();
            }
        }
        throw new IllegalArgumentException("No ask price tier found for quantity: " + quantity);
    }

    public List<PriceTier> getAskTiers() {
        return askTiers;
    }

    public List<PriceTier> getBidTiers() {
        return bidTiers;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("MarketDataEntry for ").append(currencyPair).append(":\n");
        sb.append("Bid Tiers:\n");
        for (PriceTier tier : bidTiers) {
            sb.append(tier).append("\n");
        }
        sb.append("Ask Tiers:\n");
        for (PriceTier tier : askTiers) {
            sb.append(tier).append("\n");
        }
        return sb.toString();
    }
}