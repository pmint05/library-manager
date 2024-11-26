package com.app.librarymanager.controllers;

import com.app.librarymanager.models.Book;
import com.app.librarymanager.models.Categories;
import com.app.librarymanager.services.MongoDB;
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
      return document;
    }
    document = MongoDB.getInstance()
        .addToCollection("categories", Map.of("name", categories.getName()));
    return document;
  }

  public static boolean removeCategory(Categories categories) {
    return MongoDB.getInstance().deleteFromCollection("categories", "name", categories.getName());
  }

  public static List<Categories> getCategories(int start, int length) {
    List<Categories> categories = new ArrayList<>();
    MongoDB.getInstance().findAllObject("categories", Filters.empty(), start, length)
        .forEach(document -> categories.add(new Categories(document)));
    return categories;
  }

  public static List<Book> getBookOfCategory(Categories categories, int start, int length) {
    List<Book> books = new ArrayList<>();
    MongoDB.getInstance().findAllObject("books",
            Filters.regex("categories", categories.getName().toLowerCase(), "i"))
        .forEach(document -> books.add(BookController.getBookFromDocument(document)));
    return books;
  }

  public static void main(String[] args) {
  }
}
