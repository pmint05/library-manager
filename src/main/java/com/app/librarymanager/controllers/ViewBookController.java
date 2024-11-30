package com.app.librarymanager.controllers;

import com.app.librarymanager.models.Book;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Screen;
import java.awt.Desktop;
import java.net.URI;
import java.util.List;

public class ViewBookController {

    @FXML
    private ImageView bookCoverImage;
    @FXML
    private Label titleLabel;
    @FXML
    private Label authorLabel;
    @FXML
    private Label publisherLabel;
    @FXML
    private Label pageCountLabel;
    @FXML
    private Label price;
    @FXML
    private Button readBookButton;
    @FXML
    private Button favoriteButton;
    @FXML
    private Label descriptionlabel;

    private Book book;

    @FXML
    public void initialize() {
        List<Book> listBook = BookController.findBookByKeyword("Programming");
        if (!listBook.isEmpty()) {
            book = listBook.get(0);
            updateBookDetails(book);
        } else {
            System.out.println("Không tìm thấy sách.");
        }

        setupImageViewBinding(bookCoverImage);
        setupEventHandlers();
    }

    private void setupImageViewBinding(ImageView imageView) {
        imageView.sceneProperty().addListener((observable, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.windowProperty().addListener((obs, oldWindow, newWindow) -> {
                    if (newWindow != null) {
                        newWindow.widthProperty().addListener((obsWidth, oldWidth, newWidth) -> adjustImageViewSize(imageView));
                        newWindow.heightProperty().addListener((obsHeight, oldHeight, newHeight) -> adjustImageViewSize(imageView));
                    }
                });
            }
        });
        adjustImageViewSize(imageView);
    }

    private void adjustImageViewSize(ImageView imageView) {
        double screenHeight = imageView.getScene() != null ? imageView.getScene().getWindow().getHeight() : Screen.getPrimary().getVisualBounds().getHeight();
        imageView.setFitHeight(screenHeight * 3 / 4);
        imageView.setPreserveRatio(true);
    }

    private void updateBookDetails(Book book) {
        if (book == null) {
            System.err.println("Sách không hợp lệ.");
            return;
        }

        try {
            titleLabel.setText(book.getTitle() != null ? book.getTitle() : "Không có tiêu đề");
            authorLabel.setText(book.getAuthors() != null ? "by " + book.getAuthors() : "Tác giả không xác định");
            publisherLabel.setText(book.getPublisher() != null ? "Nhà xuất bản: " + book.getPublisher() : "Không rõ nhà xuất bản");
            pageCountLabel.setText(book.getPageCount() > 0 ? book.getPageCount() + " trang" : "Không rõ số trang");
            price.setText(String.format("%.0fđ", book.getPrice() > 0 ? book.getPrice() : 0.0));
//            descriptionlabel.setText(book.getDescription() != null ? book.getDescription() : "Không có mô tả");

            String fullDescription = book.getDescription() != null ? book.getDescription() : "Không có mô tả";
            if (fullDescription.length() > 1000) {
                String shortDescription = fullDescription.substring(0, 1000);
                Text shortText = new Text(shortDescription + "... ");
                Hyperlink seeMoreLink = new Hyperlink("Xem thêm");
                seeMoreLink.setOnAction(event -> showMoreDescription(fullDescription, shortDescription, seeMoreLink));

                TextFlow textFlow = new TextFlow(shortText, seeMoreLink);
                descriptionlabel.setGraphic(textFlow);
            } else {
                descriptionlabel.setText(fullDescription);
                descriptionlabel.setGraphic(null);
            }

            if (book.getThumbnail() != null && !book.getThumbnail().isEmpty()) {
                bookCoverImage.setImage(new Image(book.getThumbnail()));
            } else {
                System.err.println("Không tìm thấy ảnh bìa.");
                bookCoverImage.setImage(new Image("https://example.com/default-cover.png"));
            }
        } catch (Exception e) {
            System.err.println("Lỗi khi cập nhật thông tin sách: " + e.getMessage());
        }
    }

    private void showMoreDescription(String fullDescription, String shortDescription, Hyperlink seeMoreLink) {
        if (seeMoreLink != null) {
            Text fullText = new Text(fullDescription.substring(shortDescription.length()));
            seeMoreLink.setText("Thu gọn");
            seeMoreLink.setOnAction(event -> collapseDescription(fullDescription, seeMoreLink));

            TextFlow textFlow = (TextFlow) descriptionlabel.getGraphic();
            textFlow.getChildren().setAll(new Text(fullDescription), seeMoreLink);
        }
    }

    private void collapseDescription(String fullDescription, Hyperlink seeMoreLink) {
        String shortDescription = fullDescription.substring(0, 1000);
        Text shortText = new Text(shortDescription + " ");
        seeMoreLink.setText("Xem thêm");
        seeMoreLink.setOnAction(event -> showMoreDescription(fullDescription, shortDescription, seeMoreLink));

        TextFlow textFlow = (TextFlow) descriptionlabel.getGraphic();
        textFlow.getChildren().setAll(shortText, seeMoreLink);
    }

    private void setupEventHandlers() {
        readBookButton.setOnAction(event -> {
            if (book != null && book.getPdfLink() != null && !book.getPdfLink().isEmpty()) {
                openBookPDF(book.getPdfLink());
            } else {
                System.err.println("Liên kết PDF không hợp lệ.");
            }
        });

        favoriteButton.setOnAction(event -> handleAddToFavorites());
    }

    private void openBookPDF(String pdfLink) {
        try {
            Desktop desktop = Desktop.getDesktop();
            if (Desktop.isDesktopSupported() && desktop.isSupported(Desktop.Action.BROWSE)) {
                desktop.browse(new URI(pdfLink));
            } else {
                System.err.println("Không hỗ trợ mở trình duyệt trên hệ thống này.");
            }
        } catch (Exception e) {
            System.err.println("Lỗi khi mở tệp PDF: " + e.getMessage());
        }
    }

    private void handleAddToFavorites() {
        if (book == null) {
            System.err.println("Không có sách nào được chọn.");
            return;
        }
        System.out.println("Thêm sách vào danh sách yêu thích: " + book.getTitle());
    }

    public void openBook(Book books) {
        System.out.println("setBook1");
        book = books;
        updateBookDetails(book);
        setupImageViewBinding(bookCoverImage);
        setupEventHandlers();
//        double screenHeight = Screen.getPrimary().getVisualBounds().getHeight();
//        bookCoverImage.setFitHeight(screenHeight * 2 / 3);
//        bookCoverImage.setPreserveRatio(true);
        readBookButton.setOnAction(event -> openBookPDF(book.getPdfLink()));
    }
}
