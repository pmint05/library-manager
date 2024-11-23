package com.app.librarymanager.controllers;

import com.app.librarymanager.interfaces.AuthStateListener;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
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
  private Label welcomeLabel;
  @FXML
  private StackPane contentPane;


  @FXML
  private void initialize() {
    AuthController.getInstance().addAuthStateListener(this);
    updateUI(AuthController.getInstance().isAuthenticated(),
        AuthController.getInstance().getUserClaims());
  }

  private void updateUI(boolean isAuthenticated, JSONObject userClaims) {
    System.out.println("User claims: " + userClaims);
    if (isAuthenticated) {
      if (!userClaims.isEmpty()) {
        String email = userClaims.getString("email");
        welcomeLabel.setText("Welcome, " + email);
//        mainTabPane.getTabs().add(authUserTab);
//        authUserTab.setDisable(false);
        if (userClaims.getBoolean("admin")) {
//          mainTabPane.getTabs().add(adminTab);
        }
      } else {
        AuthController.getInstance().logout();
      }
    } else {
//      mainTabPane.getTabs().add(unauthUserTab);
    }
  }

  @Override
  public void onAuthStateChanged(boolean isAuthenticated) {
    updateUI(isAuthenticated, AuthController.getInstance().getUserClaims());
  }

  private void loadComponent(String fxmlPath) {
    try {
      FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
      Parent component = loader.load();
      contentPane.getChildren().clear();
      contentPane.getChildren().add(component);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void handleImageClick(MouseEvent mouseEvent) {
  }

  public void handleSearch(MouseEvent mouseEvent) {
  }
}

