package com.app.librarymanager.controllers;

import com.app.librarymanager.models.Book;
import com.app.librarymanager.models.BookRating;
import com.app.librarymanager.models.User;
import com.app.librarymanager.services.MongoDB;
import java.util.HashMap;
import java.util.Map;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.json.JSONObject;

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

  public static void main(String[] args) {

  }
}
