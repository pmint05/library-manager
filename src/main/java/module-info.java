module com.app.librarymanager {
  requires javafx.controls;
  requires javafx.fxml;

  requires org.kordamp.ikonli.javafx;
  requires org.kordamp.bootstrapfx.core;
  requires static lombok;
  requires java.net.http;
  requires org.json;

  opens com.app.librarymanager to javafx.fxml;
  exports com.app.librarymanager;
  exports com.app.librarymanager.controllers;
  opens com.app.librarymanager.controllers to javafx.fxml;
}