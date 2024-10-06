package com.app.librarymanager.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import com.app.librarymanager.controllers.AuthController;

public class HomeController {

  @FXML
  private Button loginButton;
  @FXML
  private Button registerButton;

  @FXML
  private void initialize() {
    System.out.println("Home Controller Initialized");
  }

  @FXML
  private void onLoginButtonClick() {
    AuthController.openLoginWindow();
  }

  @FXML
  private void onRegisterButtonClick() {
  }

  public void closeLoginWindow() {

  }
}

