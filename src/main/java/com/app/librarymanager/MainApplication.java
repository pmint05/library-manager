package com.app.librarymanager;

import com.app.librarymanager.controllers.HomeController;
import com.app.librarymanager.services.Firebase;
import com.app.librarymanager.utils.StageManager;
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

      StageManager.setPrimaryStage(stage);
      StageManager.showHomeWindow();

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static void main(String[] args) {
    launch();
  }
}