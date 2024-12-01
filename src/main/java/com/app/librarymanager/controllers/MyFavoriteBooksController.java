package com.app.librarymanager.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class MyFavoriteBooksController {

  @FXML
  private ListView<String> favoriteBooksListView;
  @FXML
  private TextField searchField;
  @FXML
  private Button searchButton;

  private ObservableList<String> favoriteBooks = FXCollections.observableArrayList("Book A", "Book B", "Book C");

  @FXML
  private void initialize() {
    // Initialize the favorite books list view with sample data
    favoriteBooksListView.setItems(favoriteBooks);

    // Set action for search button
    searchButton.setOnAction(event -> handleSearch());
  }

  private void handleSearch() {
    String searchText = searchField.getText().toLowerCase();
    if (!searchText.isEmpty()) {
      ObservableList<String> filteredBooks = FXCollections.observableArrayList();
      for (String book : favoriteBooks) {
        if (book.toLowerCase().contains(searchText)) {
          filteredBooks.add(book);
        }
      }
      favoriteBooksListView.setItems(filteredBooks);
    } else {
      favoriteBooksListView.setItems(favoriteBooks);
    }
  }
}