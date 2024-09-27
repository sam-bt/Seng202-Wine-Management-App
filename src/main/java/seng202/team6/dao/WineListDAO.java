package seng202.team6.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import seng202.team6.model.User;
import seng202.team6.model.WineList;
import seng202.team6.util.Timer;

/**
 * Data Access Object (DAO) for handling wine list related database operations.
 */
public class WineListDAO extends DAO {

  /**
   * Constructs a new WineListDAO with the given database connection.
   *
   * @param connection The database connection to be used for wine list operations.
   */
  public WineListDAO(Connection connection) {
    super(connection, WineListDAO.class);
  }

  @Override
  public String[] getInitialiseStatements() {
    return new String[] {
        "CREATE TABLE IF NOT EXISTS LIST_NAME (" +
            "ID             INTEGER       PRIMARY KEY," +
            "USERNAME       VARCHAR(32)   NOT NULL," +
            "NAME           VARCHAR(10)   NOT NULL," +
            "FOREIGN KEY (USERNAME) REFERENCES USER(USERNAME) ON DELETE CASCADE" +
            ")",

        "CREATE TABLE IF NOT EXISTS LIST_ITEMS (" +
            "ID             INTEGER       PRIMARY KEY," +
            "LIST_ID        INTEGER       NOT NULL," +
            "WINE_ID        INTEGER       NOT NULL," +
            "FOREIGN KEY (LIST_ID) REFERENCES LIST_NAME(ID) ON DELETE CASCADE," +
            "FOREIGN KEY (WINE_ID) REFERENCES WINE(ID) ON DELETE CASCADE" +
            ")"
    };
  }

  public WineList create(User user, String listName) {
    Timer timer = new Timer();
    String sql = "INSERT INTO LIST_NAME VALUES (NULL, ?, ?)";
    try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
      statement.setString(1, user.getUsername());
      statement.setString(2, listName);
      statement.executeUpdate();

      try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
        if (generatedKeys.next()) {
          long id = generatedKeys.getLong(1);
          log.info("Successfully created list '{}' with ID {} for user '{}' in {}ms", listName,
              id, listName, user.getUsername(), timer.stop());
          return new WineList(id, listName);
        }
        log.warn("Could not create list '{}' for user '{}'", listName, user.getUsername());
      }
    } catch (SQLException error) {
      log.error("Failed to create list '{}' for user {''}", listName, user.getUsername(), error);
    }
    return null;
  }

  public void delete(WineList wineList) {
    Timer timer = new Timer();
    String sql = "DELETE FROM LIST_NAME WHERE ID = ?";
    try (PreparedStatement statement = connection.prepareStatement(sql)) {
      statement.setLong(1, wineList.id());

      int rowsAffected = statement.executeUpdate();
      if (rowsAffected == 1) {
        log.info("Successfully deleted list '{}' with ID {} in {}ms", wineList.name(),
            wineList.id(), timer.stop());
      } else {
        log.warn("Could not delete list '{}' with ID {}", wineList.name(),
            wineList.id());
      }
    } catch (SQLException error) {
      log.error("Failed to delete list with ID '{}' and name '{}'", wineList.id(),
          wineList.name(), error);
    }
  }
}
