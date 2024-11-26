package com.app.librarymanager.controllers;

import com.app.librarymanager.utils.AlertDialog;
import com.app.librarymanager.utils.DateUtil;
import com.app.librarymanager.utils.DateUtil.DateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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
import javafx.scene.layout.HBox;
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
  private TableColumn<Book, ArrayList<String>> authorsColumn;
  @FXML
  private TableColumn<Book, ArrayList<String>> categoriesColumn;
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


  private int start = 0;
  private int limit = 10;


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
    thumbnailColumn.setCellValueFactory(new PropertyValueFactory<>("thumbnail"));
    titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
    descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
    publisherColumn.setCellValueFactory(new PropertyValueFactory<>("publisher"));
    authorsColumn.setCellValueFactory(new PropertyValueFactory<>("authors"));
    categoriesColumn.setCellValueFactory(new PropertyValueFactory<>("categories"));
    priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
    discountPriceColumn.setCellValueFactory(new PropertyValueFactory<>("discountPrice"));
    currencyCodeColumn.setCellValueFactory(new PropertyValueFactory<>("currencyCode"));
    pageCountColumn.setCellValueFactory(new PropertyValueFactory<>("pageCount"));
    languageColumn.setCellValueFactory(new PropertyValueFactory<>("language"));
    isActiveColumn.setCellValueFactory(new PropertyValueFactory<>("activated"));
    publishedDateColumn.setCellValueFactory(new PropertyValueFactory<>("publishedDate"));

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
            imageView.setFitHeight(50);
            imageView.setPreserveRatio(true);
            setGraphic(imageView);
          } catch (Exception e) {
            setGraphic(null);
          }
        }
      }
    });

    _idColumn.setPrefWidth(150);
    idColumn.setPrefWidth(120);
    iSBNColumn.setPrefWidth(120);
    thumbnailColumn.setPrefWidth(50);
    titleColumn.setPrefWidth(150);
    descriptionColumn.setPrefWidth(150);
    publisherColumn.setPrefWidth(150);
    authorsColumn.setPrefWidth(150);
    categoriesColumn.setPrefWidth(100);
    priceColumn.setPrefWidth(100);
    discountPriceColumn.setPrefWidth(80);
    currencyCodeColumn.setPrefWidth(60);
    pageCountColumn.setPrefWidth(50);
    languageColumn.setPrefWidth(80);
    isActiveColumn.setPrefWidth(80);
    publishedDateColumn.setPrefWidth(150);

    setDateCellFactory(publishedDateColumn);
    setArrayCellFactory(authorsColumn);
    setArrayCellFactory(categoriesColumn);

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
        List<Book> bookList = BookController.findBookByKeyword("", start, limit);
        books.addAll(bookList);
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
    String activeFilterValue = activeFilter.getValue();

    ObservableList<Book> filteredList = FXCollections.observableArrayList();
    for (Book book : booksList) {
      boolean matchesSearch =
          book.getTitle().toLowerCase().contains(searchText) || book.getAuthors().toString()
              .toLowerCase()
              .contains(searchText) || book.getISBN().toLowerCase().contains(searchText)
              || book.getCategories().toString().toLowerCase().contains(searchText)
              || book.getDescription().toLowerCase().contains(searchText);

      if (matchesSearch && (activeFilterValue.equals("All") || activeFilterValue
          .equals(String.valueOf(book.isActivated())))) {
        filteredList.add(book);
      }
    }
    booksTable.setItems(filteredList);
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
          setText(DateUtil.ymdToDmy(item));
        }
      }
    });
  }

  private void setArrayCellFactory(TableColumn<Book, ArrayList<String>> column) {
    column.setCellFactory(col -> new TableCell<Book, ArrayList<String>>() {
      @Override
      protected void updateItem(ArrayList<String> items, boolean empty) {
        super.updateItem(items, empty);
        if (empty || items == null || items.isEmpty()) {
          setText(null);
          setGraphic(null);
        } else {
          HBox hBox = new HBox(5);
          for (String item : items) {
            Label chip = new Label(item);
            chip.getStyleClass().add("chip");
            hBox.getChildren().add(chip);
          }
          setGraphic(hBox);
          setText(null);
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
          getClass().getResource("/views/components/book-info-modal.fxml"));
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