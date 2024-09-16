package seng202.team0.util;

import seng202.team0.model.User;
import seng202.team0.managers.ManagerContext;

public class UserService {

  /**
   * Validates the user login credentials
   * @param username the username of the user attempting to login
   * @param password the password of the user attempting to login
   * @param manager the ManagerContext object
   *
   * @return A string indicating the result of the login attempt:
   *         "All fields must be filled!" if username or password is empty,
   *         "Username or Password is Incorrect" if credentials are invalid,
   *         "Admin First Success" for first-time admin login,
   *         "Admin Success" for subsequent admin logins,
   *         "Success" for successful non-admin logins.
   */
  public static String validateLogin(String username, String password, ManagerContext manager) {
    if (username.isEmpty() || password.isEmpty()) {
      return "All fields must be filled!";
    }

    User userInfo = manager.databaseManager.getUser(username);
    if (userInfo == null) {
      return "Username or Password is Incorrect";
    }

    boolean validPassword = Password.verifyPassword(password, userInfo.getPassword(),
        userInfo.getSalt());
    if (!validPassword) {
      return "Username or Password is Incorrect";
    }

    if (username.equals("admin") && password.equals("admin")) {
      return "Admin First Success";
    }
    if (username.equals("admin")) {
      return "Admin Success";
    }
    return "Success";
  }

  /**
   * Validates and processes user registration
   * @param username the username for the new user
   * @param password the password for the new user
   * @param confirmPassword the confirmed password for the user
   * @param manager the ManagerContext object
   *
   * @return A string indicating the result of the registration attempt:
   *         "All fields must be filled!" if any field is empty,
   *         "Passwords do not match!" if password and confirmPassword don't match,
   *         "Invalid name, ..." if username doesn't meet criteria,
   *         "Invalid password, ..." if password doesn't meet criteria,
   *         "Success" if registration is successful,
   *         "Unexpected Error occurred" if registration fails unexpectedly.
   */
  public static String validateRegistration(String username, String password,
      String confirmPassword, ManagerContext manager) {
    if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
      return "All fields must be filled!";
    }
    if (!password.equals(confirmPassword)) {
      return "Passwords do not match!";
    }
    if (username.length() < 3 || username.length() > 15 || !username.matches("[a-zA-Z0-9_]+")) {
      return "Invalid name, Please make sure that your name is between 3 and 15 characters long and only contains letters, numbers or underscores!";
    }
    if (password.length() < 3 || password.length() > 15 || !password.matches(
        "[a-zA-Z0-9]+")) {
      return "Invalid password, Please make sure that your password is between 3 and 15 characters long and only contains letters or numbers!";
    }
    String salt = Password.generateSalt();
    String hashedPassword = Password.hashPassword(password, salt);
    boolean userAdded = manager.databaseManager.addUser(username, hashedPassword, salt);
    if (userAdded) {
      return "Success";
    }
    return "Unexpected Error occured";
  }

  /**
   * Validates and processes a password update request
   * @param username the current username of the user
   * @param oldPassword the current password of the user
   * @param newPassword the new password for the user
   * @param manager the ManagerContext object
   *
   * @return A string indicating the result of the password update attempt:
   *         "All fields must be filled!" if any field is empty,
   *         "Password is Incorrect" if the old password is invalid,
   *         "Admin password cannot be 'admin'" for admin users trying to set 'admin' as password,
   *         "New password cannot be same as old password" if old and new passwords match,
   *         "Invalid password, ..." if the new password doesn't meet criteria,
   *         "Success" if the password update is successful,
   *         "Unexpected error Occurred" if the update fails unexpectedly.
   */
  public static String validateUpdate(String username, String oldPassword, String newPassword,
      ManagerContext manager) {
    User user = manager.databaseManager.getUser(username);
    if (oldPassword.isEmpty() || newPassword.isEmpty()) {
      return "All fields must be filled!";
    }

    boolean validPassword = Password.verifyPassword(oldPassword, user.getPassword(),
        user.getSalt());
    if (!validPassword) {
      return "Password is Incorrect";
    }
    if (username.equals("admin") && newPassword.equals("admin")) {
      return "Admin password cannot be 'admin'";
    }
    if (oldPassword.equals(newPassword)) {
      return "New password cannot be same as old password";
    }
    if (newPassword.length() < 3 || newPassword.length() > 15 || !newPassword.matches(
        "[a-zA-Z0-9]+")) {
      return "Invalid password, Please make sure that your password is between 3 and 15 characters long and only contains letters or numbers!";
    }

    String salt = Password.generateSalt();
    String hashedPassword = Password.hashPassword(newPassword, salt);
    boolean passwordUpdated = manager.databaseManager.updatePassword(username, hashedPassword,
        salt);
    if (passwordUpdated) {
      return "Success";
    }
    return "Unexpected error Occurred";
  }
}
