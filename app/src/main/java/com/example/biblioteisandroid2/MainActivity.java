package com.example.biblioteisandroid2;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.biblioteisandroid2.API.models.User;
import com.example.biblioteisandroid2.API.repository.BookRepository;
import com.example.biblioteisandroid2.API.repository.UserRepository;
import com.example.biblioteisandroid2.Componentes.Libreria.Libreria;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    EditText etEmail, etContra;
    Button btnLogin;
    UserRepository userRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etEmail = findViewById(R.id.etEmail);
        etContra = findViewById(R.id.etContra);
        btnLogin = findViewById(R.id.btnLogin);
        userRepository = new UserRepository();
        System.out.println(userRepository);

        btnLogin.setOnClickListener(v -> {
//            String email = etEmail.getText().toString();
//            String password = etContra.getText().toString();
//            login(email, password);
            login("alice@example.com", "hashedpassword1");
        });
    }

    private void login(String email, String password) {
        userRepository.getUsers(new BookRepository.ApiCallback<List<User>>() {

            @Override
            public void onSuccess(List<User> users) {
                boolean loginSuccessful = false;
                for (User user : users) {
                    if (user.getEmail().equals(email) && user.getPasswordHash().equals(password)) {
                        loginSuccessful = true;
                        break;
                    }
                }

                if (loginSuccessful) {
                    System.out.println("Login correcto");
                    Toast.makeText(MainActivity.this, "Login correcto", Toast.LENGTH_SHORT).show();
                    // Aquí se hace la transición solo si el login es exitoso
                    Intent intent = new Intent(MainActivity.this, Libreria.class);
                    startActivity(intent);
                } else {
                    System.out.println("Login incorrecto");
                    Toast.makeText(MainActivity.this, "Login incorrecto", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Throwable t) {
                System.out.println("Error fetching users");
                Toast.makeText(MainActivity.this, "Error fetching users", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
