package com.example.biblioteisandroid2.Componentes.Inicio;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import com.example.biblioteisandroid2.API.models.Book;
import com.example.biblioteisandroid2.Componentes.Libreria.Libreria;
import com.example.biblioteisandroid2.Componentes.Usuario.UsuarioActivity;
import com.example.biblioteisandroid2.MainActivity;
import com.example.biblioteisandroid2.R;

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
        setupToolbar();

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

        // Inicializar recyclerView
        recyclerViewRecommended = findViewById(R.id.recyclerViewRecommended);
        recyclerViewRecommended.setLayoutManager(new LinearLayoutManager(this));



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




    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                menuInflater.inflate(R.menu.menu_toolbar, menu);
                menu.findItem(R.id.opcion_inicio).setVisible(false);
            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                if (menuItem.getItemId() == R.id.cerrar_sesion) {
                    startActivity(new Intent(Inicio_activity.this, MainActivity.class)
                            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                    return true;
                }

                if (menuItem.getItemId() == R.id.opcion_usuario) {
                    startActivity(new Intent(Inicio_activity.this, UsuarioActivity.class)
                            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                    return true;
                }
                if (menuItem.getItemId() == R.id.opcion_libreria) {
                    startActivity(new Intent(Inicio_activity.this, Libreria.class)
                            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                    return true;
                }

                return false;
            }
        });
    }
}
