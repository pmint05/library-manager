package com.app.librarymanager.controllers;

import com.app.librarymanager.interfaces.AuthStateListener;
import com.app.librarymanager.models.User;
import com.app.librarymanager.utils.StageManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.StackPane;
import org.json.JSONObject;

public class LayoutController implements AuthStateListener {

  private static HomeController instance;

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
  private Label welcomeLabel;
  @FXML
  private StackPane contentPane;


  @FXML
  private void initialize() {
    AuthController.getInstance().addAuthStateListener(this);
    AuthController.getInstance().loadSession();
    loadComponent("/views/home.fxml");
    if (AuthController.getInstance().validateIdToken()) {
      User currentUser = AuthController.getInstance().getCurrentUser();
      updateUI(true, currentUser);
    } else {
      AuthController.getInstance().logout();
      updateUI(false, null);
    }
  }

  private void updateUI(boolean isAuthenticated, User user) {
//    mainTabPane.getTabs().clear();
//    System.out.println("User claims: " + userClaims);
//    if (isAuthenticated) {
//      if (!userClaims.isEmpty()) {
//        String email = userClaims.getString("email");
//        welcomeLabel.setText("Welcome, " + email);
//        mainTabPane.getTabs().add(authUserTab);
//        authUserTab.setDisable(false);
//        if (userClaims.getBoolean("admin")) {
//          mainTabPane.getTabs().add(adminTab);
//        }
//      } else {
//        AuthController.getInstance().logout();
//      }
//    } else {
//      mainTabPane.getTabs().add(unauthUserTab);
//    }
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
  public void handleManageBooks() {
    loadComponent("/views/admin/manage-books.fxml");
  }

  @FXML
  public void handleManageUsers() {
    loadComponent("/views/admin/manage-users.fxml");
  }

  @FXML
  void handleManageBookLoans() {
    loadComponent("/views/admin/manage-loans.fxml");
  }

  @FXML
  public void handleManageCategories() {
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

  private void loadComponent(String fxmlPath) {
    try {
      FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
      Parent component = loader.load();
//      component.getStylesheets().add(
//          Objects.requireNonNull(getClass().getResource("/styles/global.css")).toExternalForm());
      contentPane.getChildren().clear();
      contentPane.getChildren().add(component);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}

