
package com.app.librarymanager.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.scene.control.Hyperlink;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;

public class SearchController {

    @FXML
    private ImageView imageView;

    private Popup popup;

    @FXML
    public void initialize() {
        popup = new Popup();
        popup.setAutoHide(true);
        VBox popupContent = new VBox(10);
        popupContent.setStyle(
                "-fx-padding: 10;" +
                        "-fx-background-color: white;" +
                        "-fx-border-color: gray;" +
                        "-fx-border-width: 1;"
        );

        Hyperlink registerLink = new Hyperlink("Create an account");
        registerLink.setOnAction(this::handleOpenRegister);
        registerLink.setStyle("-fx-text-fill: blue; -fx-padding: 5;");

        Hyperlink logoutLink = new Hyperlink("Logout");
        logoutLink.setOnAction(this::handleOpenLogin);
        logoutLink.setStyle("-fx-text-fill: blue; -fx-padding: 5;");

        popupContent.getChildren().addAll(registerLink, logoutLink);
        popup.getContent().add(popupContent);
    }

    @FXML
    public void handleImageClick() {
        if (popup.isShowing()) {
            popup.hide();
        } else {
            Bounds boundsInScreen = imageView.localToScreen(imageView.getBoundsInLocal());
            popup.setOnShown(event -> {
                double popupX = boundsInScreen.getMinX() + (boundsInScreen.getWidth()) - (popup.getWidth());
                double popupY = boundsInScreen.getMaxY();
                popup.setX(popupX);
                popup.setY(popupY);
            });
            popup.show(imageView, popup.getX(), popup.getY());
        }
    }

    public void handleOpenRegister(ActionEvent event) {
        System.out.println("Create an account clicked");
        popup.hide();
    }

    public void handleOpenLogin(ActionEvent event) {
        System.out.println("Logout clicked");
        popup.hide();
    }

    @FXML
    public void handleSearch() {

    }
}


