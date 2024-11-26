package com.app.librarymanager.controllers;

import com.app.librarymanager.utils.AlertDialog;
import com.app.librarymanager.utils.DatePickerUtil;
import com.app.librarymanager.utils.DateUtil;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import com.app.librarymanager.models.Book;
import javafx.util.Callback;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.json.JSONObject;

public class BookModalController extends ControllerWithLoader {

  @FunctionalInterface
  public interface SaveCallback {

    void onSave(Book book);
  }

  @FXML
  private TextField _idField;
  @FXML
  private TextField idField;
  @FXML
  private TextField iSBNField;
  @FXML
  private TextField titleField;
  @FXML
  private TextField publisherField;
  @FXML
  private TextField descriptionField;
  @FXML
  private TextField pageCountField;
  @FXML
  private TextField categoriesField;
  @FXML
  private TextField authorsField;
  @FXML
  private TextField thumbnailField;
  @FXML
  private TextField languageField;
  @FXML
  private TextField priceField;
  @FXML
  private TextField currencyCodeField;
  @FXML
  private TextField pdfLinkField;
  @FXML
  private DatePicker publishedDateField;
  @FXML
  private TextField discountPriceField;
  @FXML
  private CheckBox isActiveCheckBox;

  private Book book;
  @Setter
  private SaveCallback saveCallback;
  private boolean isEditMode = false;

  @FXML
  private void initialize() {
    DatePickerUtil.setDatePickerFormat(publishedDateField);
    publishedDateField.setDayCellFactory(new Callback<DatePicker, DateCell>() {
      @Override
      public DateCell call(DatePicker param) {
        return new DateCell() {
          @Override
          public void updateItem(LocalDate item, boolean empty) {
            super.updateItem(item, empty);
            if (item.isAfter(LocalDate.now())) {
              setDisable(true);
              setStyle("-fx-opacity: 0.5;");
            }
          }
        };
      }
    });
  }

  public void setBook(Book book) {
    this.book = book;
    if (book != null) {
      isEditMode = true;
      _idField.setText(book.get_id().toString());
      _idField.setDisable(true);
      idField.setText(book.getId());
      iSBNField.setText(book.getISBN());
      titleField.setText(book.getTitle());
      publisherField.setText(book.getPublisher());
      descriptionField.setText(book.getDescription());
      pageCountField.setText(String.valueOf(book.getPageCount()));
      categoriesField.setText(book.getCategories().toString().replace("[", "").replace("]", ""));
      authorsField.setText(book.getAuthors().toString().replace("[", "").replace("]", ""));
      thumbnailField.setText(book.getThumbnail());
      languageField.setText(book.getLanguage());
      priceField.setText(String.valueOf(book.getPrice()));
      currencyCodeField.setText(book.getCurrencyCode());
      pdfLinkField.setText(book.getPdfLink());
      publishedDateField.setValue(
      DateUtil.parse(book.getPublishedDate(), DateUtil.DateFormat.YYYY_MM_DD));
//      discountPriceField.setText(String.valueOf(book.getDiscountPrice()));
//      isActiveCheckBox.setSelected(book.getIsActive());

    } else {
      isEditMode = false;
    }
  }

  @FXML
  void onSubmit() {
    if (book == null) {
      book = new Book();
    }
    book.setId(idField.getText());
    book.set_id(new ObjectId(_idField.getText()));
    book.setISBN(iSBNField.getText());
    book.setTitle(titleField.getText());
    book.setPublisher(publisherField.getText());
    book.setDescription(descriptionField.getText());
    book.setPageCount(Integer.parseInt(pageCountField.getText()));
    book.setCategories(new ArrayList<>(List.of(categoriesField.getText().split(","))));
    book.setAuthors(new ArrayList<>(List.of(authorsField.getText().split(","))));
    book.setThumbnail(thumbnailField.getText());
    book.setLanguage(languageField.getText());
    book.setPrice(Integer.parseInt(priceField.getText()));
    book.setCurrencyCode(currencyCodeField.getText());
    book.setPdfLink(pdfLinkField.getText());
    book.setPublishedDate(
        DateUtil.format(publishedDateField.getValue(), DateUtil.DateFormat.YYYY_MM_DD));
//    book.setDiscountPrice(Integer.parseInt(discountPriceField.getText()));
//    book.setIsActive(isActiveCheckBox.isSelected());

    Task<Boolean> task = new Task<Boolean>() {
      @Override
      protected Boolean call() throws Exception {
        return isEditMode ? BookController.editBook(book) : BookController.addBook(book);
      }
    };

    task.setOnRunning(e -> showLoading(true));

    task.setOnSucceeded(e -> {
      showLoading(false);
      boolean resp = task.getValue();
      System.out.println(resp);
      Stage stage = (Stage) idField.getScene().getWindow();
      if (resp) {
        AlertDialog.showAlert("success", "Success",
            isEditMode ? "Book updated successfully." : "Book added successfully.", null);
        stage.close();
        if (saveCallback != null) {
          saveCallback.onSave(book);
        }
      } else {
        AlertDialog.showAlert("error", "Error", "An error occurred while saving the book.", null);
      }
    });

    task.setOnFailed(e -> {
      showLoading(false);
      AlertDialog.showAlert("error", "Error", e.getSource().getException().getMessage(), null);
    });

    new Thread(task).start();
  }

  @FXML
  private void onCancel() {
    Stage stage = (Stage) idField.getScene().getWindow();
    stage.close();
  }
}