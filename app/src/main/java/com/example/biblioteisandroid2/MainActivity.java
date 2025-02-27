package com.example.biblioteisandroid2;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import com.example.biblioteisandroid2.API.models.User;
import com.example.biblioteisandroid2.API.repository.BookRepository;
import com.example.biblioteisandroid2.API.repository.UserRepository;
import com.example.biblioteisandroid2.Componentes.Libreria.Libreria;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final String EMAIL = "logged_email";
    EditText etEmail, etContra;
    Button btnLogin;
    UserRepository userRepository;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etEmail = findViewById(R.id.etEmail);
        etContra = findViewById(R.id.etContra);
        btnLogin = findViewById(R.id.btnLogin);
        userRepository = new UserRepository();

        // Configuración de EncryptedSharedPreferences
        try {
            MasterKey masterKey = new MasterKey.Builder(this)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build();

            sharedPreferences = EncryptedSharedPreferences.create(
                    this,
                    "secure_prefs",
                    masterKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );
        } catch (Exception e) {
            e.printStackTrace();
        }

        btnLogin.setOnClickListener(v -> {
            String email = etEmail.getText().toString();
            String password = etContra.getText().toString();
            login(email, password);
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

                        // Guardar información del usuario en EncryptedSharedPreferences
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString(EMAIL, user.getEmail());
                        editor.apply();
                        break;
                    }
                }

                if (loginSuccessful) {
                    Toast.makeText(MainActivity.this, "Login correcto", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(MainActivity.this, Libreria.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(MainActivity.this, "Login incorrecto", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Toast.makeText(MainActivity.this, "Error fetching users", Toast.LENGTH_SHORT).show();
            }
        });
    }
}