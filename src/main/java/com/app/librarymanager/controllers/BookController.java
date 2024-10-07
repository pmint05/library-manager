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
//  private static final int SUCCESS_CODE = 200;

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

//      HttpClient client = HttpClient.newHttpClient();
//      HttpRequest request = HttpRequest.newBuilder().uri(URI.create(searchUrl)).build();
//      HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
//
//      if (response.statusCode() != SUCCESS_CODE) {
//        throw new Exception("Fail when trying to search \"" + keyword + "\", status code: "
//            + response.statusCode());
//      }

      JSONObject jsonObject = Fetcher.get(searchUrl);

//      System.err.println(searchUrl);
//      System.err.println(jsonObject);

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
//        System.out.println(categories);

        JSONArray industryIdentifiers = volumeInfo.optJSONArray("industryIdentifiers");
        String iSBN = "N/A";
        if (industryIdentifiers != null) {
          for (int j = 0; j < industryIdentifiers.length(); j++) {
            JSONObject currentIdentifier = industryIdentifiers.getJSONObject(j);
            if (currentIdentifier.getString("type").equals("ISBN_13")) {
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

//        String epubLink = "N/A";
//        String pdfLink = "N/A";
//        JSONObject accessInfo = curBook.getJSONObject("accessInfo");
//        JSONObject epubInfo = accessInfo.getJSONObject("epub");
//        if (epubInfo.getBoolean("isAvailable")) {
//          epubLink = epubInfo.getString("acsTokenLink");
//        }
//        JSONObject pdfInfo = accessInfo.getJSONObject("pdf");
//        if (pdfInfo.getBoolean("isAvailable")) {
//          pdfLink = pdfInfo.getString("acsTokenLink");
//        }

//        description = "";

        bookList.add(
            new Book(id, title, publisher, publishedDate, description, pageCount, categories, iSBN,
                thumbnail, language, authors, price, currencyCode, "N/A"));
      }

//      for (Book obj : bookList) {
//        System.out.println(obj);
//      }

//      System.out.println(bookList);

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

  private static Book firebaseObjectToBook(String id) {
    FirebaseFirestore database = FirebaseFirestore.getInstance();
    Gson gson = new Gson();
    JsonElement jsonElement = gson.toJsonTree(database.getDocumentObject("books", id));
    return gson.fromJson(jsonElement, Book.class);
  }

  public static boolean isAvailable(Book book) {
    // available this object, or available book's ISBN ...
//    if (FirebaseFirestore.getInstance().haveDocument("books", book.getId())) {
//      return true;
//    }

    return FirebaseFirestore.getInstance().haveDocument("books", book.getId());
  }

  // isbn13
  // isbn should be distinct for all books!
  // how should we handle it ...
//  public static Book findBookByISBN(String iSBN) {
//    if (iSBN.length() != 13) {
//      return null;
//    }
//    for (int i = 0; i < iSBN.length(); i++) {
//      if ('0' <= iSBN.charAt(i) && iSBN.charAt(i) <= '9') {
//        continue;
//      }
//      return null;
//    }
//
//  }

  public static Book findBookByID(String id) {
    FirebaseFirestore database = FirebaseFirestore.getInstance();
    if (!database.haveDocument("books", id)) {
      return null;
    }
    return firebaseObjectToBook(id);
  }

  // find all books which title contains `keyword`
  public static ArrayList<Book> findBookByKeyword(String keyword) {
    FirebaseFirestore database = FirebaseFirestore.getInstance();
    ArrayList<Book> listRelatedBooks = new ArrayList<>();
    ArrayList<Map<String, Object>> allBooks = database.getCollection("books");
    for (Map<String, Object> book : allBooks) {
      if (((String) book.get("title")).contains(keyword)) {
        listRelatedBooks.add(firebaseObjectToBook((String) book.get("id")));
      }
    }
    return listRelatedBooks;
  }

  public static void addBook(Book book) {
    if (isAvailable(book)) {
      return;
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
  }

  public static void deleteBook(Book book) {
    if (!isAvailable(book)) {
      return;
    }
    FirebaseFirestore database = FirebaseFirestore.getInstance();
    database.deleteData("books", book.getId());

    for (String category : book.getCategories()) {
      String slugCategory = category.toLowerCase();
      database.removeFromArray("categories", slugCategory, "booksId", book.getId());
    }
  }

//  public static void editBook()

  public static void main(String[] args) {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    ArrayList<Book> siu = searchByKeyword("lập trình");
//    for (int i = 0; i < 10; i++) {
//      siu.get(i).setDescription("book " + i);
//      addBook(siu.get(i));
//    }
//    ArrayList<String> category = new ArrayList<>();
//    category.add("Education");
//    System.out.println(db.getDataWithFilter("books", "categories", category));

//    System.out.println(findBookByID("sqg5EAAAQBAJ"));
//    System.out.println(findBookByID("NotAvailable"));

//    System.out.println(db.getCollection("books"));
//    for (Book obj : findBookByKeyword("Scratch")) {
//      System.out.println(obj);
//    }
//    System.out.println(findBookByKeyword("Scratch"));
  }
}