package com.app.librarymanager.controllers;

import com.app.librarymanager.utils.StageManager;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class ForgotPasswordController {
  @FXML
  private TextField emailField;
  @FXML
  private void handleSendEmail() {
    String email = emailField.getText();
    AuthController.sendPasswordResetEmail(email);
  }
  @FXML
  private void handleOpenLogin() {
    StageManager.showLoginWindow();
  }
  @FXML
  private void handleOpenRegister() {
    StageManager.showRegisterWindow();
  }
}
