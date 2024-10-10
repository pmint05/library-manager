package com.app.librarymanager.services;

import com.app.librarymanager.controllers.AuthController;
import com.app.librarymanager.utils.Fetcher;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import org.json.JSONObject;

public class FirebaseAuthentication {

  public static boolean loginWithEmailAndPassword(String email, String password) {
    String url =
        "https://identitytoolkit.googleapis.com/v1/accounts:signInWithPassword?key="
            + Firebase.getApiKey();
    String body = "{\n"
        + "  \"email\": \"" + email + "\",\n"
        + "  \"password\": \"" + password + "\",\n"
        + "  \"returnSecureToken\": true\n"
        + "}";
    JSONObject response = Fetcher.post(url, body);
    if (response == null) {
      System.out.println("Login Failed");
      AuthController.getInstance().onLoginFailure("Login Failed");
      return false;
    }
    if (response.has("error")) {
      JSONObject error = response.getJSONObject("error");
      if (error.has("message")) {
        System.out.println("Login Failed: " + error.getString("message"));
        AuthController.getInstance().onLoginFailure(error.getString("message"));
        return false;
      }
    } else {
      System.out.println("Login Successful");
      try {
        response.put("userClaims", FirebaseAuth.getInstance().verifyIdToken(response.getString("idToken")).getClaims());
      } catch (FirebaseAuthException e) {
        throw new RuntimeException(e);
      }
      AuthController.getInstance().onLoginSuccess(response);
      return true;
    }
    return false;
  }

}
