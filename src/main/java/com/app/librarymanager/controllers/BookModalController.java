package com.app.librarymanager.controllers;

import com.app.librarymanager.services.UserService;
import com.app.librarymanager.utils.AlertDialog;
import com.app.librarymanager.utils.DatePickerUtil;
import com.app.librarymanager.utils.DateUtil;
import com.app.librarymanager.utils.DateUtil.DateFormat;
import com.app.librarymanager.utils.UploadFileUtil;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import com.app.librarymanager.models.Book;
import javafx.util.Callback;
import javax.imageio.ImageIO;
import lombok.Setter;
import org.bson.Document;
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
  private TextArea descriptionField;
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
  @FXML
  private ImageView thumbnailPreview;
  @FXML
  private Button generateIdButton;

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
    initNumberField(pageCountField);
    initNumberField(priceField);
    initNumberField(discountPriceField);

  }

  public void setBook(Book book) {
    this.book = book;
    if (book != null) {
      isEditMode = true;
      _idField.setText(book.get_id().toString());
      _idField.setDisable(true);
      idField.setText(book.getId());
      idField.setDisable(true);
      iSBNField.setText(book.getISBN());
      iSBNField.setDisable(true);
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
      publishedDateField.getEditor().setText(DateUtil.ymdToDmy(book.getPublishedDate()));
      discountPriceField.setText(String.valueOf(book.getDiscountPrice()));
      isActiveCheckBox.setSelected(book.isActivated());
      thumbnailPreview.setImage(new Image(book.getThumbnail()));
      generateIdButton.setDisable(true);
    } else {
      isEditMode = false;
    }
  }

  @FXML
  void onSubmit() {
    if (book == null) {
      book = new Book();
    } else {
      System.out.println(book.toString());
      System.out.println(_idField.getText());
      book.set_id(new ObjectId(_idField.getText()));
    }
    book.setId(idField.getText());
    book.setISBN(iSBNField.getText());
    book.setTitle(titleField.getText());
    book.setPublisher(publisherField.getText());
    book.setDescription(descriptionField.getText());
    book.setPageCount(Integer.parseInt(pageCountField.getText()));
    book.setCategories(new ArrayList<>(List.of(categoriesField.getText().split(","))));
    book.setAuthors(new ArrayList<>(List.of(authorsField.getText().split(","))));
    book.setThumbnail(thumbnailField.getText());
    book.setLanguage(languageField.getText());
    book.setPrice(Double.parseDouble(priceField.getText()));
    book.setCurrencyCode(currencyCodeField.getText());
    book.setPdfLink(pdfLinkField.getText());
    book.setPublishedDate(DateUtil.format(publishedDateField.getValue(), DateFormat.YYYY_MM_DD));
    book.setDiscountPrice(Double.parseDouble(discountPriceField.getText()));
    book.setActivated(isActiveCheckBox.isSelected());

    Task<Document> task = new Task<Document>() {
      @Override
      protected Document call() throws Exception {
        return isEditMode ? BookController.editBook(book) : BookController.addBook(book);
      }
    };

    task.setOnRunning(e -> showLoading(true));

    task.setOnSucceeded(e -> {
      showLoading(false);
      Document resp = task.getValue();
      System.out.println(resp);
      Stage stage = (Stage) idField.getScene().getWindow();
      if (resp.getObjectId("_id") != null) {
        AlertDialog.showAlert("success", "Success",
            isEditMode ? "Book updated successfully." : "Book added successfully.", null);
        stage.close();
        book.set_id(resp.getObjectId("_id"));
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

  private void initNumberField(TextField field) {
    field.textProperty().addListener(new ChangeListener<String>() {
      @Override
      public void changed(ObservableValue<? extends String> observable, String oldValue,
          String newValue) {
        if (!newValue.matches("\\d*(\\.\\d*)?")) {
          field.setText(oldValue);
        }
      }
    });
  }

  @FXML
  private void syncBookByISBN() {
    if (iSBNField.getText().isEmpty()) {
      AlertDialog.showAlert("error", "Error", "ISBN field is empty.", null);
      return;
    }
  }

  @FXML
  private void handleUploadThumbnail() {
    handleUploadFile(thumbnailField, thumbnailPreview, "Image Files", "*.jpg", "*.png", "*.jpeg");
  }

  @FXML
  private void handleUploadPdf() {
    handleUploadFile(pdfLinkField, null, "PDF File", "*.pdf");
  }

  private void handleUploadFile(TextField field, ImageView imgPreview, String title,
      String... type) {
    FileChooser fileChooser = new FileChooser();
    fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(title, type));
    File file = fileChooser.showOpenDialog(idField.getScene().getWindow());
    if (file != null) {
      field.setText(file.getAbsolutePath());
      Task<JSONObject> uploadTask = new Task<JSONObject>() {
        @Override
        protected JSONObject call() {
          return UploadFileUtil.uploadFile(file.getAbsolutePath(), file.getName());
        }
      };

      uploadTask.setOnRunning(e -> field.setText("Uploading..."));
      uploadTask.setOnSucceeded(e -> {
        JSONObject resp = uploadTask.getValue();
        if (resp.getBoolean("success")) {
//          AlertDialog.showAlert("success", "Success",
//              "File " + file.getName() + " uploaded successfully.", null);
          String fileLink = resp.getString("longURL");
          field.setText(fileLink);
          if (imgPreview != null) {
            try {
              BufferedImage image = ImageIO.read(new File(file.getAbsolutePath()));
              ByteArrayOutputStream baos = new ByteArrayOutputStream();
              ImageIO.write(image, file.getName().split("\\.")[1], baos);
              byte[] imageBytes = baos.toByteArray();
              imgPreview.setImage(new Image(new ByteArrayInputStream(imageBytes)));
            } catch (IOException ex) {
              ex.printStackTrace();
            }
          }
        } else {
          AlertDialog.showAlert("error", "Error", resp.getString("message"), null);
        }
      });
      uploadTask.setOnFailed(e -> {
        field.setText("");
        AlertDialog.showAlert("error", "Error", e.getSource().getException().getMessage(), null);
      });
      new Thread(uploadTask).start();
    } else {
//      AlertDialog.showAlert("error", "Error", "No file selected.", null);
    }
  }

  @FXML
  private void generateId() {
    String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
    StringBuilder salt = new StringBuilder();
    Random rnd = new Random();
    while (salt.length() < 12) {
      int index = (int) (rnd.nextFloat() * SALTCHARS.length());
      salt.append(SALTCHARS.charAt(index));
    }
    idField.setText(salt.toString());
  }

  @FXML
  private void onCancel() {
    Stage stage = (Stage) idField.getScene().getWindow();
    stage.close();
  }
}