package com.app.librarymanager.controllers;

import com.app.librarymanager.models.Book;
import com.app.librarymanager.models.BookCopies;
import com.app.librarymanager.models.BookLoan;
import com.app.librarymanager.models.BookUser;
import com.app.librarymanager.utils.AlertDialog;
import com.app.librarymanager.utils.DatePickerUtil;
import com.app.librarymanager.utils.DateUtil;
import com.app.librarymanager.utils.StageManager;
import java.time.LocalDate;
import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.util.function.UnaryOperator;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.TextFormatter.Change;
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
  private BookCopies copies;

  @FXML
  private VBox detailContainer;
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
  @FXML

  private boolean isFavorite = false;

  @FXML
  private void initialize() {
    showCancel(false);
    borrowEBook.setOnAction(event -> handleBorrowEBook());
    borrowPhysicalBook.setOnAction(event -> handleBorrowPhysicalBook());
    addToFavorite.setOnAction(event -> handleAddToFavorite());
//    detailContainer.setVisible(false);
  }

   void getBookDetail(String id) {
    Task<Map<String, Object>> task = new Task<>() {
      @Override
      protected Map<String, Object> call() {
        Book b = BookController.findBookByID(id);
        Document cp = BookCopiesController.findCopy(new BookCopies(id));
        boolean isFavorite = FavoriteController.findFavorite(
            new BookUser(AuthController.getInstance().getCurrentUser().getUid(), id)) != null;
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

      Platform.runLater(() -> {
        if (book != null) {
//          detailContainer.setVisible(true);
          bookTitle.setText(book.isActivated() ? book.getTitle() : "[INACTIVE] " + book.getTitle());
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
          availableCopies.setText("Available copies: " + copies.getCopies());
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
          borrowPhysicalBook.setDisable(copies.getCopies() == 0 || !book.isActivated());
          borrowEBook.setDisable(!book.isActivated());
          addToFavorite.setDisable(!book.isActivated());

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
          if (!book.isActivated()) {
            detailContainer.setStyle("-fx-opacity: 0.5;");
            AlertDialog.showAlert("warning", "Book not available",
                "This book is currently not available for borrowing", null);
          }
        } else {
          AlertDialog.showAlert("error", "Book not found",
              "An error occurred while fetching book details", null);
        }
      });
    });
    task.setOnFailed(event -> {
      showLoading(false);
      task.getException().printStackTrace();
      AlertDialog.showAlert("error", "Book not found",
          "An error occurred while fetching book details", null);
    });
    new Thread(task).start();
  }

  private String parsePrice(double price) {
    return String.format("%,.0f", price);
  }

  private void handleBorrowEBook() {
    System.out.println("Borrowing E-Book");
    Task<Void> task = new Task<>() {
      @Override
      protected Void call() {
        Platform.runLater(() -> {
          LocalDate borrowDate = LocalDate.now();
          LocalDate dueDate = borrowDate.plusDays(90);
          BookLoan bookLoan = new BookLoan(AuthController.getInstance().getCurrentUser().getUid(),
              book.getId(), borrowDate, dueDate);
          System.out.println("Borrowing E-Book: " + bookLoan.toString());
          boolean confirm = AlertDialog.showConfirm("Borrow E-Book",
              "Are you sure you want to borrow this E-Book?");
          if (!confirm) {
            return;
          }
          Document doc = BookLoanController.addLoan(bookLoan);
          if (doc != null) {
            AlertDialog.showAlert("success", "E-Book Borrowed",
                "You have successfully borrowed the E-Book", null);
          } else {
            AlertDialog.showAlert("error", "Failed to borrow E-Book",
                "An error occurred while borrowing the E-Book", null);
          }
        });
        return null;
      }
    };
    setLoadingText("Borrowing E-Book...");
    task.setOnRunning(event -> showLoading(true));
    task.setOnSucceeded(event -> showLoading(false));
    task.setOnFailed(event -> {
      showLoading(false);
      task.getException().printStackTrace();
      AlertDialog.showAlert("error", "Failed to borrow E-Book",
          "An error occurred while borrowing the E-Book", null);
    });
    new Thread(task).start();
  }

  private void handleBorrowPhysicalBook() {
    System.out.println("Borrowing Physical Book");
    showBorrowPopup();
  }

  @FXML
  private void showBorrowPopup() {
    DatePicker fromDate = new DatePicker();
    DatePicker dueDate = new DatePicker();
    Button confirmButton = new Button("Confirm");
    Button cancelButton = new Button("Cancel");

    fromDate.setDayCellFactory(picker -> new DateCell() {
      @Override
      public void updateItem(LocalDate date, boolean empty) {
        super.updateItem(date, empty);
        setDisable(empty || date.isBefore(LocalDate.now()));
      }
    });

    fromDate.valueProperty().addListener((observable, oldValue, newValue) -> {
      if (newValue != null) {
        if (dueDate.getValue() != null && newValue.isAfter(dueDate.getValue()) || newValue
            .equals(dueDate.getValue())) {
          dueDate.setValue(null);
        }
        dueDate.setDayCellFactory(picker -> new DateCell() {
          @Override
          public void updateItem(LocalDate date, boolean empty) {
            super.updateItem(date, empty);
            setDisable(empty || date.isBefore(newValue) || date.equals(newValue) || date.isAfter(
                newValue.plusDays(90)));
          }
        });
      }
    });
    int maxCopies = copies.getCopies();

    TextField copiesTextField = new TextField();

    UnaryOperator<TextFormatter.Change> filter = change -> {
      String newText = change.getControlNewText();
      if (newText.matches("\\d*")) {
        return change;
      }
      return null;
    };

    TextFormatter<Integer> textFormatter = new TextFormatter<>(filter);
    copiesTextField.setTextFormatter(textFormatter);
    copiesTextField.setPromptText("Smaller or equal to " + maxCopies + "...");

    confirmButton.getStyleClass().addAll("btn", "btn-primary");
    cancelButton.getStyleClass().addAll("btn", "btn-text");
    HBox buttonsBox = new HBox(10, confirmButton, cancelButton);
    VBox vbox = new VBox(10.0);
    fromDate.setPromptText("Select borrow date");
    dueDate.setPromptText("Select due date");
    fromDate.getStyleClass().addAll("input", "date-picker");
    dueDate.getStyleClass().addAll("input", "date-picker");
    copiesTextField.getStyleClass().add("input");
    fromDate.getEditor().setOnMouseClicked(event -> fromDate.show());
    dueDate.getEditor().setOnMouseClicked(event -> dueDate.show());
    DatePickerUtil.disableEditor(fromDate);
    DatePickerUtil.disableEditor(dueDate);
    DatePickerUtil.setDatePickerFormat(fromDate);
    DatePickerUtil.setDatePickerFormat(dueDate);

    vbox.getChildren().addAll(new Label("From Date:"), fromDate, new Label("Due Date:"), dueDate,
        new Label("Number of Copies:"), copiesTextField, buttonsBox);
    vbox.setPadding(new Insets(20));
    Scene scene = new Scene(vbox);
    scene.getStylesheets()
        .add(Objects.requireNonNull(StageManager.class.getResource("/styles/global.css"))
            .toExternalForm());
    Stage popupStage = new Stage();
    popupStage.setHeight(350);
    popupStage.setWidth(300);
    popupStage.setResizable(false);

    popupStage.setScene(scene);
    popupStage.setTitle("Select Borrow Dates and Copies");
    popupStage.initModality(Modality.APPLICATION_MODAL);

    confirmButton.setOnAction(event -> {
      LocalDate borrowDate = fromDate.getValue();
      LocalDate returnDate = dueDate.getValue();
      if (borrowDate == null || returnDate == null) {
        AlertDialog.showAlert("error", "Invalid Dates",
            "Please select valid borrow and return dates", null);
        return;
      }
      if (copiesTextField.getText().isEmpty()) {
        AlertDialog.showAlert("error", "Invalid Number of Copies",
            "Please enter the number of copies you want to borrow", null);
        return;
      }
      int numCopies = Integer.parseInt(copiesTextField.getText());
      System.out.println("Borrowing Physical Book: " + borrowDate + " - " + returnDate + " - "
          + numCopies);
      if (numCopies > maxCopies) {
        AlertDialog.showAlert("error", "Invalid Number of Copies",
            "The number of copies you requested is more than the available copies", null);
        return;
      }
      BookLoan bookLoan = new BookLoan(AuthController.getInstance().getCurrentUser().getUid(),
          book.getId(), borrowDate, returnDate, numCopies);
      Document doc = BookLoanController.addLoan(bookLoan);
      if (doc != null) {
        AlertDialog.showAlert("success", "Book Borrowed",
            "You have successfully borrowed the book", null);
      } else {
        AlertDialog.showAlert("error", "Failed to borrow book",
            "An error occurred while borrowing the book", null);
      }
      popupStage.close();
    });

    cancelButton.setOnAction(event -> popupStage.close());

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
          "An error occurred while adding the book to favorite", null);
    }
  }

  @FXML
  private void close() {
    System.out.println("Closing book detail");
    // remove this view from the stack
  }
}
