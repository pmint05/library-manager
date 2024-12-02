package com.app.librarymanager.controllers;

import com.app.librarymanager.models.Book;
import com.app.librarymanager.models.BookCopies;
import com.app.librarymanager.models.BookUser;
import com.app.librarymanager.utils.AlertDialog;
import com.app.librarymanager.utils.DateUtil;
import java.time.LocalDate;
import java.util.Map;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Pair;
import org.bson.Document;
import org.kordamp.ikonli.javafx.FontIcon;

public class BookDetailController extends ControllerWithLoader {

  private Book book;

  @FXML
  private ImageView bookCover;
  @FXML
  private Label bookTitle;
  @FXML
  private Label bookAuthor;
  @FXML
  private TextArea bookDescription;
  @FXML
  private Label bookPublisher;
  @FXML
  private FlowPane bookCategories;
  @FXML
  private Label bookLanguage;
  @FXML
  private Label bookPublishedDate;
  @FXML
  private Label bookIsbn;
  @FXML
  private Button closeBtn;
  @FXML
  private Label bookPublishingInfo;
  @FXML
  private Label availableCopies;
  @FXML
  private Text bookPrice;
  @FXML
  private Text bookDiscountPrice;
  @FXML
  private Text currencyCode;
  @FXML
  private HBox starsContainer;
  @FXML
  private Button addToFavorite;
  @FXML
  private Button borrowEBook;
  @FXML
  private Button borrowPhysicalBook;

  private boolean isFavorite = false;

  @FXML
  private void initialize() {
    showCancel(false);
    borrowEBook.setOnAction(event -> handleBorrowEBook());
    borrowPhysicalBook.setOnAction(event -> handleBorrowPhysicalBook());
    addToFavorite.setOnAction(event -> handleAddToFavorite());

  }

  public void getBookDetail(String id) {
    Task<Map<String, Object>> task = new Task<>() {
      @Override
      protected Map<String, Object> call() {
        Book b = BookController.findBookByID(id);
        Document cp = BookCopiesController.findCopy(new BookCopies(id));
        boolean isFavorite = FavoriteController.findFavorite(
            new BookUser(AuthController.getInstance().getCurrentUser().getUid(), id)) != null;
        BookCopies copies = null;
        double avgRating = BookRatingController.averageRating(id);
        if (cp != null) {
          copies = new BookCopies(cp);
        } else {
          copies = new BookCopies(id);
        }
        return Map.of("book", b, "copies", copies, "isFavorite", isFavorite, "avgRating",
            avgRating);
      }
    };
    task.setOnRunning(event -> showLoading(true));
    task.setOnSucceeded(event -> {
      showLoading(false);
      Map<String, Object> result = task.getValue();

      book = (Book) result.get("book");
      BookCopies copies = (BookCopies) result.get("copies");
      isFavorite = (boolean) result.get("isFavorite");
      double avgRating = (double) result.get("avgRating");

      System.out.println("Book found: " + book.toString());
      System.out.println("Copies found: " + copies.toString());
      System.out.println("Is favorite: " + isFavorite);

      if (book != null) {
        bookTitle.setText(book.getTitle());
        bookTitle.setText(book.getTitle());
        try {
          bookCover.setImage(new Image("https://books.google.com/books/content?id=" + id
              + "&printsec=frontcover&img=1&zoom=0&edge=curl&source=gbs_api"));
        } catch (Exception e) {
          bookCover.setImage(new Image(
              "https://books.google.com/books/content?id=&printsec=frontcover&img=1&zoom=0&edge=curl&source=gbs_api"));
        }
        bookAuthor.setText("by " + book.getAuthors().toString().replaceAll("[\\[\\]]", ""));
        bookPublishingInfo.setText(
            "Published by " + book.getPublisher() + " on " + DateUtil.ymdToDmy(
                book.getPublishedDate()));
        bookDescription.setText(book.getDescription());
        for (String category : book.getCategories()) {
          Label label = new Label(category);
          label.getStyleClass().addAll("chip", "info");
          bookCategories.getChildren().add(label);
        }
        bookLanguage.setText(book.getLanguage());
//        bookIsbn.setText(book.getISBN());
        availableCopies.setText("Available copies: " + copies.getCopies());
        borrowPhysicalBook.setDisable(copies.getCopies() == 0);
        bookPrice.setText(parsePrice(book.getPrice()));
        currencyCode.setText(book.getCurrencyCode());

        if (book.getDiscountPrice() > 0) {
          bookDiscountPrice.setText(parsePrice(book.getDiscountPrice()));
          bookPrice.getStyleClass().add("small-strike");
        } else {
          bookDiscountPrice.setVisible(false);
        }
        addToFavorite.setGraphic(
            isFavorite ? new FontIcon("antf-heart") : new FontIcon("anto-heart"));
        addToFavorite.getStyleClass().add(isFavorite ? "on" : "off");

        for (int i = 0; i < 5; i++) {
          FontIcon icon = new FontIcon();
          if (avgRating - i >= 0.5) {
            icon.setIconLiteral("antf-star");
          } else {
            icon.setIconLiteral("anto-star");
          }
          icon.getStyleClass().add("star");
          starsContainer.getChildren().add(icon);
        }
        starsContainer.getChildren().add(new Label("(" + String.format("%.1f", avgRating) + ")"));

      }
    });
    task.setOnFailed(event -> {
      showLoading(false);
      task.getException().printStackTrace();
      AlertDialog.showAlert("error", "Book not found",
          "An error occurred while fetching book details",
          null);
    });
    new Thread(task).start();
  }

  private String parsePrice(double price) {
    return String.format("%,.0f", price);
  }

  private void handleBorrowEBook() {
    System.out.println("Borrowing E-Book");
    // borrow E-Book
  }

  private void handleBorrowPhysicalBook() {
    System.out.println("Borrowing Physical Book");
    showBorrowPopup();
  }

  @FXML
  private void showBorrowPopup() {
    DatePicker fromDate = new DatePicker();
    DatePicker dueDate = new DatePicker();

    fromDate.setDayCellFactory(picker -> new DateCell() {
      @Override
      public void updateItem(LocalDate date, boolean empty) {
        super.updateItem(date, empty);
        setDisable(empty || date.isBefore(LocalDate.now()));
      }
    });

    fromDate.valueProperty().addListener((observable, oldValue, newValue) -> {
      if (newValue != null) {
        dueDate.setDayCellFactory(picker -> new DateCell() {
          @Override
          public void updateItem(LocalDate date, boolean empty) {
            super.updateItem(date, empty);
            setDisable(empty || date.isBefore(newValue) || date.isAfter(newValue.plusDays(90)));
          }
        });
      }
    });

    // Create and show the popup with the date pickers
    VBox vbox = new VBox(10, new Label("From Date:"), fromDate, new Label("Due Date:"), dueDate);
    vbox.setPadding(new Insets(10));
    Scene scene = new Scene(vbox);
    Stage popupStage = new Stage();
    popupStage.setScene(scene);
    popupStage.setTitle("Select Borrow Dates");
    popupStage.initModality(Modality.APPLICATION_MODAL);
    popupStage.showAndWait();
  }

  private void handleAddToFavorite() {
    boolean success;
    if (isFavorite) {
      success = FavoriteController.removeFromFavorite(
          new BookUser(AuthController.getInstance().getCurrentUser().getUid(), book.getId()));
    } else {
      Document favorite = FavoriteController.addToFavorite(
          new BookUser(AuthController.getInstance().getCurrentUser().getUid(), book.getId()));
      success = favorite != null;
    }
    if (success) {
      isFavorite = !isFavorite;
      addToFavorite.setGraphic(
          isFavorite ? new FontIcon("antf-heart") : new FontIcon("anto-heart"));
      addToFavorite.getStyleClass().add(isFavorite ? "on" : "off");
      addToFavorite.getStyleClass().removeAll(isFavorite ? "off" : "on");
    } else {
      AlertDialog.showAlert("error", "Failed to add to favorite",
          "An error occurred while adding the book to favorite",
          null);
    }
  }


  @FXML
  private void close() {
    System.out.println("Closing book detail");
    // remove this view from the stack
  }
}
