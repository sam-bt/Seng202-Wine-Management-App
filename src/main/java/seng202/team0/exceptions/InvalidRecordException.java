package seng202.team0.exceptions;

/**
 * This class is thrown when an invalid record is attempted to be inserted into a table
 *
 * @author Angus McDougall
 */
public class InvalidRecordException extends RuntimeException {

  public InvalidRecordException(Throwable cause) {
    super(cause);
  }

  public InvalidRecordException(String message, Throwable cause) {
    super(message, cause);
  }

  public InvalidRecordException(String message) {
    super(message);
  }

  public InvalidRecordException() {
  }
}
