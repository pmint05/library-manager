package com.app.librarymanager.controllers;

import com.app.librarymanager.controllers.BookLoanController.ReturnBookLoan;
import com.app.librarymanager.models.Book;
import com.app.librarymanager.models.BookLoan;
import com.app.librarymanager.models.BookLoan.Mode;
import com.app.librarymanager.models.User;
import com.app.librarymanager.utils.AlertDialog;
import com.app.librarymanager.utils.DateUtil;
import java.awt.Desktop;
import java.net.URI;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.util.List;
import org.bson.Document;

public class MyLoansController extends ControllerWithLoader {

  @FXML
  private FlowPane loansFlowPane;

  @FXML
  private void initialize() {
    loadLoans();
  }

  private void loadLoans() {
    Task<List<ReturnBookLoan>> task = new Task<>() {
      @Override
      protected List<ReturnBookLoan> call() {
        User currentUser = AuthController.getInstance().getCurrentUser();
        return BookLoanController.getAllLentBookOf(currentUser.getUid(), 0, 10);
      }
    };

    task.setOnRunning(event -> showLoading(true));

    task.setOnSucceeded(event -> {
      showLoading(false);
      List<ReturnBookLoan> sampleLoans = task.getValue();
      for (ReturnBookLoan loan : sampleLoans) {
        loansFlowPane.getChildren().add(createLoanCell(loan));
      }
    });

    task.setOnFailed(event -> {
      showLoading(false);
      AlertDialog.showAlert("error", "Error", "Failed to load loans", null);
    });

    new Thread(task).start();
  }

  private HBox createLoanCell(ReturnBookLoan item) {
    HBox content = new HBox();
    content.getStyleClass().add("flowPane-cell");
    ImageView thumbnail = new ImageView();
    VBox details = new VBox(3);
    Text title = new Text();
    title.setWrappingWidth(280);
    Text dates = new Text();
    Label type = new Label();
//    Label valid = new Label();
    Text numCopies = new Text();
    HBox actionButtons = new HBox();
    actionButtons.setAlignment(Pos.CENTER_LEFT);
    Button returnButton = new Button("Return");
    Button reBorrowButton = new Button("Re-borrow");
    Button readButton = new Button("Read");
//    HBox chips = new HBox(type, valid);
    HBox chips = new HBox(type);
    reBorrowButton.getStyleClass().addAll("btn", "btn-default");
    returnButton.getStyleClass().addAll("btn", "btn-danger");
    readButton.getStyleClass().addAll("btn", "btn-primary");

    thumbnail.setImage(new Image(item.getThumbnailBook()));
    thumbnail.setFitHeight(120);
    thumbnail.setPreserveRatio(true);
    title.setText(item.getTitleBook());
    title.getStyleClass().add("bold");
    BookLoan loan = item.getBookLoan();
    dates.setText(DateUtil.dateToString(loan.getBorrowDate()) + " - " + DateUtil.dateToString(
        loan.getDueDate()));
    type.setText(String.valueOf(loan.getType()));
    type.getStyleClass().addAll("chip", loan.getType().name().toLowerCase());
//    valid.setText(loan.isValid() ? "Valid" : "Expired");
//    valid.getStyleClass().add("chip");
//    valid.getStyleClass().add(loan.isValid() ? "success" : "danger");
    if (Mode.OFFLINE.equals(loan.getType())) {
      numCopies.setText("Copies: " + loan.getNumCopies());
    } else {
      numCopies.setText("");
    }
    if (loan.isValid()) {
      returnButton.setVisible(true);
      reBorrowButton.setVisible(false);
      reBorrowButton.setManaged(false);
      returnButton.setOnAction(event -> handleReturnBook(item));
    } else {
      returnButton.setVisible(false);
      returnButton.setManaged(false);
      reBorrowButton.setVisible(true);
      reBorrowButton.setOnAction(event -> handleReBorrowBook(item));
    }
    if (Mode.ONLINE.equals(loan.getType())) {
      readButton.setVisible(true);
      readButton.setOnAction(event -> {
        handleReadBook(item);
      });
    } else {
      readButton.setVisible(false);
      readButton.setManaged(false);
    }

    actionButtons.getChildren().addAll(returnButton, reBorrowButton, readButton);
    actionButtons.setSpacing(5);
    chips.setSpacing(5);
    details.getChildren().addAll(title, dates, numCopies, chips, actionButtons);
    content.getChildren().addAll(thumbnail, details);
    content.setSpacing(10);

    content.setUserData(loan);

    return content;
  }

  private void handleReturnBook(ReturnBookLoan item) {

    if (!AlertDialog.showConfirm("Return book", "Are you sure you want to return this book?")) {
      return;
    }

    BookLoan bookLoan = item.getBookLoan();
    // Handle return book logic
    Task<Document> task = new Task<>() {
      @Override
      protected Document call() {
        return BookLoanController.returnBook(bookLoan);
      }
    };

    setLoadingText("Returning book...");

    task.setOnSucceeded(event -> {
      AlertDialog.showAlert("success", "Success", "Book returned successfully", null);
      Document result = task.getValue();

      BookLoan bookLoanReturned = new BookLoan(result);
      item.setBookLoan(bookLoanReturned);
      updateLoanInFlowPane(item);
    });

    task.setOnFailed(event -> {
      AlertDialog.showAlert("error", "Error", "Failed to return book", null);
    });

    new Thread(task).start();
  }

  private void handleReBorrowBook(ReturnBookLoan item) {
    // Handle re-borrow book logic
    BookLoan bookLoan = item.getBookLoan();
    Task<Document> task = new Task<>() {
      @Override
      protected Document call() {
        return BookLoanController.addLoan(bookLoan);
      }
    };
    updateLoanInFlowPane(item);
  }

  private void handleReadBook(ReturnBookLoan item) {
    String bookId = item.getBookLoan().getBookId();
    Book b = BookController.findBookByID(bookId);
    try {
      if (b.getPdfLink() == null || b.getPdfLink().isEmpty() || b.getPdfLink().equals("N/A")) {
        AlertDialog.showAlert("error", "Error", "No pdf link found", null);
        return;
      }
      Desktop.getDesktop().browse(URI.create(b.getPdfLink()));
    } catch (Exception e) {
      e.printStackTrace();
      AlertDialog.showAlert("error", "Error", "Failed to open book", null);
    }
  }

  private void updateLoanInFlowPane(ReturnBookLoan bookLoan) {
    loansFlowPane.getChildren().removeIf(node -> {
      HBox loanCell = (HBox) node;
      BookLoan loan = (BookLoan) loanCell.getUserData();
      return loan.get_id().equals(bookLoan.getBookLoan().get_id());
    });
    loansFlowPane.getChildren().add(createLoanCell(new ReturnBookLoan(bookLoan.getBookLoan(),
        bookLoan.getTitleBook(), bookLoan.getThumbnailBook())));
  }
}