package learn.position;

import learn.position.dto.Book;
import learn.position.dto.Currency;

public interface PositionUpdateListener {
    void onPositionUpdate(Book book, Currency currency, double newPosition);
}