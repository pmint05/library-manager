package com.app.librarymanager.models;

import java.util.Date;
import org.bson.types.ObjectId;
import lombok.Data;

@Data
public class BookLoan {

  private ObjectId _id;
  private String userId;
  private String bookId;
  private Date borrowDate;
  private Date dueDate;

  public BookLoan() {
    _id = null;
    userId = null;
    bookId = null;
    borrowDate = null;
    dueDate = null;
  }

  public BookLoan(String userId, String bookId) {
    this.userId = userId;
    this._id = null;
    this.userId = userId;
    this.bookId = bookId;
    this.borrowDate = null;
    this.dueDate = null;
  }

  public BookLoan(String userId, String bookId, Date borrowDate, Date dueDate) {
    this._id = null;
    this.userId = userId;
    this.bookId = bookId;
    this.borrowDate = borrowDate;
    this.dueDate = dueDate;
  }

//  /**
//   * Constructor with date like "dd/mm/yyyy"
//   *
//   * @param userId     user id
//   * @param bookId     book id
//   * @param borrowDate borrow date
//   * @param dueDate    excepted return date
//   */
//  public BookLoan(String userId, String bookId, String borrowDate, String dueDate) {
//    this._id = _id;
//    this.userId = userId;
//    this.bookId = bookId;
//    this.
//  }
}
