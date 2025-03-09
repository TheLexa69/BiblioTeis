package com.example.biblioteisandroid2.Componentes.Usuario;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.biblioteisandroid2.R;

/**
 * ViewHolder para los ítems del RecyclerView. Contiene las vistas a mostrar para cada préstamo de libro.
 */
public class BookLendingViewHolder extends RecyclerView.ViewHolder {
    TextView title, loanDate, dueDate;
    ImageView bookImageView;
    Button btnVerInfoLibro;
    /**
     * Constructor que inicializa las vistas asociadas a un ítem de libro prestado.
     *
     * @param itemView Vista del ítem en el RecyclerView.
     */
    public BookLendingViewHolder(@NonNull View itemView) {
        super(itemView);
        title = itemView.findViewById(R.id.title);
        loanDate = itemView.findViewById(R.id.loanDate);
        dueDate = itemView.findViewById(R.id.dueDate);
        bookImageView = itemView.findViewById(R.id.bookPicture);
        btnVerInfoLibro = itemView.findViewById(R.id.btnVerInfoLibro);
    }
}
