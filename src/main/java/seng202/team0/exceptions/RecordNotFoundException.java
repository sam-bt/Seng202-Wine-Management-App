package seng202.team0.exceptions;

/**
 * Thrown when a record is not found in the database
 * @author Angus McDougall
 */
public class RecordNotFoundException extends Exception {

  public RecordNotFoundException() {
  }

  public RecordNotFoundException(String message) {
    super(message);
  }

  public RecordNotFoundException(String message, Throwable cause) {
    super(message, cause);
  }

  public RecordNotFoundException(Throwable cause) {
    super(cause);
  }
}
