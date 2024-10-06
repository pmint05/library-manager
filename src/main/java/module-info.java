module com.app.librarymanager {
  requires javafx.controls;
  requires javafx.fxml;

  requires org.kordamp.ikonli.javafx;
  requires org.kordamp.bootstrapfx.core;
  requires java.dotenv;
  requires java.net.http;
  requires annotations;
  requires  org.json;
  requires com.google.gson;
  requires firebase.admin;
  requires com.google.auth.oauth2;
  requires com.google.auth;
  requires google.cloud.firestore;
  requires google.cloud.core;
  requires google.cloud.storage;
  requires com.google.api.apicommon;
  requires static lombok;

  opens com.app.librarymanager to javafx.fxml;
  exports com.app.librarymanager;
  exports com.app.librarymanager.controllers;
  opens com.app.librarymanager.controllers to javafx.fxml;
}