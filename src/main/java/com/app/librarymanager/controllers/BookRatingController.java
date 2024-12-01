package com.app.librarymanager.controllers;

import com.app.librarymanager.controllers.BookLoanController.ReturnBookLoan;
import com.app.librarymanager.models.Book;
import com.app.librarymanager.models.BookLoan;
import com.app.librarymanager.models.BookRating;
import com.app.librarymanager.services.MongoDB;
import com.mongodb.client.model.Filters;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.Data;
import org.bson.Document;
import org.bson.types.ObjectId;

public class BookRatingController {

  public static ObjectId findIdRating(BookRating rating) {
    Document document = MongoDB.getInstance().findAnObject("bookRating",
        Map.of("userId", rating.getUserId(), "bookId", rating.getBookId()));
    if (document == null) {
      return null;
    }
    return document.getObjectId("_id");
  }

  public static Document addRating(BookRating rating) {
    ObjectId idInDatabase = findIdRating(rating);
    MongoDB database = MongoDB.getInstance();
    if (idInDatabase != null) {
      return database.updateData("bookRating", "_id", idInDatabase, MongoDB.objectToMap(rating));
    }
    return database.addToCollection("bookRating", MongoDB.objectToMap(rating));
  }

  public static boolean removeRating(BookRating rating) {
    ObjectId idInDatabase = findIdRating(rating);
    if (idInDatabase == null) {
      return false;
    }
    return MongoDB.getInstance().deleteFromCollection("bookRating", "_id", idInDatabase);
  }

  public static double averageRating(String bookId) {
    return MongoDB.getInstance().findAllObject("bookRating", "bookId", bookId).stream()
        .mapToDouble(doc -> doc.getDouble("rate")).average().orElse(0.0);
  }

  public static long numRating(String bookId) {
    return MongoDB.getInstance().countDocuments("bookRating", Filters.eq("bookId", bookId));
  }

  @Data
  public static class ReturnRating {

    BookRating bookRating;
    String titleBook;
    String thumbnailBook;

    public ReturnRating(BookRating bookRating, String titleBook, String thumbnailBook) {
      this.bookRating = bookRating;
      this.titleBook = titleBook;
      this.thumbnailBook = thumbnailBook;
    }
  }

  public static List<ReturnRating> bookRatingFromDocument(List<Document> documents) {
    try {
      Map<String, Document> bookDocs = MongoDB.getInstance().findAllObject("books",
              Filters.in("id", documents.stream().map(doc -> doc.getString("bookId")).toList()))
          .stream().collect(Collectors.toMap(doc -> doc.getString("id"), doc -> doc));
      return documents.stream().map(doc -> {
        Document bookDoc = bookDocs.get(doc.getString("bookId"));
        return new ReturnRating(new BookRating(doc), bookDoc.getString("title"),
            bookDoc.getString("thumbnail"));
      }).toList();
    } catch (Exception e) {
      return null;
    }
  }

  public static List<ReturnRating> getTopRatingBook(int start, int length) {
    try {
      List<Document> documents = MongoDB.getInstance().getAggregate("bookRating", List.of(
          new Document("$group",
              new Document("_id", "$bookId").append("average", new Document("$avg", "$rate"))),
          new Document("$sort", new Document("average", -1)), new Document("$skip", start),
          new Document("$limit", length)));
      Map<String, Document> bookDocs = BookController.findBookByListID(
              documents.stream().map(doc -> doc.getString("_id")).toList()).stream()
          .collect(Collectors.toMap(doc -> doc.getString("id"), doc -> doc));
      return documents.stream().map(doc -> {
        Document bookDoc = bookDocs.get(doc.getString("_id"));
        return new ReturnRating(new BookRating(doc.getString("_id"), "", doc.getDouble("average")),
            bookDoc.getString("title"), bookDoc.getString("thumbnail"));
      }).toList();
    } catch (Exception e) {
      System.out.println(e.getMessage());
      return null;
    }
  }


  public static void main(String[] args) {
    System.out.println(getTopRatingBook(0, 1000000).get(0).getBookRating().getBookId());
  }
}