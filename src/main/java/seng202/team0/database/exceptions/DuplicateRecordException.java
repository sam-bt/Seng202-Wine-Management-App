package seng202.team0.database.exceptions;

/**
 * Thrown when there is a duplicate record in the database
 * @author Angus McDougall
 */
public class DuplicateRecordException extends Exception {

  public DuplicateRecordException() {
  }

  public DuplicateRecordException(String message) {
    super(message);
  }

  public DuplicateRecordException(String message, Throwable cause) {
    super(message, cause);
  }

  public DuplicateRecordException(Throwable cause) {
    super(cause);
  }
}
