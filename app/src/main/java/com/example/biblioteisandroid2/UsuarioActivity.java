package com.example.biblioteisandroid2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import com.example.biblioteisandroid2.API.models.BookLending;
import com.example.biblioteisandroid2.API.models.User;
import com.example.biblioteisandroid2.API.repository.BookRepository;
import com.example.biblioteisandroid2.API.repository.UserRepository;
import com.example.biblioteisandroid2.Componentes.Libreria.Libreria;
import com.example.biblioteisandroid2.Componentes.Usuario.BookLendingAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Actividad que muestra la información del usuario y los libros que ha prestado.
 * Esta actividad gestiona la visualización del nombre, correo electrónico y la fecha de registro del usuario,
 * así como una lista de los libros prestados, mostrando también el estado de devolución de los libros.
 */
public class UsuarioActivity extends AppCompatActivity {

    private RecyclerView recyclerViewBooks;
    private BookLendingAdapter bookAdapter;
    private List<BookLending> bookLendings;
    private int userId;
    private UserRepository userRepository;

    private TextView userNameTextView, userEmailTextView, dateJoinedTextView;

    /**
     * Método de ciclo de vida de la actividad, que se llama cuando se crea la actividad.
     * Aquí se inicializan los componentes de la interfaz de usuario y se obtienen los datos del usuario.
     *
     * @param savedInstanceState El estado guardado de la actividad.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_usuario);

        // Inicializar la Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar); // Asegurarse de establecer la toolbar como ActionBar

        recyclerViewBooks = findViewById(R.id.recyclerViewBooks);
        bookLendings = new ArrayList<>();
        userRepository = new UserRepository();

        // Vincular TextViews con los elementos del layout
        userNameTextView = findViewById(R.id.userName);
        userEmailTextView = findViewById(R.id.userEmail);
        dateJoinedTextView = findViewById(R.id.dateJoined);

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
                Log.d("UsuarioActivity", "[Inicio_activity -> UsuarioActivity] ID del usuario obtenido desde SharedPreferences: " + userId);
                loadUserData(userId);
            } else {
                Log.e("UsuarioActivity", "[Inicio_activity -> UsuarioActivity] No se encontró un ID de usuario en SharedPreferences");
            }

        } catch (Exception e) {
            Log.e("UsuarioActivity", "Error al recuperar SharedPreferences", e);
        }
    }

    /**
     * Carga los datos del usuario desde el repositorio de usuarios utilizando el ID proporcionado.
     * Una vez cargados, muestra los datos en los elementos de la interfaz.
     *
     * @param userId El ID del usuario.
     */
    private void loadUserData(int userId) {
        userRepository.getUserById(userId, new BookRepository.ApiCallback<User>() {
            @Override
            public void onSuccess(User user) {
                if (user != null) {
                    userNameTextView.setText(user.getName());
                    userEmailTextView.setText(user.getEmail());
                    dateJoinedTextView.setText(user.getDateJoined().toString());

                    bookLendings = user.getBookLendings();
                    sortAndHighlightBooks();
                    setupRecyclerView();
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e("UsuarioActivity", "Error al obtener usuario", t);
            }
        });
    }

    /**
     * Ordena y resalta los libros prestados, destacando aquellos que ya están vencidos.
     *
     * La comparación se realiza en base a la fecha de devolución de los libros:
     * - Devuelve 0 si ambas fechas de devolución son nulas.
     * - Devuelve 1 si la fecha de devolución del primer libro es nula (lo coloca después).
     * - Devuelve -1 si la fecha de devolución del segundo libro es nula (lo coloca antes).
     * - De lo contrario, compara las fechas de devolución de ambos libros.
     */
    private void sortAndHighlightBooks() {
        if (bookLendings != null) {
            Collections.sort(bookLendings, (b1, b2) -> {
                if (b1.getReturnDate() == null && b2.getReturnDate() == null) {
                    return 0;
                }
                if (b1.getReturnDate() == null) {
                    return 1;
                }
                if (b2.getReturnDate() == null) {
                    return -1;
                }
                return b1.getReturnDate().compareTo(b2.getReturnDate());
            });
        }
    }

    /**
     * Configura el RecyclerView para mostrar la lista de libros prestados. Si no hay libros prestados,
     * muestra un mensaje indicando que no hay libros prestados.
     */
    private void setupRecyclerView() {
        if (bookLendings != null && !bookLendings.isEmpty()) {
            bookAdapter = new BookLendingAdapter(bookLendings, userId);
            recyclerViewBooks.setLayoutManager(new LinearLayoutManager(this));
            recyclerViewBooks.setAdapter(bookAdapter);
            findViewById(R.id.noBooksMessage).setVisibility(View.GONE);
        } else {
            findViewById(R.id.noBooksMessage).setVisibility(View.VISIBLE);
        }
    }

    /**
     * Crea el menú de la Toolbar.
     *
     * @param menu El menú que se debe mostrar.
     * @return Devuelve true si el menú se crea correctamente.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar, menu);
        return true;
    }

    /**
     * Maneja las acciones de los elementos seleccionados del menú de la Toolbar.
     *
     * @param item El ítem del menú que ha sido seleccionado.
     * @return Devuelve true si la acción fue procesada correctamente.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.inicio_libreria) {

            Intent intent = new Intent(this, Libreria.class);
//            intent.putExtra("USER_ID", userId);
            startActivity(intent);
            return true;
        }

        if (itemId == R.id.cerrar_sesion) {
            startActivity(new Intent(this, MainActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
