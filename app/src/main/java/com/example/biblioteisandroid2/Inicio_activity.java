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
import com.example.biblioteisandroid2.Componentes.Inicio.InicioBookAdapter;
import com.example.biblioteisandroid2.Componentes.Libreria.BookAdapter;
import com.example.biblioteisandroid2.Componentes.Libreria.Libreria;
import com.example.biblioteisandroid2.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Inicio_activity extends AppCompatActivity {

    private RecyclerView recyclerViewRecommended;
    private List<Book> bookList; // Añadido para mantener la lista de libros
    private InicioBookAdapter inicioBookAdapter;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio);

        userId = getIntent().getIntExtra("USER_ID", -1); // -1 como valor por defecto
        if (userId != -1) {
            Log.d("Libreria", "ID del usuario recibido: " + userId);
        } else {
            Log.e("Libreria", "No se ha proporcionado un ID de usuario");
        }


        recyclerViewRecommended = findViewById(R.id.recyclerViewRecommended);
        recyclerViewRecommended.setLayoutManager(new LinearLayoutManager(this));

        setupButtonLibreria();

        // Lista de libros hardcodeados
        bookList = new ArrayList<>();
        bookList.add(createBook(1, "C# in Depth", "Jon Skeet"));
        bookList.add(createBook(2, "ASP.NET Core in Action", "Andrew Lock"));
        bookList.add(createBook(3, "The Pragmatic Programmer", "Andrew Hunt"));

        // Configurar el adaptador con la lista de libros
        inicioBookAdapter = new InicioBookAdapter(bookList);
        recyclerViewRecommended.setAdapter(inicioBookAdapter);
    }

    private Book createBook(int id, String title, String author) {
        Book book = new Book();
        book.setId(id);
        book.setTitle(title);
        book.setAuthor(author);
        book.setIsbn("123-4567890123");
        book.setPublishedDate("2025-03-05");
        book.setAvailable(true);
        return book;
    }

    private void setupButtonLibreria() {
        Button buttonLibreria = findViewById(R.id.buttonLibreria);
        buttonLibreria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Inicio_activity.this, Libreria.class);
                Log.d("Inicio_activity", "Enviando a Libreria -> User ID: " + userId);
                intent.putExtra("USER_ID", userId);
                startActivity(intent);
            }
        });
    }
}
