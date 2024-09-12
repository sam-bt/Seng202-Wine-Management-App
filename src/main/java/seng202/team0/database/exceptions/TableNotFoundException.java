package seng202.team0.database.exceptions;

public class TableNotFoundException extends Exception {

  public TableNotFoundException() {
  }

  public TableNotFoundException(String message) {
    super(message);
  }

  public TableNotFoundException(String message, Throwable cause) {
    super(message, cause);
  }

  public TableNotFoundException(Throwable cause) {
    super(cause);
  }
}
