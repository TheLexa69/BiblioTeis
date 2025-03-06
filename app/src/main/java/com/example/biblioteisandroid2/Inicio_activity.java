package com.example.biblioteisandroid2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import com.example.biblioteisandroid2.API.models.Book;
import com.example.biblioteisandroid2.Componentes.Inicio.InicioBookAdapter;
import com.example.biblioteisandroid2.Componentes.Libreria.Libreria;

import java.util.ArrayList;
import java.util.List;

public class Inicio_activity extends AppCompatActivity {

    private RecyclerView recyclerViewRecommended;
    private List<Book> bookList; // Lista para almacenar los libros
    private InicioBookAdapter inicioBookAdapter;
    private int userId;

    /**
     * Método que se ejecuta cuando se crea la actividad.
     * Se configura el RecyclerView y se inicializa la lista de libros.
     *
     * @param savedInstanceState Estado guardado de la actividad (si existe).
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio);

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

            userId = sharedPreferences.getInt("USER_ID", -1); // Recuperamos el ID del usuario

            if (userId != -1) {
                Log.d("Inicio_activity", "[MainActivity -> Inicio_activity] ID del usuario obtenido desde SharedPreferences: " + userId);
            } else {
                Log.e("Inicio_activity", "[MainActivity -> Inicio_activity] No se encontró un ID de usuario en SharedPreferences");
            }

        } catch (Exception e) {
            Log.e("Inicio_activity", "Error al recuperar SharedPreferences", e);
        }
        //FIN RECOGER DATOS DESDE SHARED PREFERENCES


        recyclerViewRecommended = findViewById(R.id.recyclerViewRecommended);
        recyclerViewRecommended.setLayoutManager(new LinearLayoutManager(this));

        setupButtonLibreria();
        setupButtonUsuario();  // Configuramos el botón de Usuario

        // Lista de libros hardcodeados
        bookList = new ArrayList<>();
        bookList.add(createBook(1, "C# in Depth", "Jon Skeet"));
        bookList.add(createBook(2, "ASP.NET Core in Action", "Andrew Lock"));
        bookList.add(createBook(3, "The Pragmatic Programmer", "Andrew Hunt"));

        // Configuración del adaptador con la lista de libros
        inicioBookAdapter = new InicioBookAdapter(bookList);
        recyclerViewRecommended.setAdapter(inicioBookAdapter);
    }

    /**
     * Crea un objeto de tipo Book con valores predeterminados.
     *
     * @param id    El ID del libro.
     * @param title El título del libro.
     * @param author El autor del libro.
     * @return Un objeto Book con los valores proporcionados.
     */
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

    /**
     * Configura el botón para navegar a la actividad de la librería.
     * Cuando se hace clic, inicia la actividad Libreria.
     */
    private void setupButtonLibreria() {
        Button buttonLibreria = findViewById(R.id.buttonLibreria);
        buttonLibreria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Inicio_activity.this, Libreria.class);
                Log.d("Inicio_activity", "Enviando a Libreria -> User ID: " + userId);
//                intent.putExtra("USER_ID", userId);
                startActivity(intent);
            }
        });
    }

    /**
     * Configura el botón para navegar a la actividad de usuario.
     * Cuando se hace clic, inicia la actividad UsuarioActivity.
     */
    // Nuevo método para configurar el botón "Usuario"
    private void setupButtonUsuario() {
        Button buttonUsuario = findViewById(R.id.buttonUsuario);
        buttonUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Inicio_activity.this, UsuarioActivity.class);
                Log.d("Inicio_activity", "Enviando a UsuarioActivity -> User ID: " + userId);
//                intent.putExtra("USER_ID", userId);
                startActivity(intent);
            }
        });
    }
}
