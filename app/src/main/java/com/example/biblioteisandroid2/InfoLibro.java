package com.example.biblioteisandroid2;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.MenuProvider;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.biblioteisandroid2.API.models.BookLending;
import com.example.biblioteisandroid2.API.repository.BookLendingRepository;
import com.example.biblioteisandroid2.API.repository.BookRepository;
import com.example.biblioteisandroid2.Componentes.Libreria.Libreria;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;
import java.util.List;

public class InfoLibro extends AppCompatActivity {


    public static final String BOOK_ID_EXTRA = "id";
    public static final String USER_ID_EXTRA = "userId";
    private int userId; // Variable para almacenar el userId
    private BookLendingRepository bookLendingRepository;


    //declare visual components
    TextView tvISBN, tvTitulo, tvAutor, tvFecha, tvDisponible, tvBooklending;
    ImageView ivPortada;
    Button btnVolver, btnPrestarLibro, btnDevolverLibro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_info_libro);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.activity_info_libro), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //TOOLBAR
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                menuInflater.inflate(R.menu.menu_toolbar, menu);

            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                int id = menuItem.getItemId();
                if (id == R.id.inicio_libreria) {
                    Toast.makeText(InfoLibro.this, "Libreria", Toast.LENGTH_SHORT).show();
                    Intent logoutIntent = new Intent(InfoLibro.this, Libreria.class);
                    logoutIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(logoutIntent);
                    return true;
                }
                if (id == R.id.cerrar_sesion) {
                    Toast.makeText(InfoLibro.this, "Cierre de Sesión", Toast.LENGTH_SHORT).show();
                    Intent logoutIntent = new Intent(InfoLibro.this, MainActivity.class);
                    logoutIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(logoutIntent);
                    return true;
                }
                return false;
            }


        });
        //FIN TOOLBAR


        userId = getIntent().getIntExtra(USER_ID_EXTRA, -1);
        if (userId == -1) {
            Log.e("InfoLibro", "No se ha proporcionado un ID de usuario");
        } else {
            Log.d("InfoLibro", "ID del usuario: " + userId);
        }

        // Inicializar componentes visuales
        btnPrestarLibro = findViewById(R.id.buttonPrestarLibro);
        btnDevolverLibro = findViewById(R.id.buttonDevolverLibro);
        tvAutor = findViewById(R.id.author);
        tvTitulo = findViewById(R.id.title);
        tvFecha = findViewById(R.id.publishedDate);
        tvDisponible = findViewById(R.id.isAvailable);
        tvISBN = findViewById(R.id.isbn);
        tvBooklending = findViewById(R.id.bookLendings);
        btnVolver = findViewById(R.id.btnInfoBookVolver);
        bookLendingRepository = new BookLendingRepository();



        //Crear el vm
        InfoLibroVM vm = new ViewModelProvider(this).get(InfoLibroVM.class);

        //Solicitar al repo los datos del libro
        vm.getBook(getIntent().getIntExtra(BOOK_ID_EXTRA, 0));
        //cargar datos en vista cuando lleguen
        vm.getLibro().observe(this, libro -> {
            if (libro != null) {
                tvISBN.setText("ISBN: " + libro.getIsbn());
                tvTitulo.setText("Título: " + libro.getTitle());
                tvAutor.setText("Autor: " + libro.getAuthor());
                tvFecha.setText("Fecha de publicación: " + libro.getPublishedDate());
                tvDisponible.setText("Disponible: " + libro.isAvailable());
                tvBooklending.setText("Prestamos: " + libro.getBookLendings());
            }

            // Lógica para mostrar/ocultar botones
            Log.d("InfoLibro", "Libro: " + libro);
            Log.d("InfoLibro", "Libro disponible: " + libro.getBookLendings());
            Log.d("InfoLibro", "Libro disponible: " + libro.isAvailable());
            List<BookLending> bookLendings = libro.getBookLendings();
            if (bookLendings == null) {
                bookLendings = new ArrayList<>();
            }
            Log.d("InfoLibro", "Book lendings: " + bookLendings);

            if (libro.isAvailable() && (libro.getBookLendings() == null ||
                    libro.getBookLendings().stream().allMatch(lending -> lending.getReturnDate() != null))) {
                btnPrestarLibro.setEnabled(true);
                btnPrestarLibro.setBackgroundColor(getResources().getColor(R.color.green));
                btnDevolverLibro.setEnabled(false);
                // Botón para prestar libro
                btnPrestarLibro.setOnClickListener(v -> {
                    Toast.makeText(this, "Escanea el código QR del libro", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(this, ScannerActivity.class);
                    intent.putExtra("BOOK_ID", libro.getId());
                    intent.putExtra(USER_ID_EXTRA, userId);
                    startActivityForResult(intent, 1); // Usa requestCode = 1
                });

            } else {
                btnPrestarLibro.setEnabled(false);
                btnDevolverLibro.setEnabled(true);
                btnDevolverLibro.setBackgroundColor(getResources().getColor(R.color.green));

                btnDevolverLibro.setOnClickListener(v -> {
                    bookLendingRepository.returnBook(libro.getId(), new BookRepository.ApiCallback<Boolean>() {
                        @Override
                        public void onSuccess(Boolean result) {
                            Toast.makeText(InfoLibro.this, "Libro devuelto con éxito", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(InfoLibro.this, InfoLibro.class);
                            intent.putExtra(USER_ID_EXTRA, userId);
                            intent.putExtra(BOOK_ID_EXTRA, libro.getId());
                            startActivity(intent);

                        }

                        @Override
                        public void onFailure(Throwable t) {
                            Toast.makeText(InfoLibro.this, "Error al devolver el libro", Toast.LENGTH_SHORT).show();
                        }
                    });

                });
            }
        });

        btnVolver.setOnClickListener(v -> finish());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("InfoLibro", "onActivityResult: requestCode=" + requestCode + ", resultCode=" + resultCode);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            int bookId = data.getIntExtra("BOOK_ID", 0);
            String scannedData = data.getStringExtra("SCANNED_DATA");
            Toast.makeText(this, "Libro prestado con ID: " + bookId + "\nCódigo QR: " + scannedData, Toast.LENGTH_LONG).show();
        } else if (requestCode == 1 && resultCode == RESULT_CANCELED) {
            Toast.makeText(this, "Escaneo cancelado", Toast.LENGTH_LONG).show();
        }
    }
}