
package com.app.librarymanager.controllers;

import com.app.librarymanager.models.Book;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;
import javafx.stage.Screen;
import java.io.IOException;
import java.util.List;

public class SearchController {
    @FXML
    private VBox vBox;

    @FXML
    private HBox bookRow1;
    @FXML
    private HBox bookRow2;
    @FXML
    private HBox bookRow3;
    @FXML
    private VBox contentVBox;

    @FXML
    private TextField searchField;

    @FXML
    private ImageView imageView;

    private Popup popup;

    @FXML
    public void initialize() {
        List<Book> listBook1 = BookController.findBookByKeyword("algebra");
        List<Book> listBook2 = BookController.findBookByKeyword("a");
        List<Book> listBook3 = BookController.findBookByKeyword("b");
            if (listBook1.isEmpty()) {
                System.out.println("Không tìm thấy sách nào.");
                return;
            }
            addBooksToRow(bookRow1, listBook1);
            addBooksToRow(bookRow2, listBook2);
            addBooksToRow(bookRow3, listBook3);

            popup = new Popup();
            popup.setAutoHide(true);
            VBox popupContent = new VBox(10);
            popupContent.getStyleClass().add("popup-content");

            Hyperlink registerLink = new Hyperlink("Create an account");
            registerLink.getStyleClass().add("popup-link");
            registerLink.setOnAction(this::handleOpenRegister);

            Hyperlink logoutLink = new Hyperlink("Logout");
            registerLink.getStyleClass().add("popup-link");
            logoutLink.setOnAction(this::handleOpenLogin);

            popupContent.getChildren().addAll(registerLink, logoutLink);
            popup.getContent().add(popupContent);
    }

    private void addBooksToRow(HBox row,List<Book> listBook) {
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        double screenWidth = screenBounds.getWidth();
        double screenHeight = screenBounds.getHeight();
        for (Book book : listBook) {
            VBox bookBox = new VBox();
            bookBox.setSpacing(5);
            bookBox.setAlignment(Pos.CENTER);

            ImageView bookImage = new ImageView();
            double maxWidth = screenWidth * 0.3;
            double maxHeight = screenHeight * 0.3 / 1.5;
            double fitWidth = Math.min(maxWidth, maxHeight * 1.5);
            bookImage.setFitWidth(fitWidth);
            bookImage.setFitHeight(fitWidth / 1.8);
            bookImage.setPreserveRatio(true);
            bookImage.setImage(new Image(book.getThumbnail()));

            String originalTitle = book.getTitle();
            Label bookTitle = new Label(originalTitle);
            bookTitle.setWrapText(true);
            bookTitle.setMaxWidth(fitWidth);
            bookTitle.getStyleClass().add("book-title");
            System.out.println(fitWidth + " " + originalTitle);
            if (originalTitle.length() > fitWidth/7.0) {
                bookTitle.setText(originalTitle.substring(0, (int) (fitWidth/7.0 - 3)) + "...");
            }
            bookBox.setOnMouseClicked(event -> handleBookClick(book));
            bookBox.getChildren().addAll(bookImage, bookTitle);
            row.getChildren().add(bookBox);
        }
    }
    @FXML
    private void handleBookClick(Book book) {
        try {
            String fxmlPath = "/views/viewbook.fxml";
            System.out.println("Đang tải FXML từ: " + fxmlPath);

            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            if (loader == null) {
                throw new IOException("Không tìm thấy file FXML tại đường dẫn: " + fxmlPath);
            }
            Parent root = loader.load();
            ViewBookController controller = loader.getController();
            controller.setBook1(book);

            // Thay thế nội dung của VBox hiện tại bằng nội dung của FXML mới
            contentVBox.getChildren().clear(); // Xóa nội dung hiện tại
            contentVBox.getChildren().add(root); // Thêm nội dung mới vào

        } catch (NullPointerException e) {
            System.err.println("Lỗi: FXMLLoader trả về null. Kiểm tra lại đường dẫn FXML.");
            e.printStackTrace();
        } catch (IOException e) {
            System.err.println("Lỗi khi tải tệp FXML: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Lỗi không mong muốn: " + e.getMessage());
            e.printStackTrace();
        }
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
