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
  requires google.cloud.storage;
  requires com.google.api.apicommon;
  requires java.prefs;
  requires google.cloud.core;
  requires static lombok;

  requires org.mongodb.driver.core;
  requires org.mongodb.driver.sync.client;
  requires org.mongodb.bson;
  requires java.sql;

  opens com.app.librarymanager to javafx.fxml, com.google.gson;
  exports com.app.librarymanager;
  exports com.app.librarymanager.controllers;
  exports com.app.librarymanager.models;
  opens com.app.librarymanager.controllers to javafx.fxml, com.google.gson;
  opens com.app.librarymanager.models to com.google.gson;
}