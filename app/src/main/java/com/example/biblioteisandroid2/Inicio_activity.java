package com.example.biblioteisandroid2;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.biblioteisandroid2.API.models.Book;
import com.example.biblioteisandroid2.API.repository.BookRepository;
import com.example.biblioteisandroid2.Componentes.Libreria.BookAdapter;
import com.example.biblioteisandroid2.Componentes.Libreria.Libreria;
import com.example.biblioteisandroid2.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Inicio_activity extends AppCompatActivity {

    private BookRepository bookRepository;
    private RecyclerView recyclerViewRecommended;
    private BookAdapter bookAdapter;
    private List<Book> bookList; // Añadido para mantener la lista de libros

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio);

        recyclerViewRecommended = findViewById(R.id.recyclerViewRecommended);
        recyclerViewRecommended.setLayoutManager(new LinearLayoutManager(this));

        setupButtonLibreria();

        bookList = new ArrayList<>();

        // Hardcodear tres libros
        bookList.add(createBook(1, "El Quijote", "Miguel de Cervantes"));
        bookList.add(createBook(2, "Cien años de soledad", "Gabriel García Márquez"));
        bookList.add(createBook(3, "El Principito", "Antoine de Saint-Exupéry"));

        bookAdapter = new BookAdapter(this, bookList);
        recyclerViewRecommended.setAdapter(bookAdapter);

        // No llamar a loadRecentBooks ya que los datos son hardcodeados
    }

    // Método auxiliar para crear libros hardcodeados
    private Book createBook(int id, String title, String author) {
        Book book = new Book();
        book.setId(id);
        book.setTitle(title);
        book.setAuthor(author);
        book.setIsbn("123-4567890123"); // Valores de ejemplo
        book.setPublishedDate("2025-03-05");
        book.setAvailable(true);
        return book;
    }


    private void loadRecentBooks() {
        bookRepository.getBooks(new BookRepository.ApiCallback<List<Book>>() {
            @Override
            public void onSuccess(List<Book> books) {
                if (books != null && !books.isEmpty()) {
                    // Depuración: verificar que se obtuvieron libros
                    Log.d("Inicio_activity", "Libros obtenidos: " + books.size());

                    // Ordenar los libros por ID en orden descendente
                    Collections.sort(books, new Comparator<Book>() {
                        @Override
                        public int compare(Book o1, Book o2) {
                            return Integer.compare(o2.getId(), o1.getId());
                        }
                    });

                    // Tomar los tres últimos libros después del ordenamiento
                    List<Book> recentBooks = books.subList(0, Math.min(3, books.size()));

                    // Limpiar la lista actual y añadir los nuevos libros
                    bookList.clear();
                    bookList.addAll(recentBooks);

                    // Notificar al adaptador del cambio
                    bookAdapter.notifyDataSetChanged();
                } else {
                    // Si la lista está vacía o nula
                    Log.d("Inicio_activity", "La lista de libros está vacía o nula.");
                }
            }

            @Override
            public void onFailure(Throwable t) {
                // Imprimir errores en caso de fallo de la API
                Log.e("Inicio_activity", "Error al obtener los libros", t);
            }
        });
    }






    private void setupButtonLibreria() {
        Button buttonLibreria = findViewById(R.id.buttonLibreria);
        buttonLibreria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Inicio_activity.this, Libreria.class);
                startActivity(intent);
            }
        });
    }
}
