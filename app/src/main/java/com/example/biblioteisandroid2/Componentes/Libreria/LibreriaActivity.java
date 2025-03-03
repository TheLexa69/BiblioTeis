package com.example.biblioteisandroid2.Componentes.Libreria;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.biblioteisandroid2.API.models.Book;
import com.example.biblioteisandroid2.API.repository.BookRepository;
import com.example.biblioteisandroid2.R;

import java.util.ArrayList;
import java.util.List;

import android.widget.TextView; // Añadir esta importación
import android.widget.Toast;

public class LibreriaActivity extends AppCompatActivity {

    // Declaración de los componentes de la vista
    private EditText editTextAutor, editTextTitulo, editTextISBN;
    private TextView editTextFecha;
    private CheckBox checkBoxDisponibilidad;
    private Button buttonBuscar;
    private List<Book> books;
    private BookAdapter bookAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_libreria);

        // Inicializar los componentes de la vista con sus respectivos IDs
        editTextAutor = findViewById(R.id.editTextAutor);
        editTextTitulo = findViewById(R.id.editTextTitulo);
        editTextISBN = findViewById(R.id.editTextISBN);
        editTextFecha = findViewById(R.id.editTextFecha); // Cambiado a TextView
        checkBoxDisponibilidad = findViewById(R.id.checkBoxDisponibilidad);
        buttonBuscar = findViewById(R.id.buttonBuscar);

        // Inicializar la lista de libros y el adaptador
        books = new ArrayList<>();
        bookAdapter = new BookAdapter(this, books);

        // Configurar el RecyclerView con un LinearLayoutManager y el adaptador
        RecyclerView recyclerView = findViewById(R.id.rvContainer);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(bookAdapter);

        // Cargar los libros desde el repositorio
        loadBooks();

        // Añadir el listener para el botón de buscar
        buttonBuscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterBooks(); // Llamar al método filterBooks() cuando se hace clic en el botón
            }
        });
    }

    // Método para cargar los libros desde el repositorio
    private void loadBooks() {
        BookRepository bookRepository = new BookRepository();
        bookRepository.getBooks(new BookRepository.ApiCallback<List<Book>>() {
            @Override
            public void onSuccess(List<Book> result) {
                books.clear(); // Limpiar la lista de libros
                books.addAll(result); // Añadir los libros obtenidos
                bookAdapter.notifyDataSetChanged(); // Notificar al adaptador que los datos han cambiado
            }

            @Override
            public void onFailure(Throwable t) {
                // Manejar el error aquí utilizando un Toast
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Error al cargar los libros: " + t.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }


    // Método para filtrar los libros según los criterios proporcionados
    private void filterBooks() {
        String authorFilter = editTextAutor.getText().toString().toLowerCase().trim();
        String titleFilter = editTextTitulo.getText().toString().toLowerCase().trim();
        String isbnFilter = editTextISBN.getText().toString().toLowerCase().trim();
        String dateFilter = editTextFecha.getText().toString().toLowerCase().trim();
        boolean onlyAvailable = checkBoxDisponibilidad.isChecked();

        List<Book> filteredBooks = new ArrayList<>();
        for (Book book : books) {
            boolean matchesAuthor = book.getAuthor().toLowerCase().contains(authorFilter);
            boolean matchesTitle = book.getTitle().toLowerCase().contains(titleFilter);
            boolean matchesIsbn = book.getIsbn().toLowerCase().contains(isbnFilter);
            boolean matchesDate = book.getPublishedDate().toLowerCase().contains(dateFilter);
            boolean matchesAvailability = !onlyAvailable || book.isAvailable();

            // Si el libro coincide con todos los criterios de filtro, se añade a la lista de libros filtrados
            if (matchesAuthor && matchesTitle && matchesIsbn && matchesDate && matchesAvailability) {
                filteredBooks.add(book);
            }
        }

        // Actualizar la lista de libros en el adaptador con los libros filtrados
        bookAdapter.updateBooks(filteredBooks);
    }
}
