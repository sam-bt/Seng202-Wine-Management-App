/**
 * Enum representing various authentication responses in the system.
 * Each enum constant corresponds to a specific authentication scenario
 * and contains an associated message.
 */
package seng202.team6.model;

public enum AuthenticationResponse {
  /** Indicates that required fields are missing. */
  MISSING_FIELDS("All fields must be filled."),

  /** Indicates that the confirmed password does not match the original password. */
  MISMATCHING_CONFIRMED_PASSWORD("Passwords do not match."),

  /** Indicates that the provided username is invalid. */
  INVALID_USERNAME("Invalid username, Please make sure that your name is between 3 and 15 characters long and only contains letters, numbers or underscores."),

  /** Indicates that the provided password is invalid. */
  INVALID_PASSWORD("Invalid password, Please make sure that your password is between 3 and 15 characters long and only contains letters or numbers."),

  /** Indicates that the given username is already registered. */
  USERNAME_ALREADY_REGISTERED("The given username is already registered to an account."),

  /** Indicates a successful registration. */
  REGISTER_SUCCESS(null),

  /** Indicates an invalid username and password combination. */
  INVALID_USERNAME_PASSWORD_COMBINATION("Username or Password is Incorrect."),

  /** Indicates a successful login. */
  LOGIN_SUCCESS(null),

  /** Indicates that the old password provided is incorrect. */
  INCORRECT_OLD_PASSWORD("The old password is incorrect."),

  /** Indicates that the admin password cannot be set to "admin". */
  ADMIN_PASSWORD_CANNOT_BE_ADMIN("The admin password cannot be admin."),

  /** Indicates that the new password is the same as the old password. */
  OLD_PASSWORD_SAME_AS_NEW("New password cannot be same as old password."),

  /** Indicates a successful password change. */
  PASSWORD_CHANGED_SUCCESS(null),

  /** Indicates a successful logout. */
  LOGOUT_SUCCESS(null),

  /** Indicates an unexpected error occurred. */
  UNEXPECTED_ERROR("An unexpected error occurred. Please try again.");

  /** The message associated with the authentication response. */
  private final String message;

  /**
   * Constructs an AuthenticationResponse with the specified message.
   *
   * @param message The message associated with the authentication response.
   */
  AuthenticationResponse(String message) {
    this.message = message;
  }

  /**
   * Returns the message associated with the authentication response.
   *
   * @return The message string.
   */
  public String getMessage() {
    return message;
  }
}