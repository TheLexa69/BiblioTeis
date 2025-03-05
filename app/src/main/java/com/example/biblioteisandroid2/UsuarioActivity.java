package com.example.biblioteisandroid2;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.biblioteisandroid2.API.models.BookLending;
import com.example.biblioteisandroid2.API.models.User;
import com.example.biblioteisandroid2.API.repository.BookRepository;
import com.example.biblioteisandroid2.API.repository.UserRepository;
import com.example.biblioteisandroid2.Componentes.Usuario.BookLendingAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UsuarioActivity extends AppCompatActivity {

    private RecyclerView recyclerViewBooks;
    private BookLendingAdapter bookAdapter;
    private List<BookLending> bookLendings;
    private int userId;
    private UserRepository userRepository;

    private TextView userNameTextView, userEmailTextView, dateJoinedTextView; // Referencias a los TextView

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_usuario);

        recyclerViewBooks = findViewById(R.id.recyclerViewBooks);
        bookLendings = new ArrayList<>();
        userRepository = new UserRepository(); // Inicializar repositorio

        // Vincular TextViews con los elementos del layout
        userNameTextView = findViewById(R.id.userName);
        userEmailTextView = findViewById(R.id.userEmail);
        dateJoinedTextView = findViewById(R.id.dateJoined);

        // Obtener el ID del usuario
        userId = getIntent().getIntExtra("USER_ID", -1);
        if (userId != -1) {
            loadUserData(userId);
        } else {
            Log.e("UsuarioActivity", "No se ha proporcionado un ID de usuario");
        }
    }

    private void loadUserData(int userId) {
        userRepository.getUserById(userId, new BookRepository.ApiCallback<User>() {
            @Override
            public void onSuccess(User user) {
                if (user != null) {
                    Log.d("UsuarioActivity", "Usuario cargado: " + user.getName());

                    // Llenar los TextViews con la información del usuario
                    userNameTextView.setText(user.getName());
                    userEmailTextView.setText(user.getEmail());
                    dateJoinedTextView.setText(user.getDateJoined().toString());

                    bookLendings = user.getBookLendings();
                    sortAndHighlightBooks();
                    setupRecyclerView();
                } else {
                    Log.e("UsuarioActivity", "Usuario no encontrado");
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e("UsuarioActivity", "Error al obtener usuario", t);
            }
        });
    }

    private void sortAndHighlightBooks() {
        if (bookLendings != null) {
            Collections.sort(bookLendings, (b1, b2) -> b1.getReturnDate().compareTo(b2.getReturnDate()));
        }
    }

    private void setupRecyclerView() {
        bookAdapter = new BookLendingAdapter(bookLendings);
        recyclerViewBooks.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewBooks.setAdapter(bookAdapter);
    }
}
