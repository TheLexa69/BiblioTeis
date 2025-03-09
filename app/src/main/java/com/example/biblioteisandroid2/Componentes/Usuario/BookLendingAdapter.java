package com.example.biblioteisandroid2.Componentes.Usuario;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.biblioteisandroid2.API.models.Book;
import com.example.biblioteisandroid2.API.models.BookLending;
import com.example.biblioteisandroid2.InfoLibro;
import com.example.biblioteisandroid2.R;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Locale;

/**
 * Adapter que gestiona la visualización de los libros prestados en un RecyclerView.
 * Muestra información como el título del libro, la fecha de préstamo y la fecha de devolución.
 * Resalta los libros vencidos con un color rojo.
 */
public class BookLendingAdapter extends RecyclerView.Adapter<BookLendingViewHolder> {

    private final List<BookLending> bookLendingList;
    private final int userId;
    private static final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.getDefault());

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
    public BookLendingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_libro_prestado, parent, false);
        return new BookLendingViewHolder(view);
    }

    /**
     * Asocia los datos del préstamo de un libro con la vista correspondiente.
     *
     * @param holder El holder que contiene las vistas a las que se asignarán los datos.
     * @param position La posición del ítem dentro de la lista.
     */
    @Override
    public void onBindViewHolder(@NonNull BookLendingViewHolder holder, int position) {
        BookLending bookLending = bookLendingList.get(position);

        // Evitar valores nulos en los textos
        holder.title.setText(bookLending.getBook() != null ? bookLending.getBook().getTitle() : "Título no disponible");
        String prestamo = formatDate(bookLending.getLendDate());
        String devolucion = bookLending.getLendDate() != null
                ? LocalDateTime.parse(bookLending.getLendDate()).toLocalDate()
                .plusMonths(1)
                .format(DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.getDefault()))
                : "No disponible";
        holder.loanDate.setText("  Fecha de Préstamo: " + prestamo);
        holder.dueDate.setText("Fecha de Devolución: " + devolucion);

        holder.bookImageView.setImageResource(R.drawable.book_1);

        // Resaltar si quedan menos de 15 dias para que venza
        if (isLessThanFifteenDaysUntilDue(bookLending.getLendDate())) {
            holder.itemView.setBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.rojo));
            holder.dueDate.setText("   ¡Vence pronto! " + devolucion);
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
            Log.d("BookLendingAdapter", "Date string is null or empty");
            return "No disponible";
        }
        try {
            Log.d("BookLendingAdapter", "Parsing date string: " + dateString);
            LocalDateTime dateTime = LocalDateTime.parse(dateString);
            LocalDate date = dateTime.toLocalDate();
            DateTimeFormatter outputFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.getDefault());
            String formattedDate = date.format(outputFormat);
            Log.d("BookLendingAdapter", "Formatted date: " + formattedDate);
            return formattedDate;
        } catch (Exception e) {
            Log.e("BookLendingAdapter", "Error parsing date string: " + dateString, e);
            return "Formato de fecha inválido";
        }
    }

    /**
     * Verifica si quedan menos de 15 días hasta la fecha de devolución.
     *
     * @param lendDateString La fecha de devolución del libro.
     * @return True si quedan menos de 15 días, de lo contrario, false.
     */
    private boolean isLessThanFifteenDaysUntilDue(String lendDateString) {
        if (lendDateString == null || lendDateString.isEmpty()) {
            return false;
        }
        try {
            // Parsear la fecha de préstamo con LocalDateTime y extraer LocalDate
            LocalDate lendDate = LocalDateTime.parse(lendDateString).toLocalDate();
            LocalDate dueDate = lendDate.plusMonths(1);

            long daysUntilDue = ChronoUnit.DAYS.between(LocalDate.now(), dueDate);
            return daysUntilDue <= 15;
        } catch (Exception e) {
            Log.e("BookLendingAdapter", "Error parsing lend date: " + lendDateString, e);
            return false;
        }
    }

}