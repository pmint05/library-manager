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


  public static void main(String[] args) {
    addComment(new Comment("userId_example", "nhu mot con cho", "ccccc"));
    addComment(new Comment("cmmb", "aaaa", "defefefdfd"));
    System.out.println(getAllCommentOfBook("aaaa"));
    System.out.println(getAllCommentOfUser("userId_example"));
  }
}
