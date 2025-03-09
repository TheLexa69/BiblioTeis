package com.example.biblioteisandroid2;

import static com.example.biblioteisandroid2.Componentes.Libreria.BookUtils.transformarFecha;

import android.content.Intent;
import android.content.SharedPreferences;
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
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import com.example.biblioteisandroid2.API.models.BookLending;
import com.example.biblioteisandroid2.API.repository.BookLendingRepository;
import com.example.biblioteisandroid2.API.repository.BookRepository;
import com.example.biblioteisandroid2.Componentes.Inicio.Inicio_activity;
import com.example.biblioteisandroid2.Componentes.Libreria.Libreria;
import com.example.biblioteisandroid2.Componentes.Usuario.UsuarioActivity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase InfoLibro que muestra información detallada de un libro seleccionado.
 * Permite realizar acciones como prestar y devolver libros, además de navegar en la aplicación.
 */
public class InfoLibro extends AppCompatActivity {

    /**
     * Claves para los extras del intent
     */
    public static final String BOOK_ID_EXTRA = "id";
    public static final String USER_ID_EXTRA = "USER_ID";

    /**
     * ID del usuario actual
     */
    private int userId;

    /**
     * Repositorio para gestionar préstamos de libros
     */
    private BookLendingRepository bookLendingRepository;

    // Componentes visuales
    private TextView tvISBN, tvTitulo, tvAutor, tvFecha, tvDisponible, tvBooklending;
    private ImageView ivPortada;
    private Button btnVolver, btnPrestarLibro, btnDevolverLibro;
    private String fechaFormateada;

    /**
     * Método que se ejecuta al crear la actividad.
     * Configura la interfaz de usuario, inicializa componentes y carga datos del libro.
     *
     * @param savedInstanceState Estado guardado de la instancia anterior de la actividad.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_info_libro);


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
                Log.d("InfoLibro", "ID del usuario obtenido desde SharedPreferences: " + userId);
            } else {
                Log.e("InfoLibro", "No se encontró un ID de usuario en SharedPreferences");
            }

        } catch (Exception e) {
            Log.e("Libreria", "Error al recuperar SharedPreferences", e);
        }


        // Configurar ajustes de ventana
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.activity_info_libro), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Configurar la barra de herramientas (Toolbar)
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
                if (id == R.id.opcion_libreria) {
                    Toast.makeText(InfoLibro.this, "Librería", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(InfoLibro.this, Libreria.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                    intent.putExtra(USER_ID_EXTRA, userId);
                    startActivity(intent);
                    return true;
                }
                if (id == R.id.opcion_inicio) {
                    Toast.makeText(InfoLibro.this, "Inicio", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(InfoLibro.this, Inicio_activity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    return true;
                }
                if (id == R.id.opcion_usuario) {
                    Toast.makeText(InfoLibro.this, "Perfil de Usuario", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(InfoLibro.this, UsuarioActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                    intent.putExtra(USER_ID_EXTRA, userId);
                    startActivity(intent);
                    return true;
                }
                if (id == R.id.cerrar_sesion) {
                    Toast.makeText(InfoLibro.this, "Cierre de sesión", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(InfoLibro.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    return true;
                }
                return false;
            }
        });
        //FIN BARRA DE TAREAS

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

        // Crear el ViewModel y solicitar información del libro
        InfoLibroVM vm = new ViewModelProvider(this).get(InfoLibroVM.class);

        // Solicitar datos del libro
        vm.getBook(getIntent().getIntExtra(BOOK_ID_EXTRA, 0));

        // Cargar datos en la vista cuando lleguen
        vm.getLibro().observe(this, libro -> {
            if (libro != null) {
                tvISBN.setText("ISBN: " + libro.getIsbn());
                tvTitulo.setText("Título: " + libro.getTitle());
                tvAutor.setText("Autor: " + libro.getAuthor());

                tvFecha.setText("Fecha de publicación: " + transformarFecha(libro.getPublishedDate()));

                tvDisponible.setText("Disponible: " + libro.isAvailable());
            }

            Log.d("InfoLibro", "Libro: " + libro);

            // Obtener los préstamos del libro
            List<BookLending> bookLendings = libro.getBookLendings();
            if (bookLendings != null && !bookLendings.isEmpty()) {
                for (BookLending lending : bookLendings) {
                    String rawLendDate = lending.getLendDate();  // Obtener la fecha del préstamo
                    if (rawLendDate != null) {
//                        LocalDate lendDate = LocalDate.parse(rawLendDate.substring(0, 10));
//                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
//                        fechaFormateada = lendDate.format(formatter);
                        fechaFormateada = transformarFecha(rawLendDate);
                        Log.d("InfoLibro", "Fecha de préstamo formateada: " + fechaFormateada);
                    } else {
                        fechaFormateada = null;  // Si no hay fecha, dejamos null
                        Log.d("InfoLibro", "El préstamo no tiene una fecha de préstamo.");
                    }
                }
            } else {
                fechaFormateada = null;  // No hay préstamos, dejamos null
                Log.d("InfoLibro", "No hay préstamos para este libro.");
            }

            LocalDate fechaEntrega = null;
            String formattedFechaEntrega = null;
            // Verificamos si la fecha es válida antes de procesarla
            if (fechaFormateada != null) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                fechaEntrega = LocalDate.parse(fechaFormateada, formatter).plusMonths(1);
                formattedFechaEntrega = fechaEntrega.format(formatter);
                Log.d("InfoLibro", "Fecha de entrega: " + formattedFechaEntrega);
            } else {
                Log.d("InfoLibro", "El libro no ha sido prestado nunca.");
            }

            if (bookLendings == null) {
                bookLendings = new ArrayList<>();
            }


            // Buscar si el libro está prestado y por quién
            BookLending currentLending = null;
            for (BookLending lending : bookLendings) {
                if (lending.getReturnDate() == null) { // Préstamo activo
                    currentLending = lending;
                    break;
                }
            }
            if (fechaFormateada != null && !fechaFormateada.isEmpty()) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                fechaEntrega = LocalDate.parse(fechaFormateada, formatter).plusMonths(1);
                formattedFechaEntrega = fechaEntrega.format(formatter);
                Log.d("InfoLibro", "Fecha de entrega: " + formattedFechaEntrega);
            } else {
                Log.d("InfoLibro", "No hay una fecha de préstamo válida.");
            }
//            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
//            LocalDate fechaEntrega = LocalDate.parse(fechaFormateada, formatter).plusMonths(1);
//            String formattedFechaEntrega = fechaEntrega.format(formatter);
//            Log.d("InfoLibro", "Fecha de entrega: " + formattedFechaEntrega);
//            Log.d("InfoLibro", "=====================================: " + fechaFormateada);
//            Log.d("InfoLibro", "Fecha de entrega: " + fechaEntrega);
//            Log.d("InfoLibro", "Fecha actual: " + fechaFormateada);

            // Configurar visibilidad de botones
            if (libro.isAvailable()) {
                btnPrestarLibro.setEnabled(true);
                btnPrestarLibro.setBackgroundColor(getResources().getColor(R.color.azul));
                btnDevolverLibro.setEnabled(false);
                tvBooklending.setText("El libro está disponible y puede ser prestado");

            } else {
                if (currentLending != null && currentLending.getUserId() == userId) {
                    btnDevolverLibro.setEnabled(true);
                    btnDevolverLibro.setBackgroundColor(getResources().getColor(R.color.azul));


                    if (LocalDate.now().isAfter(fechaEntrega)) {
                        tvBooklending.setText("El libro que tomaste prestado está atrasado para entrega.");
                    }

                    if (ChronoUnit.DAYS.between(LocalDate.now(), fechaEntrega) < 15) {
                        tvBooklending.setTextColor(getResources().getColor(R.color.red));
                    }
                    tvBooklending.setText("Tienes hasta el dia " + formattedFechaEntrega + " para entregar el libro.");

                } else {
                    btnDevolverLibro.setEnabled(false);
                    btnDevolverLibro.setBackgroundColor(getResources().getColor(R.color.azul));

                    tvBooklending.setText("Libro prestado hasta el dia " + formattedFechaEntrega + ", perdonen las molestias!");
                }
            }

            // Acciones de los botones
            btnPrestarLibro.setOnClickListener(v -> {
                Toast.makeText(this, "Escanea el código QR del libro", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(this, ScannerActivity.class);
                intent.putExtra("BOOK_ID", libro.getId());
                intent.putExtra("TIPO", 0);
//                intent.putExtra(USER_ID_EXTRA, userId);
                startActivityForResult(intent, 1);
            });

            btnDevolverLibro.setOnClickListener(v -> {
                bookLendingRepository.returnBook(libro.getId(), new BookRepository.ApiCallback<Boolean>() {
                    @Override
                    public void onSuccess(Boolean result) {
                        Toast.makeText(InfoLibro.this, "Libro devuelto con éxito", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(InfoLibro.this, InfoLibro.class);
//                        intent.putExtra(USER_ID_EXTRA, userId);
                        intent.putExtra(BOOK_ID_EXTRA, libro.getId());
                        startActivity(intent);
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        Toast.makeText(InfoLibro.this, "Error al devolver el libro", Toast.LENGTH_SHORT).show();
                    }
                });
            });
        });

        btnVolver.setOnClickListener(v -> {
            Intent intent = new Intent(InfoLibro.this, Libreria.class);
//            intent.putExtra(USER_ID_EXTRA, userId);
            Toast.makeText(this, "Usuario con id: " + userId, Toast.LENGTH_SHORT).show();
            startActivity(intent);
        });
    }

    /**
     * Método que se ejecuta cuando se retorna de una actividad iniciada con startActivityForResult.
     * Muestra un mensaje con el ID del libro prestado y el código QR escaneado.
     *
     * @param requestCode Código de solicitud con el que se inició la actividad.
     * @param resultCode  Código de resultado devuelto por la actividad.
     * @param data        Intent que contiene los datos devueltos por la actividad.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            int bookId = data.getIntExtra("BOOK_ID", 0);
            String scannedData = data.getStringExtra("SCANNED_DATA");
            Toast.makeText(this, "Libro prestado con ID: " + bookId + "\nCódigo QR: " + scannedData, Toast.LENGTH_LONG).show();
        }
    }


}
