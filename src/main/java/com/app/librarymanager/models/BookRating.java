package com.app.librarymanager.models;

import com.google.cloud.Timestamp;
import lombok.Data;
import org.bson.Document;
import org.bson.types.ObjectId;

@Data
public class BookRating extends BookUser {

  private double rate;

  public BookRating() {
    super();
    rate = -1;
  }

  public BookRating(String _id, String bookId, String userId, double rate) {
    super(_id, bookId, userId);
    this.rate = rate;
  }

  public BookRating(ObjectId _id, String bookId, String userId, double rate) {
    super(_id, bookId, userId);
    this.rate = rate;
  }

  public BookRating(Book book, User user, double rate) {
    super(book.getId(), user.getId());
    this.rate = rate;
  }

  public BookRating(Document document) {
    super(document);
    this.rate = document.getDouble("rate");
  }
}

