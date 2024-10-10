package seng202.team6.managers;

import static seng202.team6.enums.AuthenticationResponse.PASSWORD_CHANGED_SUCCESS;
import static seng202.team6.enums.AuthenticationResponse.USERNAME_ALREADY_REGISTERED;

import seng202.team6.dao.UserDao;
import seng202.team6.enums.AuthenticationResponse;
import seng202.team6.model.User;
import seng202.team6.util.PasswordUtil;

/**
 * Manager class responsible for handling authentication-related operations. This class provides
 * methods for user registration, login, password update, and logout.
 */
public class AuthenticationManager {

  private final DatabaseManager databaseManager;
  private User authenticatedUser;
  private boolean admin;
  private boolean adminFirstLogin;

  /**
   * Constructs an AuthenticationManager.
   */
  public AuthenticationManager(DatabaseManager databaseManager) {
    this.databaseManager = databaseManager;
  }

  /**
   * Validates and processes a user registration request.
   *
   * @param username          The username for the new account.
   * @param password          The password for the new account.
   * @param confirmedPassword The confirmed password for verification.
   * @return An AuthenticationResponse indicating the result of the registration attempt.
   */
  public AuthenticationResponse validateRegistration(String username, String password,
      String confirmedPassword) {
    if (username.isEmpty() || password.isEmpty() || confirmedPassword.isEmpty()) {
      return AuthenticationResponse.MISSING_FIELDS;
    }
    if (!password.equals(confirmedPassword)) {
      return AuthenticationResponse.MISMATCHING_CONFIRMED_PASSWORD;
    }
    if (password.equals(username)) {
      return AuthenticationResponse.SAME_AS_USERNAME;
    }
    if (username.length() < 3 || username.length() > 15 || !username.matches("[a-zA-Z0-9_]+")) {
      return AuthenticationResponse.INVALID_USERNAME;
    }
    if (password.length() < 8 || password.length() > 30 || !password.matches(
        AuthenticationResponse.PASSWORD_CONSTRAINTS.getMessage())) {
      return AuthenticationResponse.INVALID_PASSWORD;
    }
    UserDao userDao = databaseManager.getUserDao();
    if (userDao.get(username) != null) {
      return USERNAME_ALREADY_REGISTERED;
    }

    String salt = PasswordUtil.generateSalt();
    String hashedPassword = PasswordUtil.hashPassword(password, salt);
    userDao.add(new User(username, hashedPassword, "user", salt));
    return AuthenticationResponse.REGISTER_SUCCESS;
  }

  /**
   * Validates and processes a password reset. Used on the admin panel.
   *
   * @param username The username of the target user
   * @param password The new password
   * @param confirm  COnfirmation of the new password, to make sure it was typed correctly.
   * @return an AuthenticationResponse with the outcome.
   */
  public AuthenticationResponse validatePasswordReset(
      String username, String password, String confirm
  ) {
    User user = databaseManager.getUserDao().get(username);
    if (password.isEmpty() || confirm.isEmpty()) {
      return AuthenticationResponse.MISSING_FIELDS;
    }
    if (!password.equals(confirm)) {
      return AuthenticationResponse.MISMATCHING_CONFIRMED_PASSWORD;
    }
    if (password.equals(user.getUsername())) {
      return AuthenticationResponse.SAME_AS_USERNAME;
    }
    if (
        password.length() < 8
            || password.length() > 30
            || !password.matches(AuthenticationResponse.PASSWORD_CONSTRAINTS.getMessage())
    ) {
      return AuthenticationResponse.INVALID_PASSWORD;
    }

    String salt = PasswordUtil.generateSalt();
    String hashedPassword = PasswordUtil.hashPassword(password, salt);
    user.setPassword(hashedPassword);
    user.setSalt(salt);
    return PASSWORD_CHANGED_SUCCESS;

  }

  /**
   * Validates and processes a user login request.
   *
   * @param username The username of the account to log in.
   * @return An AuthenticationResponse indicating the result of the login attempt.
   */
  public AuthenticationResponse validateLoginUsername(String username) {
    if (username.isEmpty()) {
      return AuthenticationResponse.MISSING_USERNAME_FIELD;
    } else {

      User user = databaseManager.getUserDao().get(username);
      if (user == null) {
        return AuthenticationResponse.INVALID_LOGIN_USERNAME;
      }

      return AuthenticationResponse.VALID_LOGIN_USERNAME;
    }
  }

  /**
   * Validates the login password.
   *
   * @param username username
   * @param password password
   * @return AuthenticationResponse
   */
  public AuthenticationResponse validateLoginPassword(String username, String password) {
    if (password.isEmpty()) {
      return AuthenticationResponse.MISSING_PASSWORD_FIELD;
    }

    User user = databaseManager.getUserDao().get(username);


    boolean validPassword = PasswordUtil.verifyPassword(password, user.getPassword(),
        user.getSalt());
    if (validPassword) {
      setAuthenticatedUser(user);
      setAdmin(username.equals("admin"));
      setAdminFirstLogin(isAdmin() && password.equals("admin"));
      return AuthenticationResponse.LOGIN_SUCCESS;
    }
    return AuthenticationResponse.INVALID_USERNAME_PASSWORD_COMBINATION;

  }

  /**
   * Validates and processes a password update request.
   *
   * @param username    The username of the account to update.
   * @param oldPassword The current password of the account.
   * @param newPassword The new password to set.
   * @return An AuthenticationResponse indicating the result of the password update attempt.
   */
  public AuthenticationResponse validateUpdate(String username,
      String oldPassword, String newPassword, String confirmNewPassword) {
    if (username.isEmpty() || oldPassword.isEmpty() || newPassword.isEmpty()) {
      return AuthenticationResponse.MISSING_FIELDS;
    }
    if (!newPassword.equals(confirmNewPassword)) {
      return AuthenticationResponse.MISMATCHING_CONFIRMED_PASSWORD;
    }

    User user = databaseManager.getUserDao().get(username);
    if (user != null) {
      boolean validPassword = PasswordUtil.verifyPassword(oldPassword, user.getPassword(),
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
      if (newPassword.equals(username)) {
        return AuthenticationResponse.SAME_AS_USERNAME;
      }
      if (!newPassword.matches(
          AuthenticationResponse.PASSWORD_CONSTRAINTS.getMessage())) {
        return AuthenticationResponse.INVALID_PASSWORD;
      }

      String salt = PasswordUtil.generateSalt();
      String hashedPassword = PasswordUtil.hashPassword(newPassword, salt);
      user.setPassword(hashedPassword);
      user.setSalt(salt);
      return PASSWORD_CHANGED_SUCCESS;
    }
    return AuthenticationResponse.UNEXPECTED_ERROR;
  }

  /**
   * Processes a user logout request.
   */
  public void logout() {
    setAuthenticatedUser(null);
    setAdmin(false);
    setAdminFirstLogin(false);
  }

  public User getAuthenticatedUser() {
    return authenticatedUser;
  }

  public void setAuthenticatedUser(User authenticatedUser) {
    this.authenticatedUser = authenticatedUser;
  }

  public boolean isAuthenticated() {
    return authenticatedUser != null;
  }

  public String getAuthenticatedUsername() {
    return authenticatedUser == null ? null : authenticatedUser.getUsername();
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
