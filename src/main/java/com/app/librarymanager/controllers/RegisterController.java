package com.app.librarymanager.controllers;

import com.app.librarymanager.models.User;
import com.app.librarymanager.utils.AlertDialog;
import com.app.librarymanager.utils.DatePickerUtil;
import com.app.librarymanager.utils.StageManager;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Map;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

public class RegisterController extends ControllerWithLoader {

  @FXML
  private TextField emailField;
  @FXML
  private PasswordField passwordField;
  @FXML
  private PasswordField confirmPasswordField;
  @FXML
  private DatePicker birthdayField;
  @FXML
  private TextField fullNameField;
//  @FXML
//  private TextField phoneNumberField;

  @FXML
  private VBox loadingOverlay;
  @FXML
  private ProgressIndicator loadingSpinner;


  @FXML
  private void initialize() {
//    birthdayField.getEditor().setOnMouseClicked(event -> {
//      birthdayField.show();
//    });
    setLoadingText("Registering your account...");
    Platform.runLater(() -> emailField.requestFocus());

    DatePickerUtil.setDatePickerFormat(birthdayField);
    birthdayField.setDayCellFactory(new Callback<DatePicker, DateCell>() {
      @Override
      public DateCell call(DatePicker param) {
        return new DateCell() {
          @Override
          public void updateItem(LocalDate item, boolean empty) {
            super.updateItem(item, empty);
            if (item.isAfter(LocalDate.now())) {
              setDisable(true);
              setStyle("-fx-opacity: 0.5;");
            }
          }
        };
      }
    });
  }

  @FXML
  private void handleRegisterAction() {
    String email = emailField.getText().trim();
    String password = passwordField.getText().trim();
    String confirmPassword = confirmPasswordField.getText().trim();
    String fullName = fullNameField.getText().trim();
//    String phoneNumber = phoneNumberField.getText().trim();
    String birthday = birthdayField.getEditor().getText().trim();

    if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || fullName.isEmpty()
        || birthday.isEmpty()) {
      AlertDialog.showAlert("error", "Invalid Input", "Please fill required the fields.", null);
      return;
    }

    if (LoginController.validateEmailAndPassword(email, password)) {
      return;
    }

    if (!password.equals(confirmPassword)) {
      AlertDialog.showAlert("error", "Password Mismatch", "Passwords do not match.", null);
      return;
    }

//    if (!phoneNumber.matches("\\d{10}")) {
//      AlertDialog.showAlert("error", "Invalid Phone Number",
//          "Please enter a valid 10-digit phone number.", null);
//      return;
//    }

    User newUser = new User(email, password, fullName, birthday, "", false);
    Task<JSONObject> registerTask = getTask(newUser);

    new Thread(registerTask).start();
  }

  @NotNull
  private Task<JSONObject> getTask(User newUser) {
    Task<JSONObject> registerTask = new Task<JSONObject>() {
      @Override
      protected JSONObject call() throws Exception {
        return AuthController.register(newUser);
      }
    };

    registerTask.setOnRunning(e -> showLoading(true));
    registerTask.setOnSucceeded(e -> {
      showLoading(false);
      JSONObject createResp = registerTask.getValue();
      if (createResp.getBoolean("success")) {
        AlertDialog.showAlert("success", "Registration Successful", createResp.getString("message"),
            null);
        StageManager.showLoginWindow();
      } else {
        AuthController.getInstance().onRegisterFailure(createResp.getString("message"));
      }
    });
    return registerTask;
  }

  @FXML
  private void handleGoogleLogin() throws IOException {
    showLoading(true);
    Task<JSONObject> googleLoginTask = new Task<JSONObject>() {
      @Override
      protected JSONObject call() throws Exception {
        return AuthController.getInstance().googleLogin();
      }
    };

    googleLoginTask.setOnSucceeded(e -> {
      showLoading(false);
      JSONObject response = googleLoginTask.getValue();
      if (response.getBoolean("success")) {
        AuthController.getInstance().onRegisterSuccess(response.getJSONObject("data"));
        StageManager.closeActiveChildWindow();
      } else {
        AuthController.getInstance().onRegisterFailure(response.getString("code"));
      }
    });
    new Thread(googleLoginTask).start();
  }

  @FXML
  private void handleOpenLogin() {
    StageManager.showLoginWindow();
  }

  @FXML
  private void handleOpenDatePickerPopup() {
  }

  @FXML
  public void handleClose() {

  }

  @FXML
  private void handleKeyPressed(KeyEvent event) {
    if (event.getCode() == KeyCode.ENTER) {
      handleRegisterAction();
    }
  }
}