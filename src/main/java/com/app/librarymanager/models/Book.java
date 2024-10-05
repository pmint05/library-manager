package com.app.librarymanager.models;

import java.util.ArrayList;
import lombok.*;

@Data
public class Book {

  private String id;
  private String title;
  private String publisher;
  private String publishedDate;
  private String description;
  private int pageCount;
  private ArrayList<String> categories;
  private String iSBN;
  private String thumbnail;
  //  private ArrayList<String> lang = null;
  private String language;
  private ArrayList<String> authors = null;
  private int price;
  private String currencyCode;
//  private String epubLink;
//  private String pdfLink;

  public Book() {
    categories = new ArrayList<>();
    authors = new ArrayList<>();
  }

  public Book(String id, String title, String publisher, String publishedDate, String description,
      int pageCount, ArrayList<String> categories, String iSBN, String thumbnail, String language,
      ArrayList<String> authors, int price, String currencyCode) {
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
//    this.epubLink = epubLink;
//    this.pdfLink = pdfLink;
  }
}
