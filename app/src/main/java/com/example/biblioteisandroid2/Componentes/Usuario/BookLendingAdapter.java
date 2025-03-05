package com.example.biblioteisandroid2.Componentes.Usuario;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.biblioteisandroid2.API.models.BookLending;
import com.example.biblioteisandroid2.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class BookLendingAdapter extends RecyclerView.Adapter<BookLendingAdapter.BookViewHolder> {

    private final List<BookLending> bookLendingList;
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    public BookLendingAdapter(List<BookLending> bookLendingList) {
        this.bookLendingList = bookLendingList != null ? bookLendingList : List.of();
    }

    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_libro_prestado, parent, false);
        return new BookViewHolder(view);
    }

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
    }

    @Override
    public int getItemCount() {
        return bookLendingList.size();
    }

    private String formatDate(String dateString) {
        if (dateString == null || dateString.isEmpty()) {
            return "No disponible";
        }
        return dateString; // Se asume que ya viene en "yyyy-MM-dd", de lo contrario, se podría formatear.
    }

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

    public static class BookViewHolder extends RecyclerView.ViewHolder {
        TextView title, loanDate, dueDate;
        ImageView bookImageView;
        public BookViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            loanDate = itemView.findViewById(R.id.loanDate);
            dueDate = itemView.findViewById(R.id.dueDate);
            bookImageView = itemView.findViewById(R.id.bookPicture);
        }
    }
}
