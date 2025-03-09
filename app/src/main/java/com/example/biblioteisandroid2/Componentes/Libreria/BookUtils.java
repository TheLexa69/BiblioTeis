package com.example.biblioteisandroid2.Componentes.Libreria;

import android.util.Log;

import com.example.biblioteisandroid2.API.models.Book;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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

    public static String transformarFecha(String fechaOriginal) {
        if (fechaOriginal == null || fechaOriginal.isEmpty()) {
            return null;
        }
        try {
            DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
            DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            LocalDateTime dateTime = LocalDateTime.parse(fechaOriginal, inputFormatter);
            return dateTime.toLocalDate().format(outputFormatter);
        } catch (Exception e) {
            Log.e("InfoLibro", "Error al transformar la fecha: " + fechaOriginal, e);
            return null;
        }
    }
}