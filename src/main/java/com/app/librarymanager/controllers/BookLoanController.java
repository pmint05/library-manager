package com.app.librarymanager.controllers;

import static com.mongodb.client.model.Filters.all;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.gt;
import static com.mongodb.client.model.Filters.gte;
import static com.mongodb.client.model.Filters.lte;

import com.app.librarymanager.models.Book;
import com.app.librarymanager.models.BookCopies;
import com.app.librarymanager.models.BookLoan;
import com.app.librarymanager.models.BookLoan.Mode;
import com.app.librarymanager.services.MongoDB;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
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
    return new HashMap<>(
        Map.of("userId", bookLoan.getUserId(), "bookId", bookLoan.getBookId(), "borrowDate",
            bookLoan.getBorrowDate(), "dueDate", bookLoan.getDueDate(), "valid", bookLoan.isValid(),
            "type", bookLoan.getType().name(), "numCopies", bookLoan.getNumCopies()));
  }

  private static Document findOnlineLoan(BookLoan bookLoan) {
    return MongoDB.getInstance().findAnObject("bookLoan", new HashMap<>(
        Map.of("userId", bookLoan.getUserId(), "bookId", bookLoan.getBookId(), "type", "ONLINE")));
  }

  private static Document addOfflineLoan(BookLoan bookLoan) {
    BookCopiesController.increaseCopy(
        new BookCopies(bookLoan.getBookId(), -bookLoan.getNumCopies()));
    return MongoDB.getInstance().addToCollection("bookLoan", bookLoanToMap(bookLoan));
  }

  private static Document addOnlineLoan(BookLoan bookLoan) {
    Document currentLoan = findOnlineLoan(bookLoan);
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

  public static Document addLoan(BookLoan bookLoan) {
    return bookLoan.getType() == Mode.OFFLINE ? addOfflineLoan(bookLoan) : addOnlineLoan(bookLoan);
  }

  //  public static Document findLoan(BookLoan bookLoan) {
//    return MongoDB.getInstance().findAnObject("bookLoan", bookLoanToMap(bookLoan));
//  }
//
//  public static boolean canView(BookLoan bookLoan) {
//    Document currentLoan = findLoan(bookLoan);
//    if (currentLoan == null) {
//      return false;
//    }
//    Date dueDate = currentLoan.getDate("dueDate");
//    return dueDate.after(new Date());
//  }
//
  public static Document returnBook(BookLoan bookLoan) {
    if (bookLoan.getType() == Mode.OFFLINE) {
      BookCopiesController.increaseCopy(
          new BookCopies(bookLoan.getBookId(), bookLoan.getNumCopies()));
    }
    return MongoDB.getInstance()
        .updateData("bookLoan", "_id", bookLoan.get_id(), new HashMap<>(Map.of("valid", false)));
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

  public static List<BookLoan> getValidLentBook(String userId) {
    return MongoDB.getInstance()
        .findAllObject("bookLoan", Filters.and(eq("userId", userId), eq("valid", true))).stream()
        .map(BookLoan::new).toList();
  }

  public static List<BookLoan> getRecentLoan(int start, int length) {
    return MongoDB.getInstance().findSortedObject("bookLoan", Filters.eq("valid", true),
            Sorts.orderBy(Sorts.descending("lastUpdated")), start, length).stream().map(BookLoan::new)
        .toList();
  }

  public static List<Document> getTopLentBook(int start, int length) {
    return MongoDB.getInstance()
        .getAggregate("bookLoan", List.of(
            new Document("$group", new Document("_id", "$bookId")
                .append("count", new Document("$sum", 1))),
            new Document("$sort", new Document("count", -1)),
            new Document("$skip", start),
            new Document("$limit", length)
        ));
  }

  public static long numberOfRecords() {
    return MongoDB.getInstance().countDocuments("bookLoan");
  }

  public static long countLentBook() {
    return MongoDB.getInstance().countDocuments("bookLoan", Filters.eq("valid", true));
  }

  public static List<BookLoan> getLentBook() {
    List<BookLoan> bookLoanList = new ArrayList<>();
    MongoDB.getInstance().findAllObject("bookLoan", Filters.eq("valid", true))
        .forEach(e -> bookLoanList.add(new BookLoan(e)));
    return bookLoanList;
  }

//  public static int countInvalidLendBook(String userId) {
//    return MongoDB.getInstance()
//        .findAllObject("bookLoan", Filters.and(eq("userId", userId), eq("valid", false))).size();
//  }

  public static boolean refreshDatabase() {
    Date curDate = new Date();
    Bson filterDate = Filters.and(lte("dueDate", curDate), eq("valid", true));
    Bson filterOffline = Filters.and(filterDate, eq("type", Mode.OFFLINE.name()));
    MongoDB.getInstance().findAllObject("bookLoan", filterOffline).forEach(
        e -> BookCopiesController.increaseCopy(
            new BookCopies(e.getString("bookId"), e.getInteger("numCopies"))));
    return MongoDB.getInstance().updateAll("bookLoan", filterDate,
        Updates.combine(Updates.set("valid", false),
            Updates.set("lastUpdated", new Timestamp(System.currentTimeMillis()))));
  }

  public static void main(String[] args) {
    System.out.println(getTopLentBook(0, 1000000));
  }
}
