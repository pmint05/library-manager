package com.app.librarymanager.utils;

import java.util.Objects;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.Region;
import org.jetbrains.annotations.NotNull;

public class AlertDialog {

  public AlertDialog() {
  }

  public static void showAlert(String type, String title, String message) {
    Alert alert = getAlert(title, message, type);
    alert.getDialogPane().getStylesheets().add(
        Objects.requireNonNull(StageManager.class.getResource("/styles/global.css")).toExternalForm());

    alert.getDialogPane().getStyleClass().add("custom-alert");
    alert.getDialogPane().lookup(".content.label").getStyleClass().add("custom-alert-content");
    if (alert.getDialogPane().lookup(".header-panel") != null) {
      alert.getDialogPane().lookup(".header-panel").getStyleClass().add("custom-alert-header");
    }
    alert.getDialogPane().lookupButton(ButtonType.OK).getStyleClass().add("custom-alert-button");
    alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
    alert.showAndWait();
  }

  @NotNull
  private static Alert getAlert(String title, String message, String type) {
    Alert alert = new Alert(AlertType.INFORMATION);
    alert.setTitle((!type.isEmpty() ? type.toUpperCase() : "INFORMATION") + " | " + title);
    switch (type) {
      case "error":
        alert.setAlertType(AlertType.ERROR);
        break;
      case "warning":
        alert.setAlertType(AlertType.WARNING);
        break;
      case "confirmation":
        alert.setAlertType(AlertType.CONFIRMATION);
        break;
      default:
        alert.setAlertType(AlertType.INFORMATION);
        break;
    }

    alert.setHeaderText(null);
    alert.setContentText(message);
    return alert;
  }

}
