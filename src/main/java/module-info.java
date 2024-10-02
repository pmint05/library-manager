module com.app.librarymanager {
  requires javafx.controls;
  requires javafx.fxml;

  requires org.kordamp.ikonli.javafx;
  requires org.kordamp.bootstrapfx.core;

  opens com.app.librarymanager to javafx.fxml;
  exports com.app.librarymanager;
}