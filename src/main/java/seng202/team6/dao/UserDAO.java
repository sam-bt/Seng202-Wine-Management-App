package seng202.team6.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import seng202.team6.model.User;
import seng202.team6.util.Timer;

/**
 * Data Access Object (DAO) for handling user related database operations.
 */
public class UserDAO extends DAO {

  /**
   * Constructs a new UserDAO with the given database connection.
   *
   * @param connection The database connection to be used for user operations.
   */
  public UserDAO(Connection connection) {
    super(connection, UserDAO.class);
  }

  /**
   * Returns the SQL statements required to initialise the WINE table.
   * If the default admin account does not exist in the table, it will be inserted.
   *
   * @return Array of SQL statements for initialising the USER table
   */
  @Override
  public String[] getInitialiseStatements() {
    return new String[] {
        "CREATE TABLE IF NOT EXISTS USER (" +
            "USERNAME       VARCHAR(64)   PRIMARY KEY," +
            "PASSWORD       VARCHAR(64)   NOT NULL," +
            "ROLE           VARCHAR(8)    NOT NULL," +
            "SALT           VARCHAR(32)" +
            ")",
        "INSERT INTO USER (USERNAME, PASSWORD, ROLE, SALT) " +
            "SELECT 'admin', 'admin', 'admin', 'admin' " +
            "WHERE NOT EXISTS (" +
            "SELECT 1 FROM USER WHERE USERNAME = 'admin')"
    };
  }

  /**
   * Retrieves the total number of users in the database.
   *
   * @return The count of users in the USER table
   */
  public int getCount() {
    Timer timer = new Timer();
    String sql = "SELECT COUNT(*) FROM USER";
    try (Statement statement = connection.createStatement()) {
      try (ResultSet resultSet = statement.executeQuery(sql)) {
        if (resultSet.next()) {
          int count = resultSet.getInt(1);
          log.info("Counted {} elements in the USER table in {}ms", count, timer.stop());
          return count;
        }
      }
    } catch (SQLException error) {
      log.error("Failed to count the number of elements in USER table", error);
    }
    return 0;
  }

  /**
   * Retrieves a User object from the database with the specified username.
   *
   * @param username The username of the user to retrieve
   * @return A User object if the user is found, null otherwise
   */
  public User get(String username) {
    String sql = "SELECT * FROM USER WHERE USERNAME = ?";
    try (PreparedStatement statement = connection.prepareStatement(sql)) {
      statement.setString(1, username);

      try (ResultSet resultSet = statement.executeQuery()) {
        if (resultSet.next()) {
          log.info("Successfully found user '{}' in table USER", username);
          return new User(
              resultSet.getString("USERNAME"),
              resultSet.getString("PASSWORD"),
              resultSet.getString("ROLE"),
              resultSet.getString("SALT")
          );
        }
      }
      log.info("Failed to find user '{}' in table USER", username);
    } catch (SQLException error) {
      log.error("Failed to get user {} from the USER table", username, error);
    }
    return null;
  }

  /**
   * Adds the specified user to the USER table.
   *
   * @param user The user to be added
   */
  public void add(User user) {
    String sql = "INSERT INTO USER VALUES (?, ?, 'user', ?)";
    try (PreparedStatement statement = connection.prepareStatement(sql,
        Statement.RETURN_GENERATED_KEYS)) {
      statement.setString(1, user.getUsername());
      statement.setString(2, user.getPassword());
      statement.setString(3, user.getSalt());

      int rowsAffected = statement.executeUpdate();
      if (rowsAffected == 1) {
        log.info("Successfully added user '{}' to the USER table", user.getUsername());
      } else {
        log.warn("Failed to add user '{}' to the USER table", user.getUsername());
      }
    } catch (SQLException error) {
      if (!error.getMessage().contains("A PRIMARY KEY constraint failed")) {
        log.error("Failed to add a user to the USER table", error);
      }
    }
  }

  /**
   * Removes the specified user from the USER table.
   *
   * @param user The user to be removed
   */
  public void delete(User user) {
    String sql = "DELETE FROM USER WHERE USERNAME = ?";
    try (PreparedStatement statement = connection.prepareStatement(sql)) {
      statement.setString(1, user.getUsername());

      int rowsAffected = statement.executeUpdate();
      if (rowsAffected == 1) {
        log.info("Successfully deleted user '{}' from the USER table", user.getUsername());
      } else {
        log.warn("Failed to delete user '{}' from the USER table", user.getUsername());
      }
    } catch (SQLException error) {
      log.error("Unable to delete user {} in the USER table", user.getUsername(), error);
    }
  }

  /**
   * Deletes all the users in the USER table except the default admin account.
   */
  public void deleteAll() {
    String sql = "DELETE FROM USER WHERE USERNAME != 'admin'";
    try (Statement statement = connection.createStatement()) {
      statement.executeUpdate(sql);
    } catch (SQLException error) {
      log.error("Unable to delete all users in the USER table", error);
    }
  }
}
