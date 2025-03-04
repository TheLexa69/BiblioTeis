 package com.example.biblioteisandroid2;

import android.content.Intent;
import android.os.Bundle;
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

import com.example.biblioteisandroid2.Componentes.Libreria.Libreria;

public class InfoLibro extends AppCompatActivity {


    public static final String BOOK_ID_EXTRA = "id";
    //declare visual components
    TextView tvISBN, tvTitulo, tvAutor, tvFecha, tvDisponible, tvBooklending;
    ImageView ivPortada;
    Button btnVolver;

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

        //        TOOLBAR
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
        //        FIN TOOLBAR


        //initialize visual components
        tvAutor = findViewById(R.id.author);
        tvTitulo = findViewById(R.id.title);
        tvFecha = findViewById(R.id.publishedDate);
        tvDisponible = findViewById(R.id.isAvailable);
        tvISBN = findViewById(R.id.isbn);
        tvBooklending = findViewById(R.id.bookLendings);
        btnVolver = findViewById(R.id.btnInfoBookVolver);
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
        });

        btnVolver.setOnClickListener(v -> finish());
    }
}