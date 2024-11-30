package com.app.librarymanager.controllers;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.gt;
import static com.mongodb.client.model.Filters.gte;
import static com.mongodb.client.model.Filters.lte;

import com.app.librarymanager.models.Book;
import com.app.librarymanager.models.BookLoan;
import com.app.librarymanager.services.MongoDB;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

public class BookLoanController {

  public static Map<String, Object> bookLoanToMap(BookLoan bookLoan) {
    return Map.of("userId", bookLoan.getUserId(), "bookId", bookLoan.getBookId(), "borrowDate",
        bookLoan.getBorrowDate(), "dueDate", bookLoan.getDueDate(), "valid", bookLoan.isValid());
  }

  public static Document findLoan(BookLoan bookLoan) {
    return MongoDB.getInstance().findAnObject("bookLoan",
        Map.of("userId", bookLoan.getUserId(), "bookId", bookLoan.getBookId()));
  }

  public static Document addLoan(BookLoan bookLoan) {
    Document currentLoan = findLoan(bookLoan);
    ObjectId idInDatabase = null;
    if (currentLoan != null) {
      idInDatabase = currentLoan.getObjectId("_id");
    }
    MongoDB database = MongoDB.getInstance();
    if (idInDatabase != null) {
      return database.updateData("bookLoan", "_id", idInDatabase, bookLoanToMap(bookLoan));
    }
    return database.addToCollection("bookLoan", bookLoanToMap(bookLoan));
  }

  public static boolean canView(BookLoan bookLoan) {
    Document currentLoan = findLoan(bookLoan);
    if (currentLoan == null) {
      return false;
    }
    Date dueDate = currentLoan.getDate("dueDate");
    return dueDate.after(new Date());
  }

  public static Document returnBook(BookLoan bookLoan) {
    Document currentLoan = findLoan(bookLoan);
    ObjectId idInDatabase = null;
    if (currentLoan != null) {
      idInDatabase = currentLoan.getObjectId("_id");
    }
    if (idInDatabase == null) {
      return null;
    }
    return MongoDB.getInstance()
        .updateData("bookLoan", "_id", idInDatabase, new HashMap<>(Map.of("valid", false)));
  }

  public static List<Book> getAllLentBook(String userId) {
    List<Document> documentList = MongoDB.getInstance()
        .findAllObject("bookLoan", Filters.and(eq("userId", userId), eq("valid", true)));
    List<Book> bookList = new ArrayList<>();
    documentList.forEach(e -> bookList.add(BookController.findBookByID(e.getString("bookId"))));
    return bookList;
  }

  public static long countLentBookOf(String userId) {
    return MongoDB.getInstance()
        .countDocuments("bookLoan", Filters.and(eq("userId", userId), eq("valid", true)));
  }

  public static int countValidLendBook(String userId) {
    return MongoDB.getInstance()
        .findAllObject("bookLoan", Filters.and(eq("userId", userId), eq("valid", true))).size();
  }

  public static int countInvalidLendBook(String userId) {
    return MongoDB.getInstance()
        .findAllObject("bookLoan", Filters.and(eq("userId", userId), eq("valid", false))).size();
  }

  public static void refreshDatabase() {
    Date curDate = new Date();
    Bson filter = Filters.and(lte("dueDate", curDate), eq("valid", "true"));
    Bson change = Updates.combine(Updates.set("valid", false),
        Updates.set("lastUpdated", new Timestamp(System.currentTimeMillis())));
    MongoDB.getInstance().updateAll("bookLoan", filter, change);
  }
}
