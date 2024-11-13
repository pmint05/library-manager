package com.app.librarymanager.services;

import com.app.librarymanager.controllers.AuthController;
import com.app.librarymanager.utils.Fetcher;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONObject;

public class FirebaseAuthentication {

  private static final String LOGIN_URL = "https://identitytoolkit.googleapis.com/v1/accounts:signInWithPassword?key=";
  private static final String REGISTER_URL = "https://identitytoolkit.googleapis.com/v1/accounts:signUp?key=";
  private static final String RESET_PASSWORD_URL = "https://identitytoolkit.googleapis.com/v1/accounts:sendOobCode?key=";

  public static JSONObject loginWithEmailAndPassword(String email, String password) {
    String url = LOGIN_URL + Firebase.getApiKey();
    String body =
        "{\n" + "  \"email\": \"" + email + "\",\n" + "  \"password\": \"" + password + "\",\n"
            + "  \"returnSecureToken\": true\n" + "}";
    JSONObject response = Fetcher.post(url, body);
    if (response == null) {
      return new JSONObject(Map.of("success", false, "message", "Login Failed"));
    }
    if (response.has("error")) {
      JSONObject error = response.getJSONObject("error");
      if (error.has("message")) {
        System.out.println("Login Failed: " + error.getString("message"));
        return new JSONObject(Map.of("success", false, "message", error.getString("message")));
      }
    } else {
      try {
        response.put("userClaims",
            FirebaseAuth.getInstance().verifyIdToken(response.getString("idToken")).getClaims());
      } catch (FirebaseAuthException e) {
        throw new RuntimeException(e);
      }
      return new JSONObject(Map.of("success", true, "data", response));
    }
    return new JSONObject(Map.of("success", false, "message", "Login Failed"));
  }

  public static boolean createAccountWithEmailAndPassword(Map user) {
    String url = REGISTER_URL + Firebase.getApiKey();
    String body =
        "{\n" + "  \"email\": \"" + user.get("email") + "\",\n" + "  \"password\": \"" + user.get(
            "password") + "\",\n" + " \"displayName\": \"" + user.get("fullName") + "\",\n"
            + " \"phoneNumber\": \"" + user.get("phoneNumber") + "\",\n"
            + "  \"returnSecureToken\": true\n" + "}";
    JSONObject response = Fetcher.post(url, body);
    if (response == null) {
      AuthController.getInstance().onRegisterFailure("Registration Failed");
      return false;
    }
    if (response.has("error")) {
      JSONObject error = response.getJSONObject("error");
      if (error.has("message")) {
        AuthController.getInstance().onRegisterFailure(error.getString("message"));
        return false;
      }
    } else {
      try {
        Map<String, Object> claims = new HashMap<>();
        claims.put("admin", false);
        claims.put("birthday", user.get("birthday"));
        FirebaseAuth.getInstance().setCustomUserClaims(response.getString("localId"), claims);
        response.put("userClaims",
            FirebaseAuth.getInstance().verifyIdToken(response.getString("idToken")).getClaims());
      } catch (FirebaseAuthException e) {
        throw new RuntimeException(e);
      }
      AuthController.getInstance().onRegisterSuccess(response);
      return true;
    }
    return false;
  }

  public static boolean sendPasswordResetEmail(String email) {
    String url = RESET_PASSWORD_URL + Firebase.getApiKey();
    String body =
        "{\n" + "  \"requestType\": \"PASSWORD_RESET\",\n" + "  \"email\": \"" + email + "\"\n"
            + "}";
    JSONObject response = Fetcher.post(url, body);
    if (response == null) {
      return false;
    }
    if (response.has("error")) {
      JSONObject error = response.getJSONObject("error");
      if (error.has("message")) {
        AuthController.onSendPasswordEmailFailure(error.getString("message"));
        return false;
      }
    } else {
      System.out.println("Password reset email sent");
      return true;
    }
    return false;
  }

}
