package com.app.librarymanager.controllers;

import com.app.librarymanager.models.Book;
import com.app.librarymanager.services.FirebaseFirestore;
import com.app.librarymanager.utils.Fetcher;
import com.google.cloud.Timestamp;
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
   * Get all string contains in an entity of a json object. For example: jsonObject = { "string":
   * ["string1", "string2"] }, it would return {"string1", "string2" }.
   *
   * @param key        name of the entity needed to get
   * @param jsonObject object to fetch from
   * @return an ArrayList contains all string in `key`
   */
  private static ArrayList<String> getAllString(String key, JSONObject jsonObject) {
    try {
      JSONArray jsonArray = jsonObject.getJSONArray(key);
      ArrayList<String> listString = new ArrayList<>();
      for (int i = 0; i < jsonArray.length(); i++) {
        listString.add(jsonArray.optString(i, "N/A"));
      }
      return listString;
    } catch (Exception e) {
      return new ArrayList<>();
    }
  }

  /**
   * Search related books which title contains the given keyword in Google Books' database.
   *
   * @param keyword to be contained in the book's title
   * @return an ArrayList of related books
   */
  public static ArrayList<Book> searchByKeyword(String keyword) {
    try {
      ArrayList<Book> bookList = new ArrayList<>();
      String encodedKeyword = URLEncoder.encode(keyword, StandardCharsets.UTF_8);
      String searchUrl = SEARCH_URL + encodedKeyword + "&key=" + dotenv.get("GBOOKS_API_KEY");

//      System.err.println(searchUrl);

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

//        System.err.println("check book " + indexBook + " " + id);

        ArrayList<String> authors = getAllString("authors", volumeInfo);

        JSONObject saleInfo = curBook.getJSONObject("saleInfo");
        if (saleInfo.getString("saleability").equals("NOT_FOR_SALE")) {
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

  /**
   * Get books' information with given id in our database.
   *
   * @param id of book need to search
   * @return a Book which id equals to given id
   */
  private static Book idToBook(String id) {
    return FirebaseFirestore.mapToObject(
        FirebaseFirestore.getInstance().getDocumentObject("books", id), Book.class);
  }

  /**
   * Check if a book exists in our database.
   *
   * @param book need to search
   * @return book exists or not
   */
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


  /**
   * Find an book with given ISBN-13 or ISBN-10.
   *
   * @param iSBN to find
   * @return book
   */
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
      return FirebaseFirestore.mapToObject(relevantBooks.getJSONObject(0).toMap(), Book.class);
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

  // TBD
  public static void refreshDatabase() {
  }

  public static boolean addBook(Book book) {
    if (isAvailable(book)) {
      return false;
    }

    FirebaseFirestore database = FirebaseFirestore.getInstance();
    book.setUpdatedTime(Timestamp.now());
    book.setCreatedTime(Timestamp.now());
    database.addData("books", book.getId(), FirebaseFirestore.objectToMap(book));

    for (String category : book.getCategories()) {
      String slugCategory = category.toLowerCase();

      if (!database.haveDocument("categories", slugCategory)) {
        Map<String, Object> emptyData = new HashMap<>();
        emptyData.put("booksId", new ArrayList<String>());
        emptyData.put("createdTime", Timestamp.now());
        database.addData("categories", slugCategory, emptyData);
      }

      database.appendToArray("categories", slugCategory, "booksId", book.getId());
      database.updateField("categories", slugCategory, "updatedTime", Timestamp.now());
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

  /**
   * Update information of given book, specified by id.
   *
   * @param book
   * @return true iff
   */
  public static boolean editBook(Book book) {
    FirebaseFirestore database = FirebaseFirestore.getInstance();
    if (database.getData("books", book.getId()) == null) {
      return false;
    }
    database.updateData("books", book.getId(), FirebaseFirestore.objectToMap(book));
    database.updateField("books", book.getId(), "updatedTime", Timestamp.now());
    return true;
  }

  public static void main(String[] args) {

  }
}