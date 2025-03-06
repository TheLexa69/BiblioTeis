package com.example.biblioteisandroid2.Componentes.Libreria;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.LinearLayout;
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
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

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

/**
 * Actividad principal de la librería, donde los usuarios pueden buscar y filtrar libros.
 */
public class Libreria extends AppCompatActivity {

    /**
     * Campos de entrada para buscar libros por autor, título e ISBN
     */
    private EditText editTextAutor, editTextTitulo, editTextISBN;
    /**
     * Campo de entrada para la fecha de publicación
     */
    private TextView editTextFecha, toggleFilters;
    /**
     * Checkbox para filtrar por disponibilidad
     */
    private CheckBox checkBoxDisponibilidad;
    /**
     * Botón para realizar la búsqueda de libros
     */
    private Button buttonBuscar;
    /**
     * Icono para abrir el selector de fecha
     */
    private ImageView imageViewDatePicker, imageViewReset, imageViewResetFilter;
    /**
     * RecyclerView para mostrar la lista de libros
     */
    private RecyclerView recyclerView;
    /**
     * Adaptador para gestionar la lista de libros
     */
    private BookAdapter bookAdapter;
    /**
     * Lista de libros cargados
     */
    private List<Book> bookList = new ArrayList<>();
    /**
     * Lista original de libros para restaurar filtros
     */
    private List<Book> originalBookList = new ArrayList<>();
    /**
     * Contenedor de filtros
     */
    private LinearLayout filtersContainer;
    /**
     * ID del usuario actual
     */
    private int userId;

    /**
     * Método que se ejecuta al crear la actividad.
     * Inicializa los componentes de la interfaz de usuario y configura el RecyclerView.
     * También carga los libros desde el repositorio y configura los listeners para los botones.
     *
     * @param savedInstanceState Estado guardado de la instancia anterior de la actividad.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_libreria);

        // Inicializar componentes
        //ESTE ES EL ANTIGUO METODO        userId = getIntent().getIntExtra("USER_ID", -1); // -1 como valor por defecto
        //AHORA RECOGE DESDE EL SHAREDPRERENCES
        try {
            MasterKey masterKey = new MasterKey.Builder(this)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build();

            SharedPreferences sharedPreferences = EncryptedSharedPreferences.create(
                    this,
                    "secure_prefs",
                    masterKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );

            userId = sharedPreferences.getInt("USER_ID", -1);

            if (userId != -1) {
                Log.d("Libreria", "ID del usuario obtenido desde SharedPreferences: " + userId);
            } else {
                Log.e("Libreria", "No se encontró un ID de usuario en SharedPreferences");
            }

        } catch (Exception e) {
            Log.e("Libreria", "Error al recuperar SharedPreferences", e);
        }

        //SISTEMA DE FILTRADO PARA QUE NO OCUPE 3/4 DE LA PANTALLA
        toggleFilters = findViewById(R.id.toggleFilters);
        filtersContainer = findViewById(R.id.filtersContainer);

        toggleFilters.setOnClickListener(v -> {
            if (filtersContainer.getVisibility() == View.VISIBLE) {
                filtersContainer.setVisibility(View.GONE);
            } else {
                filtersContainer.setVisibility(View.VISIBLE);
            }
        });
        //FIN DE SISTEMA DE FILTRADO

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

        bookAdapter = new BookAdapter(this, bookList, userId);
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

    /**
     * Configura la barra de herramientas con opciones de menú.
     */
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

    /**
     * Carga los libros desde el repositorio.
     */
    private void loadBooks() {
        new BookRepository().getBooks(new BookRepository.ApiCallback<List<Book>>() {
            @Override
            public void onSuccess(List<Book> list) {
                bookList.clear();
                bookList.addAll(list);
                //Log.d("BookAdapter", "Cargando libro: " + bookList);
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

    /**
     * Filtra los libros según los criterios ingresados por el usuario.
     */
    private void filterBooks() {
        String authorFilter = editTextAutor.getText().toString().trim().toLowerCase();
        String titleFilter = editTextTitulo.getText().toString().trim().toLowerCase();
        String isbnFilter = editTextISBN.getText().toString().trim().toLowerCase();
        String dateFilter = editTextFecha.getText().toString().trim();

        boolean onlyAvailable = checkBoxDisponibilidad.isChecked();

        if (authorFilter.isEmpty() && titleFilter.isEmpty() && isbnFilter.isEmpty() && dateFilter.isEmpty() && !onlyAvailable) {
            bookAdapter.updateBooks(new ArrayList<>(bookList));
            return;
        }

        List<Book> filteredBooks = new ArrayList<>();
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-M-d", Locale.getDefault());
        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        String formattedDateFilter = dateFilter;
        try {
            formattedDateFilter = outputFormat.format(inputFormat.parse(dateFilter)); // Convierte a "YYYY-MM-DD"
        } catch (Exception e) {
            e.printStackTrace();
        }

        for (Book book : bookList) {

            boolean matchesAuthor = authorFilter.isEmpty() || book.getAuthor().toLowerCase().contains(authorFilter.toLowerCase());
            boolean matchesTitle = titleFilter.isEmpty() || book.getTitle().toLowerCase().contains(titleFilter.toLowerCase());
            boolean matchesIsbn = isbnFilter.isEmpty() || book.getIsbn().toLowerCase().contains(isbnFilter.toLowerCase());
            boolean matchesDate = dateFilter.isEmpty() || book.getPublishedDate().split("T")[0].equals(formattedDateFilter);

            boolean matchesAvailability = !onlyAvailable || book.isAvailable();

            if (matchesAuthor && matchesTitle && matchesIsbn && matchesDate && matchesAvailability) {
                filteredBooks.add(book);
            }
        }


        bookAdapter.updateBooks(filteredBooks);
    }

    /**
     * Muestra un cuadro de diálogo para seleccionar una fecha.
     * La fecha seleccionada se muestra en el campo de texto de la fecha.
     */
    private void showDatePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year, month, dayOfMonth) ->
                editTextFecha.setText(year + "-" + (month + 1) + "-" + dayOfMonth),
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }


}
