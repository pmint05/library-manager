package com.app.librarymanager.controllers;

import com.app.librarymanager.utils.AlertDialog;
import com.app.librarymanager.utils.DateUtil;
import java.util.Date;
import java.util.List;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.bson.types.ObjectId;
import org.json.JSONObject;
import com.app.librarymanager.models.Categories;

public class ManageCategoriesController extends ControllerWithLoader {

  @FXML
  private TableView<Categories> bookLoansTable;

  @FXML
  private TableColumn<Categories, ObjectId> _idColumn;

  @FXML
  private TableColumn<Categories, String> nameColumn;

  @FXML
  private TableColumn<Categories, ObjectId> createdAtColumn;

  @FXML
  private TableColumn<Categories, Date> updatedAtColumn;

  @FXML
  private TableColumn<Categories, Void> actionAtColumn;

  @FXML
  private TextField searchField;

  private int start = 0;
  private int limit = 10;

  private ObservableList<Categories> categoriesList = FXCollections.observableArrayList();

  @FXML
  public void initialize() {
    setLoadingText("Loading categories...");

    _idColumn.setCellValueFactory(new PropertyValueFactory<>("_id"));
    _idColumn.setCellFactory(col -> new TableCell<Categories, ObjectId>() {
      @Override
      protected void updateItem(ObjectId item, boolean empty) {
        super.updateItem(item, empty);
        if (empty || item == null) {
          setText(null);
        } else {
          setText(item.toString());
        }
      }
    });
    nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
    createdAtColumn.setCellValueFactory(new PropertyValueFactory<>("_id"));
    updatedAtColumn.setCellValueFactory(new PropertyValueFactory<>("lastUpdated"));

//    setObjectIdCellFactory(createdAtColumn);
//    setObjectIdCellFactory(_idColumn);
    setDateCellFactory(updatedAtColumn);

    _idColumn.setPrefWidth(150);
    nameColumn.setPrefWidth(150);
    createdAtColumn.setPrefWidth(150);
    updatedAtColumn.setPrefWidth(150);

    loadCategories();
    setRowContextMenu();
  }

  private void loadCategories() {
    Task<ObservableList<Categories>> task = new Task<>() {
      @Override
      protected ObservableList<Categories> call() {
        ObservableList<Categories> categories = FXCollections.observableArrayList();
        List<Categories> cateList = CategoriesController.getCategories(start, limit);
        categories.addAll(cateList);
        return categories;
      }
    };

    task.setOnRunning(e -> Platform.runLater(() -> showLoading(true)));
    task.setOnSucceeded(e -> {
      Platform.runLater(() -> {
        categoriesList.setAll(task.getValue());
        bookLoansTable.setItems(categoriesList);
        showLoading(false);
      });
    });
    task.setOnFailed(e -> {
      Platform.runLater(() -> {
        showLoading(false);
        System.out.println("Error while fetching categories: " + task.getException().getMessage());
      });
    });

    new Thread(task).start();
  }

  @FXML
  private void onCreateCategories() {
    openCategoriesModal(null);
  }

  @FXML
  private void onSearch() {
    String searchText = searchField.getText().toLowerCase();
    ObservableList<Categories> filteredList = FXCollections.observableArrayList();
    for (Categories category : categoriesList) {
      if (category.getName().toLowerCase().contains(searchText)) {
        filteredList.add(category);
      }
    }
    bookLoansTable.setItems(filteredList);
  }

  private void setRowContextMenu() {
    bookLoansTable.setRowFactory(tableView -> {
      final TableRow<Categories> row = new TableRow<>();
      final ContextMenu contextMenu = new ContextMenu();
      final MenuItem editMenuItem = new MenuItem("Edit Categories");
      final MenuItem deleteMenuItem = new MenuItem("Delete Categories");

      editMenuItem.setOnAction(event -> {
        Categories category = row.getItem();
        openCategoriesModal(category);
      });

      deleteMenuItem.setOnAction(event -> {
        Categories category = row.getItem();
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Categories");
        alert.setHeaderText("Are you sure you want to delete category " + category.getName() + "?");
        alert.getButtonTypes().setAll(ButtonType.YES, ButtonType.CANCEL);
        alert.showAndWait().ifPresent(response -> {
          if (response == ButtonType.YES) {
            Task<JSONObject> task = new Task<JSONObject>() {
              @Override
              protected JSONObject call() throws Exception {
//                return CategoriesController.deleteCategories(category);
                return null;
              }

            };
            task.setOnRunning(ev -> showLoading(true));
            task.setOnSucceeded(e -> {
              JSONObject delRes = task.getValue();
              showLoading(false);
              removeCategoriesFromTable(category);
            });
            task.setOnFailed(ev -> {
              showLoading(false);
              System.out.println(
                  "Error while deleting category: " + task.getException().getMessage());
            });
            new Thread(task).start();
          }
        });
      });

      contextMenu.getItems().addAll(editMenuItem, deleteMenuItem);
      row.contextMenuProperty().bind(
          javafx.beans.binding.Bindings.when(row.emptyProperty()).then((ContextMenu) null)
              .otherwise(contextMenu));
      return row;
    });
  }

  private void setDateCellFactory(TableColumn<Categories, Date> column) {
    column.setCellFactory(col -> new TableCell<Categories, Date>() {
      @Override
      protected void updateItem(Date item, boolean empty) {
        super.updateItem(item, empty);
        if (empty || item == null) {
          setText(null);
        } else {
          setText(item.toString());
        }
      }
    });
  }

  private void removeCategoriesFromTable(Categories category) {
    categoriesList.remove(category);
    bookLoansTable.refresh();
  }

  private void openCategoriesModal(Categories category) {
    try {
      FXMLLoader loader = new FXMLLoader(
          getClass().getResource("/views/components/category-info-modal.fxml"));
      Parent parent = loader.load();
      CategoriesModalController controller = loader.getController();
      controller.setCategories(category);
      controller.setSaveCallback(updatedCategories -> {
        if (category == null) {
          categoriesList.add(updatedCategories);
          bookLoansTable.refresh();
        } else {
          updateCategoriesInTable(updatedCategories);
        }
      });

      Dialog<Void> dialog = new Dialog<>();
      dialog.setTitle(category == null ? "Create Categories" : "Edit Categories");
      dialog.initOwner(bookLoansTable.getScene().getWindow());
      dialog.getDialogPane().setContent(parent);

      String okButtonText = category != null ? "Save & Update" : "Create";

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

  public void updateCategoriesInTable(Categories updatedCategories) {
    categoriesList.replaceAll(
        category -> category.get_id().equals(updatedCategories.get_id()) ? updatedCategories
            : category);
    bookLoansTable.refresh();
  }

  public void onCreateCategory(ActionEvent actionEvent) {
  }
}