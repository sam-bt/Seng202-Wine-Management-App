package seng202.team6.model;

public enum AuthenticationResponse {
  MISSING_FIELDS("All fields must be filled."),
  MISMATCHING_CONFIRMED_PASSWORD("Passwords do not match."),
  INVALID_USERNAME("Invalid username, Please make sure that your name is between 3 and 15 characters long and only contains letters, numbers or underscores."),
  INVALID_PASSWORD("Invalid password, Please make sure that your password is between 3 and 15 characters long and only contains letters or numbers."),
  USERNAME_ALREADY_REGISTERED("The given username is already registered to an account."),
  REGISTER_SUCCESS(null),
  ;

  private final String message;


  AuthenticationResponse(String message) {
    this.message = message;
  }

  public String getMessage() {
    return message;
  }
}
