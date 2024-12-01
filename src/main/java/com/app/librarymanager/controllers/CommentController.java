package com.app.librarymanager.controllers;

import com.app.librarymanager.controllers.BookLoanController.ReturnBookLoan;
import com.app.librarymanager.models.BookLoan;
import com.app.librarymanager.models.Comment;
import com.app.librarymanager.services.MongoDB;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.Data;
import org.bson.Document;

@Data
public class CommentController {

  public static Document addComment(Comment comment) {
    return MongoDB.getInstance().addToCollection("comment", MongoDB.objectToMap(comment));
  }

  public static boolean removeComment(Comment comment) {
    return MongoDB.getInstance().deleteFromCollection("comment", "_id", comment.get_id());
  }

  public static Document editComment(Comment comment) {
    return MongoDB.getInstance()
        .updateData("comment", "_id", comment.get_id(), MongoDB.objectToMap(comment));
  }

  public static List<Comment> getAllCommentOfBook(String bookId) {
    return MongoDB.getInstance().findAllObject("comment", "bookId", bookId).stream()
        .map(Comment::new).toList();
  }

  public static List<Comment> getAllCommentOfUser(String userId) {
    return MongoDB.getInstance().findAllObject("comment", "userId", userId).stream()
        .map(Comment::new).toList();
  }

  @Data
  public static class ReturnComment {

    String bookTitle;
    String bookThumbnail;
    int numComment;

    public ReturnComment(String bookTitle, String bookThumbnail, int numComment) {
      this.bookTitle = bookTitle;
      this.bookThumbnail = bookThumbnail;
      this.numComment = numComment;
    }
  }

  public static List<ReturnComment> getMostCommentedBooks(int start, int length) {
    try {
      List<Document> documents = MongoDB.getInstance().getAggregate("comment", List.of(
          new Document("$group",
              new Document("_id", "$bookId").append("count", new Document("$sum", 1))),
          new Document("$sort", new Document("count", -1)), new Document("$skip", start),
          new Document("$limit", length)));
      Map<String, Document> bookDocs = BookController.findBookByListID(
              documents.stream().map(doc -> doc.getString("_id")).toList()).stream()
          .collect(Collectors.toMap(doc -> doc.getString("id"), doc -> doc));
      return documents.stream().map(doc -> {
        Document bookDoc = bookDocs.get(doc.getString("_id"));
        return new ReturnComment(bookDoc.getString("id"), bookDoc.getString("thumbnail"),
            doc.getInteger("count"));
      }).toList();
    } catch (Exception e) {
      System.out.println(e.getMessage());
      return null;
    }
  }

  public static void main(String[] args) {
    System.out.println(getMostCommentedBooks(0, 100000));
  }
}
