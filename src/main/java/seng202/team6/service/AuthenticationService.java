package seng202.team6.service;

import static seng202.team6.model.AuthenticationResponse.PASSWORD_CHANGED_SUCCESS;
import static seng202.team6.model.AuthenticationResponse.UNEXPECTED_ERROR;

import seng202.team6.managers.AuthenticationManager;
import seng202.team6.managers.DatabaseManager;
import seng202.team6.model.AuthenticationResponse;
import seng202.team6.model.User;

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
      return AuthenticationResponse.REGISTER_SUCCESS;
    }
    return AuthenticationResponse.USERNAME_ALREADY_REGISTERED;
  }

  public AuthenticationResponse validateLogin(String username, String password) {
    if (username.isEmpty() || password.isEmpty()) {
      return AuthenticationResponse.MISSING_FIELDS;
    }

    User userInfo = databaseManager.getUser(username);
    if (userInfo == null) {
      return AuthenticationResponse.INVALID_USERNAME_PASSWORD_COMBINATION;
    }

    boolean validPassword = EncryptionService.verifyPassword(password, userInfo.getPassword(),
        userInfo.getSalt());
    if (validPassword) {
      authenticationManager.setAuthenticated(true);
      authenticationManager.setUsername(username);
      authenticationManager.setAdmin(username.equals("admin"));
      authenticationManager.setAdminFirstLogin(password.equals("admin"));
      return AuthenticationResponse.LOGIN_SUCCESS;
    }
    return AuthenticationResponse.INVALID_USERNAME_PASSWORD_COMBINATION;
  }

  public AuthenticationResponse validateUpdate(String username, String oldPassword, String newPassword) {
    User user = databaseManager.getUser(username);
    if (oldPassword.isEmpty() || newPassword.isEmpty()) {
      return AuthenticationResponse.MISSING_FIELDS;
    }

    boolean validPassword = EncryptionService.verifyPassword(oldPassword, user.getPassword(),
        user.getSalt());
    if (!validPassword) {
      return AuthenticationResponse.INCORRECT_OLD_PASSWORD;
    }
    if (username.equals("admin") && newPassword.equals("admin")) {
      return AuthenticationResponse. ADMIN_PASSWORD_CANNOT_BE_ADMIN;
    }
    if (oldPassword.equals(newPassword)) {
      return AuthenticationResponse.OLD_PASSWORD_SAME_AS_NEW;
    }
    if (newPassword.length() < 3 || newPassword.length() > 15 || !newPassword.matches(
        "[a-zA-Z0-9]+")) {
      return AuthenticationResponse.INVALID_PASSWORD;
    }

    String salt = EncryptionService.generateSalt();
    String hashedPassword = EncryptionService.hashPassword(newPassword, salt);
    boolean passwordUpdated = databaseManager.updatePassword(username, hashedPassword,
        salt);
    if (passwordUpdated) {
      return PASSWORD_CHANGED_SUCCESS;
    }
    return AuthenticationResponse.UNEXPECTED_ERROR;
  }

  public AuthenticationResponse logout() {
    if (authenticationManager.isAuthenticated()) {
      authenticationManager.setAuthenticated(false);
      authenticationManager.setAdmin(false);
      authenticationManager.setAdminFirstLogin(false);
      authenticationManager.setUsername(null);
      return AuthenticationResponse.LOGOUT_SUCCESS;
    }
    return UNEXPECTED_ERROR;
  }
}
