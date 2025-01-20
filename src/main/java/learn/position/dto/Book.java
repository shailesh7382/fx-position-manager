package learn.position.dto;

import java.util.*;

public enum Book {
    ECOMM(null),          // Parent: None
    WAREHOUSE(ECOMM),     // Parent: ECOMM
    AUTOHEDGED(ECOMM),    // Parent: ECOMM
    VOICE(null),          // Parent: None
    MANUAL(VOICE),        // Parent: VOICE
    NDF(VOICE),           // Parent: VOICE
    BULLION(VOICE);       // Parent: VOICE

    private final Book parent;

    Book(Book parent) {
        this.parent = parent;
    }

    public Book getParent() {
        return parent;
    }

    /**
     * Returns all child books for a given book.
     */
    public List<Book> getChildren() {
        List<Book> children = new ArrayList<>();
        for (Book book : Book.values()) {
            if (book.getParent() == this) {
                children.add(book);
            }
        }
        return children;
    }

    @Override
    public String toString() {
        return this.name().charAt(0) + this.name().substring(1).toLowerCase();
    }
}