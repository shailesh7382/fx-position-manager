package learn.position.skew;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class FXSkewDetector {

    // Class to represent market data
    static class MarketData {
        LocalDateTime timestamp;
        double bidPrice;
        double askPrice;
        int bidSize;
        int askSize;
        double traditionalMidPrice;
        double derivedMidPrice; // Mid-price calculated using alternative methods
        double skew;

        public MarketData(LocalDateTime timestamp, double bidPrice, double askPrice, int bidSize, int askSize) {
            this.timestamp = timestamp;
            this.bidPrice = bidPrice;
            this.askPrice = askPrice;
            this.bidSize = bidSize;
            this.askSize = askSize;
            this.traditionalMidPrice = (bidPrice + askPrice) / 2;
            this.derivedMidPrice = calculateDerivedMidPrice(bidPrice, askPrice, bidSize, askSize);
            this.skew = calculateSkew(derivedMidPrice, bidPrice, askPrice);
        }

        // Calculate derived mid-price using volume-weighted average
        private double calculateDerivedMidPrice(double bidPrice, double askPrice, int bidSize, int askSize) {
            if (bidSize + askSize == 0) {
                return (bidPrice + askPrice) / 2; // Fallback to traditional mid-price
            }
            return (bidPrice * askSize + askPrice * bidSize) / (bidSize + askSize);
        }

        // Calculate skew as the difference between ask-mid and bid-mid spreads
        private double calculateSkew(double midPrice, double bidPrice, double askPrice) {
            return (askPrice - midPrice) - (midPrice - bidPrice);
        }

        @Override
        public String toString() {
            return String.format("%s | Traditional Mid: %.6f | Derived Mid: %.6f | Skew: %.6f",
                    timestamp, traditionalMidPrice, derivedMidPrice, skew);
        }
    }

    public static void main(String[] args) {
        // Simulate FX market data
        List<MarketData> marketDataList = simulateMarketData(1000);

        // Analyze skew
        double skewThreshold = 0.001; // Threshold for significant skew
        List<MarketData> skewOpportunities = new ArrayList<>();

        for (MarketData data : marketDataList) {
            if (Math.abs(data.skew) > skewThreshold) {
                skewOpportunities.add(data);
            }
        }

        // Print skew opportunities
        System.out.println("Skew Opportunities:");
        for (MarketData opportunity : skewOpportunities) {
            System.out.println(opportunity);
        }
    }

    // Simulate FX market data
    private static List<MarketData> simulateMarketData(int numRecords) {
        List<MarketData> marketDataList = new ArrayList<>();
        Random random = new Random();
        LocalDateTime startTime = LocalDateTime.now();

        double bidPrice = 1.1000;
        double askPrice = 1.1005;

        for (int i = 0; i < numRecords; i++) {
            // Simulate small price changes
            bidPrice += random.nextGaussian() * 0.0001;
            askPrice += random.nextGaussian() * 0.0001;

            // Simulate bid and ask sizes
            int bidSize = random.nextInt(100) + 1;
            int askSize = random.nextInt(100) + 1;

            // Create market data record
            MarketData data = new MarketData(
                    startTime.plusSeconds(i),
                    bidPrice,
                    askPrice,
                    bidSize,
                    askSize
            );
            marketDataList.add(data);
        }

        return marketDataList;
    }
}