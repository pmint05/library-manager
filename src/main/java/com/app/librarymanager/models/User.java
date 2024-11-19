package com.app.librarymanager.models;

import lombok.Data;

@Data
public class User {
  private String id;
  private String name;
  private String dateOfBirth;
  private String phoneNumber;
  private String profilePicture;
  private String createdDate;
  private String lastModifiedDate;


}
