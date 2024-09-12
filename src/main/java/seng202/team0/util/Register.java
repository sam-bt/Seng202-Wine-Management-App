package seng202.team0.util;

import static java.util.Objects.isNull;

import java.util.Objects;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import seng202.team0.database.User;
import seng202.team0.managers.ManagerContext;

public class Register {



  public static String validateRegistration(String username,String password,String confirmPassword, ManagerContext manager) {
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
}
