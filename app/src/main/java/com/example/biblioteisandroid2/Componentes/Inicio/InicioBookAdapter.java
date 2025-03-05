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

    /**
     * Constructor para recibir la lista de libros.
     *
     * @param bookList Lista de libros a mostrar en el RecyclerView.
     */
    public InicioBookAdapter(List<Book> bookList) {
        this.bookList = bookList;
    }

    /**
     * Método que se llama cuando se crea una nueva vista para un ítem en el RecyclerView.
     * Este método infla el layout de cada ítem y devuelve un ViewHolder.
     *
     * @param parent   El ViewGroup al que se añadirá la vista.
     * @param viewType El tipo de vista que se va a crear.
     * @return Un nuevo objeto BookViewHolder con la vista inflada.
     */
    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflar el layout de cada ítem
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_inicio, parent, false);
        return new BookViewHolder(view);
    }

    /**
     * Método que se llama para enlazar los datos de un libro con las vistas del ViewHolder.
     * Asigna los valores del libro (título, autor, fecha de publicación) a las vistas correspondientes.
     *
     * @param holder   El ViewHolder que contiene las vistas de cada ítem.
     * @param position La posición del libro en la lista.
     */
    @Override
    public void onBindViewHolder(@NonNull BookViewHolder holder, int position) {
        // Obtener el libro y asignar los valores a los elementos
        Book book = bookList.get(position);
        holder.titleTextView.setText(book.getTitle());
        holder.authorTextView.setText(book.getAuthor());
        holder.publishedDateTextView.setText(book.getPublishedDate());
        // Aquí podrías poner una imagen real si tienes una URL o recurso
        holder.bookImageView.setImageResource(R.drawable.book_1);
    }

    /**
     * Método que devuelve el número total de ítems en la lista.
     * Este valor es utilizado por RecyclerView para determinar cuántos ítems mostrar.
     *
     * @return El tamaño de la lista de libros.
     */
    @Override
    public int getItemCount() {
        return bookList.size();
    }

    /**
     * ViewHolder que contiene las vistas de cada ítem del RecyclerView.
     * Se encarga de inicializar las vistas y asignarlas a las variables correspondientes.
     */
    public static class BookViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView, authorTextView, publishedDateTextView;
        ImageView bookImageView;
        Button btnVerInfoLibro;

        /**
         * Constructor que inicializa las vistas del ítem.
         *
         * @param itemView La vista del ítem del RecyclerView.
         */
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
