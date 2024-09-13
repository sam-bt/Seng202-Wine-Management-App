package seng202.team0.util;

import static java.util.Objects.isNull;

import java.util.Objects;
import seng202.team0.database.User;
import seng202.team0.managers.ManagerContext;

public class UserService {
  public static String validateLogin(String username,String password, ManagerContext manager) {
    if (Objects.equals(username, "") || Objects.equals(password, "")) {
      return "All fields must be filled!";
    }
    seng202.team0.database.User userInfo = manager.databaseManager.getUser(username);
    if (isNull(userInfo)) {
      return "Username or Password is Incorrect";
      } else if (Objects.equals(password, userInfo.getPassword()) && username.equals("admin") && Objects.equals(password, "admin")) {
        return "Admin First Success";
      } else if (Objects.equals(password, userInfo.getPassword()) && username.equals("admin")) {
        return "Admin Success";
      } else if (Objects.equals(password, userInfo.getPassword())) {
        return "Success";
      } else {
        return "Username or Password is Incorrect";
    }
  }
  public static String validateRegistration(String username,String password,String confirmPassword, ManagerContext manager) {
    if (Objects.equals(username, "") || Objects.equals(password, "") || Objects.equals(confirmPassword, "")) {
      return "All fields must be filled!";
    }
    if (!Objects.equals(password, confirmPassword)) {
      return "Passwords do not match!";
    }
    if (username.length() < 3 || username.length() > 15 || !username.matches("[a-zA-Z0-9_]+")) {
      return "Invalid name, Please make sure that your name is between 3 and 15 characters long and only contains letters, numbers or underscores!";
    } else if (password.length() < 3 || password.length() > 15 || !password.matches("[a-zA-Z0-9]+")) {
      return "Invalid password, Please make sure that your password is between 3 and 15 characters long and only contains letters or numbers!";
    } else {
      boolean userAdded = manager.databaseManager.addUser(username,password);
      if (userAdded) {
        return "Success";
      } else {
        return "Unexpected Error occured";
      }
    }
  }

  public static String validateUpdate(String username, String oldPassword, String newPassword, ManagerContext manager) {
    User user = manager.databaseManager.getUser(username);
    if (Objects.equals(oldPassword, "") || Objects.equals(newPassword, "")) {
      return "All fields must be filled!";
    }
    if (Objects.equals(oldPassword, newPassword)) {
      return "New password cannot be same as old password";
    }
    if (!Objects.equals(user.getPassword(), oldPassword)) {
      return "Password incorrect";
    }
    else if (newPassword.length() < 3 || newPassword.length() > 15 || !newPassword.matches("[a-zA-Z0-9]+")) {
      return "Invalid password, Please make sure that your password is between 3 and 15 characters long and only contains letters or numbers!";
    }
    boolean passwordUpdated = manager.databaseManager.updatePassword(username,newPassword);
    if (passwordUpdated) {
      return "Success";
    } else {
      return  "Unexpected error Occured";
    }
  }
}
