package com.app.librarymanager.controllers;


import com.app.librarymanager.controllers.BookLoanController.ReturnBookLoan;
import com.app.librarymanager.models.BookLoan;
import java.util.List;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class AdminDashboardController extends ControllerWithLoader {

  private int totalBooks;
  private int totalCategories;
  private int totalUsers;
  private int activeBooks;
  private List<ReturnBookLoan> topLentBooks;
  private ObservableList<HBox> topLentBooksObservableList = FXCollections.observableArrayList();

  @FXML
  private Text totalBooksCount;
  @FXML
  private Text totalCategoriesCount;
  @FXML
  private Text totalUsersCount;
  @FXML
  private Text activeBooksCount;
  @FXML
  private ListView<ReturnBookLoan> topLentBooksList;
  @FXML
  private ScrollPane adminScrollPane;
  @FXML
  private FlowPane adminFlowPane;

  @FXML
  private void initialize() {
    adminScrollPane.viewportBoundsProperty()
        .addListener((observable, oldValue, newValue) -> {
          adminFlowPane.setPrefWidth(newValue.getWidth());
        });
    setLoadingText("Loading dashboard...");
    loadStats();
  }

  private void loadStats() {
    Task<Void> task = new Task<Void>() {
      @Override
      protected Void call() {
        totalBooks = (int) BookController.numberOfBooks();
        totalCategories = (int) CategoriesController.countCategories();
        totalUsers = UserController.countTotalUser();
        activeBooks = (int) BookController.numberOfActiveBooks();
        topLentBooks = BookLoanController.getTopLentBook(0, 10);
        return null;
      }
    };
    task.setOnRunning(event -> showLoading(true));
    task.setOnSucceeded(event -> {
      showLoading(false);
      totalBooksCount.setText(String.format("%d", totalBooks));
      totalCategoriesCount.setText(String.format("%d", totalCategories));
      totalUsersCount.setText(String.format("%d", totalUsers));
      activeBooksCount.setText(String.format("%d", activeBooks));
      renderTopLentBooks();
    });
    task.setOnFailed(event -> {
      showLoading(false);
      totalBooksCount.setText("-");
      totalCategoriesCount.setText("-");
      totalUsersCount.setText("-");
      activeBooksCount.setText("-");
    });
    new Thread(task).start();
  }

  private void renderTopLentBooks() {
    topLentBooksList.setItems(FXCollections.observableArrayList(topLentBooks));

    topLentBooksList.setCellFactory(listView -> new ListCell<>() {
      @Override
      protected void updateItem(ReturnBookLoan bookLoan, boolean empty) {
        super.updateItem(bookLoan, empty);
        if (empty || bookLoan == null) {
          setGraphic(null);
        } else {
          Task<HBox> renderTask = new Task<>() {
            @Override
            protected HBox call() {
              return createTopLentBookComponent(bookLoan);
            }
          };

          renderTask.setOnSucceeded(event -> setGraphic(renderTask.getValue()));
          renderTask.setOnFailed(event -> {
            System.out.println("Failed to render item for: " + bookLoan.getTitleBook());
            setGraphic(null);
          });

          new Thread(renderTask).start();
        }
      }
    });
  }


  private HBox createTopLentBookComponent(ReturnBookLoan returnBookLoan) {
    HBox bookComponent = new HBox(10);
    ImageView bookImage = new ImageView(returnBookLoan.getThumbnailBook());
    bookImage.setFitWidth(100);
    bookImage.setPreserveRatio(true);
    Text bookTitle = new Text(returnBookLoan.getTitleBook());
    bookTitle.setWrappingWidth(600);
    bookTitle.getStyleClass().addAll("bold", "link");
    bookTitle.setOnMouseClicked(e -> handleShowBook(returnBookLoan.getBookLoan().getBookId()));
    BookLoan bookLoan = returnBookLoan.getBookLoan();
    Label type = new Label(String.valueOf(bookLoan.getType()));
    type.getStyleClass().addAll("chip", "info");
    Text count = new Text("Total Copies Count: " + bookLoan.getNumCopies());
    VBox bookInfo = new VBox(bookTitle, count, type);
    bookComponent.getChildren().addAll(bookImage, bookInfo);
    return bookComponent;
  }

  private void handleShowBook(String bookId) {
    try {
//      BookController.showBook(bookId);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

}