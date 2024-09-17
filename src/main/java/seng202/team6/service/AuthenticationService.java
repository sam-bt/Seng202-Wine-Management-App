package seng202.team6.service;

import static seng202.team6.model.AuthenticationResponse.PASSWORD_CHANGED_SUCCESS;
import static seng202.team6.model.AuthenticationResponse.UNEXPECTED_ERROR;

import seng202.team6.managers.DatabaseManager;
import seng202.team6.model.AuthenticationResponse;
import seng202.team6.model.User;
import seng202.team6.util.EncryptionUtil;

/**
 * Service class responsible for handling authentication-related operations.
 * This class provides methods for user registration, login, password update, and logout.
 */
public class AuthenticationService {
  private final DatabaseManager databaseManager;
  private String authenticatedUsername;
  private boolean admin;
  private boolean adminFirstLogin;

  /**
   * Constructs an AuthenticationService with the database manager
   *
   * @param databaseManager The DatabaseManager to interact with the user database.
   */
  public AuthenticationService(DatabaseManager databaseManager) {
    this.databaseManager = databaseManager;
  }

  /**
   * Validates and processes a user registration request.
   *
   * @param username The username for the new account.
   * @param password The password for the new account.
   * @param confirmedPassword The confirmed password for verification.
   * @return An AuthenticationResponse indicating the result of the registration attempt.
   */
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
    String salt = EncryptionUtil.generateSalt();
    String hashedPassword = EncryptionUtil.hashPassword(password, salt);
    boolean userAdded = databaseManager.addUser(username, hashedPassword, salt);
    if (userAdded) {
      return AuthenticationResponse.REGISTER_SUCCESS;
    }
    return AuthenticationResponse.USERNAME_ALREADY_REGISTERED;
  }

  /**
   * Validates and processes a user login request.
   *
   * @param username The username of the account to log in.
   * @param password The password of the account to log in.
   * @return An AuthenticationResponse indicating the result of the login attempt.
   */
  public AuthenticationResponse validateLogin(String username, String password) {
    if (username.isEmpty() || password.isEmpty()) {
      return AuthenticationResponse.MISSING_FIELDS;
    }

    User userInfo = databaseManager.getUser(username);
    if (userInfo == null) {
      return AuthenticationResponse.INVALID_USERNAME_PASSWORD_COMBINATION;
    }

    boolean validPassword = EncryptionUtil.verifyPassword(password, userInfo.getPassword(),
        userInfo.getSalt());
    if (validPassword) {
      setAuthenticatedUsername(username);
      setAdmin(username.equals("admin"));
      setAdminFirstLogin(password.equals("admin"));
      return AuthenticationResponse.LOGIN_SUCCESS;
    }
    return AuthenticationResponse.INVALID_USERNAME_PASSWORD_COMBINATION;
  }

  /**
   * Validates and processes a password update request.
   *
   * @param username The username of the account to update.
   * @param oldPassword The current password of the account.
   * @param newPassword The new password to set.
   * @return An AuthenticationResponse indicating the result of the password update attempt.
   */
  public AuthenticationResponse validateUpdate(String username, String oldPassword, String newPassword) {
    if (username.isEmpty() || oldPassword.isEmpty() || newPassword.isEmpty()) {
      return AuthenticationResponse.MISSING_FIELDS;
    }

    User user = databaseManager.getUser(username);
    if (user != null) {
      boolean validPassword = EncryptionUtil.verifyPassword(oldPassword, user.getPassword(),
          user.getSalt());
      if (!validPassword) {
        return AuthenticationResponse.INCORRECT_OLD_PASSWORD;
      }
      if (username.equals("admin") && newPassword.equals("admin")) {
        return AuthenticationResponse.ADMIN_PASSWORD_CANNOT_BE_ADMIN;
      }
      if (oldPassword.equals(newPassword)) {
        return AuthenticationResponse.OLD_PASSWORD_SAME_AS_NEW;
      }
      if (newPassword.length() < 3 || newPassword.length() > 15 || !newPassword.matches(
          "[a-zA-Z0-9]+")) {
        return AuthenticationResponse.INVALID_PASSWORD;
      }

      String salt = EncryptionUtil.generateSalt();
      String hashedPassword = EncryptionUtil.hashPassword(newPassword, salt);
      boolean passwordUpdated = databaseManager.updatePassword(username, hashedPassword,
          salt);
      if (passwordUpdated) {
        return PASSWORD_CHANGED_SUCCESS;
      }
    }
    return AuthenticationResponse.UNEXPECTED_ERROR;
  }

  /**
   * Processes a user logout request.
   *
   * @return An AuthenticationResponse indicating the result of the logout attempt.
   */
  public AuthenticationResponse logout() {
    if (isAuthenticated()) {
      setAuthenticatedUsername(null);
      setAdmin(false);
      setAdminFirstLogin(false);
      return AuthenticationResponse.LOGOUT_SUCCESS;
    }
    return UNEXPECTED_ERROR;
  }

  public boolean isAuthenticated() {
    return authenticatedUsername != null;
  }

  public String getAuthenticatedUsername() {
    return authenticatedUsername;
  }

  public void setAuthenticatedUsername(String authenticatedUsername) {
    this.authenticatedUsername = authenticatedUsername;
  }

  public boolean isAdmin() {
    return admin;
  }

  public void setAdmin(boolean admin) {
    this.admin = admin;
  }

  public boolean isAdminFirstLogin() {
    return adminFirstLogin;
  }

  public void setAdminFirstLogin(boolean adminFirstLogin) {
    this.adminFirstLogin = adminFirstLogin;
  }
}
