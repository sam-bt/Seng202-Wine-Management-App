package seng202.team0.exceptions;

public class DuplicateTableException extends Exception {

  public DuplicateTableException() {
  }

  public DuplicateTableException(String message) {
    super(message);
  }

  public DuplicateTableException(String message, Throwable cause) {
    super(message, cause);
  }

  public DuplicateTableException(Throwable cause) {
    super(cause);
  }
}
