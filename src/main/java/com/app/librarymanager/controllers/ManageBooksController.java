package com.app.librarymanager.controllers;

import com.app.librarymanager.utils.AlertDialog;
import java.util.Date;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import com.app.librarymanager.models.Book;
import javafx.scene.text.Text;
import org.json.JSONArray;
import org.json.JSONObject;

public class ManageBooksController extends ControllerWithLoader {

  private static ManageBooksController instance;

  @FXML
  private TableView<Book> booksTable;
  @FXML
  private TableColumn<Book, String> _idColumn;
  @FXML
  private TableColumn<Book, String> idColumn;
  @FXML
  private TableColumn<Book, String> iSBNColumn;
  @FXML
  private TableColumn<Book, String> titleColumn;
  @FXML
  private TableColumn<Book, String> descriptionColumn;
  @FXML
  private TableColumn<Book, String> publisherColumn;
  @FXML
  private TableColumn<Book, String> authorsColumn;
  @FXML
  private TableColumn<Book, String> categoriesColumn;
  @FXML
  private TableColumn<Book, Double> priceColumn;
  @FXML
  private TableColumn<Book, Double> discountPriceColumn;
  @FXML
  private TableColumn<Book, String> currencyCodeColumn;
  @FXML
  private TableColumn<Book, Integer> pageCountColumn;
  @FXML
  private TableColumn<Book, String> languageColumn;
  @FXML
  private TableColumn<Book, String> publishedDateColumn;
  @FXML
  private TableColumn<Book, String> thumbnailColumn;
  @FXML
  private TableColumn<Book, Boolean> isActiveColumn;

  @FXML
  private TextField searchField;

  @FXML
  private ComboBox<String> activeFilter;

  private ObservableList<Book> booksList = FXCollections.observableArrayList();

  public synchronized static ManageBooksController getInstance() {
    if (instance == null) {
      instance = new ManageBooksController();
    }
    return instance;
  }

  @FXML
  public void initialize() {
    setLoadingText("Loading books...");

    _idColumn.setCellValueFactory(new PropertyValueFactory<>("_id"));
    idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
    iSBNColumn.setCellValueFactory(new PropertyValueFactory<>("iSBN"));
    thumbnailColumn.setCellValueFactory(new PropertyValueFactory<>("displayName"));
    descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
    publisherColumn.setCellValueFactory(new PropertyValueFactory<>("birthday"));
    authorsColumn.setCellValueFactory(new PropertyValueFactory<>("admin"));
    categoriesColumn.setCellValueFactory(new PropertyValueFactory<>("emailVerified"));
    priceColumn.setCellValueFactory(new PropertyValueFactory<>("disabled"));
    discountPriceColumn.setCellValueFactory(new PropertyValueFactory<>("photoUrl"));
    currencyCodeColumn.setCellValueFactory(new PropertyValueFactory<>("createdAt"));
    pageCountColumn.setCellValueFactory(new PropertyValueFactory<>("lastModifiedDate"));
    languageColumn.setCellValueFactory(new PropertyValueFactory<>("lastLoginAt"));
    isActiveColumn.setCellValueFactory(new PropertyValueFactory<>("lastLoginAt"));
    publishedDateColumn.setCellValueFactory(new PropertyValueFactory<>("lastLoginAt"));

    thumbnailColumn.setCellFactory(column -> new TableCell<Book, String>() {
      private final ImageView imageView = new ImageView();

      @Override
      protected void updateItem(String item, boolean empty) {
        super.updateItem(item, empty);
        if (empty || item == null || item.isEmpty()) {
          setGraphic(null);
        } else {
          try {
            Image image = new Image(item, true);
            image.errorProperty().addListener((observable, oldValue, newValue) -> {
              if (newValue) {
                setGraphic(null);
              }
            });
            imageView.setImage(image);
            imageView.setFitHeight(40);
            imageView.setFitWidth(40);
            setGraphic(imageView);
          } catch (Exception e) {
            setGraphic(null);
          }
        }
      }
    });

    _idColumn.setPrefWidth(150);
    idColumn.setPrefWidth(150);
    iSBNColumn.setPrefWidth(200);
    thumbnailColumn.setPrefWidth(200);
    titleColumn.setPrefWidth(150);
    descriptionColumn.setPrefWidth(150);
    publisherColumn.setPrefWidth(100);
    authorsColumn.setPrefWidth(150);
    categoriesColumn.setPrefWidth(100);
    priceColumn.setPrefWidth(50);
    discountPriceColumn.setPrefWidth(200);
    currencyCodeColumn.setPrefWidth(200);
    pageCountColumn.setPrefWidth(200);
    languageColumn.setPrefWidth(200);
    isActiveColumn.setPrefWidth(200);
    publishedDateColumn.setPrefWidth(200);

    setDateCellFactory(publishedDateColumn);

    activeFilter.getItems().addAll("All", "True", "False");

    activeFilter.setValue("All");
    activeFilter.setPrefWidth(150);

    activeFilter.setOnAction(e -> onFilter());

    loadBooks();
    setRowContextMenu();
  }

  private void loadBooks() {
    Task<ObservableList<Book>> task = new Task<>() {
      @Override
      protected ObservableList<Book> call() {
        ObservableList<Book> books = FXCollections.observableArrayList();
//        JSONObject response = BookController.listBooks();
        JSONObject response = new JSONObject();
        if (response.getBoolean("success")) {
//          JSONArray booksArray = response.getJSONArray("data");
//          for (int i = 0; i < booksArray.length(); i++) {
//            JSONObject bookJson = booksArray.getJSONObject(i);
//            Book book = new Book(bookJson.getString("uid"), bookJson.getString("email"),
//                bookJson.optString("password", ""), bookJson.optString("displayName", ""),
//                bookJson.optJSONObject("customClaims").optString("birthday", ""),
//                bookJson.optString("phoneNumber", ""), bookJson.optString("photoUrl", ""),
//                String.valueOf(
//                    bookJson.getJSONObject("bookMetadata").optLong("creationTimestamp", 0L)),
//                String.valueOf(
//                    bookJson.getJSONObject("bookMetadata").optLong("lastModifiedAt", 0L)),
//                String.valueOf(
//                    bookJson.getJSONObject("bookMetadata").optLong("lastSignInTimestamp", 0L)),
//                bookJson.optString("providerId", ""),
//                bookJson.optJSONObject("customClaims").optBoolean("admin", false),
//                bookJson.getBoolean("emailVerified"), bookJson.getBoolean("disabled"));
//            books.add(book);
//          }
        } else {
          AlertDialog.showAlert("error", "Error", response.getString("message"), null);
        }
        return books;
      }
    };

    task.setOnRunning(e -> showLoading(true));
    task.setOnSucceeded(e -> {
      booksList.setAll(task.getValue());
      booksTable.setItems(booksList);
      System.out.println("Books loaded successfully. Total: " + booksList.size());
      showLoading(false);
    });
    task.setOnFailed(e -> {
      System.out.println("Error while fetching books: " + task.getException().getMessage());
      showLoading(false);
    });

    new Thread(task).start();
  }

  @FXML
  private void onCreateBook() {
    openBookModal(null);
  }

  @FXML
  private void onSearch() {
    String searchText = searchField.getText().toLowerCase();
    ObservableList<Book> filteredList = FXCollections.observableArrayList();
    for (Book book : booksList) {
      if (book.getTitle().toLowerCase().contains(searchText) || book.getAuthors().toString()
          .toLowerCase()
          .contains(searchText) || book.getISBN().toLowerCase().contains(searchText)
          || book.getCategories().toString().toLowerCase().contains(searchText)
          || book.getDescription().toLowerCase().contains(searchText)) {
        filteredList.add(book);
      }
    }
    booksTable.setItems(filteredList);
  }

  @FXML
  private void onFilter() {
    String searchText = searchField.getText().toLowerCase();
    String adminFilterValue = activeFilter.getValue();
//    String emailVerifiedFilterValue = emailVerifiedFilter.getValue();
//    String disabledFilterValue = disabledFilter.getValue();

    ObservableList<Book> filteredList = FXCollections.observableArrayList();
    for (Book book : booksList) {
      boolean matchesSearch =
          book.getTitle().toLowerCase().contains(searchText) || book.getAuthors().toString()
              .toLowerCase()
              .contains(searchText) || book.getISBN().toLowerCase().contains(searchText)
              || book.getCategories().toString().toLowerCase().contains(searchText)
              || book.getDescription().toLowerCase().contains(searchText);

//      boolean matchesActive =
//          adminFilterValue.equals("All") || (adminFilterValue.equals("True") && book.isActive()) || (
      boolean matchesActive = true;
      if (matchesSearch && matchesActive) {
        filteredList.add(book);
      }
    }
    booksTable.setItems(filteredList);
  }

  private void setColumnWidthsToFitContent() {
    booksTable.getColumns().forEach(column -> {
      Text text = new Text(column.getText());
      double maxWidth = text.getLayoutBounds().getWidth();
      for (int i = 0; i < booksTable.getItems().size(); i++) {
        if (column.getCellData(i) != null) {
          text = new Text(column.getCellData(i).toString());
          double width = text.getLayoutBounds().getWidth();
          if (width > maxWidth) {
            maxWidth = width;
          }
        }
      }
      column.setPrefWidth(maxWidth + 20);
    });
  }

  private void setRowContextMenu() {
    booksTable.setRowFactory(tableView -> {
      final TableRow<Book> row = new TableRow<>();
      final ContextMenu contextMenu = new ContextMenu();
      final MenuItem viewMenuItem = new MenuItem("View Book Details");
      final MenuItem editMenuItem = new MenuItem("Edit Book");
      final MenuItem deleteMenuItem = new MenuItem("Delete Book");

      viewMenuItem.setOnAction(event -> {
        Book book = row.getItem();
        System.out.println("View book: " + book.get_id());
        openBookModal(book);
      });

      editMenuItem.setOnAction(event -> {
        Book book = row.getItem();
        System.out.println("Edit book: " + book.get_id());
        openBookModal(book);
      });

      deleteMenuItem.setOnAction(event -> {
        Book book = row.getItem();
        System.out.println("Delete book: " + book.get_id());
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Book");
        alert.setHeaderText("Are you sure you want to delete book " + book.getISBN() + "?");
        alert.getButtonTypes().setAll(ButtonType.YES, ButtonType.CANCEL);
        alert.showAndWait().ifPresent(response -> {
          if (response == ButtonType.YES) {
            Task<Boolean> task = new Task<Boolean>() {
              @Override
              protected Boolean call() throws Exception {
                return BookController.deleteBook(book);
              }
            };
            task.setOnRunning(ev -> showLoading(true));
            task.setOnSucceeded(e -> {
              boolean delRes = task.getValue();
              System.out.println("Book deleted: " + delRes);
              showLoading(false);
              removeBookFromTable(book);
            });
            task.setOnFailed(ev -> {
              showLoading(false);
              System.out.println("Error while deleting book: " + task.getException().getMessage());
            });
            new Thread(task).start();
          }
        });
      });

      contextMenu.getItems().addAll(viewMenuItem, editMenuItem, deleteMenuItem);
      row.contextMenuProperty().bind(
          javafx.beans.binding.Bindings.when(row.emptyProperty()).then((ContextMenu) null)
              .otherwise(contextMenu));
      return row;
    });
  }

  private void setDateCellFactory(TableColumn<Book, String> column) {
    column.setCellFactory(col -> new TableCell<Book, String>() {
      @Override
      protected void updateItem(String item, boolean empty) {
        super.updateItem(item, empty);
        if (empty || item == null || item.isEmpty()) {
          setText(null);
        } else {
          setText(new Date(Long.parseLong(item)).toLocaleString());
        }
      }
    });
  }

  public void updateBookInTable(Book updatedBook) {
    booksList.replaceAll(book -> book.get_id().equals(updatedBook.get_id()) ? updatedBook : book);
    booksTable.refresh();
  }

  private void removeBookFromTable(Book book) {
    booksList.remove(book);
    booksTable.refresh();
  }

  private void openBookModal(Book book) {
    try {
      FXMLLoader loader = new FXMLLoader(
          getClass().getResource("/views/components/book-info-modal.fxml.fxml"));
      Parent parent = loader.load();
      BookModalController controller = loader.getController();
      controller.setBook(book);
      controller.setSaveCallback(updatedBook -> {
        if (book == null) {
          booksList.add(updatedBook);
          booksTable.refresh();
        } else {
          updateBookInTable(updatedBook);
        }
      });

      Dialog<Void> dialog = new Dialog<>();
      dialog.setTitle(book == null ? "Create Book" : "Edit Book");
      dialog.initOwner(booksTable.getScene().getWindow());
      dialog.getDialogPane().setContent(parent);

      String okButtonText = book != null ? "Save & Update" : "Create";

      ButtonType okButtonType = new ButtonType(okButtonText, ButtonBar.ButtonData.OK_DONE);
      ButtonType cancelButtonType = ButtonType.CANCEL;
      dialog.getDialogPane().getButtonTypes().addAll(okButtonType, cancelButtonType);

      Button saveButton = (Button) dialog.getDialogPane().lookupButton(okButtonType);
      saveButton.getStyleClass().addAll("btn", "btn-primary");

      saveButton.addEventFilter(ActionEvent.ACTION, event -> {
        controller.onSubmit();
        event.consume();
      });
      Button cancelButton = (Button) dialog.getDialogPane().lookupButton(cancelButtonType);
      cancelButton.getStyleClass().add("btn");
      cancelButton.addEventFilter(ActionEvent.ACTION, event -> dialog.close());

      dialog.setResultConverter(dialogButton -> null);

      dialog.showAndWait();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}