package com.app.librarymanager.controllers;

import com.app.librarymanager.utils.AlertDialog;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import com.app.librarymanager.models.User;
import lombok.Setter;
import org.json.JSONObject;

public class UserModalController {

  @FunctionalInterface
  public interface SaveCallback {

    void onSave(User user);
  }

  @FXML
  private TextField emailField;
  @FXML
  private TextField displayNameField;
  @FXML
  private TextField phoneNumberField;
  @FXML
  private TextField birthdayField;
  @FXML
  private TextField passWordField;
  @FXML
  private CheckBox adminCheckBox;
  @FXML
  private CheckBox emailVerifiedCheckBox;
  @FXML
  private CheckBox disabledCheckBox;
  @FXML
  private VBox loadingOverlay;

  private User user;
  @Setter
  private SaveCallback saveCallback;
  private boolean isEditMode = false;

  public void setUser(User user) {
    this.user = user;
    if (user != null) {
      isEditMode = true;
      emailField.setText(user.getEmail());
      emailField.setDisable(true);
      displayNameField.setText(user.getDisplayName());
      phoneNumberField.setText(user.getPhoneNumber());
      birthdayField.setText(user.getBirthday());
      adminCheckBox.setSelected(user.isAdmin());
      emailVerifiedCheckBox.setSelected(user.isEmailVerified());
      disabledCheckBox.setSelected(user.isDisabled());
    } else {
      isEditMode = false;
      emailField.setDisable(false);
    }
  }

  @FXML
  void onSubmit() {
    if (user == null) {
      user = new User();
    }
    user.setEmail(emailField.getText());
    user.setDisplayName(displayNameField.getText());
    user.setPhoneNumber(phoneNumberField.getText());
    user.setBirthday(birthdayField.getText());
    user.setAdmin(adminCheckBox.isSelected());
    user.setEmailVerified(emailVerifiedCheckBox.isSelected());
    user.setDisabled(disabledCheckBox.isSelected());
    user.setPassword(passWordField.getText());

    Task<JSONObject> task = new Task<JSONObject>() {
      @Override
      protected JSONObject call() throws Exception {
        return isEditMode ? UserController.updateUser(user) : UserController.createUser(user);
      }
    };

    task.setOnRunning(e -> showLoading(true));

    task.setOnSucceeded(e -> {
      showLoading(false);
      JSONObject resp = task.getValue();
//      System.out.println(resp);
      Stage stage = (Stage) emailField.getScene().getWindow();
      if (resp.getBoolean("success")) {
        AlertDialog.showAlert("success", "Success", resp.getString("message"), null);
        stage.close();
        if (saveCallback != null) {
          saveCallback.onSave(user);
        }
      } else {
        AlertDialog.showAlert("error", "Error", resp.getString("message"), null);
      }
    });

    task.setOnFailed(e -> {
      showLoading(false);
      AlertDialog.showAlert("error", "Error", "An error occurred while saving the user.", null);
    });

    new Thread(task).start();
  }

  @FXML
  private void onCancel() {
    Stage stage = (Stage) emailField.getScene().getWindow();
    stage.close();
  }

  private void showLoading(boolean show) {
    loadingOverlay.setVisible(show);
  }
}