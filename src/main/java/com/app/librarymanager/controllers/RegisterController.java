package com.app.librarymanager.controllers;

import com.app.librarymanager.utils.AlertDialog;
import com.app.librarymanager.utils.StageManager;
import java.util.Map;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.DatePicker;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

public class RegisterController {

  @FXML
  private TextField emailField;
  @FXML
  private PasswordField passwordField;
  @FXML
  private PasswordField confirmPasswordField;
  @FXML
  private DatePicker birthdayField;
  @FXML
  private TextField fullNameField;
  @FXML
  private TextField phoneNumberField;

  @FXML
  private void initialize() {
    birthdayField.getEditor().setOnMouseClicked(event -> {
        birthdayField.show();
    });
  }

  @FXML
  private void handleRegisterAction() {
    String email = emailField.getText();
    String password = passwordField.getText();
    String confirmPassword = confirmPasswordField.getText();
    String fullName = fullNameField.getText();
    String phoneNumber = phoneNumberField.getText();
    String birthday = birthdayField.getEditor().getText();
    Map<String, String> user = Map.of(
      "email", email,
      "password", password,
      "confirmPassword", confirmPassword,
      "fullName", fullName,
      "phoneNumber", phoneNumber,
      "birthday", birthday
    );
    boolean success = AuthController.register(user);
    if (success) {
      AlertDialog.showAlert("Registration", "Registration Successful", "You have successfully registered");
    }
  }

  @FXML
  private void handleGoogleLogin() {
    System.out.println("Login with Google Clicked");
  }

  @FXML
  private void handleOpenLogin() {
    StageManager.showLoginWindow();
  }

  @FXML
  private void handleOpenDatePickerPopup() {
  }

  @FXML
  public void handleClose() {

  }
}