package com.example.biblioteisandroid2.Componentes.Inicio;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.biblioteisandroid2.R;

/**
 * ViewHolder que contiene las vistas de cada ítem del RecyclerView.
 * Se encarga de inicializar las vistas y asignarlas a las variables correspondientes.
 */
public class BookInicioViewHolder extends RecyclerView.ViewHolder {
    TextView titleTextView, authorTextView, publishedDateTextView;
    ImageView bookImageView;
    Button btnVerInfoLibro;

    /**
     * Constructor que inicializa las vistas del ítem.
     *
     * @param itemView La vista del ítem del RecyclerView.
     */
    public BookInicioViewHolder(View itemView) {
        super(itemView);
        titleTextView = itemView.findViewById(R.id.title);
        authorTextView = itemView.findViewById(R.id.author);
        publishedDateTextView = itemView.findViewById(R.id.publishedDate);
        bookImageView = itemView.findViewById(R.id.bookPicture);
        btnVerInfoLibro = itemView.findViewById(R.id.btnVerInfoLibro);
    }
}