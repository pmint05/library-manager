package com.app.librarymanager.controllers;

import com.app.librarymanager.models.User;
import com.app.librarymanager.services.FirebaseAuthentication;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserRecord;
import com.google.firebase.auth.UserRecord.UpdateRequest;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
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
      UpdateRequest userUpdate = getUpdateRequest(user);
      UserRecord updatedUser = FirebaseAuth.getInstance().updateUser(userUpdate);
      return new JSONObject().put("success", true).put("data", new JSONObject(updatedUser))
          .put("message", "User updated successfully.");
    } catch (Exception e) {
      JSONObject errorResponse = new JSONObject();
      try {
        String responseBody = e.getMessage().substring(e.getMessage().indexOf("{"));
        JSONObject responseJson = new JSONObject(responseBody);
        String errorMessage = responseJson.getJSONObject("error").getString("message");
        errorResponse.put("message", errorMessage);
      } catch (Exception parseException) {
        errorResponse.put("message", e.getMessage());
      }
      return new JSONObject().put("success", false).put("message", errorResponse.getString("message"));
    }
  }

  @NotNull
  private static UpdateRequest getUpdateRequest(User user) {
    UpdateRequest userUpdate = new UpdateRequest(user.getUid());
    userUpdate.setEmailVerified(user.isEmailVerified());
    userUpdate.setPhoneNumber(user.getPhoneNumber() != null && !user.getPhoneNumber().isEmpty() ? user.getPhoneNumber() : null);
    if (user.getDisplayName() != null && !user.getDisplayName().isEmpty()) {
      userUpdate.setDisplayName(user.getDisplayName());
    }
    if (user.getPhotoUrl() != null && !user.getPhotoUrl().isEmpty()) {
      userUpdate.setPhotoUrl(user.getPhotoUrl());
    }
    userUpdate.setDisabled(user.isDisabled());
    return userUpdate;
  }

  public static JSONObject deleteUser(User user) {
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

  public static JSONObject listUsers() {
    try {
      checkPermission();
      return new JSONObject().put("success", true)
          .put("data", new JSONArray(FirebaseAuth.getInstance().listUsers(null).getValues()));
    } catch (Exception e) {
      return new JSONObject().put("success", false).put("message", e.getMessage());
    }
  }
}
