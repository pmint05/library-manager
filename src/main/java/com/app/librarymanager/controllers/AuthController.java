package com.app.librarymanager.controllers;

import com.app.librarymanager.services.FirebaseAuthentication;
import com.app.librarymanager.utils.AlertDialog;
import com.app.librarymanager.interfaces.AuthStateListener;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.prefs.Preferences;
import javafx.stage.Stage;
import org.json.JSONObject;

public class AuthController {

  private static Stage loginStage;
  private static Stage registerStage;
  private static AuthController instance;
  private List<AuthStateListener> authStateListeners = new ArrayList<>();

  private String idToken;
  private String refreshToken;
  private Date lastAuthTime;
  private Boolean isAuthenticated;
  private JSONObject userClaims;
  private final Preferences authPrefs;


  public AuthController() {
    authPrefs = Preferences.userNodeForPackage(AuthController.class);
    loadSession();
  }
  public static synchronized AuthController getInstance() {
    if (instance == null) {
      instance = new AuthController();
    }
    return instance;
  }
  private void loadSession() {
    this.idToken = authPrefs.get("idToken", null);
    this.refreshToken = authPrefs.get("refreshToken", null);
    this.isAuthenticated = (idToken != null);
    this.lastAuthTime = new Date(authPrefs.getLong("lastAuthTime", 0));
    this.userClaims = new JSONObject(authPrefs.get("userClaims", "{}"));
  }

  public static JSONObject login(String email, String password) {
    return FirebaseAuthentication.loginWithEmailAndPassword(email, password);
  }

  public static boolean register(Map user) {
    return FirebaseAuthentication.createAccountWithEmailAndPassword(user);
  }

  public void loginWithGoogle() {
    System.out.println("Logging in with Google");
  }

  public void onLoginSuccess(JSONObject user) {
    this.isAuthenticated = true;
    this.idToken = user.getString("idToken");
    this.refreshToken = user.getString("refreshToken");
    this.lastAuthTime = new Date();
    this.userClaims = user.getJSONObject("userClaims");
    authPrefs.put("idToken", idToken);
    authPrefs.put("refreshToken", refreshToken);
    authPrefs.putLong("lastAuthTime", lastAuthTime.getTime());
    authPrefs.put("userClaims", user.getJSONObject("userClaims").toString());
    System.out.println(user.getJSONObject("userClaims").toString());
    notifyAuthStateListeners();
  }

  public void onLoginFailure(String errorMessage) {
    System.out.println(errorMessage);
    this.isAuthenticated = false;
    switch (errorMessage) {
      case "EMAIL_NOT_FOUND":
        AlertDialog.showAlert("error", "Email Not Found", "Email not found. Please register first.");
        break;
        case "INVALID_EMAIL":
        AlertDialog.showAlert("error", "Invalid Email", "Please enter a valid email address.");
        break;
      case "INVALID_LOGIN_CREDENTIALS":
        AlertDialog.showAlert("error", "Invalid Credentials", "Wrong email or password. Please try again.");
        break;
      default:
        AlertDialog.showAlert("error", "Login Error", "An error occurred while logging in. Please try again later.");
        break;
    }
      notifyAuthStateListeners();
  }

  public void onRegisterSuccess(JSONObject user) {
    this.isAuthenticated = true;
    this.idToken = user.getString("idToken");
    this.refreshToken = user.getString("refreshToken");
    this.lastAuthTime = new Date();
    this.userClaims = user.getJSONObject("userClaims");
    authPrefs.put("idToken", idToken);
    authPrefs.put("refreshToken", refreshToken);
    authPrefs.putLong("lastAuthTime", lastAuthTime.getTime());
    authPrefs.put("userClaims", user.getJSONObject("userClaims").toString());
    System.out.println("User registered: " + user.getString("email"));
    notifyAuthStateListeners();
  }

  public void onRegisterFailure(String errorMessage){
    this.isAuthenticated = false;
    switch (errorMessage) {
      case "EMAIL_EXISTS":
        AlertDialog.showAlert("error", "Email Exists", "Email already exists. Please login.");
        break;
      case "INVALID_EMAIL":
        AlertDialog.showAlert("error", "Invalid Email", "Please enter a valid email address.");
        break;
      default:
        AlertDialog.showAlert("error", "Registration Error", "An error occurred while registering. Please try again later.");
        break;
    }
    notifyAuthStateListeners();
  }

  public void logout() {
    authPrefs.remove("idToken");
    authPrefs.remove("refreshToken");
    authPrefs.remove("lastAuthTime");
    authPrefs.remove("userClaims");
    this.idToken = null;
    this.refreshToken = null;
    this.isAuthenticated = false;
    this.userClaims = new JSONObject();
    System.out.println("User logged out.");
    notifyAuthStateListeners();
  }

  public static void sendPasswordResetEmail(String email) {
    boolean success = FirebaseAuthentication.sendPasswordResetEmail(email);
    if (success) {
      instance.onResetPasswordEmailSent();
    } else {
      AlertDialog.showAlert("error", "Error", "Failed to send password reset email. Please try again later.");
    }
  }

  public void onRegisterSuccess() {
    System.out.println("User registered successfully.");
    notifyAuthStateListeners();
  }

  public void onResetPasswordEmailSent() {
    System.out.println("Password reset email sent.");
    AlertDialog.showAlert("info", "Email Sent", "Password reset email sent. If the email exists, you will receive a link to reset the password, please check your inbox (or spam folder).");
  }
  public static void onSendPasswordEmailFailure(String errorMessage) {
    switch (errorMessage) {
      case "EMAIL_NOT_FOUND":
        AlertDialog.showAlert("error", "Email Not Found", "Email not found. Please register first.");
        break;
      case "INVALID_EMAIL":
        AlertDialog.showAlert("error", "Invalid Email", "Please enter a valid email address.");
        break;
      default:
        AlertDialog.showAlert("error", "Error", "Failed to send password reset email. Please try again later.");
        break;
    }
  }

  public void addAuthStateListener(AuthStateListener listener) {
    authStateListeners.add(listener);
  }

  public void removeAuthStateListener(AuthStateListener listener) {
    authStateListeners.remove(listener);
  }


  private void notifyAuthStateListeners() {
    for (AuthStateListener listener : authStateListeners) {
      listener.onAuthStateChanged(isAuthenticated, userClaims);
    }
  }

  public boolean isAuthenticated() {
    return isAuthenticated;
  }

  public JSONObject getUserClaims() {
    return userClaims;
  }


}