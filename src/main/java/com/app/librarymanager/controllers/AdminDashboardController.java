package com.app.librarymanager.controllers;


import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.text.Text;

public class AdminDashboardController extends ControllerWithLoader {

  private int totalBooks;
  private int totalCategories;
  private int totalUsers;

  @FXML
  private Text totalBooksCount;
  @FXML
  private Text totalCategoriesCount;
  @FXML
  private Text totalUsersCount;

  @FXML
  private void initialize() {
    loadStats();
  }

  private void loadStats() {
    Task<Void> task = new Task<Void>() {
      @Override
      protected Void call() {
        totalBooks = (int) BookController.numberOfBooks();
        totalCategories = (int) CategoriesController.countCategories();
        totalUsers = UserController.countTotalUser();
        return null;
      }
    };
    task.setOnSucceeded(event -> {
      totalBooksCount.setText(String.format("%d", totalBooks));
      totalCategoriesCount.setText(String.format("%d", totalCategories));
      totalUsersCount.setText(String.format("%d", totalUsers));
    });
    task.setOnFailed(event -> {
      totalBooksCount.setText("0");
      totalCategoriesCount.setText("0");
      totalUsersCount.setText("0");
    });
    new Thread(task).start();
  }

}