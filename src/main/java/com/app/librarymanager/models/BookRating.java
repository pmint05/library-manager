package com.app.librarymanager.models;

import com.google.cloud.Timestamp;
import lombok.Data;

@Data
public class BookRating {

  private String id;
  private String bookId;
  private String userId;
  private double rate;
  private Timestamp createdTime;
  private Timestamp updatedTime;

  BookRating() {
    id = null;
    bookId = null;
    userId = null;
    rate = -1;
    createdTime = null;
    updatedTime = null;
  }

  BookRating(String id, String bookId, String userId, double rate) {
    this.id = id;
    this.bookId = bookId;
    this.userId = userId;
    this.rate = rate;
  }
}

