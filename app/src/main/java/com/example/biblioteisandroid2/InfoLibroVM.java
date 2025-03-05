package com.example.biblioteisandroid2;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.biblioteisandroid2.API.models.Book;
import com.example.biblioteisandroid2.API.repository.BookRepository;

/**
 * ViewModel para gestionar la información de un libro.
 * Se encarga de obtener los datos del libro desde el repositorio
 * y almacenarlos en un LiveData para ser observados por la UI.
 */
public class InfoLibroVM extends ViewModel {

    /** LiveData que contiene la información del libro. */
    private MutableLiveData<Book> libro = new MutableLiveData<>();

    /**
     * Método para obtener un libro por su ID.
     * Llama al repositorio para recuperar los datos del libro y los almacena en LiveData.
     *
     * @param id ID del libro a obtener.
     */
    public void getBook(int id) {

        BookRepository bookRepository = new BookRepository();
        bookRepository.getBookById(id, new BookRepository.ApiCallback<Book>() {
            @Override
            public void onSuccess(Book book) {
                libro.postValue(book);
            }

            @Override
            public void onFailure(Throwable t) {
                libro.postValue(null);
            }
        });
    }

    /**
     * Método para obtener el LiveData del libro.
     *
     * @return LiveData que contiene la información del libro.
     */
    public MutableLiveData<Book> getLibro() {
        return libro;
    }
}
