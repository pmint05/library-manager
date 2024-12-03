package com.app.librarymanager.utils;

import java.io.IOException;
import java.util.Objects;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lombok.Getter;

public class StageManager {

  @Getter
  private static Stage primaryStage;
  private static Stage childStage;

  private static void showStage(Scene scene, Stage stage, String title, boolean resizable) {
    scene.getStylesheets().add(
        Objects.requireNonNull(StageManager.class.getResource("/styles/global.css"))
            .toExternalForm());
    stage.setScene(scene);
    stage.setTitle(title);
    stage.setResizable(resizable);
    stage.setMinWidth(800);
    stage.setMinHeight(600);
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

  public static void handleClosePrimaryStage() {
    closeActiveChildWindow();
    Platform.exit();
    System.exit(0);
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

  public interface FirebaseAuthCallback {

    void onSuccess(String idToken);

    void onError(String error);

    void onCancel();
  }
//  public static void showGoogleLoginPopup(FirebaseAuthCallback callback) {
//    WebView webView = new WebView();
//    WebEngine webEngine = webView.getEngine();
//
//    // Load Firebase Authentication logic into WebView
//    String htmlContent = "<html><body>"
//        + "<script src=\"https://www.gstatic.com/firebasejs/9.24.0/firebase-app.js\"></script>"
//        + "<script src=\"https://www.gstatic.com/firebasejs/9.24.0/firebase-auth.js\"></script>"
//        + "<script>"
//        + "const firebaseConfig = { apiKey: 'YOUR_API_KEY', authDomain: 'YOUR_PROJECT_ID.firebaseapp.com', projectId: 'YOUR_PROJECT_ID', storageBucket: 'YOUR_PROJECT_ID.appspot.com', messagingSenderId: 'YOUR_SENDER_ID', appId: 'YOUR_APP_ID'};"
//        + "firebase.initializeApp(firebaseConfig);"
//        + "const provider = new firebase.auth.GoogleAuthProvider();"
//        + "firebase.auth().signInWithPopup(provider).then(result => {"
//        + "  result.user.getIdToken().then(idToken => {"
//        + "    window.javaApp.sendToken(idToken);"
//        + "  });"
//        + "}).catch(error => {"
//        + "  window.javaApp.sendError(error.message);"
//        + "});"
//        + "</script>"
//        + "</body></html>";
//
//    webEngine.load("data:text/html," + htmlContent);
//
//    // Set up JavaScript-to-Java communication
//    webEngine.setJavaScriptEnabled(true);
//    webEngine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
//      if (newState == javafx.concurrent.Worker.State.SUCCEEDED) {
//        webEngine.executeScript("window.javaApp = {"
//            + "sendToken: function(token) { javaApp.sendToken(token); },"
//            + "sendError: function(error) { javaApp.sendError(error); }"
//            + "};");
//      }
//    });
//
//    // Handle token or error from JavaScript
//    webEngine.executeScript("window.javaApp = {"
//        + "sendToken: function(token) { javaApp.sendToken(token); },"
//        + "sendError: function(error) { javaApp.sendError(error); }"
//        + "};");
//
//    // Set callback for token or error handling
//    stage.setOnCloseRequest(event -> callback.onCancel());
//    stage.setScene(new Scene(webView, 800, 600));
//    stage.show();
//  }

  public static void showLoginWindow() {
    showChildWindow("/views/auth/login.fxml", "Login | Library Manager", false);
  }

  public static void showRegisterWindow() {
    showChildWindow("/views/auth/register.fxml", "Register | Library Manager", false);
  }

  public static void showHomeWindow() {
    primaryStage.setWidth(1280);
    primaryStage.setHeight(720);
    showStage(Objects.requireNonNull(loadScene("/views/layout.fxml")), primaryStage,
        "Library Manager", true);
  }

  public static void showForgotPasswordWindow() {
    showChildWindow("/views/auth/forgot-password.fxml", "Forgot Password | Library Manager", false);
  }

  public static void setPrimaryStage(Stage stage) {
    primaryStage = stage;
    primaryStage.setOnCloseRequest(event -> handleClosePrimaryStage());
  }

}
