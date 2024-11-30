package com.app.librarymanager.controllers;

import com.app.librarymanager.models.Comment;
import com.app.librarymanager.services.MongoDB;
import java.util.ArrayList;
import java.util.List;
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
    List<Document> documents = MongoDB.getInstance().findAllObject("comment", "bookId", bookId);
    if (documents == null) {
      return null;
    }
    List<Comment> comments = new ArrayList<>();
    documents.forEach(document -> {
      comments.add(new Comment(document));
    });
    return comments;
  }

  public static List<Comment> getAllCommentOfUser(String userId) {
    List<Document> documents = MongoDB.getInstance().findAllObject("comment", "userId", userId);
    if (documents == null) {
      return null;
    }
    List<Comment> comments = new ArrayList<>();
    documents.forEach(document -> {
      comments.add(new Comment(document));
    });
    return comments;
  }

  public static List<Document> getMostCommentedBooks(int start, int length) {
    return MongoDB.getInstance()
        .getAggregate("comment", List.of(
            new Document("$group", new Document("_id", "$bookId")
                .append("count", new Document("$sum", 1))),
            new Document("$sort", new Document("count", -1)),
            new Document("$skip", start),
            new Document("$limit", length)
        ));
  }

  public static void main(String[] args) {
    System.out.println(getMostCommentedBooks(0, 100000));
  }
}
