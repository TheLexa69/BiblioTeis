package com.example.biblioteisandroid2.Componentes.Libreria;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuProvider;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.biblioteisandroid2.API.models.Book;
import com.example.biblioteisandroid2.API.repository.BookRepository;
import com.example.biblioteisandroid2.MainActivity;
import com.example.biblioteisandroid2.R;

import java.text.SimpleDateFormat;
import java.util.Locale;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class Libreria extends AppCompatActivity {
    private EditText editTextAutor, editTextTitulo, editTextISBN;
    private TextView editTextFecha;
    private CheckBox checkBoxDisponibilidad;
    private Button buttonBuscar;
    private ImageView imageViewDatePicker, imageViewReset, imageViewResetFilter;
    private RecyclerView recyclerView;
    private BookAdapter bookAdapter;
    private List<Book> bookList = new ArrayList<>();
    private List<Book> originalBookList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_libreria);

        // Inicializar componentes
        editTextAutor = findViewById(R.id.editTextAutor);
        editTextTitulo = findViewById(R.id.editTextTitulo);
        editTextISBN = findViewById(R.id.editTextISBN);
        editTextFecha = findViewById(R.id.editTextFecha);
        checkBoxDisponibilidad = findViewById(R.id.checkBoxDisponibilidad);
        buttonBuscar = findViewById(R.id.buttonBuscar);
        imageViewDatePicker = findViewById(R.id.imageViewDatePicker);
        imageViewReset = findViewById(R.id.imageViewResetFecha);
        recyclerView = findViewById(R.id.rvContainer);
        imageViewResetFilter = findViewById(R.id.imageViewResetFilters);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        bookAdapter = new BookAdapter(this, bookList);
        recyclerView.setAdapter(bookAdapter);

        loadBooks();

        buttonBuscar.setOnClickListener(v -> filterBooks());
        imageViewDatePicker.setOnClickListener(v -> showDatePickerDialog());
        imageViewReset.setOnClickListener(v -> editTextFecha.setText(""));
        imageViewResetFilter.setOnClickListener(v -> {
            editTextAutor.setText("");
            editTextTitulo.setText("");
            editTextISBN.setText("");
            editTextFecha.setText("");
            checkBoxDisponibilidad.setChecked(false);
            bookAdapter.updateBooks(new ArrayList<>(originalBookList));
        });
        setupToolbar();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                menuInflater.inflate(R.menu.menu_toolbar, menu);
                menu.findItem(R.id.inicio_libreria).setEnabled(false);
            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                if (menuItem.getItemId() == R.id.cerrar_sesion) {
                    startActivity(new Intent(Libreria.this, MainActivity.class)
                            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                    return true;
                }
                return false;
            }
        });
    }


    private void loadBooks() {
        new BookRepository().getBooks(new BookRepository.ApiCallback<List<Book>>() {
            @Override
            public void onSuccess(List<Book> list) {
                bookList.clear();
                bookList.addAll(list);
                originalBookList.clear();
                originalBookList.addAll(list);  // Guarda una copia original
                bookAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e("Libreria", "Error fetching books", t);
            }
        });
    }



    private void filterBooks() {
        String authorFilter = editTextAutor.getText().toString().trim().toLowerCase();
        String titleFilter = editTextTitulo.getText().toString().trim().toLowerCase();
        String isbnFilter = editTextISBN.getText().toString().trim().toLowerCase();
        String dateFilter = editTextFecha.getText().toString().trim();

        boolean onlyAvailable = checkBoxDisponibilidad.isChecked();

        System.out.println("authorFilter: " + authorFilter);
        System.out.println("titleFilter: " + titleFilter);
        System.out.println("isbnFilter: " + isbnFilter);
        System.out.println("dateFilter: " + dateFilter);
        System.out.println("onlyAvailable: " + onlyAvailable);

        if (authorFilter.isEmpty() && titleFilter.isEmpty() && isbnFilter.isEmpty() && dateFilter.isEmpty() && !onlyAvailable) {
            bookAdapter.updateBooks(new ArrayList<>(bookList));
            return;
        }

        List<Book> filteredBooks = new ArrayList<>();
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-M-d", Locale.getDefault());
        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        String formattedDateFilter = dateFilter;
        try {
            formattedDateFilter = outputFormat.format(inputFormat.parse(dateFilter)); // Convertimos la fecha al formato "YYYY-MM-DD"
        } catch (Exception e) {
            e.printStackTrace();
        }

        for (Book book : bookList) {
            boolean matchesAuthor = book.getAuthor().toLowerCase().contains(authorFilter);
            boolean matchesTitle = book.getTitle().toLowerCase().contains(titleFilter);
            boolean matchesIsbn = book.getIsbn().toLowerCase().contains(isbnFilter);
            boolean matchesDate = book.getPublishedDate().split("T")[0].equals(formattedDateFilter);

            System.out.println("matchesDate: " + matchesDate);
            System.out.println("bookPublishedDate: " + book.getPublishedDate().split("T")[0] + " / DateFilter: " + formattedDateFilter);

            boolean matchesAvailability = !onlyAvailable || book.isAvailable();

            if (matchesAuthor && matchesTitle && matchesIsbn && matchesDate && matchesAvailability) {
                filteredBooks.add(book);
            }
        }
        bookAdapter.updateBooks(filteredBooks);
    }


    private void showDatePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year, month, dayOfMonth) ->
                editTextFecha.setText(year + "-" + (month + 1) + "-" + dayOfMonth),
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }
}
