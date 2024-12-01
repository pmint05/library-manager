package com.app.librarymanager.controllers;

import static com.mongodb.client.model.Filters.all;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.gt;
import static com.mongodb.client.model.Filters.gte;
import static com.mongodb.client.model.Filters.lte;

import com.app.librarymanager.controllers.BookRatingController.ReturnRating;
import com.app.librarymanager.models.Book;
import com.app.librarymanager.models.BookCopies;
import com.app.librarymanager.models.BookLoan;
import com.app.librarymanager.models.BookLoan.Mode;
import com.app.librarymanager.models.BookRating;
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
import java.util.stream.Collectors;
import lombok.Data;
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

  @Data
  public static class ReturnBookLoan {

    BookLoan bookLoan;
    String titleBook;
    String thumbnailBook;

    public ReturnBookLoan(BookLoan bookLoan, String titleBook, String thumbnailBook) {
      this.bookLoan = bookLoan;
      this.thumbnailBook = thumbnailBook;
      this.titleBook = titleBook;
    }
  }

  private static List<ReturnBookLoan> bookLoanFromDocument(List<Document> documents) {
    try {
      Map<String, Document> bookDocs = BookController.findBookByListID(
              documents.stream().map(doc -> doc.getString("bookId")).toList()).stream()
          .collect(Collectors.toMap(doc -> doc.getString("id"), doc -> doc));
      return documents.stream().map(doc -> {
        Document bookDoc = bookDocs.get(doc.getString("bookId"));
        return new ReturnBookLoan(new BookLoan(doc), bookDoc.getString("title"),
            bookDoc.getString("thumbnail"));
      }).toList();
    } catch (Exception e) {
      return null;
    }
  }

  public static List<ReturnBookLoan> getAllLentBook(int start, int length) {
    return bookLoanFromDocument(
        MongoDB.getInstance().findAllObject("bookLoan", Filters.eq("valid", true), start, length));
  }

  public static List<ReturnBookLoan> getAllLentBookOf(String userId, int start, int length) {
    return bookLoanFromDocument(MongoDB.getInstance()
        .findAllObject("bookLoan", Filters.and(eq("userId", userId), eq("valid", true)), start,
            length));
  }

  public static long countLentBookOf(String userId) {
    return MongoDB.getInstance()
        .countDocuments("bookLoan", Filters.and(eq("userId", userId), eq("valid", true)));
  }

  public static List<ReturnBookLoan> getRecentLoan(int start, int length) {
    return bookLoanFromDocument(MongoDB.getInstance()
        .findSortedObject("bookLoan", Filters.eq("valid", true),
            Sorts.orderBy(Sorts.descending("lastUpdated")), start, length));
  }

  public static List<ReturnBookLoan> getTopLentBook(int start, int length) {
    try {
      List<Document> documents = MongoDB.getInstance().getAggregate("bookLoan", List.of(
          new Document("$group",
              new Document("_id", "$bookId").append("count", new Document("$sum", 1))),
          new Document("$sort", new Document("count", -1)), new Document("$skip", start),
          new Document("$limit", length)));
      Map<String, Document> bookDocs = BookController.findBookByListID(
              documents.stream().map(doc -> doc.getString("_id")).toList()).stream()
          .collect(Collectors.toMap(doc -> doc.getString("id"), doc -> doc));
      return documents.stream().map(doc -> {
        Document bookDoc = bookDocs.get(doc.getString("_id"));
        return new ReturnBookLoan(
            new BookLoan("", doc.getString("_id"), new Date(), new Date(), doc.getInteger("count")),
            bookDoc.getString("title"), bookDoc.getString("thumbnail"));
      }).toList();
    } catch (Exception e) {
      System.out.println(e.getMessage());
      return null;
    }
  }

  public static long numberOfRecords() {
    return MongoDB.getInstance().countDocuments("bookLoan");
  }

  public static long countLentBook() {
    return MongoDB.getInstance().countDocuments("bookLoan", Filters.eq("valid", true));
  }

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
//    for (ReturnBookLoan x : getAllLentBookOf("bb", 0, 1000000)) {
//      System.out.println("======");
//      System.out.println(
//          "BookLoan = " + x.getBookLoan().getBookId() + " " + x.getBookLoan().getUserId() + " "
//              + x.getBookLoan().getBorrowDate() + " " + x.getBookLoan().getDueDate()
//              + x.getBookLoan().isValid() + " " + x.getBookLoan().getType()
//              + x.getBookLoan().getNumCopies());
//      System.out.println("titleBook = " + x.getTitleBook());
//      System.out.println("thumbnailBook = " + x.getThumbnailBook());
//      System.out.println("Time = " + x.getBookLoan().getLastUpdated());
//    }
//    System.out.println(getRecentLoan(0, 1000000));
  }
}
