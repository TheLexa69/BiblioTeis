package com.example.biblioteisandroid2.Componentes.Inicio;

import static androidx.core.app.ActivityCompat.startActivityForResult;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.biblioteisandroid2.API.models.Book;
import com.example.biblioteisandroid2.R;
import com.example.biblioteisandroid2.ScannerActivity;

import java.util.List;

public class InicioBookAdapter extends RecyclerView.Adapter<BookInicioViewHolder> {

    private List<Book> bookList;

    /**
     * Constructor para recibir la lista de libgitros.
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
    public BookInicioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflar el layout de cada ítem
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_inicio, parent, false);
        return new BookInicioViewHolder(view);
    }

    /**
     * Método que se llama para enlazar los datos de un libro con las vistas del ViewHolder.
     * Asigna los valores del libro (título, autor, fecha de publicación) a las vistas correspondientes.
     *
     * @param holder   El ViewHolder que contiene las vistas de cada ítem.
     * @param position La posición del libro en la lista.
     */
    @Override
    public void onBindViewHolder(@NonNull BookInicioViewHolder holder, int position) {
        // Obtener el libro y asignar los valores a los elementos
        Book book = bookList.get(position);
        holder.titleTextView.setText(book.getTitle());
        holder.authorTextView.setText(book.getAuthor());
        holder.publishedDateTextView.setText(book.getPublishedDate());
        // Aquí podrías poner una imagen real si tienes una URL o recurso
        holder.bookImageView.setImageResource(R.drawable.book_1);
        //Para el boton ver mas...
        holder.btnVerInfoLibro.setOnClickListener(v -> {
            Toast.makeText(holder.itemView.getContext(), "Escanea el código QR del libro", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(holder.itemView.getContext(), ScannerActivity.class);
            intent.putExtra("BOOK_ID", book.getId());
            ((Activity) holder.itemView.getContext()).startActivityForResult(intent, 1);
      });
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
}
