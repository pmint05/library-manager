package com.app.librarymanager.controllers;

import com.app.librarymanager.models.User;
import com.app.librarymanager.services.FirebaseAuthentication;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserRecord;
import com.google.firebase.auth.UserRecord.UpdateRequest;
import org.json.JSONObject;

public class UserController {

  private static void checkPermission() {
    JSONObject userClaims = AuthController.getInstance().getUserClaims();
    if (userClaims == null || !userClaims.optBoolean("admin", false)) {
      throw new SecurityException("Access denied! You don't have permission to make the request.");
    }
  }

  public static JSONObject createUser(User user) {
    try {
      checkPermission();
      return new JSONObject().put("success",
          FirebaseAuthentication.createAccountWithEmailAndPassword(user));
    } catch (Exception e) {
      return new JSONObject().put("success", false).put("message", e.getMessage());
    }
  }

  public static JSONObject updateUser(User user) {
    try {
      checkPermission();
      UpdateRequest userUpdate = new UpdateRequest(user.getUid()).setEmail(user.getEmail())
          .setEmailVerified(user.isEmailVerified()).setPhoneNumber(user.getPhoneNumber())
          .setDisplayName(user.getDisplayName()).setPhotoUrl(user.getPhotoUrl())
          .setDisabled(user.isDisabled());
      UserRecord updatedUser = FirebaseAuth.getInstance().updateUser(userUpdate);
      return new JSONObject().put("success", true).put("data", updatedUser);
    } catch (Exception e) {
      return new JSONObject().put("success", false).put("message", e.getMessage());
    }


  }

  public JSONObject deleteUser(User user) {
    try {
      checkPermission();
      FirebaseAuth.getInstance().deleteUser(user.getUid());
      return new JSONObject().put("success", true).put("message", "User deleted successfully.");
    } catch (Exception e) {
      return new JSONObject().put("success", false).put("message", e.getMessage());
    }
  }

  public JSONObject getUser(String userId) {
    try {
      UserRecord user = FirebaseAuth.getInstance().getUser(userId);
      return new JSONObject().put("success", true).put("data", user);
    } catch (Exception e) {
      return new JSONObject().put("success", false).put("message", e.getMessage());
    }
  }

  public JSONObject listUsers() {
    try {
      checkPermission();
      return new JSONObject().put("success", true)
          .put("data", FirebaseAuth.getInstance().listUsers(null));
    } catch (Exception e) {
      return new JSONObject().put("success", false).put("message", e.getMessage());
    }
  }
}
