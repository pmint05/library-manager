package com.app.librarymanager.controllers;

import com.app.librarymanager.models.Book;
import com.app.librarymanager.models.BookUser;
import com.app.librarymanager.models.User;
import com.app.librarymanager.services.MongoDB;
import java.util.ArrayList;
import java.util.List;
import org.bson.Document;

public class FavoriteController {

  public static Document addToFavorite(BookUser favorite) {
    return MongoDB.getInstance().addToCollection("favorite", MongoDB.objectToMap(favorite));
  }

  public static boolean removeFromFavorite(BookUser favorite) {
    return MongoDB.getInstance().deleteFromCollection("favorite", "_id", favorite.get_id());
  }

  public static List<Book> getFavoriteBookOfUser(String userId) {
    List<Document> documents = MongoDB.getInstance().findAllObject("favorite", "userId", userId);
    List<Book> favoriteBook = new ArrayList<>();
    documents.forEach(document -> {
//      favoriteBook.add(new Book)
    });
    return favoriteBook;
  }
}
