package com.app.librarymanager.controllers;

import com.app.librarymanager.models.Book;
import com.app.librarymanager.utils.AlertDialog;
import java.util.ArrayList;
import java.util.List;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Duration;
import lombok.Setter;

public class SearchController {

  private String keyword;

  @FXML
  private TextField searchInput;

  @FXML
  private FlowPane searchResultsPane;
  @FXML
  private ScrollPane searchResultsScrollPane;
  private PauseTransition pauseTransition;
  @FXML
  private Text searchStatus;
  private Task<List<Book>> searchTask;
  private List<Task<VBox>> renderTasks = new ArrayList<>();


  @FXML
  private void initialize() {
    searchResultsScrollPane.viewportBoundsProperty()
        .addListener((observable, oldValue, newValue) -> {
          searchResultsPane.setPrefWidth(newValue.getWidth());
        });
    pauseTransition = new PauseTransition(Duration.millis(250));
    pauseTransition.setOnFinished(event -> searchBooks());

    searchInput.textProperty().addListener((observable, oldValue, newValue) -> {
      keyword = newValue.trim();
      searchResultsPane.getChildren().clear();
      pauseTransition.playFromStart();
      if (keyword.isEmpty()) {
        searchStatus.setText("Enter a keyword to search");
      }
    });

    searchResultsScrollPane.addEventFilter(ScrollEvent.SCROLL, event -> {
      double deltaY = event.getDeltaY() * 3;
      searchResultsScrollPane.setVvalue(
          searchResultsScrollPane.getVvalue() - deltaY / searchResultsScrollPane.getContent()
              .getBoundsInLocal().getHeight());
      event.consume();
    });
  }

  private void searchBooks() {
    if (searchTask != null && searchTask.isRunning()) {
      searchTask.cancel();
    }

    renderTasks.forEach(task -> {
      if (task.isRunning()) {
        task.cancel();
      }
    });
    renderTasks.clear();

    String searchQuery = searchInput.getText().trim();
    searchStatus.setText("Searching...");
    searchResultsPane.getChildren().clear();

    searchTask = new Task<List<Book>>() {
      @Override
      protected List<Book> call() {
        return BookController.findBookByKeyword(searchQuery, 0, 10);
      }
    };

    searchTask.setOnSucceeded(workerStateEvent -> {
      List<Book> books = searchTask.getValue();
      if (searchQuery.equals(searchInput.getText().trim())) {
        searchStatus.setText(books.size() + " results found");
        updateSearchResults(books);
      }
    });

    searchTask.setOnFailed(e -> {
      e.getSource().getException().printStackTrace();
      AlertDialog.showAlert("error", "Search failed", "An error occurred while searching for books",
          null);
    });

    new Thread(searchTask).start();
  }


  private void updateSearchResults(List<Book> books) {
    searchResultsPane.getChildren().clear();
    if (books.isEmpty()) {
      return;
    }

    for (Book book : books) {
      Task<VBox> bookTask = new Task<VBox>() {
        @Override
        protected VBox call() throws Exception {
          FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/components/book.fxml"));
          VBox bookComponent = loader.load();
          BookComponentController bookComponentController = loader.getController();
          bookComponentController.setBook(book);
          return bookComponent;
        }
      };

      bookTask.setOnSucceeded(workerStateEvent -> {
        VBox bookComponent = bookTask.getValue();
        Platform.runLater(() -> searchResultsPane.getChildren().add(bookComponent));
      });

      bookTask.setOnFailed(e -> {
        e.getSource().getException().printStackTrace();
        AlertDialog.showAlert("error", "Loading failed",
            "An error occurred while loading a book component", null);
      });
      renderTasks.add(bookTask);
      new Thread(bookTask).start();
    }
  }

  public void setKeyword(String keyword) {
    this.keyword = keyword;
    if (searchInput != null) {
      searchInput.setText(keyword);
    }
  }
}