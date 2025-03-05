package com.example.biblioteisandroid2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
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

    EditText etEmail, etContra;
    Button btnLogin;
    UserRepository userRepository;
    SharedPreferences sharedPreferences;
    private static final String EMAIL = "logged_email";
    private static final String PASSWORD = "logged_password";

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
            // Tomamos los valores reales del EditText
            String email = etEmail.getText().toString().trim();
            String password = etContra.getText().toString().trim();

            // Si están vacíos, usamos valores por defecto
            if (email.isEmpty()) email = "alice@example.com";
            if (password.isEmpty()) password = "hashedpassword1";

            Log.d("MainActivity", "Intentando login con: " + email + " - " + password);

            login(email, password);
        });
    }

    private void login(String email, String password) {
        userRepository.getUsers(new BookRepository.ApiCallback<List<User>>() {
            @Override
            public void onSuccess(List<User> users) {
                int userId = -1;
                User usuarioAcreditado = null;

                Log.d("MainActivity", "Usuarios obtenidos: " + users.size());

                for (User user : users) {
                    Log.d("MainActivity", "Verificando: " + user.getEmail() + " - " + user.getPasswordHash());

                    // Eliminamos espacios y aseguramos coincidencia exacta
                    if (user.getEmail().trim().equalsIgnoreCase(email.trim()) &&
                            user.getPasswordHash().trim().equals(password.trim())) {

                        usuarioAcreditado = user;
                        userId = user.getId();
                        Log.d("MainActivity", "Usuario encontrado: " + userId);
                        break;
                    }
                }

                if (userId != -1) {
                    Toast.makeText(MainActivity.this, "Login correcto", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(MainActivity.this, Inicio_activity.class);
                    intent.putExtra("USER_ID", userId);
                    startActivity(intent);

                    // Guardar información del usuario en EncryptedSharedPreferences
                    sharedPreferences.edit()
                            .putString(EMAIL, usuarioAcreditado.getEmail())
                            .putString(PASSWORD, usuarioAcreditado.getPasswordHash())
                            .apply();


                } else {
                    Log.d("MainActivity", "Login incorrecto");
                    Toast.makeText(MainActivity.this, "Login incorrecto", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e("MainActivity", "Error al obtener usuarios", t);
                Toast.makeText(MainActivity.this, "Error al obtener usuarios", Toast.LENGTH_SHORT).show();
            }
        });
    }

}