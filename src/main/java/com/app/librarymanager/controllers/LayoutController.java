package com.app.librarymanager.controllers;

import com.app.librarymanager.interfaces.AuthStateListener;
import com.app.librarymanager.models.User;
import com.app.librarymanager.utils.StageManager;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.StackPane;
import org.json.JSONObject;

public class LayoutController implements AuthStateListener {

  private static HomeController instance;

  private final List<String> ADMIN_ROUTES = List.of(
      "/views/admin/dashboard.fxml",
      "/views/admin/manage-books.fxml",
      "/views/admin/manage-users.fxml",
      "/views/admin/manage-loans.fxml",
      "/views/admin/manage-categories.fxml"
  );

  private final List<String> USER_ROUTES = List.of(
      "/views/home.fxml",
      "/views/profile.fxml"
  );

  public static synchronized HomeController getInstance() {
    if (instance == null) {
      instance = new HomeController();
    }
    return instance;
  }

  @FXML
  private MenuItem loginMenuItem;
  @FXML
  private MenuItem registerMenuItem;
  @FXML
  private MenuItem logoutMenuItem;
  @FXML
  private MenuItem profileMenuItem;
  @FXML
  private ToolBar adminToolBar;
  @FXML
  private StackPane contentPane;


  private final Map<String, Parent> componentCache = new HashMap<>();

  @FXML
  private void initialize() {
    AuthController.getInstance().addAuthStateListener(this);
    AuthController.getInstance().loadSession();

    if (AuthController.getInstance().validateIdToken()) {
      User currentUser = AuthController.getInstance().getCurrentUser();
//      preloadComponents(currentUser);
      updateUI(true, currentUser);
    } else {
      AuthController.getInstance().logout();
//      preloadComponents(null);
      updateUI(false, null);
    }
    loadComponent("/views/home.fxml");
  }

  private void preloadComponents(User user) {
    try {
      for (String route : USER_ROUTES) {
        componentCache.put(route, loadFXML(route));
      }
      if (user != null) {
        if (user.isAdmin()) {
          for (String route : ADMIN_ROUTES) {
            componentCache.put(route, loadFXML(route));
          }
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private Parent loadFXML(String fxmlPath) throws Exception {
    FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
    return loader.load();
  }

  private void updateUI(boolean isAuthenticated, User user) {
    loginMenuItem.setVisible(!isAuthenticated);
    registerMenuItem.setVisible(!isAuthenticated);
    logoutMenuItem.setVisible(isAuthenticated);
    profileMenuItem.setVisible(isAuthenticated);
    adminToolBar.setVisible(isAuthenticated && user.isAdmin());
    if (isAuthenticated) {
    } else {
    }
  }

  @FXML
  private void onLoginButtonClick() {
    StageManager.showLoginWindow();
  }

  @FXML
  public void handleSearch() {
    // Implement search functionality for unauthenticated user
  }

  @FXML
  public void handleAuthSearch() {
    // Implement search functionality for authenticated user
  }

  @FXML
  public void handleViewBookDetails() {
    // Show book details for selected book
  }

  @FXML
  public void handleProfileSettings() {
    loadComponent("/views/profile.fxml");
  }

  @FXML
  public void handleShowDashboard(Event e) {
    handleChangeActiveButton(e);
    loadComponent("/views/admin/dashboard.fxml");

  }

  @FXML
  public void handleManageBooks(Event e) {
    handleChangeActiveButton(e);
    loadComponent("/views/admin/manage-books.fxml");
  }

  @FXML
  public void handleManageUsers(Event e) {
    handleChangeActiveButton(e);
    loadComponent("/views/admin/manage-users.fxml");
  }

  @FXML
  void handleManageBookLoans(Event e) {
    handleChangeActiveButton(e);
    loadComponent("/views/admin/manage-loans.fxml");
  }

  @FXML
  public void handleManageCategories(Event e) {
    handleChangeActiveButton(e);
    loadComponent("/views/admin/manage-categories.fxml");
  }

  @FXML
  private void onRegisterButtonClick() {
    StageManager.showRegisterWindow();
  }


  @FXML
  private void onLogoutButtonClick() {
    AuthController.getInstance().logout();
    loadComponent("/views/home.fxml");
  }

  @FXML
  public void closeLoginWindow() {

  }

  @Override
  public void onAuthStateChanged(boolean isAuthenticated) {
    updateUI(isAuthenticated, AuthController.getInstance().getCurrentUser());
  }

  private void handleChangeActiveButton(Event e) {
    adminToolBar.getItems().stream()
        .filter(node -> node instanceof Button)
        .map(node -> (Button) node)
        .filter(button -> button.getStyleClass().contains("active"))
        .forEach(button -> button.getStyleClass().remove("active"));

    Button clickedButton = (Button) e.getSource();
    clickedButton.getStyleClass().add("active");
  }

  private void loadComponent(String fxmlPath) {
    try {
      Parent component = componentCache.get(fxmlPath);
      if (component == null) {
        component = loadFXML(fxmlPath);
      }
      contentPane.getChildren().clear();
      contentPane.getChildren().add(component);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}