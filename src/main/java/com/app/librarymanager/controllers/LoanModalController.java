package com.app.librarymanager.controllers;

import com.app.librarymanager.controllers.BookLoanController.BookLoanUser;
import com.app.librarymanager.models.Book;
import com.app.librarymanager.models.BookLoan;
import com.app.librarymanager.utils.AlertDialog;
import com.app.librarymanager.utils.DatePickerUtil;
import com.app.librarymanager.utils.DateUtil;
import java.time.LocalDate;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.bson.Document;

public class LoanModalController extends ControllerWithLoader {

  @FunctionalInterface
  public interface SaveCallback {

    void onSave(BookLoanUser bookLoanUser);
  }

  @FXML
  private DatePicker borrowDateField;
  @FXML
  private DatePicker dueDateField;
  @FXML
  private ComboBox<String> typeField;
  @FXML
  private TextField copiesField;
  @FXML
  private CheckBox validField;

  private SaveCallback saveCallback;

  private BookLoanUser bookLoanUser;

  public void initialize() {
    typeField.getItems().addAll("ONLINE", "OFFLINE");
    showCancel(false);
    DatePickerUtil.setDatePickerFormat(borrowDateField);
    DatePickerUtil.setDatePickerFormat(dueDateField);
    DatePickerUtil.disableEditor(borrowDateField);
    DatePickerUtil.disableEditor(dueDateField);
    borrowDateField.setDayCellFactory(picker -> new DateCell() {
      @Override
      public void updateItem(LocalDate date, boolean empty) {
        super.updateItem(date, empty);
        setDisable(empty || date.isBefore(LocalDate.now()));
      }
    });

    borrowDateField.valueProperty().addListener((observable, oldValue, newValue) -> {
      if (newValue != null) {
        if (dueDateField.getValue() != null && newValue.isAfter(dueDateField.getValue()) || newValue
            .equals(dueDateField.getValue())) {
          dueDateField.setValue(null);
        }
        dueDateField.setDayCellFactory(picker -> new DateCell() {
          @Override
          public void updateItem(LocalDate date, boolean empty) {
            super.updateItem(date, empty);
            setDisable(empty || date.isBefore(newValue) || date.equals(newValue) || date.isAfter(
                newValue.plusDays(90)));
          }
        });
      }
    });
  }

  public void setBookLoanUser(BookLoanUser bookLoanUser) {
    this.bookLoanUser = bookLoanUser;
    borrowDateField.setValue(DateUtil.dateToLocalDate(bookLoanUser.getBookLoan().getBorrowDate()));
    dueDateField.setValue(DateUtil.dateToLocalDate(bookLoanUser.getBookLoan().getDueDate()));
    typeField.setValue(bookLoanUser.getBookLoan().getType().toString());
    copiesField.setText(String.valueOf(bookLoanUser.getBookLoan().getNumCopies()));
    validField.setSelected(bookLoanUser.getBookLoan().isValid());
  }

  public void setSaveCallback(SaveCallback saveCallback) {
    this.saveCallback = saveCallback;
  }

  public void onSubmit() {
    setLoadingText("Saving...");
    Task<Document> task = new Task<Document>() {
      @Override
      protected Document call() {
        showLoading(true);
        BookLoan updatedBookLoan = new BookLoan(
            bookLoanUser.getBookLoan().get_id(),
            bookLoanUser.getBookLoan().getUserId(),
            bookLoanUser.getBookLoan().getBookId(),
            DateUtil.localDateToDate(borrowDateField.getValue()),
            DateUtil.localDateToDate(dueDateField.getValue()), validField.isSelected(),
            BookLoan.Mode.valueOf(typeField.getValue()), Integer.parseInt(copiesField.getText()));
        return BookLoanController.editLoan(updatedBookLoan);
      }
    };
    task.setOnSucceeded(event -> {
      showLoading(false);
      Stage stage = (Stage) borrowDateField.getScene().getWindow();

      if (task.getValue() == null) {
        AlertDialog.showAlert("error", "Error", "Failed to update loan", null);
        return;
      } else {
        AlertDialog.showAlert("success", "Success", "Loan updated successfully", null);
        bookLoanUser.getBookLoan().setType(BookLoan.Mode.valueOf(typeField.getValue()));
        bookLoanUser.getBookLoan()
            .setBorrowDate(DateUtil.localDateToDate(borrowDateField.getValue()));
        bookLoanUser.getBookLoan().setDueDate(DateUtil.localDateToDate(dueDateField.getValue()));
        bookLoanUser.getBookLoan().setNumCopies(Integer.parseInt(copiesField.getText()));
        bookLoanUser.getBookLoan().setValid(validField.isSelected());
        stage.close();
        if (saveCallback != null) {
          saveCallback.onSave(bookLoanUser);
        }
      }

    });

    task.setOnFailed(event -> {
      showLoading(false);
      AlertDialog.showAlert("error", "Error", "Failed to update loan", null);
    });

    new Thread(task).start();
  }
}
