package seng202.team6.util.exceptions;

/**
 * Thrown when validation for an item has failed.
 */
public class ValidationException extends Exception {

  /**
   * Constructor.
   *
   * @param message error message
   * @param cause   cause
   */
  public ValidationException(String message, Throwable cause) {
    super(message, cause);
  }

}
