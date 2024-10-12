package seng202.team6.enums;


/**
 * Enum representing various authentication responses in the system. Each enum constant corresponds
 * to a specific authentication scenario and contains an associated message.
 */
public enum AuthenticationResponse {
  /**
   * Indicates that required fields are missing.
   */
  MISSING_FIELDS("All fields must be filled."),

  /**
   * Indicates that the username field is empty.
   */
  MISSING_USERNAME_FIELD("Please enter a username"),

  /**
   * Indicates that the password field is empty.
   */
  MISSING_PASSWORD_FIELD("Please enter a password"),


  /**
   * Indicates that the confirmed password does not match the original password.
   */
  MISMATCHING_CONFIRMED_PASSWORD("Passwords do not match."),

  /**
   * Indicates that the provided username is invalid.
   */
  INVALID_USERNAME(
      "Invalid username, Please make sure that your name is between 3 and"
          + " 15 characters long and only contains letters, numbers or underscores."),
  /**
   * Indicates the provided username has an invalid length.
   */
  INVALID_USERNAME_LENGTH("Username must be between 3 and 15 characters long"),

  /**
   * Indicates the provided username has a special character or space
   */
  INVALID_USERNAME_SYMBOL("Username cannot contain special characters or spaces"),
  /**
   * Indicates that the provided password is invalid.
   */
  INVALID_PASSWORD(
      "Password must be between 8 and 30 characters long. Password must contain a "
          + "lowercase letter, an uppercase letter, a number and a special character."),
  /**
   * Indicates that the provided password has an invalid length.
   */
  INVALID_PASSWORD_LENGTH("Password must be between 8 and 30 characters long"),

  /**
   * Indicates that the provided password has a space.
   */
  INVALID_PASSWORD_CONTAINS_SPACES("Password cannot contain spaces"),

  /**
   * Indicates that the provided password is missing a symbol.
   */
  INVALID_PASSWORD_HEADER("Missing a "),

  /**
   * Indicates that the provided password is missing a lowercase
   */
  INVALID_PASSWORD_MISSING_LOWERCASE("lowercase, "),
  /**
   * Indicates that the provided password is missing an uppercase,
   */
  INVALID_PASSWORD_MISSING_UPPERCASE("uppercase, "),

  /**
   * Indicates that the provided password is missing a number.
   */
  INVALID_PASSWORD_MISSING_NUMBER("number, "),

  /**
   * Indicates that the provided password is missing a special character.
   */

  INVALID_PASSWORD_MISSING_SPECIAL_CHAR("special character"),

  /**
   * Indicates that the given username is already registered.
   */
  USERNAME_ALREADY_REGISTERED("Username is already in use"),

  /**
   * Indicates a successful registration.
   */
  REGISTER_SUCCESS(null),

  /**
   * Indicates an invalid username and password combination.
   */
  INVALID_USERNAME_PASSWORD_COMBINATION("Username or Password is Incorrect."),

  /**
   * Indicates that the username is invalid.
   */
  INVALID_LOGIN_USERNAME("User cannot be found"),
  /**
   * Indicates that the password is invalid.
   */
  INVALID_LOGIN_PASSWORD("Incorrect password"),
  /**
   * Indicates a successful login.
   */
  LOGIN_SUCCESS(null),

  /**
   * Indicates the username is valid.
   */
  VALID_LOGIN_USERNAME(null),

  /**
   * Indicates that the old password provided is incorrect.
   */
  INCORRECT_OLD_PASSWORD("The old password is incorrect."),

  /**
   * Indicates that the admin password cannot be set to "admin".
   */
  ADMIN_PASSWORD_CANNOT_BE_ADMIN("The admin password cannot be admin."),

  /**
   * Indicates that the new password is the same as the old password.
   */
  OLD_PASSWORD_SAME_AS_NEW("New password cannot be same as old password."),

  /**
   * Indicates a successful password change.
   */
  PASSWORD_CHANGED_SUCCESS(null),

  /**
   * Indicates a successful logout.
   */
  LOGOUT_SUCCESS(null),

  /**
   * Indicates an unexpected error occurred.
   */
  UNEXPECTED_ERROR("An unexpected error occurred. Please try again."),

  SAME_AS_USERNAME("Password cannot be the same as username"),

  /**
   * Password constraints.
   */
  PASSWORD_CONSTRAINTS(
      "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&*.()\\-+={\\[\\]}])"
          + "[A-Za-z0-9!@#$%^&*.()\\-+={\\[\\]}]{8,30}$");


  /**
   * The message associated with the authentication response.
   */
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