package com.example.biblioteisandroid2;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

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