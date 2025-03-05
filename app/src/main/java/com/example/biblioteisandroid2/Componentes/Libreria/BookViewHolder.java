package com.example.biblioteisandroid2.Componentes.Libreria;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.biblioteisandroid2.R;

/**
 * ViewHolder para representar un libro en la lista de RecyclerView.
 * Contiene las vistas necesarias para mostrar la información de un libro.
 */
public class BookViewHolder extends RecyclerView.ViewHolder {

    TextView tvTitle;  // Título del libro
    TextView tvAuthor;  // Autor del libro
    TextView tvPublishedDate;  // Fecha de publicación del libro
    ImageView bookPicture;  // Imagen de la portada del libro
    Button btnVerInfoLibro;  // Botón para ver detalles del libro

    TextView tvExistencias;  // Texto que muestra la cantidad total de existencias
    TextView tvDisponibles;  // Texto que muestra la cantidad de libros disponibles

    /**
     * Constructor del ViewHolder.
     *
     * @param itemView Vista del elemento en la lista.
     */
    public BookViewHolder(@NonNull View itemView) {
        super(itemView);

        tvTitle = itemView.findViewById(R.id.title);
        tvAuthor = itemView.findViewById(R.id.author);
        tvPublishedDate = itemView.findViewById(R.id.publishedDate);
        bookPicture = itemView.findViewById(R.id.bookPicture);
        btnVerInfoLibro = itemView.findViewById(R.id.btnVerInfoLibro);
        tvExistencias = itemView.findViewById(R.id.tvExistencias);
        tvDisponibles = itemView.findViewById(R.id.tvDisponibles);
    }
}
