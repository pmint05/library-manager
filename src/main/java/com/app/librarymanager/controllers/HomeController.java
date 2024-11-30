package com.app.librarymanager.controllers;

import com.app.librarymanager.interfaces.AuthStateListener;

import com.app.librarymanager.models.Book;
import com.app.librarymanager.models.User;
import com.app.librarymanager.utils.AlertDialog;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.util.Duration;
import org.json.JSONObject;

public class HomeController implements AuthStateListener {

  private static HomeController instance;

  public static synchronized HomeController getInstance() {
    if (instance == null) {
      instance = new HomeController();
    }
    return instance;
  }

  @FXML
  private Label welcomeLabel;
  @FXML
  private StackPane contentPane;
  @FXML
  private TextField searchField;
  @FXML
  private VBox searchResults;
  private Timer debounceTimer;
  private PauseTransition pauseTransition;


  @FXML
  private void initialize() {
    AuthController.getInstance().addAuthStateListener(this);
    updateUI(AuthController.getInstance().isAuthenticated(),
        AuthController.getInstance().getCurrentUser());
    searchField.setOnKeyPressed(keyEvent -> {
      if (keyEvent.getCode().toString().equals("ENTER")) {
        searchBooks();
      }
    });
    pauseTransition = new PauseTransition(Duration.millis(200));
    pauseTransition.setOnFinished(event -> searchBooks());
  }

  private void updateUI(boolean isAuthenticated, User user) {
    if (isAuthenticated) {
      welcomeLabel.setText("Welcome, " + user.getEmail());
//      loadComponent("/fxml/ManageBookLoans.fxml");
    } else {
      welcomeLabel.setText("Welcome, Guest");
//      loadComponent("/fxml/Login.fxml");
    }
    searchField.setOnKeyReleased(keyEvent -> {
      if (keyEvent.getCode().toString().equals("ENTER")) {
        searchBooks();
      } else {
        pauseTransition.playFromStart();
      }
    });
  }

  private void searchBooks() {
    String searchQuery = searchField.getText();
    if (searchQuery.isEmpty()) {
      searchResults.getChildren().clear();
      return;
    }
    Task<List<Book>> searchTask = new Task<List<Book>>() {
      @Override
      protected List<Book> call() {
        return BookController.findBookByKeyword(searchQuery, 0, 100);
      }
    };
    searchTask.setOnSucceeded(workerStateEvent -> {
      List<Book> books = searchTask.getValue();
      updateSearchResults(books);
    });
    searchTask.setOnFailed(e -> {
      e.getSource().getException().printStackTrace();
      AlertDialog.showAlert("error", "Search failed", "An error occurred while searching for books",
          null);
    });
    new Thread(searchTask).start();
  }

  private void updateSearchResults(List<Book> books) {
    searchResults.getChildren().clear();
    if (books.isEmpty()) {
      searchResults.getChildren().add(new Label("No results found"));
      return;
    }
    for (Book book : books) {
      searchResults.getChildren().add(new Label(book.getTitle()));
    }
  }


  private void debounceSearch() {
    if (debounceTimer != null) {
      debounceTimer.cancel();
    }
    debounceTimer = new Timer();
    debounceTimer.schedule(new TimerTask() {
      @Override
      public void run() {
        Platform.runLater(() -> searchBooks());
      }
    }, 100);
  }

  @Override
  public void onAuthStateChanged(boolean isAuthenticated) {
    updateUI(isAuthenticated, AuthController.getInstance().getCurrentUser());
  }

  private void loadComponent(String fxmlPath) {
    try {
      FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
      Parent component = loader.load();
      contentPane.getChildren().clear();
      contentPane.getChildren().add(component);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void handleImageClick(MouseEvent mouseEvent) {
  }

  public void handleSearch(MouseEvent mouseEvent) {
  }
}

