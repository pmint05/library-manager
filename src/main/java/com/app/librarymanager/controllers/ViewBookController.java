//
//package com.app.librarymanager.controllers;
//
//import com.app.librarymanager.models.Book;
//import javafx.beans.binding.Bindings;
//import javafx.event.ActionEvent;
//import javafx.fxml.FXML;
//import javafx.geometry.Bounds;
//import javafx.geometry.Pos;
//import javafx.geometry.Rectangle2D;
//import javafx.scene.Scene;
//import javafx.scene.control.Hyperlink;
//import javafx.scene.control.Label;
//import javafx.scene.control.TextField;
//import javafx.scene.image.Image;
//import javafx.scene.image.ImageView;
//import javafx.scene.layout.HBox;
//import javafx.scene.layout.VBox;
//import javafx.stage.Popup;
//import javafx.stage.Screen;
//import org.jetbrains.annotations.NotNull;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class ViewBookController {
//    @FXML
//    private Popup popup;
//    @FXML
//    private ImageView imageView;
//    @FXML
//    private Label titleLabel;
//    @FXML
//    private Label authorLabel;
//    @FXML
//    private Label releaseDateLabel;
//    @FXML
//    private Label pageCountLabel;
//    @FXML
//    private Label price;
//    @FXML
//    private ImageView bookCoverImage;
//
//    private Book book;
//    private List<Book> listBook = BookController.findBookByKeyword("");
//
////    private void updateView() {
////        titleLabel.setText(book.getTitle());
////        System.out.println(book.get_id());
////        System.out.println(book.getPrice());
////        System.out.println(book.getISBN());
////        System.out.println(book.getAuthors());
////        System.out.println(book.getId());
////        System.out.println(book.getTitle());
////        authorLabel.setText("Tác giả: " + book.getAuthors());
////        releaseDateLabel.setText("Ngày phát hành: " + book.getPublishedDate());
////        pageCountLabel.setText(book.getPageCount() + " trang");
//////        price.setText(String.format("%.0fđ", book.getPrice()));
//////        bookCoverImage.setImage(new Image(getClass().getResourceAsStream(book.getThumbnail())));
////    }
//
//    @FXML
//    public void initialize() {
//        book = BookController.findBookByID(listBook.get(0).getId());
//        System.out.println(book.get_id());
//        System.out.println(book.getPrice());
//        System.out.println(book.getISBN());
//        System.out.println(book.getAuthors());
//        System.out.println(book.getId());
//        System.out.println(book.getTitle());
////        updateView();
//        popup = new Popup();
//        popup.setAutoHide(true);
//        VBox popupContent = new VBox(10);
//        popupContent.getStyleClass().add("popup-content");
//
//        Hyperlink registerLink = new Hyperlink("Create an account");
//        registerLink.getStyleClass().add("popup-link");
//        registerLink.setOnAction(this::handleOpenRegister);
//
//
//        Hyperlink logoutLink = new Hyperlink("Logout");
//        registerLink.getStyleClass().add("popup-link");
//        logoutLink.setOnAction(this::handleOpenLogin);
//
//        popupContent.getChildren().addAll(registerLink, logoutLink);
//        popup.getContent().add(popupContent);
//    }
//
//    @FXML
//    public void handleImageClick() {
//        if (popup.isShowing()) {
//            popup.hide();
//        } else {
//            Bounds boundsInScreen = imageView.localToScreen(imageView.getBoundsInLocal());
//            popup.setOnShown(event -> {
//                double popupX = boundsInScreen.getMinX() + (boundsInScreen.getWidth()) - (popup.getWidth());
//                double popupY = boundsInScreen.getMaxY();
//                popup.setX(popupX);
//                popup.setY(popupY);
//            });
//            popup.show(imageView, popup.getX(), popup.getY());
//        }
//    }
//    public void handleOpenRegister(ActionEvent event) {
//        System.out.println("Create an account clicked");
//        popup.hide();
//    }
//
//    public void handleOpenLogin(ActionEvent event) {
//        System.out.println("Logout clicked");
//        popup.hide();
//    }
//
//    public void getImageView() {
//        ImageView bookImage = new ImageView();
//        bookImage.setPreserveRatio(true);
//
//        bookImage.setImage(new Image(book.getThumbnail()));
//        String originalTitle = book.getTitle();
//        Label bookTitle = new Label(originalTitle);
//    }
//
//    @FXML
//    public void handleSearch() {
//
//    }
//}
package com.app.librarymanager.controllers;

import com.app.librarymanager.models.Book;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Screen;

import java.awt.*;
import java.net.URI;
import java.util.List;

public class ViewBookController {
    @FXML
    public Label descriptionlabel;
    @FXML
    private Label titleLabel;
    @FXML
    private Label authorLabel;
    @FXML
    private Label publisherLabel;
    @FXML
    private ImageView bookCoverImage;
    @FXML
    private Label pageCountLabel;
    @FXML
    private Label price;
    @FXML
    private Label discountPriceLabel;
    @FXML
    private Button freeSampleButton;
    @FXML
    private Button readBookButton;
    @FXML
    private Button favoriteButton;

    private Book book; // Sách hiện tại
    private List<Book> booksList; // Danh sách sách

    @FXML
    public void initialize() {
        try {
            booksList = BookController.findBookByKeyword("");
            if (booksList != null && !booksList.isEmpty()) {
                for (Book bk : booksList) {
                    if (bk.getPdfLink() != "N/A") {
                        book = bk;
                        System.out.println(book.getPdfLink());
                        updateBookDetails(book);
                    }
                }
                System.out.println(book.getPdfLink());
            } else {
                System.err.println("Danh sách sách trống.");
                showError("Không tìm thấy sách nào trong danh sách.");
            }
            setupEventHandlers();
        } catch (Exception e) {
            System.err.println("Lỗi khi khởi tạo ViewBookController: " + e.getMessage());
            e.printStackTrace();
            showError("Đã xảy ra lỗi trong quá trình khởi tạo.");
        }
        double screenHeight = Screen.getPrimary().getVisualBounds().getHeight();
        bookCoverImage.setFitHeight(screenHeight * 2 / 3);
        bookCoverImage.setPreserveRatio(true);
        readBookButton.setOnAction(event -> openBookPDF(book.getPdfLink()));
    }

    private void updateBookDetails(Book book) {
        try {
            if (book == null) {
                throw new IllegalArgumentException("Sách hiện tại không hợp lệ.");
            }

            titleLabel.setText(book.getTitle() != null ? book.getTitle() + ", " + book.getPublishedDate()  : "Không có tiêu đề");
            authorLabel.setText(book.getAuthors() != null ? "by " + book.getAuthors() : "Tác giả không xác định");
            publisherLabel.setText(book.getPublishedDate() != null ? "Nhà xuất bản: " + book.getPublisher() : "Không rõ nhà xuất bản");
            pageCountLabel.setText(book.getPageCount() > 0 ? book.getPageCount() + " trang" : "Không rõ số trang");
            price.setText(String.format("%.0fđ", book.getPrice() > 0 ? book.getPrice() : 0.0));
            discountPriceLabel.setText("Không có giảm giá");
            descriptionlabel.setText(book.getDescription() != null ? book.getDescription() : "Không có mô tả");

            if (book.getThumbnail() != null && !book.getThumbnail().isEmpty()) {
                bookCoverImage.setImage(new Image(book.getThumbnail()));
            } else {
                System.err.println("Không tìm thấy ảnh bìa.");
                bookCoverImage.setImage(null); // Hoặc đặt ảnh mặc định
            }
        } catch (IllegalArgumentException e) {
            System.err.println("Lỗi thông tin sách: " + e.getMessage());
            showError("Thông tin sách không hợp lệ: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Lỗi khi cập nhật thông tin sách: " + e.getMessage());
            e.printStackTrace();
            showError("Đã xảy ra lỗi khi hiển thị thông tin sách.");
        }
    }

    private void setupEventHandlers() {
        try {
            favoriteButton.setOnAction(event -> handleAddToFavorites());
        } catch (Exception e) {
            System.err.println("Lỗi khi cài đặt sự kiện: " + e.getMessage());
            e.printStackTrace();
            showError("Không thể cài đặt sự kiện cho các nút.");
        }
    }

    private void openBookPDF(String pdfLink) {
        if (pdfLink == null || pdfLink.isEmpty()) {
            System.err.println("Không tìm thấy liên kết PDF.");
            return;
        }
        try {
            Desktop desktop = Desktop.getDesktop();
            if (Desktop.isDesktopSupported() && desktop.isSupported(Desktop.Action.BROWSE)) {
                desktop.browse(new URI(pdfLink));
            } else {
                System.err.println("Mở trình duyệt không được hỗ trợ trên hệ thống này.");
            }
        } catch (Exception e) {
            System.err.println("Lỗi khi mở tệp PDF: " + e.getMessage());
        }
    }

    private void handleAddToFavorites() {
        try {
            if (book == null) {
                throw new IllegalStateException("Không có sách nào được chọn để thêm vào danh sách yêu thích.");
            }
            System.out.println("Thêm sách vào danh sách yêu thích: " + book.getTitle());
            // Logic để thêm vào danh sách yêu thích
        } catch (IllegalStateException e) {
            System.err.println(e.getMessage());
            showError(e.getMessage());
        } catch (Exception e) {
            System.err.println("Lỗi khi thêm sách vào danh sách yêu thích: " + e.getMessage());
            e.printStackTrace();
            showError("Không thể thêm sách vào danh sách yêu thích.");
        }
    }

    public void setBook1(Book books) {
        book = books;
    }

    private void showError(String message) {
        System.err.println("Thông báo lỗi: " + message);
    }

    public void handleSearch(MouseEvent mouseEvent) {
    }

    public void handleImageClick(MouseEvent mouseEvent) {
    }
}
