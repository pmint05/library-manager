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
  private String providerId;
  private boolean admin;
  private boolean emailVerified;
  private boolean disabled;


  public User(String email, String password, String displayName, String birthday,
      String phoneNumber, String photoUrl, String createdAt, String lastModifiedDate,
      boolean admin) {
    this.email = email;
    this.password = password;
    this.displayName = displayName;
    this.birthday = birthday;
    this.phoneNumber = phoneNumber;
    this.photoUrl = photoUrl;
    this.createdAt = createdAt;
    this.lastModifiedDate = lastModifiedDate;
    this.admin = admin;
  }

  public User(String uid, String email, String password, String displayName, String birthday,
      String phoneNumber, String photoUrl, String createdAt, String lastModifiedDate, String lastLoginAt, String providerId,
      boolean admin, boolean emailVerified, boolean disabled) {
    this.uid = uid;
    this.email = email;
    this.password = password;
    this.displayName = displayName;
    this.birthday = birthday;
    this.phoneNumber = phoneNumber;
    this.photoUrl = photoUrl;
    this.createdAt = createdAt;
    this.lastModifiedDate = lastModifiedDate;
    this.admin = admin;
    this.emailVerified = emailVerified;
    this.disabled = disabled;
    this.lastLoginAt = lastLoginAt;
    this.providerId = providerId;
  }

  public User() {
    this.admin = false;
    this.emailVerified = false;
    this.disabled = false;
  }

  public User(String email, String password, String fullName, String birthday, String photoUrl, boolean admin) {
    this.email = email;
    this.password = password;
    this.displayName = fullName;
    this.birthday = birthday;
    this.admin = admin;

  }
}
