package com.app.librarymanager.models;

import com.app.librarymanager.utils.DateUtil;
import java.time.LocalDate;
import java.util.Date;
import lombok.EqualsAndHashCode;
import org.bson.Document;
import org.bson.types.ObjectId;
import lombok.Data;

@EqualsAndHashCode(callSuper = true)
@Data
public class BookLoan extends BookUser {

  private Date borrowDate;
  private Date dueDate;
  private boolean valid;

  public BookLoan() {
    super();
    borrowDate = null;
    dueDate = null;
    valid = false;
  }

  public BookLoan(String userId, String bookId) {
    super(userId, bookId);
    this.borrowDate = null;
    this.dueDate = null;
    valid = false;
  }

  public BookLoan(String userId, String bookId, Date borrowDate, Date dueDate) {
    super(userId, bookId);
    this.borrowDate = borrowDate;
    this.dueDate = dueDate;
    valid = true;
  }

  public BookLoan(String userId, String bookId, LocalDate borrowDate, LocalDate dueDate) {
    super(userId, bookId);
    this.borrowDate = DateUtil.localDateToDate(borrowDate);
    this.dueDate = DateUtil.localDateToDate(dueDate);
    valid = true;
  }

  /**
   * Constructor with date like "dd/mm/yyyy"
   *
   * @param userId     user id
   * @param bookId     book id
   * @param borrowDate borrow date
   * @param dueDate    excepted return date
   */
  public BookLoan(String userId, String bookId, String borrowDate, String dueDate) {
    super(userId, bookId);
    this.borrowDate = DateUtil.localDateToDate(DateUtil.parse(borrowDate));
    this.dueDate = DateUtil.localDateToDate(DateUtil.parse(dueDate));
    valid = true;
  }

  public BookLoan(Document document) {
    super(document);
    this.borrowDate = document.getDate("borrowDate");
    this.dueDate = document.getDate("dueDate");
    this.valid = document.getBoolean("valid");
  }
}
