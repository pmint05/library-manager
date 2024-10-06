package com.app.librarymanager.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController {


  @FXML
  private TextField emailField;

  @FXML
  private PasswordField passwordField;

  @FXML
  private void handleLoginAction() {
    String email = emailField.getText();
    String password = passwordField.getText();

    AuthController.login(email, password);
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
  private void handleSignUp() {
    AuthController.closeLoginWindow();
  }

  @FXML
  public void handleClose() {
    AuthController.closeLoginWindow();
  }

}