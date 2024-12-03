package com.app.librarymanager.controllers;

import com.app.librarymanager.controllers.BookLoanController.ReturnBookLoan;
import com.app.librarymanager.utils.AlertDialog;
import java.util.Date;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import com.app.librarymanager.models.BookLoan;
import javafx.scene.text.Text;
import org.json.JSONArray;
import org.json.JSONObject;

public class ManageBookLoansController extends ControllerWithLoader {

  @FXML
  private TableView<BookLoan> bookLoansTable;
  @FXML
  private TableColumn<BookLoan, String> _idColumn;
  @FXML
  private TableColumn<BookLoan, String> userIdColumn;
  @FXML
  private TableColumn<BookLoan, String> userDisplayNameColumn;
  @FXML
  private TableColumn<BookLoan, String> userAvatarColumn;
  @FXML
  private TableColumn<BookLoan, String> userEmailColumn;
  @FXML
  private TableColumn<BookLoan, String> bookIdColumn;
  @FXML
  private TableColumn<BookLoan, String> bookTitleColumn;
  @FXML
  private TableColumn<BookLoan, String> bookThumbnailColumn;
  @FXML
  private TableColumn<BookLoan, String> borrowDateColumn;
  @FXML
  private TableColumn<BookLoan, String> dueDateColumn;
  @FXML
  private TableColumn<BookLoan, Boolean> validColumn;
  @FXML
  private TableColumn<BookLoan, String> createdAtColumn;
  @FXML
  private TableColumn<BookLoan, String> lastUpdatedColumn;
  @FXML
  private TableColumn<BookLoan, Void> actionColumn;

  private int currentPage = 0;
  private int pageSize = 10;
  private int totalRecords = 0;


  @FXML
  private TextField searchField;

  private ObservableList<BookLoan> bookLoansList = FXCollections.observableArrayList();

  @FXML
  public void initialize() {
    setLoadingText("Loading book loans...");

    _idColumn.setCellValueFactory(new PropertyValueFactory<>("_id"));
    userIdColumn.setCellValueFactory(new PropertyValueFactory<>("userId"));
    userDisplayNameColumn.setCellValueFactory(new PropertyValueFactory<>("userDisplayName"));
    userAvatarColumn.setCellValueFactory(new PropertyValueFactory<>("userAvatar"));
    userEmailColumn.setCellValueFactory(new PropertyValueFactory<>("userEmail"));
    bookIdColumn.setCellValueFactory(new PropertyValueFactory<>("bookId"));
    bookTitleColumn.setCellValueFactory(new PropertyValueFactory<>("bookTitle"));
    bookThumbnailColumn.setCellValueFactory(new PropertyValueFactory<>("bookThumbnail"));
    borrowDateColumn.setCellValueFactory(new PropertyValueFactory<>("borrowDate"));
    dueDateColumn.setCellValueFactory(new PropertyValueFactory<>("dueDate"));
    validColumn.setCellValueFactory(new PropertyValueFactory<>("valid"));
    createdAtColumn.setCellValueFactory(new PropertyValueFactory<>("createdAt"));
    lastUpdatedColumn.setCellValueFactory(new PropertyValueFactory<>("lastUpdated"));

    bookThumbnailColumn.setCellFactory(column -> new TableCell<BookLoan, String>() {
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

    setDateCellFactory(borrowDateColumn);
    setDateCellFactory(dueDateColumn);
    setDateCellFactory(createdAtColumn);
    setDateCellFactory(lastUpdatedColumn);

    loadBookLoans();
  }

  private void loadBookLoans() {
    Task<List<ReturnBookLoan>> task = new Task<>() {
      @Override
      protected List<ReturnBookLoan> call() {
        List<ReturnBookLoan> bookLoans = BookLoanController.getAllLentBook(currentPage, pageSize);
        return bookLoans;
      }
    };

    task.setOnRunning(e -> showLoading(true));
    task.setOnSucceeded(e -> {
      bookLoansList.clear();
      bookLoansTable.setItems(bookLoansList);
      System.out.println("Book loans loaded successfully. Total: " + bookLoansList.size());
      showLoading(false);
    });
    task.setOnFailed(e -> {
      System.out.println("Error while fetching book loans: " + task.getException().getMessage());
      showLoading(false);
    });

    new Thread(task).start();
  }

  @FXML
  private void onCreateLoan() {
    openLoanModal(null);
  }

  @FXML
  private void onSearch() {
    String searchText = searchField.getText().toLowerCase();
    ObservableList<BookLoan> filteredList = FXCollections.observableArrayList();
    for (BookLoan bookLoan : bookLoansList) {
//      if (bookLoan.getUserDisplayName().toLowerCase().contains(searchText) ||
//          bookLoan.getUserEmail().toLowerCase().contains(searchText) ||
//          bookLoan.getBookTitle().toLowerCase().contains(searchText)) {
//        filteredList.add(bookLoan);
//      }
    }
    bookLoansTable.setItems(filteredList);
  }

  private void setDateCellFactory(TableColumn<BookLoan, String> column) {
    column.setCellFactory(col -> new TableCell<BookLoan, String>() {
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

  private void openLoanModal(BookLoan bookLoan) {
    // Implement the modal opening logic here
  }
}