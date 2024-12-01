package com.app.librarymanager.controllers;

import com.app.librarymanager.models.Book;
import com.app.librarymanager.models.BookLoan;
import com.app.librarymanager.models.User;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class MyLoansController {

  @FXML
  private ListView<Book> loansListView;

  @FXML
  private void initialize() {
    User currentUser = AuthController.getInstance().getCurrentUser();
    List<Book> sampleLoans = BookLoanController.getAllLentBook(currentUser.getUid());
    loansListView.getItems().addAll(sampleLoans);

    loansListView.setCellFactory(new Callback<ListView<Book>, ListCell<Book>>() {
      @Override
      public ListCell<Book> call(ListView<Book> param) {
        return new BookLoanCell();
      }
    });
  }

  private class BookLoanCell extends ListCell<Book> {

    private HBox content;
    private ImageView thumbnail;
    private VBox details;
    private Label title;
    private Label dates;
    private Label type;
    private Label valid;
    private Label numCopies;
    private Button actionButton;

    public BookLoanCell() {
      super();
      thumbnail = new ImageView();
      title = new Label();
      dates = new Label();
      type = new Label();
      valid = new Label();
      numCopies = new Label();
      actionButton = new Button();
      details = new VBox(title, dates, type, valid, numCopies);
      content = new HBox(thumbnail, details, actionButton);
      content.setSpacing(10);
    }

    @Override
    protected void updateItem(Book item, boolean empty) {
      super.updateItem(item, empty);
//      if (item != null && !empty) {
//        thumbnail.setImage(new Image(getClass().getResourceAsStream("/images/" + item.getThumbnail())));
//        title.setText(item.getTitle());
//        dates.setText(item.getBorrowDate() + " - " + item.getDueDate());
//        type.setText(item.getType());
//        valid.setText(item.isValid() ? "Valid" : "Not Valid");
//        valid.getStyleClass().add("chip");
//        if ("OFFLINE".equals(item.getType())) {
//          numCopies.setText("Copies: " + item.getNumCopies());
//        } else {
//          numCopies.setText("");
//        }
//        if (item.isValid()) {
//          actionButton.setText("Return");
//          actionButton.setOnAction(event -> handleReturnBook(item));
//        } else {
//          actionButton.setText("Re-borrow");
//          actionButton.setOnAction(event -> handleReBorrowBook(item));
//        }
//        setGraphic(content);
//      } else {
//        setGraphic(null);
//      }
//    }
    }

    private void handleReturnBook(BookLoan bookLoan) {
      // Handle return book logic
      bookLoan.setValid(false);
      loansListView.refresh();
    }

    private void handleReBorrowBook(BookLoan bookLoan) {
      // Handle re-borrow book logic
      bookLoan.setValid(true);
      loansListView.refresh();
    }
  }
}