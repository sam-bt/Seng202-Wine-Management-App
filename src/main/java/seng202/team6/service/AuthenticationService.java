package seng202.team6.service;

import seng202.team6.managers.AuthenticationManager;
import seng202.team6.managers.DatabaseManager;
import seng202.team6.model.AuthenticationResponse;

public class AuthenticationService {
  private final AuthenticationManager authenticationManager;
  private final DatabaseManager databaseManager;

  public AuthenticationService(AuthenticationManager authenticationManager,
      DatabaseManager databaseManager) {
    this.authenticationManager = authenticationManager;
    this.databaseManager = databaseManager;
  }

  public AuthenticationResponse validateRegistration(String username, String password, String confirmedPassword) {
    if (username.isEmpty() || password.isEmpty() || confirmedPassword.isEmpty()) {
      return AuthenticationResponse.MISSING_FIELDS;
    }
    if (!password.equals(confirmedPassword)) {
      return AuthenticationResponse.MISMATCHING_CONFIRMED_PASSWORD;
    }
    if (username.length() < 3 || username.length() > 15 || !username.matches("[a-zA-Z0-9_]+")) {
      return AuthenticationResponse.INVALID_USERNAME;
    }
    if (password.length() < 3 || password.length() > 15 || !password.matches(
        "[a-zA-Z0-9]+")) {
      return AuthenticationResponse.INVALID_PASSWORD;
    }
    String salt = EncryptionService.generateSalt();
    String hashedPassword = EncryptionService.hashPassword(password, salt);
    boolean userAdded = databaseManager.addUser(username, hashedPassword, salt);
    if (userAdded) {
      authenticationManager.setAuthenticated(true);
      authenticationManager.setAdmin(username.equals("admin"));
      return AuthenticationResponse.REGISTER_SUCCESS;
    }
    return AuthenticationResponse.USERNAME_ALREADY_REGISTERED;
  }
}
