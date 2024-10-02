package seng202.team6.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import seng202.team6.managers.DatabaseManager;
import seng202.team6.model.User;
import seng202.team6.util.EncryptionUtil;
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
   *
   * @return Array of SQL statements for initialising the USER table
   */
  @Override
  public String[] getInitialiseStatements() {
    String salt = EncryptionUtil.generateSalt();
    String hashedAdminPassword = EncryptionUtil.hashPassword("admin", salt);
    return new String[]{
        "CREATE TABLE IF NOT EXISTS USER (" +
            "USERNAME       VARCHAR(64)   PRIMARY KEY," +
            "PASSWORD       VARCHAR(64)   NOT NULL," +
            "ROLE           VARCHAR(8)    NOT NULL," +
            "SALT           VARCHAR(32)" +
            ")"
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
          log.info("Counted {} users in {}ms", count, timer.stop());
          return count;
        }
      }
    } catch (SQLException error) {
      log.error("Failed to count the number of users", error);
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
    Timer timer = new Timer();
    String sql = "SELECT * FROM USER WHERE USERNAME = ?";
    try (PreparedStatement statement = connection.prepareStatement(sql)) {
      statement.setString(1, username);

      try (ResultSet resultSet = statement.executeQuery()) {
        if (resultSet.next()) {
          log.info("Successfully found user '{}' in {}ms", username, timer.stop());
          User user = new User(
              resultSet.getString("USERNAME"),
              resultSet.getString("PASSWORD"),
              resultSet.getString("ROLE"),
              resultSet.getString("SALT")
          );
          bindUpdater(user);
          return user;
        } else {
          log.warn("Could not find user '{}' in {}ms", username, timer.stop());
        }
      }
    } catch (SQLException error) {
      log.error("Failed to retrieve user {}", username, error);
    }
    return null;
  }

  public ObservableList<User> getAll() {
    Timer timer = new Timer();

    ObservableList<User> users = FXCollections.observableArrayList();


    String sql = "SELECT * FROM USER WHERE USERNAME != 'admin'";
    try (PreparedStatement statement = connection.prepareStatement(sql)) {
      try (ResultSet resultSet = statement.executeQuery()) {
        if (resultSet.next()) {
          User user = new User(
                  resultSet.getString("USERNAME"),
                  resultSet.getString("PASSWORD"),
                  resultSet.getString("ROLE"),
                  resultSet.getString("SALT")
          );
          users.add(user);
        } else {
          log.warn("No users found in {}ms", timer.stop());
        }
      }
    } catch (SQLException error) {
      log.error("Failed to retrieve users", error);
      log.error(error.getMessage());
    }
    return users;
  }

  /**
   * Adds the specified user to the USER table.
   *
   * @param user The user to be added
   */
  public void add(User user) {
    Timer timer = new Timer();
    String sql = "INSERT INTO USER VALUES (?, ?, 'user', ?)";
    try (PreparedStatement statement = connection.prepareStatement(sql,
        Statement.RETURN_GENERATED_KEYS)) {
      statement.setString(1, user.getUsername());
      statement.setString(2, user.getPassword());
      statement.setString(3, user.getSalt());

      int rowsAffected = statement.executeUpdate();
      if (rowsAffected == 1) {
        log.info("Successfully added user '{}' in {}ms", user.getUsername(), timer.stop());
      } else {
        log.warn("Failed to add user '{}' in {}ms", user.getUsername(), timer.stop());
      }
      bindUpdater(user);
    } catch (SQLException error) {
      log.error("Failed to add a user", error);
    }
  }

  /**
   * Removes the specified user from the USER table.
   *
   * @param user The user to be removed
   */
  public void delete(User user) {
    Timer timer = new Timer();
    String sql = "DELETE FROM USER WHERE USERNAME = ?";
    try (PreparedStatement statement = connection.prepareStatement(sql)) {
      statement.setString(1, user.getUsername());

      int rowsAffected = statement.executeUpdate();
      if (rowsAffected == 1) {
        log.info("Successfully deleted user '{}' in {}ms", user.getUsername(), timer.stop());
      } else {
        log.warn("Failed to delete user '{}' in {}ms", user.getUsername(), timer.stop());
      }
    } catch (SQLException error) {
      log.error("Unable to delete user '{}'", user.getUsername(), error);
    }
  }

  /**
   * Deletes all the users in the USER table except the default admin account.
   */
  public void deleteAll() {
    Timer timer = new Timer();
    String sql = "DELETE FROM USER WHERE USERNAME != 'admin'";
    try (Statement statement = connection.createStatement()) {
      int rowsAffected = statement.executeUpdate(sql);

      log.info("Successfully deleted {} users in {}ms", rowsAffected, timer.stop());
    } catch (SQLException error) {
      log.error("Unable to delete all users in the USER table", error);
    }
  }

  /**
   * Binds listeners to the User object to ensure that any changes to the users properties are
   * automatically reflected in the database.
   *
   * @param user The User object to bind listeners to
   */
  private void bindUpdater(User user) {
    user.passwordProperty().addListener((observableValue, before, after) -> {
      updateAttribute(user.getUsername(), "PASSWORD", update -> {
        update.setString(1, after);
      });
    });
    user.roleProperty().addListener((observableValue, before, after) -> {
      updateAttribute(user.getUsername(), "ROLE", update -> {
        update.setString(1, after);
      });
    });
    user.saltProperty().addListener((observableValue, before, after) -> {
      updateAttribute(user.getUsername(), "SALT", update -> {
        update.setString(1, after);
      });
    });
  }

  /**
   * Updates a specific attribute of the user in the USER table
   *
   * @param attributeName   name of attribute
   * @param attributeSetter callback to set attribute
   */
  private void updateAttribute(String username, String attributeName,
      DatabaseManager.AttributeSetter attributeSetter) {
    Timer timer = new Timer();
    String sql = "UPDATE USER set " + attributeName + " = ? where USERNAME = ?";
    try (PreparedStatement update = connection.prepareStatement(sql)) {
      attributeSetter.setAttribute(update);
      update.setString(2, username);

      int rowsAffected = update.executeUpdate();
      if (rowsAffected == 1) {
        log.info("Successfully updated attribute '{}' for user '{}' in {}ms",
            attributeName, username, timer.stop());
      } else {
        log.info("Could not update attribute '{}' for user '{}' in {}ms",
            attributeName, username, timer.stop());
      }
    } catch (SQLException error) {
      log.error("Failed to update attribute '{}' for user '{}' in {}ms",
          attributeName, username, timer.stop(), error);
    }
  }
}
