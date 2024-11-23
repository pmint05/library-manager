package com.app.librarymanager.controllers;

import com.app.librarymanager.utils.AlertDialog;
import javafx.fxml.FXML;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class ControllerWithLoader {

  @FXML
  protected VBox loadingOverlay;
  @FXML
  protected ProgressIndicator loadingSpinner;
  @FXML
  protected Text loadingText;

  protected void setLoadingText(String text) {
    loadingText.setText(text);
  }

  protected void showLoading(boolean show) {
    if (loadingOverlay == null || loadingSpinner == null) {
      AlertDialog.showAlert("Error", "Loading components not found.",
          "Please check the FXML file for missing components.", null);
      return;
    }
    loadingOverlay.setVisible(show);
    loadingSpinner.setVisible(show);
  }
}
