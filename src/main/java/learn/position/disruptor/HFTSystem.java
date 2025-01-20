package learn.position.disruptor;

import learn.position.dto.Book;
import learn.position.FXPositionManager;
import learn.position.dto.FXTransaction;
import learn.position.marketdata.MarketDataSnapshot;

public class HFTSystem {
    private final PositionManagerDisruptor disruptor;

    public HFTSystem(FXPositionManager positionManager) {
        this.disruptor = new PositionManagerDisruptor(positionManager);
    }

    public void executeTrade(FXTransaction transaction, Book book) {
        disruptor.publishTransaction(transaction, book);
    }

    public void updateMarketData(MarketDataSnapshot snapshot) {
        disruptor.publishMarketData(snapshot);
    }
}