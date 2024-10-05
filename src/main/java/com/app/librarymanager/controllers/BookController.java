package com.app.librarymanager.controllers;

import com.app.librarymanager.models.Book;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class BookController {

  private static final String SEARCH_URL = "https://www.googleapis.com/books/v1/volumes?q=";
  private static final int SUCCESS_CODE = 200;

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
          SEARCH_URL + encodedKeyword + "&key=" + "YOUR_API_KEY";

      HttpClient client = HttpClient.newHttpClient();
      HttpRequest request = HttpRequest.newBuilder().uri(URI.create(searchUrl)).build();
      HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

      if (response.statusCode() != SUCCESS_CODE) {
        throw new Exception("Fail when trying to search \"" + keyword + "\", status code: "
            + response.statusCode());
      }

      JSONObject jsonObject = new JSONObject(response.body());
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
                thumbnail, language, authors, price, currencyCode));
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

  public static void main(String[] args) {
    ArrayList<Book> siu = searchByKeyword("lập trình");
  }
}
