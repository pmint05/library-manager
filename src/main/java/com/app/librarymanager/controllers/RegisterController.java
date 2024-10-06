package com.app.librarymanager.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class RegisterController {

  @FXML
  private TextField emailField;

  @FXML
  private PasswordField passwordField;
  @FXML
  private PasswordField confirmPasswordField;

  @FXML
  private void handleRegisterAction() {
    String email = emailField.getText();
    String password = passwordField.getText();
    String confirmPassword = confirmPasswordField.getText();

    AuthController.register(email, password, confirmPassword);


  }

  @FXML
  private void handleForgotPassword() {
    // Handle forgot password logic here
    System.out.println("Forgot Password Clicked");
  }

  @FXML
  private void handleGoogleLogin() {
    // Handle Google login logic here
    System.out.println("Login with Google Clicked");
  }

  @FXML
  private void handleLogin() {
    AuthController.closeRegisterWindow();
  }

  @FXML
  public void handleClose() {
    AuthController.closeRegisterWindow();
  }


}