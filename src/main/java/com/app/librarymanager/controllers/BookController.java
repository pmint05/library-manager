package com.app.librarymanager.controllers;

import com.app.librarymanager.models.Book;
import com.app.librarymanager.services.FirebaseFirestore;
import com.app.librarymanager.utils.Fetcher;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import io.github.cdimascio.dotenv.Dotenv;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class BookController {

  private static final Dotenv dotenv = Dotenv.load();
  private static final String SEARCH_URL = "https://www.googleapis.com/books/v1/volumes?q=";

  /**
   * Get all string contains in an entity of a json object.
   *
   * @param key        name of the entity needed to get
   * @param jsonObject object to fetch from
   * @return an ArrayList contains all string in `key`
   */
  private static ArrayList<String> getAllString(String key, JSONObject jsonObject) {
    JSONArray jsonArray = jsonObject.getJSONArray(key);
    ArrayList<String> listString = new ArrayList<>();
    for (int i = 0; i < jsonArray.length(); i++) {
      listString.add(jsonArray.optString(i, "N/A"));
    }
    return listString;
  }

  /**
   * Search related books which title contains the given keyword.
   *
   * @param keyword to be contained in the book's title
   * @return an ArrayList of related books
   */
  public static ArrayList<Book> searchByKeyword(String keyword) {
    try {
      ArrayList<Book> bookList = new ArrayList<>();
      String encodedKeyword = URLEncoder.encode(keyword, StandardCharsets.UTF_8);
      String searchUrl =
          SEARCH_URL + encodedKeyword + "&key=" + dotenv.get("GBOOKS_API_KEY");

      JSONObject jsonObject = Fetcher.get(searchUrl);
      assert jsonObject != null;
      JSONArray jsonArray = jsonObject.getJSONArray("items");

      for (int indexBook = 0; indexBook < jsonArray.length(); indexBook++) {
        JSONObject curBook = jsonArray.getJSONObject(indexBook);

        String id = curBook.getString("id");

        JSONObject volumeInfo = curBook.getJSONObject("volumeInfo");
        String title = volumeInfo.optString("title", "N/A");
        String publisher = volumeInfo.optString("publisher", "N/A");
        String publishedDate = volumeInfo.optString("publishedDate", "N/A");
        String description = volumeInfo.optString("description", "N/A");
        int pageCount = volumeInfo.optInt("pageCount", -1);

        ArrayList<String> categories = getAllString("categories", volumeInfo);

        JSONArray industryIdentifiers = volumeInfo.optJSONArray("industryIdentifiers");
        String iSBN = "N/A";
        if (industryIdentifiers != null) {
          // this practice is bad when moving to multi-thread?
          // consider moving to gson to parse this
          for (int j = 0; j < industryIdentifiers.length(); j++) {
            JSONObject currentIdentifier = industryIdentifiers.getJSONObject(j);
            if (currentIdentifier.getString("type").equals("ISBN_13")) {
              iSBN = currentIdentifier.getString("identifier");
              break;
            }
            if (currentIdentifier.getString("type").equals("ISBN_10")) {
              iSBN = currentIdentifier.getString("identifier");
              break;
            }
          }
        }

        String thumbnail = "N/A";
        JSONObject imageLinks = volumeInfo.optJSONObject("imageLinks");
        if (imageLinks != null) {
          thumbnail = imageLinks.getString("thumbnail");
        }

        String language = volumeInfo.optString("language", "N/A");

        ArrayList<String> authors = getAllString("authors", volumeInfo);

        JSONObject saleInfo = curBook.getJSONObject("saleInfo");
        if (saleInfo.getString("saleability").equals("NOT_FOR_SALE")) {
          // TBD
          continue;
        }
        JSONObject retailPrice = saleInfo.getJSONObject("retailPrice");
        int price = retailPrice.getInt("amount");
        String currencyCode = retailPrice.getString("currencyCode");

        bookList.add(
            new Book(id, title, publisher, publishedDate, description, pageCount, categories, iSBN,
                thumbnail, language, authors, price, currencyCode, "N/A"));
      }

      return bookList;
    } catch (Exception e) {
      e.printStackTrace();
      return new ArrayList<>();
    }
  }

  private static Map<String, Object> bookToFirebaseObject(Book book) {
    Gson gson = new Gson();
    return gson.fromJson(gson.toJson(book), new TypeToken<Map<String, Object>>() {
    }.getType());
  }

  private static Book mapToBook(Map<String, Object> data) {
    Gson gson = new Gson();
    JsonElement jsonElement = gson.toJsonTree(data);
    return gson.fromJson(jsonElement, Book.class);
  }

  private static Book idToBook(String id) {
    return mapToBook(FirebaseFirestore.getInstance().getDocumentObject("books", id));
  }

  // available this object, or available book's ISBN ...
  public static boolean isAvailable(Book book) {
    FirebaseFirestore database = FirebaseFirestore.getInstance();

    if (database.haveDocument("books", book.getId())) {
      return true;
    }

    if (book.getISBN().equals("N/A")) {
      return false;
    }
    return !database.getDataWithFilter("books", "iSBN", book.getISBN()).isEmpty();
  }

  // isbn13
  // isbn should be distinct for all books!
  // how should we handle it ...
  public static Book findBookByISBN(String iSBN) {
    try {
      FirebaseFirestore database = FirebaseFirestore.getInstance();
      JSONArray relevantBooks = database.getDataWithFilter("books", "iSBN", iSBN);
      if (relevantBooks.length() > 1) {
        throw new Exception("Database contains more than one books which have ISBN = " + iSBN);
      }
      if (relevantBooks.isEmpty()) {
        return null;
      }
      return mapToBook(relevantBooks.getJSONObject(0).toMap());
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public static Book findBookByID(String id) {
    FirebaseFirestore database = FirebaseFirestore.getInstance();
    if (!database.haveDocument("books", id)) {
      return null;
    }
    return idToBook(id);
  }

  // find all books which title contains `keyword`
  public static ArrayList<Book> findBookByKeyword(String keyword) {
    FirebaseFirestore database = FirebaseFirestore.getInstance();
    ArrayList<Book> listRelatedBooks = new ArrayList<>();
    ArrayList<Map<String, Object>> allBooks = database.getCollection("books");
    for (Map<String, Object> book : allBooks) {
      if (((String) book.get("title")).contains(keyword)) {
        listRelatedBooks.add(idToBook((String) book.get("id")));
      }
    }
    return listRelatedBooks;
  }

  public static void refreshDatabase() {
//    FirebaseFirestore database = FirebaseFirestore.getInstance();
//    ArrayList<Map<String, Object>> listBooks = database.getCollection("books");
//    for (Map<String, Object> curBook : listBooks) {
//
//    }
  }

  public static boolean addBook(Book book) {
    if (isAvailable(book)) {
      return false;
    }

    FirebaseFirestore database = FirebaseFirestore.getInstance();
    database.addData("books", book.getId(), bookToFirebaseObject(book));

    for (String category : book.getCategories()) {
      String slugCategory = category.toLowerCase();

      if (!database.haveDocument("categories", slugCategory)) {
        Map<String, Object> emptyData = new HashMap<>();
        emptyData.put("booksId", new ArrayList<String>());

        database.addData("categories", slugCategory, emptyData);
      }

      database.appendToArray("categories", slugCategory, "booksId", book.getId());
    }

    return true;
  }

  public static boolean deleteBook(Book book) {
    if (!isAvailable(book)) {
      return false;
    }
    FirebaseFirestore database = FirebaseFirestore.getInstance();
    database.deleteData("books", book.getId());

    for (String category : book.getCategories()) {
      String slugCategory = category.toLowerCase();
      database.removeFromArray("categories", slugCategory, "booksId", book.getId());
    }

    return true;
  }

  public static void main(String[] args) {
    refreshDatabase();
  }
}