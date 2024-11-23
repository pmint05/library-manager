package com.app.librarymanager.controllers;

import com.app.librarymanager.models.User;
import com.app.librarymanager.services.UserService;
import com.app.librarymanager.utils.AlertDialog;
import com.app.librarymanager.utils.DatePickerUtil;
import com.app.librarymanager.utils.DateUtil;
import com.google.firebase.auth.FirebaseAuth;
import java.time.LocalDate;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.util.Callback;
import org.json.JSONObject;

public class ProfileController extends ControllerWithLoader {

  @FXML
  private TextField emailField;

  @FXML
  private TextField displayNameField;

  @FXML
  private TextField phoneNumberField;

  @FXML
  private DatePicker birthdayField;

  @FXML
  private PasswordField passwordField;

  @FXML
  private ImageView profileImageView;

  @FXML
  private HBox hBox;

  @FXML
  private StackPane stackPane;

  @FXML
  private Button saveChangesButton;

  private String initialDisplayName;
  private String initialPhoneNumber;
  private String initialBirthday;

  public ProfileController() {
  }

  @FXML
  public void initialize() {
    Task<Void> task = new Task<Void>() {
      @Override
      protected Void call() {
        loadUserProfile();
        return null;
      }
    };
    task.setOnRunning(event -> showLoading(true));
    task.setOnSucceeded(event -> showLoading(false));
    task.setOnFailed(event -> showLoading(false));
    new Thread(task).start();
    hBox.prefWidthProperty().bind(stackPane.widthProperty());
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
    addFieldListeners();
  }

  private void loadUserProfile() {
    JSONObject userClaims = AuthController.getInstance().getUserClaims();
    if (userClaims != null) {
      emailField.setText(userClaims.optString("email", ""));
      initialDisplayName = userClaims.optString("displayName", "");
      displayNameField.setText(initialDisplayName);
      initialPhoneNumber = userClaims.optString("phoneNumber", "");
      phoneNumberField.setText(initialPhoneNumber);
      initialBirthday = userClaims.optString("birthday", "");
      birthdayField.setValue(initialBirthday.isEmpty() ? null : DateUtil.parse(initialBirthday));
      String photoUrl = userClaims.optString("photoUrl", "");
      if (!photoUrl.isEmpty()) {
        profileImageView.setImage(new Image(photoUrl));
      }
    }
  }

  private void addFieldListeners() {
    displayNameField.textProperty()
        .addListener((observable, oldValue, newValue) -> checkForChanges());
    phoneNumberField.textProperty()
        .addListener((observable, oldValue, newValue) -> checkForChanges());
    birthdayField.valueProperty()
        .addListener((observable, oldValue, newValue) -> checkForChanges());
    passwordField.textProperty().addListener((observable, oldValue, newValue) -> checkForChanges());
  }

  private void checkForChanges() {
    boolean hasChanges = !displayNameField.getText().equals(initialDisplayName) ||
        !phoneNumberField.getText().equals(initialPhoneNumber) ||
        !passwordField.getText().isEmpty() || (birthdayField.getValue() != null
        && !birthdayField.getValue().toString().equals(initialBirthday));
    saveChangesButton.setDisable(!hasChanges);
  }

  @FXML
  private void handleSaveChanges() {
    String displayName = displayNameField.getText().trim();
    String phoneNumber = phoneNumberField.getText().trim();
    String birthday = birthdayField.getValue().toString().trim();
    String newPassword = passwordField.getText().trim();

    if (!phoneNumber.isEmpty() && !phoneNumber.matches("\\d{10}")) {
      AlertDialog.showAlert("error", "Invalid phone number", "Phone number must be 10 digits",
          null);
      return;
    }

    User user = new User();
    user.setDisplayName(displayName);
    user.setPhoneNumber(phoneNumber);
    user.setBirthday(birthday);

    setLoadingText("Updating profile...");

    Task<JSONObject> task = new Task<JSONObject>() {
      @Override
      protected JSONObject call() {
        return UserService.getInstance().updateUserProfile(user);
      }
    };
    if (!newPassword.isEmpty()) {
      UserService.getInstance().updateUserPassword(user);
    }

    initialDisplayName = displayName;
    initialPhoneNumber = phoneNumber;
    initialBirthday = birthday;
    passwordField.clear();
    saveChangesButton.setDisable(true);
  }
}