package learn.position;

import learn.position.dto.Book;
import learn.position.dto.Currency;

public class PositionUpdateLogger implements PositionUpdateListener {
    @Override
    public void onPositionUpdate(Book book, Currency currency, double newPosition) {
        System.out.println("Significant Position Update - Book: " + book + ", Currency: " + currency + ", New Position: " + newPosition);
    }
}