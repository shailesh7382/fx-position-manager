package learn.position.disruptor;

import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.dsl.ProducerType;
import learn.position.dto.Book;
import learn.position.FXPositionManager;
import learn.position.dto.FXTransaction;
import learn.position.marketdata.MarketDataSnapshot;

import java.util.concurrent.Executors;

public class PositionManagerDisruptor {
    private final Disruptor<TransactionEvent> transactionDisruptor;
    private final Disruptor<MarketDataEvent> marketDataDisruptor;
    private final FXPositionManager positionManager;

    public PositionManagerDisruptor(FXPositionManager positionManager) {
        this.positionManager = positionManager;

        // Initialize transaction disruptor
        this.transactionDisruptor = new Disruptor<>(
                TransactionEvent::new,
                1024, // Ring buffer size
                Executors.defaultThreadFactory(),
                ProducerType.MULTI, // Multiple producers
                new BlockingWaitStrategy() // Wait strategy
        );

        // Initialize market data disruptor
        this.marketDataDisruptor = new Disruptor<>(
                MarketDataEvent::new,
                1024, // Ring buffer size
                Executors.defaultThreadFactory(),
                ProducerType.MULTI, // Multiple producers
                new BlockingWaitStrategy() // Wait strategy
        );

        // Set up event handlers
        transactionDisruptor.handleEventsWith(this::handleTransactionEvent);
        marketDataDisruptor.handleEventsWith(this::handleMarketDataEvent);

        // Start disruptors
        transactionDisruptor.start();
        marketDataDisruptor.start();
    }

    private void handleTransactionEvent(TransactionEvent event, long sequence, boolean endOfBatch) {
        positionManager.addTransaction(event.getBook(), event.getTransaction());
    }

    private void handleMarketDataEvent(MarketDataEvent event, long sequence, boolean endOfBatch) {
        positionManager.onMarketDataSnapshot(event.getSnapshot());
    }

    public void publishTransaction(FXTransaction transaction, Book book) {
        RingBuffer<TransactionEvent> ringBuffer = transactionDisruptor.getRingBuffer();
        long sequence = ringBuffer.next();
        try {
            TransactionEvent event = ringBuffer.get(sequence);
            event.setTransaction(transaction);
            event.setBook(book);
        } finally {
            ringBuffer.publish(sequence);
        }
    }

    public void publishMarketData(MarketDataSnapshot snapshot) {
        RingBuffer<MarketDataEvent> ringBuffer = marketDataDisruptor.getRingBuffer();
        long sequence = ringBuffer.next();
        try {
            MarketDataEvent event = ringBuffer.get(sequence);
            event.setSnapshot(snapshot);
        } finally {
            ringBuffer.publish(sequence);
        }
    }
}