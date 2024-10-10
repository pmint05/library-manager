package com.app.librarymanager.controllers;

import com.app.librarymanager.interfaces.AuthStateListener;
import com.app.librarymanager.utils.StageManager;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import org.json.JSONObject;

public class HomeController implements AuthStateListener {

  private static HomeController instance;

  public static synchronized HomeController getInstance() {
    if (instance == null) {
      instance = new HomeController();
    }
    return instance;
  }

  @FXML
  private Button loginButton;
  @FXML
  private Button registerButton;
  // UI elements
  @FXML
  private MenuItem loginMenuItem;
  @FXML
  private MenuItem registerMenuItem;
  @FXML
  private MenuItem logoutMenuItem;
  @FXML
  private MenuItem profileMenuItem;
  @FXML
  private TabPane mainTabPane;
  @FXML
  private Tab unauthUserTab;
  @FXML
  private Tab authUserTab;
  @FXML
  private Tab adminTab;
  @FXML
  private Label welcomeLabel;

  @FXML
  private void initialize() {
    AuthController.getInstance().addAuthStateListener(this);
    boolean isAuthenticated = AuthController.getInstance().isAuthenticated();
    JSONObject claims = AuthController.getInstance().getUserClaims();
    updateUI(isAuthenticated, claims);
  }

  private void updateUI(boolean isAuthenticated, JSONObject userClaims) {
    mainTabPane.getTabs().clear();
    if (isAuthenticated) {
      if (!userClaims.isEmpty()) {
        String email = userClaims.getString("email");
        welcomeLabel.setText("Welcome, " + email);
        if (userClaims.has("role")) {
          String role = userClaims.getString("role");
          if (role.equals("admin")) {
            mainTabPane.getTabs().add(adminTab);
          } else {
            authUserTab.setDisable(false);
            mainTabPane.getTabs().add(authUserTab);
          }
        } else {
          authUserTab.setDisable(false);
          mainTabPane.getTabs().add(authUserTab);
        }
      }  else {
        AuthController.getInstance().logout();
      }
    } else {
      mainTabPane.getTabs().add(unauthUserTab);
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
    // Open user profile settings
  }

  @FXML
  public void handleManageBooks() {
    // Open admin manage books view
  }

  @FXML
  public void handleManageUsers() {
    // Open admin manage users view
  }


  @FXML
  private void onRegisterButtonClick() {
    StageManager.showRegisterWindow();
  }

  @FXML
  private void onLogoutButtonClick() {
    AuthController.getInstance().logout();
  }

  @FXML
  public void closeLoginWindow() {

  }

  @Override
  public void onAuthStateChanged(boolean isAuthenticated, JSONObject userClaims) {
    updateUI(isAuthenticated, userClaims);
  }
}

