package com.example.biblioteisandroid2;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.biblioteisandroid2.API.models.Book;
import com.example.biblioteisandroid2.API.repository.BookRepository;

public class InfoLibroVM extends ViewModel {

    private MutableLiveData<Book> libro = new MutableLiveData<>();

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

    public MutableLiveData<Book> getLibro() {
        return libro;
    }
}
