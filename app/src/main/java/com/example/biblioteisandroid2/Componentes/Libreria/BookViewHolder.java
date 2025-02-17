package com.example.biblioteisandroid2.Componentes.Libreria;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.biblioteisandroid2.R;

public class BookViewHolder extends RecyclerView.ViewHolder {

    TextView tvTitle;
    TextView tvAuthor;
    TextView tvPublishedDate;
    ImageView bookPicture;
    Button btnVerInfoLibro;



    public BookViewHolder(@NonNull View itemView) {
        super(itemView);

        tvTitle = itemView.findViewById(R.id.title);
        tvAuthor = itemView.findViewById(R.id.author);
        tvPublishedDate = itemView.findViewById(R.id.publishedDate);
        bookPicture = itemView.findViewById(R.id.bookPicture);
        btnVerInfoLibro = itemView.findViewById(R.id.btnVerInfoLibro);

    }


}
