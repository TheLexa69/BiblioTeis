package com.example.biblioteisandroid2.Componentes.Usuario;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuProvider;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import com.example.biblioteisandroid2.API.models.BookLending;
import com.example.biblioteisandroid2.API.models.User;
import com.example.biblioteisandroid2.Componentes.Libreria.Libreria;
import com.example.biblioteisandroid2.Inicio_activity;
import com.example.biblioteisandroid2.MainActivity;
import com.example.biblioteisandroid2.R;

import java.util.List;

/**
 * Actividad que muestra la información del usuario y sus libros prestados.
 */
public class UsuarioActivity extends AppCompatActivity {
    private UsuarioViewModel usuarioViewModel;
    private RecyclerView recyclerViewBooks;
    private BookLendingAdapter bookAdapter;
    private TextView userNameTextView, userEmailTextView, dateJoinedTextView;

    /**
     * Método llamado cuando la actividad es creada.
     * @param savedInstanceState Estado guardado de la actividad (si existe).
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_usuario);

        recyclerViewBooks = findViewById(R.id.recyclerViewBooks);
        userNameTextView = findViewById(R.id.userName);
        userEmailTextView = findViewById(R.id.userEmail);
        dateJoinedTextView = findViewById(R.id.dateJoined);

        usuarioViewModel = new ViewModelProvider(this).get(UsuarioViewModel.class);

        // Configurar observadores para actualizar la UI en tiempo real
        usuarioViewModel.getUserLiveData().observe(this, user -> {
            if (user != null) {
                userNameTextView.setText(user.getName());
                userEmailTextView.setText(user.getEmail());
                dateJoinedTextView.setText(user.getDateJoined());
                setupRecyclerView(user.getBookLendings());
            }
        });

        usuarioViewModel.getIsLoading().observe(this, isLoading -> {
            // Se puede agregar un indicador de carga aquí
        });

        // Obtener el ID del usuario desde SharedPreferences
        int userId = obtenerUserIdDesdeSharedPreferences();
        if (userId != -1) {
            usuarioViewModel.loadUserData(userId);
        }

        // Configurar la barra de herramientas (Toolbar)
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                menuInflater.inflate(R.menu.menu_toolbar, menu);
                menu.findItem(R.id.opcion_usuario).setEnabled(false);
            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                return manejarMenuItem(menuItem);
            }
        });
    }

    /**
     * Obtiene el ID del usuario desde SharedPreferences de forma segura.
     * @return ID del usuario o -1 si no se encuentra.
     */
    private int obtenerUserIdDesdeSharedPreferences() {
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

            return sharedPreferences.getInt("USER_ID", -1);
        } catch (Exception e) {
            Log.e("UsuarioActivity", "Error al recuperar SharedPreferences", e);
            return -1;
        }
    }

    /**
     * Maneja la selección de elementos del menú.
     * @param menuItem Elemento del menú seleccionado.
     * @return true si se maneja el evento, false en caso contrario.
     */
    private boolean manejarMenuItem(MenuItem menuItem) {
        int id = menuItem.getItemId();
        Intent intent = null;

        if (id == R.id.opcion_libreria) {
            Toast.makeText(UsuarioActivity.this, "Libreria", Toast.LENGTH_SHORT).show();
            intent = new Intent(this, Libreria.class);
        } else if (id == R.id.opcion_inicio) {
            Toast.makeText(UsuarioActivity.this, "Inicio", Toast.LENGTH_SHORT).show();
            intent = new Intent(this, Inicio_activity.class);
        } else if (id == R.id.cerrar_sesion) {
            Toast.makeText(UsuarioActivity.this, "Sesion Cerrada", Toast.LENGTH_SHORT).show();
            intent = new Intent(this, MainActivity.class);
        }

        if (intent != null) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            return true;
        }
        return false;
    }

    /**
     * Configura el RecyclerView con la lista de libros prestados.
     * @param bookLendings Lista de libros prestados por el usuario.
     */
    private void setupRecyclerView(List<BookLending> bookLendings) {
        Log.d("UsuarioActivity", "Número de libros prestados: " + (bookLendings != null ? bookLendings.size() : 0));
        if (bookLendings != null && !bookLendings.isEmpty()) {
            bookAdapter = new BookLendingAdapter(bookLendings, obtenerUserIdDesdeSharedPreferences());
            recyclerViewBooks.setLayoutManager(new LinearLayoutManager(this));
            recyclerViewBooks.setAdapter(bookAdapter);
            findViewById(R.id.noBooksMessage).setVisibility(View.GONE);
        } else {
            findViewById(R.id.noBooksMessage).setVisibility(View.VISIBLE);
        }
    }
}
