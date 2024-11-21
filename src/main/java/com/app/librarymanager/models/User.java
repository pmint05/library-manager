package com.app.librarymanager.models;

import lombok.Data;

@Data
public class User {
  private String uid;
  private String email;
  private String password;
  private String displayName;
  private String birthday;
  private String phoneNumber;
  private String photoUrl;
  private String createdAt;
  private String lastModifiedDate;
  private String lastLoginAt;
  private boolean admin;
  private boolean emailVerified;
  private boolean disabled;


  public User(String email, String password, String fullName, String birthday, String phoneNumber, String photoUrl, String createdAt, String lastModifiedDate, boolean admin) {
    this.email = email;
    this.password = password;
    this.displayName = fullName;
    this.birthday = birthday;
    this.phoneNumber = phoneNumber;
    this.photoUrl = photoUrl;
    this.createdAt = createdAt;
    this.lastModifiedDate = lastModifiedDate;
    this.admin = admin;
  }

}
