package com.app.librarymanager.controllers;

import com.app.librarymanager.models.Book;
import com.app.librarymanager.models.Categories;
import com.app.librarymanager.services.MongoDB;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.text.WordUtils;
import org.bson.Document;
import org.bson.types.ObjectId;

public class CategoriesController {

  public static Document findCategory(Categories categories) {
    return MongoDB.getInstance()
        .findAnObject("categories", Filters.eq("name", categories.getName()));
  }

  public static Document addCategory(Categories categories) {
    Document document = findCategory(categories);
    if (document != null) {
      return null;
    }
    document = MongoDB.getInstance()
        .addToCollection("categories", Map.of("name", categories.getName()));
    return document;
  }

  public static boolean addCategoryList(List<Categories> categories) {
    // Just update bulk in this function, so I decided to hard-code
    try {
      System.err.println("Trying to add " + categories);
      MongoCollection<Document> categoriesCollection = MongoDB.getInstance().getDatabase()
          .getCollection("categories");
      categoriesCollection.insertMany(categories.stream().map(Categories::toDocument).toList());
      return true;
    } catch (Exception e) {
      System.out.println("Fail when trying to add categories: " + categories);
      return false;
    }
  }

  public static boolean removeCategory(Categories categories) {
    return MongoDB.getInstance().deleteFromCollection("categories", "name", categories.getName());
  }

  public static long countCategories() {
    return MongoDB.getInstance().countDocuments("categories");
  }

  public static List<Categories> getCategories(int start, int length) {
    return MongoDB.getInstance()
        .findAllObject("categories", Filters.empty(), start, length)
        .stream()
        .map(Categories::new)
        .toList();
  }

  public static List<Book> getBookOfCategory(Categories categories, int start, int length) {
    return MongoDB.getInstance()
        .findAllObject("books",
            Filters.regex("categories", categories.getName().toLowerCase(), "i"))
        .stream()
        .map(BookController::getBookFromDocument)
        .toList();
  }

  public static long countBookOfCategory(Categories categories) {
    return MongoDB.getInstance().countDocuments("books",
        Filters.regex("categories", categories.getName().toLowerCase(), "i"));
  }

  public static void main(String[] args) {
  }
}
