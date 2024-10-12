package seng202.team6.util.Exceptions;

/**
 * Thrown when validation for an item has failed.
 */
public class ValidationException extends Exception {

  /**
   * Constructor.
   */
  public ValidationException() {
    super();
  }

  /**
   * Constructor.
   *
   * @param message error message
   */
  public ValidationException(String message) {
    super(message);
  }

  /**
   * Constructor.
   *
   * @param message error message
   * @param cause   cause
   */
  public ValidationException(String message, Throwable cause) {
    super(message, cause);
  }

  /**
   * Constructor.
   *
   * @param cause cause
   */
  public ValidationException(Throwable cause) {
    super(cause);
  }
}
