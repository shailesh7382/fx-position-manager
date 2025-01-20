package learn.position.disruptor;

import learn.position.dto.Book;
import learn.position.dto.FXTransaction;

public class TransactionEvent {
    private FXTransaction transaction;
    private Book book;

    // Getters and setters

    public FXTransaction getTransaction() {
        return transaction;
    }

    public void setTransaction(FXTransaction transaction) {
        this.transaction = transaction;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }
}
