package com.app.librarymanager.models;

import com.app.librarymanager.utils.DateUtil;
import java.time.LocalDate;
import java.util.Date;
import org.bson.Document;

public class BookLoanOffline extends BookLoan {

  private int numCopies;

  public BookLoanOffline() {
    super();
    this.numCopies = 0;
  }

  public BookLoanOffline(String userId, String bookId) {
    super(userId, bookId);
    this.numCopies = 0;
  }

  public BookLoanOffline(String userId, String bookId, Date borrowDate, Date dueDate) {
    super(userId, bookId, borrowDate, dueDate);
    this.numCopies = 0;
  }

  public BookLoanOffline(String userId, String bookId, LocalDate borrowDate, LocalDate dueDate) {
    super(userId, bookId, borrowDate, dueDate);
    this.numCopies = 0;
  }

  /**
   * Constructor with date like "dd/mm/yyyy"
   *
   * @param userId     user id
   * @param bookId     book id
   * @param borrowDate borrow date
   * @param dueDate    excepted return date
   */
  public BookLoanOffline(String userId, String bookId, String borrowDate, String dueDate) {
    super(userId, bookId, borrowDate, dueDate);
    this.numCopies = 0;
  }

  public BookLoanOffline(String userId, String bookId, Date borrowDate, Date dueDate,
      int numCopies) {
    super(userId, bookId, borrowDate, dueDate);
    this.numCopies = numCopies;
  }

  public BookLoanOffline(String userId, String bookId, LocalDate borrowDate, LocalDate dueDate,
      int numCopies) {
    super(userId, bookId, borrowDate, dueDate);
    this.numCopies = numCopies;
  }

  /**
   * Constructor with date like "dd/mm/yyyy"
   *
   * @param userId     user id
   * @param bookId     book id
   * @param borrowDate borrow date
   * @param dueDate    excepted return date
   * @param numCopies  number of borrowed books
   */
  public BookLoanOffline(String userId, String bookId, String borrowDate, String dueDate,
      int numCopies) {
    super(userId, bookId, borrowDate, dueDate);
    this.numCopies = numCopies;
  }

  public BookLoanOffline(Document document) {
    super(document);
    this.numCopies = document.getInteger("numCopies");
  }
}
