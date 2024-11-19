package com.app.librarymanager.controllers;

import com.app.librarymanager.models.Book;
import com.app.librarymanager.models.BookRating;
import com.app.librarymanager.models.User;
import com.app.librarymanager.services.MongoDB;
import java.util.HashMap;
import java.util.Map;
import org.bson.types.ObjectId;
import org.json.JSONObject;

public class BookRatingController {

  public static String findRating(User user, Book book) {
    Map<String, Object> criteria = new HashMap<>();
    criteria.put("userId", user.getId());
    criteria.put("bookId", book.getId());
    String jsonString = MongoDB.getInstance().findAnObject("bookRating", criteria);
    if (jsonString == null) {
      return null;
    }
    JSONObject jsonObject = new JSONObject(jsonString);
    JSONObject idObject = jsonObject.getJSONObject("_id");
    return idObject.getString("$oid");
  }

  public static boolean addRating(User user, Book book, double rate) {
    String idInDatabase = findRating(user, book);
    MongoDB database = MongoDB.getInstance();
    Map<String, Object> data = new HashMap<>();
    data.put("userId", user.getId());
    data.put("bookId", book.getId());
    data.put("rate", rate);
    if (idInDatabase != null) {
      return database.updateData("bookRating", "_id", new ObjectId(idInDatabase), data);
    }
    return database.addToCollection("bookRating", data);
  }

  public static boolean removeRating(User user, Book book) {
    String idInDatabase = findRating(user, book);
    if (idInDatabase == null) {
      return false;
    }
    return MongoDB.getInstance().deleteFromCollection("bookRating", "_id", new ObjectId(idInDatabase));
  }

  public static void main(String[] args) {
    User sus = new User();
    sus.setId("aaaa");
    Book book = new Book();
    book.setId("bbbb");
    addRating(sus,book,69.420);
  }
}
