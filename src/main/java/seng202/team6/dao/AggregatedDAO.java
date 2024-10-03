package seng202.team6.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import seng202.team6.model.Note;
import seng202.team6.model.User;
import seng202.team6.model.Wine;
import seng202.team6.model.WineDatePair;
import seng202.team6.model.WineList;
import seng202.team6.util.Timer;

/**
 * Aggregated Data Access Object (DAO) is responsible for combining functionality from multiple DAOs
 * and handling operations that involve joining data from different tables. It avoids code
 * repetition by using existing DAO methods to extract data
 */
public class AggregatedDAO extends DAO {

  private final WineNotesDAO wineNotesDAO;
  private final WineDAO wineDAO;

  /**
   * Constructs a new DAO with the given database connection and initializes references to DAOs.
   *
   * @param connection   The database connection to be used by this DAO.
   * @param wineNotesDAO The DAO responsible for handling operations related to wine notes.
   * @param wineDAO      The DAO responsible for handling operations related to wines.
   */
  public AggregatedDAO(Connection connection, WineNotesDAO wineNotesDAO, WineDAO wineDAO) {
    super(connection, AggregatedDAO.class);
    this.wineNotesDAO = wineNotesDAO;
    this.wineDAO = wineDAO;
  }

  /**
   * Retrieves all notes mapped to wines for a specific user. This method joins the NOTES table with
   * the WINE table based on the wine ID and returns a map of Wine objects to their associated Note
   * objects for the specified user.
   *
   * @param user The user for whom to retrieve the notes and wines.
   * @return An ObservableMap where the key is a Wine object and the value is the associated Note
   * object.
   */
  public ObservableMap<Wine, Note> getAllNotesMappedWithWinesByUser(User user) {
    Timer timer = new Timer();
    String sql = "SELECT * FROM NOTES " +
        "INNER JOIN WINE ON NOTES.WINE_ID = WINE.ID " +
        "WHERE NOTES.USERNAME = ?";
    ObservableMap<Wine, Note> wineAndNotes = FXCollections.observableHashMap();
    try (PreparedStatement statement = connection.prepareStatement(sql)) {
      statement.setString(1, user.getUsername());

      try (ResultSet resultSet = statement.executeQuery()) {
        while (resultSet.next()) {
          Wine wine = wineDAO.extractWineFromResultSet(resultSet);
          Note note = wineNotesDAO.extractNoteFromResultSet(resultSet);
          wineAndNotes.put(wine, note);
        }
      }
      log.info("Successfully retrieves {} wines with notes by user '{}' in {}ms",
          wineAndNotes.size(), user.getUsername(), timer.stop());
    } catch (SQLException e) {
      log.error("Failed to retrieve wines with notes by user '{}'", user.getUsername());
    }
    return wineAndNotes;
  }

  /**
   * Gets a list of wine date pairs from a list.
   * @param wineList list of wines
   * @return wine-date pair with date added to list
   */
  public ObservableList<WineDatePair> getWinesMappedWithDatesFromList(WineList wineList) {
    Timer timer = new Timer();
    String sql = "SELECT * FROM WINE " +
        "INNER JOIN LIST_ITEMS ON WINE.ID = LIST_ITEMS.WINE_ID " +
        "INNER JOIN LIST_NAME ON LIST_ITEMS.LIST_ID = LIST_NAME.ID " +
        "LEFT JOIN GEOLOCATION on lower(WINE.REGION) like lower(GEOLOCATION.NAME) " +
        "WHERE LIST_NAME.ID = ?";
    ObservableList<WineDatePair> winesAndDates = FXCollections.observableArrayList();
    try (PreparedStatement statement = connection.prepareStatement(sql)) {
      statement.setLong(1, wineList.id());

      try (ResultSet resultSet = statement.executeQuery()) {
        while (resultSet.next()) {
          Wine wine = wineDAO.extractWineFromResultSet(resultSet);
          Date date = resultSet.getDate("DATE_ADDED");
          winesAndDates.add(new WineDatePair(wine, date));
        }
      }
      log.info("Successfully retrieves {} wines with dates in list {} in {}ms",
          winesAndDates.size(), wineList.id(), timer.stop());
    } catch (SQLException e) {
      log.error("Failed to retrieve wines with dates in list {}", wineList.id());
    }
    return winesAndDates;
  }

  /**
   * Gets the wines in a given wine list.
   * @param wineList wine list
   * @return list of wines
   */
  public ObservableList<Wine> getWinesInList(WineList wineList) {
    Timer timer = new Timer();
    String sql = "SELECT * FROM WINE " +
        "INNER JOIN LIST_ITEMS ON WINE.ID = LIST_ITEMS.WINE_ID " +
        "INNER JOIN LIST_NAME ON LIST_ITEMS.LIST_ID = LIST_NAME.ID " +
        "LEFT JOIN GEOLOCATION on lower(WINE.REGION) like lower(GEOLOCATION.NAME) " +
        "WHERE LIST_NAME.ID = ?";
    ObservableList<Wine> wines = FXCollections.observableArrayList();
    try (PreparedStatement statement = connection.prepareStatement(sql)) {
      statement.setLong(1, wineList.id());

      try (ResultSet resultSet = statement.executeQuery()) {
        while (resultSet.next()) {
          Wine wine = wineDAO.extractWineFromResultSet(resultSet);
          wines.add(wine);
        }
      }
      log.info("Successfully retrieves {} wines in list {} in {}ms",
          wines.size(), wineList.id(), timer.stop());
    } catch (SQLException e) {
      log.error("Failed to retrieve wines in list {}", wineList.id());
    }
    return wines;
  }
}
