package seng202.team0.util;

import static java.util.Objects.isNull;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Objects;
import seng202.team0.database.User;
import seng202.team0.managers.ManagerContext;

public class Login {
  public static String validateLogin(String username,String password, ManagerContext manager) {
    User userInfo = manager.databaseManager.getUser(username);
    if (isNull(userInfo)) {
      return "User does not exist";
      } else if (Objects.equals(password, userInfo.getPassword()) && username.equals("admin") && Objects.equals(password, "admin")) {
        return "Admin First Success";
      } else if (Objects.equals(password, userInfo.getPassword()) && username.equals("admin")) {
        return "Admin Success";
      } else if (Objects.equals(password, userInfo.getPassword())) {
        return "Success";
      } else {
        return "Password incorrect";
    }
  }
}
