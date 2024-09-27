package seng202.team6.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import seng202.team6.model.Note;
import seng202.team6.model.User;
import seng202.team6.model.Wine;
import seng202.team6.model.WineList;
import seng202.team6.util.DatabaseObjectUniquer;
import seng202.team6.util.Timer;

/**
 * Data Access Object (DAO) for handling wine list related database operations.
 */
public class WineListDAO extends DAO {

  /**
   * Cache to store and reuse WineList objects to avoid duplication
   */
  private final DatabaseObjectUniquer<WineList> wineListCache = new DatabaseObjectUniquer<>();


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
            ")",
    };
  }

  public ObservableList<WineList> getAll(User user) {
    Timer timer = new Timer();
    String sql = "SELECT ID, NAME FROM LIST_NAME WHERE USERNAME = ?";
    try (PreparedStatement statement = connection.prepareStatement(sql)) {
      statement.setString(1, user.getUsername());

      try (ResultSet resultSet = statement.executeQuery()) {
        ObservableList<WineList> wineLists = extractAllWineListsFromResultSet(resultSet);
        log.info("Successfully retrieved all {} wine lists for user '{}' in {}ms",
            wineLists.size(), user.getUsername(), timer.stop());
        return wineLists;
      }
    } catch (SQLException error) {
      log.info("Failed to retrieve wine lists for user '{}'", user.getUsername(), error);
    }
    return FXCollections.emptyObservableList();
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

  public boolean isWineInList(WineList wineList, Wine wine) {
    Timer timer = new Timer();
    String sql = "SELECT 1 FROM LIST_ITEMS WHERE LIST_ID = ? AND WINE_ID = ?";
    try (PreparedStatement statement = connection.prepareStatement(sql)) {
      statement.setLong(1, wineList.id());
      statement.setLong(1, wine.getKey());

      try (ResultSet resultSet = statement.executeQuery()) {
        boolean found = resultSet.next();
        log.info("Successfully found wine with ID {} is {} list with ID {} in {}ms",
            wine.getKey(), found ? "in" : "not in", wineList.id(), timer.stop());
        return resultSet.next();
      }
    } catch (SQLException error) {
      log.error("Failed to check if wine with ID {} is in list '{}'", wineList.id(),
          wineList.name(), error);
    }
    return false;
  }

  public void addWine(WineList wineList, Wine wine) {
    Timer timer = new Timer();
    String sql = "INSERT INTO LIST_ITEMS VALUES (null, ?, ?)";
    try (PreparedStatement statement = connection.prepareStatement(sql)) {
      statement.setLong(1, wineList.id());
      statement.setLong(2, wine.getKey());

      int rowsAffected = statement.executeUpdate();
      if (rowsAffected == 1) {
        log.info("Successfully added wine with ID {} to list with ID {} in {}ms",
            wine.getKey(), wineList.id(), timer.stop());
      } else {
        log.warn("Could not add wine with ID {} to list with ID {} in {}ms",
            wine.getKey(), wineList.id(), timer.stop());
      }
    } catch (SQLException error) {
      log.error("Failed to add wine with ID {} to list with ID {}",
          wine.getKey(), wineList.id(), error);
    }
  }

  public void removeWine(WineList wineList, Wine wine) {
    Timer timer = new Timer();
    String sql = "DELETE FROM LIST_ITEMS WHERE LIST_ID = ? AND WINE_ID = ?";
    try (PreparedStatement statement = connection.prepareStatement(sql)) {
      statement.setLong(1, wineList.id());
      statement.setLong(2, wine.getKey());

      int rowsAffected = statement.executeUpdate();
      if (rowsAffected == 1) {
        log.info("Successfully removed wine with ID {} from list with ID {} in {}ms",
            wine.getKey(), wineList.id(), timer.stop());
      } else {
        log.warn("Could not remove wine with ID {} from list with ID {} in {}ms",
            wine.getKey(), wineList.id(), timer.stop());
      }
    } catch (SQLException error) {
      log.error("Failed to remove wine with ID {} from list with ID {}",
          wine.getKey(), wineList.id(), error);
    }
  }

  private ObservableList<WineList> extractAllWineListsFromResultSet(ResultSet resultSet)
      throws SQLException {
    ObservableList<WineList> wineLists = FXCollections.observableArrayList();
    while (resultSet.next()) {
      wineLists.add(extractWineListFromResultSet(resultSet));
    }
    return wineLists;
  }

  private WineList extractWineListFromResultSet(ResultSet resultSet) throws SQLException {
    long id = resultSet.getLong("ID");
    WineList cachedWineList = wineListCache.tryGetObject(id);
    if (cachedWineList != null) {
      return cachedWineList;
    }

    return new WineList(
        id,
        resultSet.getString("NAME")
    );
  }
}
