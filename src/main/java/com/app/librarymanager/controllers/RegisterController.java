package com.app.librarymanager.controllers;

import com.app.librarymanager.utils.AlertDialog;
import com.app.librarymanager.utils.StageManager;
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
    System.out.println(email);
    System.out.println(password);
    System.out.println(confirmPassword);
    System.out.println(fullName);
    System.out.println(phoneNumber);
    System.out.println(birthday);
    AlertDialog.showAlert("Registration", "Registration Successful", "You have successfully registered");

//    AuthController.register(email, password, confirmPassword);
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