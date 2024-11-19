package com.app.librarymanager.controllers;

import static com.mongodb.client.model.Filters.lt;

import com.app.librarymanager.models.Book;
import com.app.librarymanager.services.MongoDB;
import com.app.librarymanager.utils.Fetcher;
import com.google.cloud.Timestamp;
import com.mongodb.MongoException;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import io.github.cdimascio.dotenv.Dotenv;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;
import org.bson.Document;
import org.bson.types.ObjectId;
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
      IntStream.range(0, jsonArray.length())
          .forEach(i -> listString.add(jsonArray.optString(i, "N/A")));
      return listString;
    } catch (Exception e) {
      return new ArrayList<>();
    }
  }

  public static ArrayList<Book> searchByKeyword(String keyword) {
    try {
      ArrayList<Book> bookList = new ArrayList<>();
      String encodedKeyword = URLEncoder.encode(keyword, StandardCharsets.UTF_8);
      String searchUrl = SEARCH_URL + encodedKeyword + "&key=" + dotenv.get("GBOOKS_API_KEY");

//      System.out.println(searchUrl);

      JSONObject jsonObject = Fetcher.get(searchUrl);
      assert jsonObject != null;
      JSONArray jsonArray = jsonObject.getJSONArray("items");

      for (int indexBook = 0; indexBook < jsonArray.length(); indexBook++) {
        JSONObject curBook = jsonArray.getJSONObject(indexBook);

        String id = curBook.getString("id");

        System.err.println(id);

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
          continue;
        }
        JSONObject retailPrice = saleInfo.optJSONObject("retailPrice");
        int price;
        String currencyCode;
        if (retailPrice == null) {
          price = -1;
          currencyCode = "N/A";
        } else {
          price = retailPrice.getInt("amount");
          currencyCode = retailPrice.getString("currencyCode");
        }

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

  public static boolean isAvailable(Book book) {
    MongoDB database = MongoDB.getInstance();
    if (database.findAnObject("books", "id", book.getId()) != null) {
      return true;
    }
    if (book.getISBN().equals("N/A")) {
      return false;
    }
    return database.findAnObject("books", "iSBN", book.getISBN()) != null;
  }

  public static Book findBookByISBN(String iSBN) {
    MongoDB database = MongoDB.getInstance();
    String jsonBook = database.findAnObject("books", "iSBN", iSBN);
    return MongoDB.jsonToObject(jsonBook, Book.class);
  }

  public static Book findBookByID(String id) {
    MongoDB database = MongoDB.getInstance();
    String jsonBook = database.findAnObject("books", "id", id);
    return MongoDB.jsonToObject(jsonBook, Book.class);
  }

  // find all books which title contains `keyword`
  public static List<Book> findBookByKeyword(String keyword) {
    MongoDB database = MongoDB.getInstance();
    List<String> jsonBook = database.findAllObject("books", "title", keyword);
    List<Book> result = new ArrayList<>();
    jsonBook.forEach(curBook -> {
      result.add(MongoDB.jsonToObject(curBook, Book.class));
    });
    return result;
  }

  public static boolean addBook(Book book) {
    if (isAvailable(book)) {
      return false;
    }
    MongoDB database = MongoDB.getInstance();
    database.addToCollection("books", MongoDB.objectToMap(book));
    return true;
  }

  public static boolean deleteBook(Book book) {
    if (!isAvailable(book)) {
      return false;
    }
    MongoDB database = MongoDB.getInstance();
    database.deleteFromCollection("books", "id", book.getId());
    return true;
  }

  public static boolean editBook(Book book) {
    MongoDB database = MongoDB.getInstance();
    if (database.findAnObject("books", "id", book.getId()) == null) {
      return false;
    }
    database.updateData("books", "id", book.getId(), MongoDB.objectToMap(book));
    return true;
  }

  public static void main(String[] args) {
    List<Book> bk = searchByKeyword("math");
    bk.get(0).setDescription("let him cook1");
    editBook(bk.get(0));
//    System.out.println(bk.get(0));
  }
}