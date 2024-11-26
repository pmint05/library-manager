package com.app.librarymanager.controllers;

import com.app.librarymanager.models.Book;
import com.app.librarymanager.services.MongoDB;
import com.app.librarymanager.utils.Fetcher;
import com.mongodb.client.model.Filters;
import io.github.cdimascio.dotenv.Dotenv;
import java.util.List;
import java.util.stream.IntStream;
import org.bson.Document;
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
        double price;
        String currencyCode;
        if (retailPrice == null) {
          price = -1;
          currencyCode = "N/A";
        } else {
          price = retailPrice.getInt("amount");
          currencyCode = retailPrice.getString("currencyCode");
        }

        JSONObject accessInfo = curBook.optJSONObject("accessInfo");
        String pdfLink = "N/A";
        if (accessInfo != null) {
          JSONObject pdfJson = accessInfo.optJSONObject("pdf");
          if (pdfJson != null) {
            pdfLink = pdfJson.optString("downloadLink", "N/A");
          }
        }

        bookList.add(
            new Book(id, title, publisher, publishedDate, description, pageCount, categories, iSBN,
                thumbnail, language, authors, price, currencyCode, pdfLink));
      }

      return bookList;
    } catch (Exception e) {
      System.err.println(e.getMessage());
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

  public static Book getBookFromDocument(Document document) {
    Book curBook = MongoDB.jsonToObject(document.toJson(), Book.class);
    curBook.set_id(document.getObjectId("_id"));
    curBook.setLastUpdated(document.getDate("lastUpdated"));
    return curBook;
  }

  public static Book findBookByISBN(String iSBN) {
    return getBookFromDocument(MongoDB.getInstance().findAnObject("books", "iSBN", iSBN));
  }

  public static Book findBookByID(String id) {
    return getBookFromDocument(MongoDB.getInstance().findAnObject("books", "id", id));
  }

  // find all books which title contains `keyword`
  public static List<Book> findBookByKeyword(String keyword, int start, int length) {
    List<Document> jsonBook = MongoDB.getInstance()
        .findAllObject("books", Filters.regex("title", keyword, "i"), start, length);
    List<Book> result = new ArrayList<>();
    jsonBook.forEach(curBook -> result.add(getBookFromDocument(curBook)));
    return result;
  }


  public static Document addBook(Book book) {
    if (isAvailable(book)) {
      return null;
    }
    MongoDB database = MongoDB.getInstance();
    return database.addToCollection("books", MongoDB.objectToMap(book));
  }

  public static boolean deleteBook(Book book) {
    if (!isAvailable(book)) {
      return false;
    }
    MongoDB database = MongoDB.getInstance();
    database.deleteFromCollection("books", "id", book.getId());
    return true;
  }

  public static Document editBook(Book book) {
    MongoDB database = MongoDB.getInstance();
    Document document = database.findAnObject("books", "id", book.getId());
    if (document == null) {
      return null;
    }
    document = database.updateData("books", "id", book.getId(), MongoDB.objectToMap(book));
    return document;
  }

  public static void main(String[] args) {
  }
}