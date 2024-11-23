package com.app.librarymanager.controllers;

import com.app.librarymanager.models.BookLoan;
import com.app.librarymanager.models.BookRating;
import com.app.librarymanager.services.MongoDB;
import java.util.Date;
import java.util.Map;
import org.bson.Document;
import org.bson.types.ObjectId;

public class BookLoanController {

  public static Document findLoan(BookLoan rating) {
    return MongoDB.getInstance().findAnObject("bookRating",
        Map.of("userId", rating.getUserId(), "bookId", rating.getBookId()));
  }

  public static Document addLoan(BookLoan bookLoan) {
    Document currentLoan = findLoan(bookLoan);
    ObjectId idInDatabase = null;
    if (currentLoan != null) {
      idInDatabase = findLoan(bookLoan).getObjectId("_id");
    }
    MongoDB database = MongoDB.getInstance();
    if (idInDatabase != null) {
      return database.updateData("bookLoan", "_id", idInDatabase, MongoDB.objectToMap(bookLoan));
    }
    return database.addToCollection("bookLoan", MongoDB.objectToMap(bookLoan));
  }

  public static boolean canView(BookLoan bookLoan) {
    Document currentLoan = findLoan(bookLoan);
    if (currentLoan == null) {
      return false;
    }
    Date dueDate = currentLoan.getDate("dueDate");
    return dueDate.compareTo(new Date()) > 0;
  }

  public static void main(String[] args) {

  }
}
