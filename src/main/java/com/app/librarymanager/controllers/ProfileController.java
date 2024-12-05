package com.app.librarymanager.controllers;

import com.app.librarymanager.models.User;
import com.app.librarymanager.services.UserService;
import com.app.librarymanager.utils.AlertDialog;
import com.app.librarymanager.utils.AvatarUtil;
import com.app.librarymanager.utils.DatePickerUtil;
import com.app.librarymanager.utils.DateUtil;
import com.app.librarymanager.utils.UploadFileUtil;
import com.google.firebase.auth.FirebaseAuth;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Objects;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.util.Callback;
import javax.imageio.ImageIO;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

public class ProfileController extends ControllerWithLoader {

  @FXML
  private Text localIdField;

  @FXML
  private Text emailField;

  @FXML
  private TextField displayNameField;

  @FXML
  private TextField phoneNumberField;

  @FXML
  private DatePicker birthdayField;

  @FXML
  private PasswordField oldPasswordField;

  @FXML
  private PasswordField newPasswordField;

  @FXML
  private ImageView profileImageView;

//  @FXML
//  private HBox hBox;

  @FXML
  private StackPane stackPane;

  @FXML
  private Button saveChangesButton;

  @FXML
  private Button updatePasswordButton;
  @FXML
  private Label changeAvatarLabel;

  @FXML
  private StackPane imageStackPane;

  @FXML
  private HBox avatarContainer;

  private String initialDisplayName;
  private String initialPhoneNumber;
  private String initialBirthday;
  private boolean admin;
  private String filePath;

  public ProfileController() {
  }

  @FXML
  public void initialize() {
    showCancel(false);
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

    birthdayField.getEditor().setOnMouseClicked(event -> {
      birthdayField.show();
    });

    DatePickerUtil.setDatePickerFormat(birthdayField);
    DatePickerUtil.disableFutureDates(birthdayField);
    DatePickerUtil.disableEditor(birthdayField);
    profileImageView.setOnMouseClicked(event -> {
      FileChooser fileChooser = new FileChooser();
      fileChooser.getExtensionFilters()
          .add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));
      fileChooser.setTitle("Choose an image");
      fileChooser.setInitialDirectory(new File(UserService.initialDirectory));
      File file = fileChooser.showOpenDialog(profileImageView.getScene().getWindow());
      if (file != null) {
        filePath = file.getAbsolutePath();
        BufferedImage croppedImage;
        try {
          croppedImage = UploadFileUtil.cropImage(file, 96);
          ByteArrayOutputStream baos = new ByteArrayOutputStream();
          ImageIO.write(croppedImage, "png", baos);
          Image image = new Image(new ByteArrayInputStream(baos.toByteArray()));
          profileImageView.setImage(image);
        } catch (IOException e) {
          throw new RuntimeException(e);
        }

        saveChangesButton.setDisable(false);
      }
    });
    imageStackPane.setOnMouseEntered(event -> changeAvatarLabel.setVisible(true));
    imageStackPane.setOnMouseExited(event -> changeAvatarLabel.setVisible(false));

    addFieldListeners();
  }

  private void loadUserProfile() {
    User currentUser = AuthController.getInstance().getCurrentUser();
    if (currentUser != null) {
      localIdField.setText(currentUser.getUid());
      emailField.setText(currentUser.getEmail());
      initialDisplayName = currentUser.getDisplayName();
      displayNameField.setText(initialDisplayName);
      initialPhoneNumber = currentUser.getPhoneNumber();
      phoneNumberField.setText(initialPhoneNumber);
      initialBirthday = currentUser.getBirthday();
      birthdayField.setValue(initialBirthday.isEmpty() ? null : DateUtil.parse(initialBirthday));
      admin = currentUser.isAdmin();
      String photoUrl = currentUser.getPhotoUrl();
      if (!photoUrl.isEmpty()) {
        //  System.out.println("Loading photoUrl: " + photoUrl);
        try {
          setDefaultAvatar();
          profileImageView.setImage(currentUser.getAvatar());
        } catch (Exception e) {
          //  System.err.println("Failed to load image from photoUrl: " + e.getMessage());
        }
      } else {
        setDefaultAvatar();
      }
    }
  }

  private void setDefaultAvatar() {
    avatarContainer.setVisible(true);
    AvatarUtil avatarUtil = new AvatarUtil();
    avatarUtil.setRounded(true).setBold(true).setBackground("bae6fd");
    profileImageView.setImage(new Image(
        avatarUtil.getAvatarUrl(Objects.requireNonNullElse(initialDisplayName, "Anonymous"))));
  }

  private void addFieldListeners() {
    displayNameField.textProperty()
        .addListener((observable, oldValue, newValue) -> checkForChanges());
    phoneNumberField.textProperty()
        .addListener((observable, oldValue, newValue) -> checkForChanges());
    birthdayField.valueProperty()
        .addListener((observable, oldValue, newValue) -> checkForChanges());
    oldPasswordField.textProperty()
        .addListener((observable, oldValue, newValue) -> checkForPasswordChanges());
    newPasswordField.textProperty()
        .addListener((observable, oldValue, newValue) -> checkForPasswordChanges());
  }

  private void checkForChanges() {
    boolean hasChanges =
        !displayNameField.getText().equals(initialDisplayName) ||
            !phoneNumberField.getText().equals(initialPhoneNumber) ||
            (birthdayField.getValue() != null && !DateUtil.format(birthdayField.getValue())
                .equals(initialBirthday)) ||
            (birthdayField.getValue() == null && initialBirthday != null
                && !initialBirthday.isEmpty());
    saveChangesButton.setDisable(!hasChanges);
  }

  private void checkForPasswordChanges() {
    boolean hasPasswordChanges =
        !oldPasswordField.getText().isEmpty() && !newPasswordField.getText().isEmpty();
    updatePasswordButton.setDisable(!hasPasswordChanges);
  }

  @FXML
  private void handleSaveChanges() {
    String displayName = displayNameField.getText().trim();
    String phoneNumber = phoneNumberField.getText().trim();
    String birthday = birthdayField.getEditor().getText().trim();
    String localId = localIdField.getText().trim();

    User user = new User();
    user.setDisplayName(displayName);
    user.setPhoneNumber(phoneNumber);
    user.setBirthday(birthday);
    user.setUid(localId);
    user.setPhotoUrl(filePath);
    user.setAdmin(admin);

    setLoadingText("Updating profile...");

    Task<JSONObject> task = new Task<JSONObject>() {
      @Override
      protected JSONObject call() {
        return UserService.getInstance().updateUserProfile(user);
      }
    };
    task.setOnRunning(event -> showLoading(true));
    task.setOnSucceeded(event -> {
      JSONObject resp = task.getValue();
      showLoading(false);
      if (resp.getBoolean("success")) {
        initialDisplayName = displayName;
        initialPhoneNumber = phoneNumber;
        initialBirthday = birthday;
        saveChangesButton.setDisable(true);
        AlertDialog.showAlert("success", "Success", resp.getString("message"), null);
      } else {
        AlertDialog.showAlert("error", "Error", resp.getString("message"), null);
      }
    });
    task.setOnFailed(event -> {
      showLoading(false);
      AlertDialog.showAlert("error", "Error", "An error occurred while updating the profile.",
          null);
    });
    new Thread(task).start();
  }

  @FXML
  private void handleUpdatePassword() {
    String oldPassword = oldPasswordField.getText().trim();
    String newPassword = newPasswordField.getText().trim();
    String localId = localIdField.getText().trim();
    String email = emailField.getText().trim();

    setLoadingText("Updating password...");

    Task<JSONObject> task = new Task<JSONObject>() {
      @Override
      protected JSONObject call() {
        return UserService.getInstance()
            .updateUserPassword(localId, email, oldPassword, newPassword);
      }
    };
    task.setOnRunning(event -> showLoading(true));
    task.setOnSucceeded(event -> {
      JSONObject resp = task.getValue();
      showLoading(false);
      if (resp.getBoolean("success")) {
        oldPasswordField.clear();
        newPasswordField.clear();
        updatePasswordButton.setDisable(true);
        AlertDialog.showAlert("success", "Success", resp.getString("message"), null);
      } else {
        AlertDialog.showAlert("error", "Error", resp.getString("message"), null);
      }
    });
    task.setOnFailed(event -> {
      showLoading(false);
      AlertDialog.showAlert("error", "Error", "An error occurred while updating the password.",
          null);
    });
    new Thread(task).start();
  }
}