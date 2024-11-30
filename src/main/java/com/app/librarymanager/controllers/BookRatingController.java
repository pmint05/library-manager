package com.app.librarymanager.controllers;

import com.app.librarymanager.models.Book;
import com.app.librarymanager.models.BookRating;
import com.app.librarymanager.services.MongoDB;
import com.mongodb.client.model.Filters;
import java.util.List;
import java.util.Map;
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
    return MongoDB.getInstance()
        .deleteFromCollection("bookRating", "_id", idInDatabase);
  }

  public static double averageRating(String bookId) {
    return MongoDB.getInstance()
        .findAllObject("bookRating", "bookId", bookId)
        .stream().mapToDouble(doc -> doc.getDouble("rate"))
        .average().orElse(0.0);
  }

  public static long numRating(String bookId) {
    return MongoDB.getInstance().countDocuments("bookRating", Filters.eq("bookId", bookId));
  }

  public static List<Document> getTopRatingBook(int start, int length) {
    return MongoDB.getInstance()
        .getAggregate("bookRating", List.of(
            new Document("$group", new Document("_id", "$bookId")
                .append("average", new Document("$avg", "$rate"))),
            new Document("$sort", new Document("average", -1)),
            new Document("$skip", start),
            new Document("$limit", length)
        ));
  }

  public static void main(String[] args) {
  }
}