package com.example.biblioteisandroid2.Componentes.Libreria;

import com.example.biblioteisandroid2.API.models.Book;

import java.util.*;

public class BookUtils {
    public static List<Book> groupBooksByISBN(List<Book> books) {
        Map<String, Book> bookMap = new HashMap<>();

        for (Book book : books) {
            String isbn = book.getIsbn();
            if (bookMap.containsKey(isbn)) {
                // Ya hay un libro con este ISBN, se actualizan las existencias
                Book existingBook = bookMap.get(isbn);
                existingBook.setAvailable(existingBook.isAvailable() || book.isAvailable());
            } else {
                bookMap.put(isbn, book);
            }
        }
        return new ArrayList<>(bookMap.values());
    }
}