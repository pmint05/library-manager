package com.app.librarymanager.controllers;

import com.app.librarymanager.MainApplication;
import com.app.librarymanager.services.Firebase;
import com.app.librarymanager.services.FirebaseFirestore;
import java.util.Objects;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class AuthController {

  private static Stage loginStage;
  private static Stage registerStage;
  private static AuthController instance;


  public AuthController() {
  }

  public AuthController getInstance() {
    if (instance == null) {
      instance = new AuthController();
    }
    return instance;
  }

  public static void openLoginWindow() {
    Stage parentStage = MainApplication.getStage();
    if (loginStage == null) {
      try {
        loginStage = new Stage();
        openWindow("/views/login.fxml", "Login | Library Manager", parentStage, loginStage);
        loginStage.setOnCloseRequest(e -> loginStage = null);
      } catch (Exception e) {
        e.printStackTrace();
      }
    } else {
      loginStage.toFront();
    }
  }

  public static void openRegisterWindow() {
    Stage parentStage = MainApplication.getStage();
    if (registerStage == null) {
      try {
        registerStage = new Stage();
        openWindow("/views/register.fxml", "Register | Library Manager", parentStage,
            registerStage);
        registerStage.setOnCloseRequest(e -> registerStage = null);
      } catch (Exception e) {
        e.printStackTrace();
      }
    } else {
      registerStage.toFront();
    }
  }

  private static void openWindow(String fxmlPath, String title, Stage parentStage, Stage stage) {
    try {
      FXMLLoader loader = new FXMLLoader(AuthController.class.getResource(fxmlPath));
      Scene scene = new Scene(loader.load());
      scene.getStylesheets().add(
          Objects.requireNonNull(AuthController.class.getResource("/styles/global.css"))
              .toExternalForm());
      stage.setScene(scene);
      stage.setTitle(title);
      stage.initModality(Modality.APPLICATION_MODAL);
      stage.initOwner(parentStage);
      stage.setResizable(false);
      stage.show();

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static void login(String email, String password) {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    Firebase.login(email, password);
    db.addData("users", email, null);
  }

  public static void register(String email, String password, String confirmPassword) {
    System.out.println("Registering with email: " + email);
    closeRegisterWindow();
  }

  public void loginWithGoogle() {
    System.out.println("Logging in with Google");
    closeLoginWindow();
  }

  public static void closeLoginWindow() {
    if (loginStage != null) {
      loginStage.close();
      loginStage = null;
    }
  }

  public static void closeRegisterWindow() {
    if (registerStage != null) {
      registerStage.close();
      registerStage = null;
    }
  }
}