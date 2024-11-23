package com.app.librarymanager.services;

import com.app.librarymanager.models.User;
import org.json.JSONObject;

public class UserService {

  private static UserService instance;

  private UserService() {
  }

  public synchronized static UserService getInstance() {
    if (instance == null) {
      instance = new UserService();
    }

    return instance;
  }

  public JSONObject updateUserProfile(User user) {
    return new JSONObject().put("success", true).put("message", "User profile updated successfully.");
  }

  public JSONObject updateUserPassword(User user) {
    return new JSONObject().put("success", true).put("message", "User password updated successfully.");
  }


}
