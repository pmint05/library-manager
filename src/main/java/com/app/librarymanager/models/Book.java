package com.app.librarymanager.models;

import com.app.librarymanager.services.MongoDB;
import com.google.gson.annotations.Expose;
import java.util.ArrayList;
import java.util.Date;
import lombok.*;
import org.bson.Document;
import org.bson.types.ObjectId;

@Data
public class Book {

  private ObjectId _id;

  @Expose
  private String id;

  @Expose
  private String title;

  @Expose
  private String publisher;

  @Expose
  private String publishedDate;

  @Expose
  private String description;

  @Expose
  private int pageCount;

  @Expose
  private ArrayList<String> categories;

  @Expose
  private String iSBN;

  @Expose
  private String thumbnail;

  @Expose
  private String language;

  @Expose
  private ArrayList<String> authors;

  @Expose
  private int price;

  @Expose
  private String currencyCode;

  @Expose
  private String pdfLink;

  private Date lastUpdated;


  public Book() {
    _id = null;
    id = "N/A";
    title = "N/A";
    publisher = "N/A";
    publishedDate = "N/A";
    description = "N/A";
    pageCount = -1;
    categories = new ArrayList<>();
    iSBN = "N/A";
    thumbnail = "N/A";
    language = "N/A";
    authors = new ArrayList<>();
    price = -1;
    currencyCode = "N/A";
    pdfLink = "N/A";
    lastUpdated = null;
  }

  public Book(String id, String title, String publisher, String publishedDate, String description,
      int pageCount, ArrayList<String> categories, String iSBN, String thumbnail, String language,
      ArrayList<String> authors, int price, String currencyCode, String pdfLink) {
    this.id = id;
    this.title = title;
    this.publisher = publisher;
    this.publishedDate = publishedDate;
    this.description = description;
    this.pageCount = pageCount;
    this.categories = categories;
    this.iSBN = iSBN;
    this.thumbnail = thumbnail;
    this.language = language;
    this.authors = authors;
    this.price = price;
    this.currencyCode = currencyCode;
    this.pdfLink = pdfLink;
  }

  public Book(ObjectId _id, String id, String title, String publisher, String publishedDate,
      String description, int pageCount, ArrayList<String> categories, String iSBN,
      String thumbnail, String language, ArrayList<String> authors, int price, String currencyCode,
      String pdfLink, Date lastUpdated) {
    this._id = _id;
    this.id = id;
    this.title = title;
    this.publisher = publisher;
    this.publishedDate = publishedDate;
    this.description = description;
    this.pageCount = pageCount;
    this.categories = categories;
    this.iSBN = iSBN;
    this.thumbnail = thumbnail;
    this.language = language;
    this.authors = authors;
    this.price = price;
    this.currencyCode = currencyCode;
    this.pdfLink = pdfLink;
    this.lastUpdated = lastUpdated;
  }

//  public String toString() {
//    return "Book book = new Book(" + _id + ", "
//    id + ", " + title + ", " + publisher + ", " + publishedDate + ", " + description + ", "+pageCount+", ";
//  }
}
