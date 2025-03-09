package com.example.biblioteisandroid2.Componentes.Libreria;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.biblioteisandroid2.API.models.Book;
import com.example.biblioteisandroid2.API.repository.BookRepository;
import com.example.biblioteisandroid2.InfoLibro;
import com.example.biblioteisandroid2.R;

import java.util.List;
import java.util.Objects;

/**
 * Adaptador para la lista de libros en la biblioteca.
 * Maneja la visualización de los libros en un RecyclerView.
 */
public class BookAdapter extends RecyclerView.Adapter<BookViewHolder> {

    private List<Book> books;
    private FragmentActivity activity;
    private int userId;

    /**
     * Constructor del adaptador de libros.
     *
     * @param activity La actividad donde se mostrará el RecyclerView.
     * @param books Lista de libros a mostrar.
     * @param userId ID del usuario actual.
     */
    public BookAdapter(FragmentActivity activity, List<Book> books, int userId) {
        this.activity = activity;
        this.books = books;
        this.userId = userId;
    }

    /**
     * Crea una nueva vista para un libro en la lista.
     *
     * @param parent   El ViewGroup en el que se añadirá la nueva vista.
     * @param viewType El tipo de vista.
     * @return Un nuevo ViewHolder que contiene la vista del libro.
     */
    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_libro, parent, false);
        return new BookViewHolder(view);
    }

    /**
     * Asocia los datos de un libro a un ViewHolder.
     *
     * @param holder   ViewHolder que debe ser actualizado.
     * @param position Posición del libro en la lista.
     */
    @Override
    public void onBindViewHolder(@NonNull BookViewHolder holder, int position) {
        Log.d("BookAdapter", "Libros " + books.get(position).getIsbn());

        Book book = books.get(position);
        holder.tvTitle.setText(book.getTitle());
        holder.tvAuthor.setText("Autor: " + book.getAuthor());
//        holder.tvPublishedDate.setText(String.valueOf(book.getPublishedDate()));
        holder.tvPublishedDate.setText("Fecha Publicación: " + BookUtils.transformarFecha(String.valueOf(book.getPublishedDate())));

        // Configurar la imagen del libro
        String imageName = "book_" + book.getBookPicture();  // "book_" + ID de la imagen
        int imageResId = activity.getResources().getIdentifier(imageName, "drawable", activity.getPackageName());
        if (imageResId != 0) {  // Si la imagen existe
            holder.bookPicture.setImageResource(imageResId);
        } else {
            holder.bookPicture.setImageResource(R.drawable.default_image);
        }

        // Obtener la cantidad de existencias del libro
        obtenerExistencias(book, holder.tvExistencias, holder.tvDisponibles);

        holder.btnVerInfoLibro.setOnClickListener(v -> {
            // Cojo la id del elemento seleccionado
            int id = book.getId();

            // Creo intent a detalle de libro
            Intent intent = new Intent(activity, InfoLibro.class);

            // Cargo el id en el intent
            intent.putExtra(InfoLibro.BOOK_ID_EXTRA, id);
            intent.putExtra(InfoLibro.USER_ID_EXTRA, userId);

            Log.d("BookAdapter", "Enviando a InfoLibro -> Book ID: " + id + ", User ID: " + userId);

            // Lanzo el intent
            activity.startActivity(intent);
        });
    }

    /**
     * Devuelve el número total de libros en la lista.
     *
     * @return Cantidad de libros en la lista.
     */
    @Override
    public int getItemCount() {
        return books.size();
    }

    /**
     * Actualiza la lista de libros en el RecyclerView.
     *
     * @param newBooks Nueva lista de libros a mostrar.
     */
    public void updateBooks(List<Book> newBooks) {
        Log.d("BookAdapter", "Actualizando lista con " + newBooks.size() + " libros");
        books.clear();
        books.addAll(newBooks);
        notifyDataSetChanged();  // Asegurar que se actualiza la UI
    }

    /**
     * Obtiene la cantidad total y disponible de un libro en la biblioteca.
     *
     * @param book        Libro del cual se quieren obtener las existencias.
     * @param existencias TextView donde se mostrará la cantidad total de existencias.
     * @param disponibles TextView donde se mostrará la cantidad de libros disponibles.
     */
    public static void obtenerExistencias(Book book, TextView existencias, TextView disponibles) {
        BookRepository bookRepository = new BookRepository();
        bookRepository.getBooks(new BookRepository.ApiCallback<List<Book>>() {
            @Override
            public void onSuccess(List<Book> result) {
                int existenciasTotal = 0;
                int existenciasDisponibles = 0;
                for (Book b : result) {
                    if (Objects.equals(b.getIsbn(), book.getIsbn())) {
                        existenciasTotal++;
                        existenciasDisponibles += b.isAvailable() ? 1 : 0;
                    }
                }
                existencias.setText("Totales: " + existenciasTotal);
                disponibles.setText("Disponibles: " + existenciasDisponibles);
            }

            @Override
            public void onFailure(Throwable t) {
                Toast.makeText(existencias.getContext(), "Error al obtener las existencias", Toast.LENGTH_SHORT).show();
                existencias.setText("Error");
                disponibles.setText("Error");
            }
        });
    }
}
