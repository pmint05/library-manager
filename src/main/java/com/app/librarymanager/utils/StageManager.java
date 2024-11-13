package com.app.librarymanager.utils;

import java.io.IOException;
import java.util.Objects;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class StageManager {
  private static Stage primaryStage;
  private static Stage childStage;

  private static void showStage(Scene scene, Stage stage, String title, boolean resizable) {
    scene.getStylesheets().add(
        Objects.requireNonNull(StageManager.class.getResource("/styles/global.css")).toExternalForm());
    stage.setScene(scene);
    stage.setTitle(title);
    stage.setResizable(resizable);
    stage.centerOnScreen();
    stage.show();
  }

  private static Scene loadScene(String fxmlPath) {
    try {
      return new Scene(FXMLLoader.load(
          Objects.requireNonNull(StageManager.class.getResource(fxmlPath))));
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }

  public static void closeActiveChildWindow() {
    if (childStage != null) {
      childStage.close();
      childStage = null;
    }
  }

  public static void showChildWindow(String fxmlPath, String title, boolean resizable) {
    closeActiveChildWindow();
    Scene childScene = loadScene(fxmlPath);
    if (childScene != null) {
      Stage childStage = new Stage();
      childStage.initOwner(primaryStage);
      childStage.initModality(Modality.WINDOW_MODAL);
      showStage(childScene, childStage, title, resizable);
      StageManager.childStage = childStage;
    }
  }

  public static void showLoginWindow() {
    showChildWindow("/views/login.fxml", "Login | Library Manager", false);
  }

  public static void showRegisterWindow() {
    showChildWindow("/views/register.fxml", "Register | Library Manager", false);
  }

  public static void showHomeWindow() {
    primaryStage.setHeight(600);
    primaryStage.setWidth(800);
    showStage(Objects.requireNonNull(loadScene("/views/home.fxml")), primaryStage, "Library Manager", true);
  }
  public static void showForgotPasswordWindow() {
    showChildWindow("/views/forgot-password.fxml", "Forgot Password | Library Manager", false);
  }

  public static void setPrimaryStage(Stage stage) {
    StageManager.primaryStage = stage;
    primaryStage.setOnCloseRequest(event -> closeActiveChildWindow());
  }

  public static Stage getPrimaryStage() {
    return primaryStage;
  }
}
