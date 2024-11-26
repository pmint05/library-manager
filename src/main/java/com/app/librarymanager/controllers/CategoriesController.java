package com.app.librarymanager.controllers;

import com.app.librarymanager.models.Book;
import com.app.librarymanager.models.Categories;
import com.app.librarymanager.services.MongoDB;
import com.mongodb.client.model.Filters;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.bson.Document;

public class CategoriesController {

  public static Document findCategory(Categories categories) {
    return MongoDB.getInstance()
        .findAnObject("categories", Filters.eq("name", categories.getName().toLowerCase()));
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
//    addCategory(new Categories("ahuhu"));
//    addCategory(new Categories("ahuhu"));
//    addCategory(new Categories("ahihi"));
//    System.out.println(getCategories(0, 10000));
//    Categories cat = new Categories("Mathematics");
//    System.out.println(cat.getName());
//    System.out.println(getBookOfCategory(new Categories("Mathematics"), 0, 1000000));
  }
}
