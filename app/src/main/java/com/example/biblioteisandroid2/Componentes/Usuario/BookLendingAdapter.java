package com.example.biblioteisandroid2.Componentes.Usuario;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.biblioteisandroid2.API.models.BookLending;
import com.example.biblioteisandroid2.InfoLibro;
import com.example.biblioteisandroid2.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Adapter que gestiona la visualización de los libros prestados en un RecyclerView.
 * Muestra información como el título del libro, la fecha de préstamo y la fecha de devolución.
 * Resalta los libros vencidos con un color rojo.
 */
public class BookLendingAdapter extends RecyclerView.Adapter<BookLendingAdapter.BookViewHolder> {

    private final List<BookLending> bookLendingList;
    private final int userId;
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    /**
     * Constructor que inicializa la lista de libros prestados.
     *
     * @param bookLendingList Lista de objetos BookLending que serán mostrados en el RecyclerView.
     */
    public BookLendingAdapter(List<BookLending> bookLendingList, int userId) {
        this.bookLendingList = bookLendingList != null ? bookLendingList : List.of();
        this.userId = userId;
    }

    /**
     * Crea una nueva vista de un ítem en el RecyclerView.
     *
     * @param parent El contenedor en el que se creará la vista.
     * @param viewType El tipo de vista que se debe crear.
     * @return Un nuevo objeto BookViewHolder que contiene la vista.
     */
    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_libro_prestado, parent, false);
        return new BookViewHolder(view);
    }

    /**
     * Asocia los datos del préstamo de un libro con la vista correspondiente.
     *
     * @param holder El holder que contiene las vistas a las que se asignarán los datos.
     * @param position La posición del ítem dentro de la lista.
     */
    @Override
    public void onBindViewHolder(@NonNull BookViewHolder holder, int position) {
        BookLending bookLending = bookLendingList.get(position);

        // Evitar valores nulos en los textos
        holder.title.setText(bookLending.getBook() != null ? bookLending.getBook().getTitle() : "Título no disponible");
        holder.loanDate.setText("Fecha de Préstamo: " + formatDate(bookLending.getLendDate()));
        holder.dueDate.setText("Fecha de Devolución: " + formatDate(bookLending.getReturnDate()));
        holder.bookImageView.setImageResource(R.drawable.book_1);

        // Resaltar si está vencido
        if (isOverdue(bookLending.getReturnDate())) {
            holder.itemView.setBackgroundColor(Color.RED);
        } else {
            holder.itemView.setBackgroundColor(Color.TRANSPARENT);
        }

        holder.btnVerInfoLibro.setOnClickListener(v -> {
            Context context = v.getContext();
            Intent intent = new Intent(context, InfoLibro.class);
            intent.putExtra(InfoLibro.BOOK_ID_EXTRA, bookLending.getBook().getId());
            intent.putExtra(InfoLibro.USER_ID_EXTRA, userId);
            context.startActivity(intent);
        });
    }

    /**
     * Retorna la cantidad total de ítems en la lista de libros prestados.
     *
     * @return El número de libros prestados.
     */
    @Override
    public int getItemCount() {
        return bookLendingList.size(); // Retornamos el tamaño de la lista de préstamos
    }

    /**
     * Formatea una fecha en el formato "yyyy-MM-dd".
     *
     * @param dateString La fecha a formatear.
     * @return La fecha formateada.
     */
    private String formatDate(String dateString) {
        if (dateString == null || dateString.isEmpty()) {
            return "No disponible";
        }
        return dateString; // Se asume que ya viene en "yyyy-MM-dd", de lo contrario, se podría formatear.
    }

    /**
     * Verifica si la fecha de devolución está vencida en comparación con la fecha actual.
     *
     * @param returnDateString La fecha de devolución del libro.
     * @return True si el libro está vencido, de lo contrario, false.
     */
    private boolean isOverdue(String returnDateString) {
        if (returnDateString == null || returnDateString.isEmpty()) {
            return false;
        }
        try {
            Date returnDate = dateFormat.parse(returnDateString);
            return returnDate != null && returnDate.before(new Date());
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * ViewHolder para los ítems del RecyclerView. Contiene las vistas a mostrar para cada préstamo de libro.
     */
    public static class BookViewHolder extends RecyclerView.ViewHolder {
        TextView title, loanDate, dueDate;
        ImageView bookImageView;
        Button btnVerInfoLibro;
        /**
         * Constructor que inicializa las vistas asociadas a un ítem de libro prestado.
         *
         * @param itemView Vista del ítem en el RecyclerView.
         */
        public BookViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            loanDate = itemView.findViewById(R.id.loanDate);
            dueDate = itemView.findViewById(R.id.dueDate);
            bookImageView = itemView.findViewById(R.id.bookPicture);
            btnVerInfoLibro = itemView.findViewById(R.id.btnVerInfoLibro);
        }
    }
}
