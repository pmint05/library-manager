package com.app.librarymanager.models;

import com.google.cloud.Timestamp;
import lombok.Data;

@Data
public class BookRating {

  private String _id;
  private String bookId;
  private String userId;
  private double rate;

  BookRating() {
    _id = null;
    bookId = null;
    userId = null;
    rate = -1;
  }

  BookRating(String _id, String bookId, String userId, double rate) {
    this._id = _id;
    this.bookId = bookId;
    this.userId = userId;
    this.rate = rate;
  }

  BookRating(Book book, User user, double rate) {
    this._id = null;
    this.bookId = book.getId();
    this.userId = user.getId();
    this.rate = rate;
  }
}

