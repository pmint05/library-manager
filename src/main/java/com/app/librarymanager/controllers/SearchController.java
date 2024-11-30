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
import java.net.URL;
import java.util.List;
import java.util.Objects;

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

        addBooksToRow(bookRow1, listBook1);
        addBooksToRow(bookRow2, listBook2);
        addBooksToRow(bookRow3, listBook3);

        initializePopup();
    }

    private void initializePopup() {
        popup = new Popup();
        popup.setAutoHide(true);
        VBox popupContent = new VBox(10);
        popupContent.getStyleClass().add("popup-content");

        Hyperlink registerLink = new Hyperlink("Create an account");
        registerLink.getStyleClass().add("popup-link");
        registerLink.setOnAction(this::handleOpenRegister);

        Hyperlink logoutLink = new Hyperlink("Logout");
        logoutLink.getStyleClass().add("popup-link");
        logoutLink.setOnAction(this::handleOpenLogin);

        popupContent.getChildren().addAll(registerLink, logoutLink);
        popup.getContent().add(popupContent);
    }

    private void addBooksToRow(HBox row, List<Book> listBook) {
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        double screenWidth = screenBounds.getWidth();
        double screenHeight = screenBounds.getHeight();

        for (Book book : listBook) {
            try {
                if (book.getThumbnail() == null || book.getThumbnail().isEmpty()) {
                    System.out.println("Book thumbnail is missing.");
                    continue;
                }
                if (book.getTitle() == null || book.getTitle().isEmpty()) {
                    System.out.println("Book title is missing.");
                    continue;
                }

                VBox bookBox = createBookBox(book, screenWidth, screenHeight);
                row.getChildren().add(bookBox);

            } catch (Exception e) {
                System.err.println("Lỗi khi thêm sách '" + (book.getTitle() != null ? book.getTitle() : "null") + "' vào hàng: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private VBox createBookBox(Book book, double screenWidth, double screenHeight) {
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
        try {
            String image = book.getThumbnail();
            if (Objects.equals(image, "N/A") || image == null) {
                URL defaultImageUrl = getClass().getResource("/images/book.jpg");
                if (defaultImageUrl == null) {
                    throw new IOException("Không tìm thấy hình ảnh mặc định: /images/book.jpg");
                }
                bookImage.setImage(new Image(defaultImageUrl.toExternalForm()));
            } else {
                bookImage.setImage(new Image(image, true));
            }
        } catch (Exception e) {
            bookImage.setImage(new Image(Objects.requireNonNull(getClass().getResource("/images/book.jpg")).toExternalForm()));
            System.err.println("Lỗi khi tải hình ảnh cho sách '" + book.getTitle() + "': " + e.getMessage());
        }

        String originalTitle = book.getTitle();
        Label bookTitle = new Label(originalTitle);
        bookTitle.setWrapText(true);
        bookTitle.setMaxWidth(fitWidth);
        bookTitle.getStyleClass().add("book-title");

        if (originalTitle.length() > fitWidth / 7.0) {
            bookTitle.setText(originalTitle.substring(0, (int) (fitWidth / 7.0 - 3)) + "...");
        }

        bookBox.setOnMouseClicked(event -> handleBookClick(book));
        bookBox.getChildren().addAll(bookImage, bookTitle);

        return bookBox;
    }

    @FXML
    private void handleBookClick(Book book) {
        try {
            String fxmlPath = "/views/viewbook.fxml";
            URL fxmlUrl = getClass().getResource(fxmlPath);
            if (fxmlUrl == null) {
                throw new IOException("Không tìm thấy file FXML tại đường dẫn: " + fxmlPath);
            }

            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            Parent root = loader.load();
            ViewBookController controller = loader.getController();
            controller.openBook(book);

                contentVBox.getChildren().clear();
            contentVBox.getChildren().add(root);

        } catch (IOException e) {
            System.err.println("Lỗi khi tải FXML: " + e.getMessage());
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
        try {
            String keyword = searchField.getText().trim();
            if (keyword.isEmpty()) {
                throw new IllegalArgumentException("Từ khóa tìm kiếm không được để trống.");
            }
            List<Book> searchResults = BookController.findBookByKeyword(keyword);
            if (searchResults.isEmpty()) {
                System.out.println("Không tìm thấy sách nào cho từ khóa: " + keyword);
            } else {
                bookRow1.getChildren().clear();
                addBooksToRow(bookRow1, searchResults);
            }
        } catch (Exception e) {
            System.err.println("Lỗi khi tìm kiếm: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
