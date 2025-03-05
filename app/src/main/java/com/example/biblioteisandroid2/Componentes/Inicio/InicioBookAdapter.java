package com.example.biblioteisandroid2.Componentes.Inicio;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.biblioteisandroid2.API.models.Book;
import com.example.biblioteisandroid2.R;

import java.util.List;

public class InicioBookAdapter extends RecyclerView.Adapter<InicioBookAdapter.BookViewHolder> {

    private List<Book> bookList;

    // Constructor para recibir la lista de libros
    public InicioBookAdapter(List<Book> bookList) {
        this.bookList = bookList;
    }

    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflar el layout de cada ítem
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_inicio, parent, false); // Usamos tu fragmento XML
        return new BookViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookViewHolder holder, int position) {
        // Obtener el libro y asignar los valores a los elementos
        Book book = bookList.get(position);
        holder.titleTextView.setText(book.getTitle());
        holder.authorTextView.setText(book.getAuthor());
        holder.publishedDateTextView.setText(book.getPublishedDate());
        // Aquí podrías poner una imagen real si tienes una URL o recurso
        holder.bookImageView.setImageResource(R.drawable.book_1);  // Usamos una imagen placeholder por ahora
    }

    @Override
    public int getItemCount() {
        return bookList.size();
    }

    public static class BookViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView, authorTextView, publishedDateTextView;
        ImageView bookImageView;
        Button btnVerInfoLibro;

        public BookViewHolder(View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.title);
            authorTextView = itemView.findViewById(R.id.author);
            publishedDateTextView = itemView.findViewById(R.id.publishedDate);
            bookImageView = itemView.findViewById(R.id.bookPicture);
            btnVerInfoLibro = itemView.findViewById(R.id.btnVerInfoLibro);
        }
    }
}
