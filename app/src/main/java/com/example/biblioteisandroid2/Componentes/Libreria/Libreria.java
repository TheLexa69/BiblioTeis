package com.example.biblioteisandroid2.Componentes.Libreria;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.MenuProvider;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.biblioteisandroid2.API.models.Book;
import com.example.biblioteisandroid2.API.repository.BookRepository;
import com.example.biblioteisandroid2.MainActivity;
import com.example.biblioteisandroid2.R;

import java.util.ArrayList;
import java.util.List;

public class Libreria extends AppCompatActivity {

    private RecyclerView recyclerView;
    private BookAdapter bookAdapter;
    private List<Book> bookList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_libreria);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_libreria), (v, insets) -> {
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

                //Aqui desactivamos la opcion de ir a la libreria
                MenuItem inicioLibreriaItem = menu.findItem(R.id.inicio_libreria);
                if (inicioLibreriaItem != null) {
                    inicioLibreriaItem.setEnabled(false);
                }
            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                int id = menuItem.getItemId();
//                if (id == R.id.inicio_libreria) {
//                    Toast.makeText(Libreria.this, "Libreria", Toast.LENGTH_SHORT).show();
//                    Intent logoutIntent = new Intent(Libreria.this, Libreria.class);
//                    logoutIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                    startActivity(logoutIntent);
//                    return true;
//                }
                if (id == R.id.cerrar_sesion) {
                    Toast.makeText(Libreria.this, "Cierre de Sesión", Toast.LENGTH_SHORT).show();
                    Intent logoutIntent = new Intent(Libreria.this, MainActivity.class);
                    logoutIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(logoutIntent);
                    return true;
                }
                return false;
            }


        });
        //        FIN TOOLBAR


        recyclerView = findViewById(R.id.rvContainer);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        new BookRepository().getBooks(new BookRepository.ApiCallback<List<Book>>() {
            @Override
            public void onSuccess(List<Book> list) {
                bookList = list;
                setAdapter(list);
                System.out.println("Books fetched");
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e("Libreria", "Error fetching books", t);
                System.out.println("Error fetching books");
            }
        });
    }

    private void setAdapter(List<Book> list) {
        bookAdapter = new BookAdapter(this, list);
        recyclerView.setAdapter(new BookAdapter(this, list));
    }

    //    TOOLBAR


//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        int id = item.getItemId();
//        if (id == R.id.cerrar_sesion) {
//            Intent logoutIntent = new Intent(this, MainActivity.class);
//            logoutIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//            startActivity(logoutIntent);
//            return true;
//        } else if (id == R.id.inicio_libreria) {
//            Intent homeIntent = new Intent(this, Libreria.class);
//            startActivity(homeIntent);
//            return true;
//        } else {
//            return super.onOptionsItemSelected(item);
//        }
//    }
}