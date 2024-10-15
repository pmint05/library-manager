package com.app.librarymanager.controllers;

import com.app.librarymanager.utils.AlertDialog;
import com.app.librarymanager.utils.StageManager;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;

public class LoginController {

  @FXML
  private TextField emailField;
  @FXML
  private PasswordField passwordField;
  @FXML
  private Button googleLoginButton;
  @FXML
  private Button loginButton;
  @FXML
  private VBox loadingOverlay;
  @FXML
  private ProgressIndicator loadingSpinner;


  @FXML
  private void handleLoginAction() {
    showLoading(true);
    String email = emailField.getText();
    String password = passwordField.getText();

    if (email.isEmpty() || password.isEmpty()) {
      AlertDialog.showAlert("error", "Validation Error", "Please enter your email and password.");
      return;
    }
    boolean success = AuthController.login(email, password);
    if (success) {
      StageManager.closeActiveChildWindow();
    }
    showLoading(false);
  }

  @FXML
  private void handleForgotPassword() {
    StageManager.showForgotPasswordWindow();
  }

  @FXML
  private void handleGoogleLogin() {
    System.out.println("Login with Google Clicked");
  }

  @FXML
  private void handleOpenRegister() {
    StageManager.showRegisterWindow();
  }

  @FXML
  private void handleKeyPressed(KeyEvent event) {
    if (event.getCode() == KeyCode.ENTER) {
      handleLoginAction();
    }
  }

  @FXML
  public void handleClose() {
  }

  private void showLoading(boolean show) {
    loadingOverlay.setVisible(show);
    loadingSpinner.setVisible(show);
  }


}