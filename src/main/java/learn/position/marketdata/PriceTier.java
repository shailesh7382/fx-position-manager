package learn.position.marketdata;

public class PriceTier {
    private double minQuantity; // Minimum quantity for this tier
    private double maxQuantity; // Maximum quantity for this tier
    private double bidPrice;    // Bid price for this tier
    private double askPrice;    // Ask price for this tier

    public PriceTier(double minQuantity, double maxQuantity, double bidPrice, double askPrice) {
        this.minQuantity = minQuantity;
        this.maxQuantity = maxQuantity;
        this.bidPrice = bidPrice;
        this.askPrice = askPrice;
    }

    public double getMinQuantity() {
        return minQuantity;
    }

    public double getMaxQuantity() {
        return maxQuantity;
    }

    public double getBidPrice() {
        return bidPrice;
    }

    public double getAskPrice() {
        return askPrice;
    }

    @Override
    public String toString() {
        return String.format("Qty: %.2f-%.2f, Bid=%.4f, Ask=%.4f",
                minQuantity, maxQuantity, bidPrice, askPrice);
    }
}