package com.app.librarymanager.controllers;

import com.app.librarymanager.utils.AlertDialog;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import com.app.librarymanager.models.User;
import org.json.JSONObject;

public class EditUserController {

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

  private User user;
  private SaveCallback saveCallback;

  public void setUser(User user) {
    this.user = user;
    emailField.setText(user.getEmail());
    emailField.setDisable(true);
    displayNameField.setText(user.getDisplayName());
    phoneNumberField.setText(user.getPhoneNumber());
    birthdayField.setText(user.getBirthday());
    adminCheckBox.setSelected(user.isAdmin());
    emailVerifiedCheckBox.setSelected(user.isEmailVerified());
    disabledCheckBox.setSelected(user.isDisabled());
  }

  public void setSaveCallback(SaveCallback saveCallback) {
    this.saveCallback = saveCallback;
  }

  @FXML
  void onSave() {
    user.setEmail(emailField.getText());
    user.setDisplayName(displayNameField.getText());
    user.setPhoneNumber(phoneNumberField.getText());
    user.setBirthday(birthdayField.getText());
    user.setAdmin(adminCheckBox.isSelected());
    user.setEmailVerified(emailVerifiedCheckBox.isSelected());
    user.setDisabled(disabledCheckBox.isSelected());
    user.setPassword(passWordField.getText());

    JSONObject resp = UserController.updateUser(user);
    System.out.println(resp);
    Stage stage = (Stage) emailField.getScene().getWindow();
    if (resp.getBoolean("success")) {
      AlertDialog.showAlert("success", "Success", resp.getString("message"), null);
      stage.close();
      if (saveCallback != null) {
        saveCallback.onSave(user);
      }
    } else {
      AlertDialog.showAlert("error", "Success", resp.getString("message"), null);
    }
  }

  @FXML
  private void onCancel() {
    Stage stage = (Stage) emailField.getScene().getWindow();
    stage.close();
  }
}