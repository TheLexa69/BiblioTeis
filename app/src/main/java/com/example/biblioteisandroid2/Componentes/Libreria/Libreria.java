package com.example.biblioteisandroid2.Componentes.Libreria;

import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.biblioteisandroid2.API.models.Book;
import com.example.biblioteisandroid2.API.repository.BookRepository;
import com.example.biblioteisandroid2.R;

import java.util.List;

public class Libreria extends AppCompatActivity {

    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_libreria);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        recyclerView = findViewById(R.id.rvContainer);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        new BookRepository().getBooks(new BookRepository.ApiCallback<List<Book>>() {
            @Override
            public void onSuccess(List<Book> list) {
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
        recyclerView.setAdapter(new BookAdapter(this, list));
    }
}