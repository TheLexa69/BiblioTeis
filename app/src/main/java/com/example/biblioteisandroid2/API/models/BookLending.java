package com.example.biblioteisandroid2.API.models;

public class BookLending {
    private int id;
    private int bookId;
    private int userId;
    private String lendDate;
    private String returnDate;

    private User user;
    private Book book;

    // Getters & Setters


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getLendDate() {
        return lendDate;
    }

    public void setLendDate(String lendDate) {
        this.lendDate = lendDate;
    }

    public String getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(String returnDate) {
        this.returnDate = returnDate;
    }

    public Book getBook() {return book;}
    public void setBook(Book book) {this.book = book;}

    public User getUser() {return user;}
    public void setUser(User user) {this.user = user;}

    @Override
    public String toString() {
        return "BookLending{" +
                "id=" + id +
                ", bookId=" + bookId +
                ", userId=" + userId +
                ", lendDate='" + lendDate + '\'' +
                ", returnDate='" + returnDate + '\'' +
                ", user=" + user +
                ", book=" + book +
                '}';
    }
}