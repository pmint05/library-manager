package com.app.librarymanager;

import com.app.librarymanager.controllers.HomeController;
import com.app.librarymanager.services.Firebase;
import java.util.Objects;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MainApplication extends Application {

  private static Stage currentStage;

  @Override
  public void start(Stage stage) throws IOException {
    try {
      Firebase firebase = Firebase.getInstance();
      FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("/views/home.fxml"));
      Parent root = fxmlLoader.load();
      Scene scene = new Scene(root);
      scene.getStylesheets().add(
          Objects.requireNonNull(getClass().getResource("/styles/global.css")).toExternalForm());
      stage.setTitle("Library Manager");
      stage.setResizable(false);
      stage.setScene(scene);
      stage.setOnCloseRequest(e -> {
        HomeController controller = fxmlLoader.getController();
        controller.closeLoginWindow();
      });

      currentStage = stage;

      stage.show();

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static Stage getStage() {
    return currentStage;
  }

  public static void main(String[] args) {
    launch();
  }
}