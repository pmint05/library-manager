package com.app.librarymanager.controllers;

import com.app.librarymanager.interfaces.AuthStateListener;

import com.app.librarymanager.models.User;
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
        AuthController.getInstance().getCurrentUser());
  }

  private void updateUI(boolean isAuthenticated, User user) {
    if (isAuthenticated) {
      welcomeLabel.setText("Welcome, " + user.getEmail());
//      loadComponent("/fxml/ManageBookLoans.fxml");
    } else {
      welcomeLabel.setText("Welcome, Guest");
//      loadComponent("/fxml/Login.fxml");
    }
  }

  @Override
  public void onAuthStateChanged(boolean isAuthenticated) {
    updateUI(isAuthenticated, AuthController.getInstance().getCurrentUser());
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

