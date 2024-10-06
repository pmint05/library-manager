package com.app.librarymanager.models;

import java.util.ArrayList;

public class Member extends User {

  public static class BorrowedBook {

    private Book infoBook;
    private String borrowDate;
    private String returnDate;
    private String expectedReturnDate;
  }

  ArrayList<BorrowedBook> borrowedBooks;
  ArrayList<Book> favoriteBooks;
  ArrayList<Book> viewedBooks;

  Member() {
    borrowedBooks = new ArrayList<>();
    favoriteBooks = new ArrayList<>();
    viewedBooks = new ArrayList<>();
  }

  public void addFavoriteBooks(Book book) {
    favoriteBooks.add(book);
  }
}
