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

        setupButtonLibreria(); // Llamada al método para configurar el botón

        bookRepository = new BookRepository();

        bookList = new ArrayList<>(); // Inicialización de la lista de libros
        bookAdapter = new BookAdapter(this, bookList); // Inicialización del adaptador con la lista vacía
        recyclerViewRecommended.setAdapter(bookAdapter); // Configuración del adaptador en el RecyclerView

        loadRecentBooks();
    }

    private void loadRecentBooks() {
        bookRepository.getBooks(new BookRepository.ApiCallback<List<Book>>() {
            @Override
            public void onSuccess(List<Book> books) {
                // Ordenar los libros por ID en orden descendente
                Collections.sort(books, new Comparator<Book>() {
                    @Override
                    public int compare(Book o1, Book o2) {
                        return Integer.compare(o2.getId(), o1.getId());
                    }
                });

                // Tomar los tres últimos libros
                List<Book> recentBooks = books.subList(0, Math.min(3, books.size()));

                // Actualizar la lista de libros en el adaptador
                bookAdapter.updateBooks(recentBooks);
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e("Inicio_activity", "Error fetching books", t);
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
