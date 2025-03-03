package com.example.biblioteisandroid2.Componentes.Libreria;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.biblioteisandroid2.API.models.Book;
import com.example.biblioteisandroid2.InfoLibro;
import com.example.biblioteisandroid2.R;

import java.util.List;

public class BookAdapter extends RecyclerView.Adapter<BookViewHolder> {

    private List<Book> books;
    private FragmentActivity activity;

    public BookAdapter(FragmentActivity activity, List<Book> books) {
        this.activity = activity;
        this.books = books;
    }

    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_libro, parent, false);
        return new BookViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookViewHolder holder, int position) {
        Book book = books.get(position);
        holder.tvTitle.setText(book.getTitle());
        holder.tvAuthor.setText(book.getAuthor());
        holder.tvPublishedDate.setText(String.valueOf(book.getPublishedDate()));
        String imageName = "book_" + book.getBookPicture();  // "book_" + ID de la imagen
        Log.d("BookAdapter", "Book res: " + imageName);
        int imageResId = activity.getResources().getIdentifier(imageName, "drawable", activity.getPackageName());
        Log.d("BookAdapter", "Book res ID: " + imageResId);
        if (imageResId != 0) {  // Si la imagen existe
            holder.bookPicture.setImageResource(imageResId);
        } else {
            holder.bookPicture.setImageResource(R.drawable.default_image);
        }
        // Log the book picture value
        Log.d("BookAdapter", "Book picture: " + book.getBookPicture());
        Log.d("BookAdapter", "Book picture type: " + book.getBookPicture().getClass().getName());

        holder.btnVerInfoLibro.setOnClickListener(v -> {
            //Cojo la id del elemento seleccionado
            int id = book.getId();

            //Creo intent a detalle de libro
            Intent intent = new Intent(activity, InfoLibro.class);

            //Cargo el id en el intent
            intent.putExtra( InfoLibro.BOOK_ID_EXTRA, id);

            //Lanzo el intent
            activity.startActivity(intent);

        });

    }

    @Override
    public int getItemCount() {
        System.out.println("BookAdapter getItemCount: " + books.size());
        return books.size();
    }

    // Actualiza la lista de libros con una nueva lista de libros.

    public void updateBooks(List<Book> newBooks) {
        // Limpiar la lista de libros actual.
        books.clear();
        // Añadir todos los libros de la nueva lista a la lista de libros.
        books.addAll(newBooks);
        // Notificar al adaptador que los datos han cambiado, para que la vista se actualice.
        notifyDataSetChanged();
    }

}
