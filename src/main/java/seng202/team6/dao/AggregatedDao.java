package seng202.team6.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import kotlin.Pair;
import seng202.team6.model.Note;
import seng202.team6.model.ReviewFilters;
import seng202.team6.model.User;
import seng202.team6.model.Vineyard;
import seng202.team6.model.Wine;
import seng202.team6.model.WineDatePair;
import seng202.team6.model.WineList;
import seng202.team6.model.WineReview;
import seng202.team6.util.Timer;

/**
 * Aggregated Data Access Object (DAO) is responsible for combining functionality from multiple DAOs
 * and handling operations that involve joining data from different tables. It avoids code
 * repetition by using existing DAO methods to extract data
 */
public class AggregatedDao extends Dao {

  private final WineReviewDao wineReviewDao;
  private final WineNotesDao wineNotesDao;
  private final WineDao wineDao;

  /**
   * Constructs a new DAO with the given database connection and initializes references to DAOs.
   *
   * @param connection   The database connection to be used by this DAO.
   * @param wineNotesDao The DAO responsible for handling operations related to wine notes.
   * @param wineDao      The DAO responsible for handling operations related to wines.
   */
  public AggregatedDao(Connection connection,
      WineReviewDao wineReviewDao,
      WineNotesDao wineNotesDao,
      WineDao wineDao) {
    super(connection, AggregatedDao.class);
    this.wineReviewDao = wineReviewDao;
    this.wineNotesDao = wineNotesDao;
    this.wineDao = wineDao;
  }

  /**
   * Retrieves all notes mapped to wines for a specific user. This method joins the NOTES table with
   * the WINE table based on the wine ID and returns a map of Wine objects to their associated Note
   * objects for the specified user.
   *
   * @param user The user for whom to retrieve the notes and wines.
   * @return An ObservableMap where the key is a Wine object and the value is the associated Note
   *        object.
   */
  public ObservableMap<Wine, Note> getAllNotesMappedWithWinesByUser(User user) {
    Timer timer = new Timer();
    String sql = "SELECT WINE.ID as wine_id, WINE.*, NOTES.ID as note_id, NOTES.* "
        + "FROM NOTES "
        + "INNER JOIN WINE ON NOTES.WINE_ID = WINE.ID "
        + "WHERE NOTES.USERNAME = ?";
    ObservableMap<Wine, Note> wineAndNotes = FXCollections.observableHashMap();
    try (PreparedStatement statement = connection.prepareStatement(sql)) {
      statement.setString(1, user.getUsername());

      try (ResultSet resultSet = statement.executeQuery()) {
        while (resultSet.next()) {
          Wine wine = wineDao.extractWineFromResultSet(resultSet, "wine_id");
          Note note = wineNotesDao.extractNoteFromResultSet(resultSet, "note_id");
          wineAndNotes.put(wine, note);
        }
      }
      log.info("Successfully retrieves {} wines with notes by user '{}' in {}ms",
          wineAndNotes.size(), user.getUsername(), timer.currentOffsetMilliseconds());
    } catch (SQLException e) {
      log.error("Failed to retrieve wines with notes by user '{}'", user.getUsername());
    }
    return wineAndNotes;
  }

  /**
   * Gets a list of wine date pairs from a list.
   *
   * @param wineList list of wines
   * @return wine-date pair with date added to list
   */
  public ObservableList<WineDatePair> getWinesMappedWithDatesFromList(WineList wineList) {
    Timer timer = new Timer();
    String sql = "SELECT WINE.ID as wine_id, WINE.*, GEOLOCATION.LATITUDE, GEOLOCATION.LONGITUDE, "
        + "DATE_ADDED "
        + "FROM WINE "
        + "INNER JOIN LIST_ITEMS ON WINE.ID = LIST_ITEMS.WINE_ID "
        + "INNER JOIN LIST_NAME ON LIST_ITEMS.LIST_ID = LIST_NAME.ID "
        + "LEFT JOIN GEOLOCATION on lower(WINE.REGION) like lower(GEOLOCATION.NAME) "
        + "WHERE LIST_NAME.ID = ?";
    ObservableList<WineDatePair> winesAndDates = FXCollections.observableArrayList();
    try (PreparedStatement statement = connection.prepareStatement(sql)) {
      statement.setLong(1, wineList.id());

      try (ResultSet resultSet = statement.executeQuery()) {
        while (resultSet.next()) {
          Wine wine = wineDao.extractWineFromResultSet(resultSet, "wine_id");
          Date date = resultSet.getDate("DATE_ADDED");
          winesAndDates.add(new WineDatePair(wine, date));
        }
      }
      log.info("Successfully retrieves {} wines with dates in list {} in {}ms",
          winesAndDates.size(), wineList.id(), timer.currentOffsetMilliseconds());
    } catch (SQLException e) {
      log.error("Failed to retrieve wines with dates in list {}", wineList.id());
    }
    return winesAndDates;
  }

  /**
   * Gets the wines in a given wine list.
   *
   * @param wineList wine list
   * @return list of wines
   */
  public ObservableList<Wine> getWinesInList(WineList wineList) {
    Timer timer = new Timer();
    String sql = "SELECT WINE.ID as wine_id, WINE.*, GEOLOCATION.LATITUDE, GEOLOCATION.LONGITUDE "
        + "FROM WINE "
        + "INNER JOIN LIST_ITEMS ON WINE.ID = LIST_ITEMS.WINE_ID "
        + "INNER JOIN LIST_NAME ON LIST_ITEMS.LIST_ID = LIST_NAME.ID "
        + "LEFT JOIN GEOLOCATION on lower(WINE.REGION) like lower(GEOLOCATION.NAME) "
        + "WHERE LIST_NAME.ID = ?";
    ObservableList<Wine> wines = FXCollections.observableArrayList();
    try (PreparedStatement statement = connection.prepareStatement(sql)) {
      statement.setLong(1, wineList.id());

      try (ResultSet resultSet = statement.executeQuery()) {
        while (resultSet.next()) {
          Wine wine = wineDao.extractWineFromResultSet(resultSet, "wine_id");
          wines.add(wine);
        }
      }
      log.info("Successfully retrieved {} wines in list {} in {}ms",
          wines.size(), wineList.id(), timer.currentOffsetMilliseconds());
    } catch (SQLException e) {
      log.error("Failed to retrieve wines in list {}", wineList.id());
    }
    return wines;
  }

  /**
   * Gets a list of wine reviews and wines.
   *
   * @param begin begin
   * @param end   end
   * @return sub range of pairs between begin and end
   */
  public ObservableList<Pair<WineReview, Wine>> getWineReviewsAndWines(int begin, int end) {
    Timer timer = new Timer();
    String sql = "SELECT WINE.ID as wine_id, WINE.*, WINE_REVIEW.ID as wine_review_id, "
        + "WINE_REVIEW.*, GEOLOCATION.LATITUDE, GEOLOCATION.LONGITUDE "
        + "FROM WINE_REVIEW "
        + "INNER JOIN WINE ON WINE_REVIEW.WINE_ID = WINE.ID "
        + "LEFT JOIN GEOLOCATION on lower(WINE.REGION) like lower(GEOLOCATION.NAME) "
        + "LIMIT ? "
        + "OFFSET ?";
    ObservableList<Pair<WineReview, Wine>> wineReviewPairs = FXCollections.observableArrayList();
    try (PreparedStatement statement = connection.prepareStatement(sql)) {
      statement.setInt(1, end - begin);
      statement.setInt(2, begin);

      try (ResultSet resultSet = statement.executeQuery()) {
        while (resultSet.next()) {
          WineReview wineReview = wineReviewDao.extractWineReviewFromResultSet(resultSet,
              "wine_review_id");
          Wine wine = wineDao.extractWineFromResultSet(resultSet, "wine_id");
          wineReviewPairs.add(new Pair<>(wineReview, wine));
        }
        log.info("Successfully retrieved {} reviews with wines in range {}-{} in {}ms",
            wineReviewPairs.size(), begin, end, timer.currentOffsetMilliseconds());
        return wineReviewPairs;
      }
    } catch (SQLException e) {
      log.error("Failed to retrieve with wines in range {}-{}", begin, end, e);
    }
    return wineReviewPairs;
  }

  /**
   * Gets a list of wine reviews and wines given filters.
   *
   * @param begin   begin
   * @param end     end
   * @param filters the review filters to filter by
   * @return sub range of pairs between begin and end
   */
  public ObservableList<Pair<WineReview, Wine>> getWineReviewsAndWines(int begin, int end,
      ReviewFilters filters) {
    Timer timer = new Timer();
    String sql = "SELECT WINE.ID as wine_id, WINE.*, WINE_REVIEW.ID as wine_review_id, "
        + "WINE_REVIEW.*, GEOLOCATION.LATITUDE, GEOLOCATION.LONGITUDE "
        + "FROM WINE_REVIEW "
        + "INNER JOIN WINE ON WINE_REVIEW.WINE_ID = WINE.ID "
        + "LEFT JOIN GEOLOCATION on lower(WINE.REGION) like lower(GEOLOCATION.NAME) "
        + (filters == null ? "" : "WHERE WINE_REVIEW.USERNAME LIKE ? "
        + "AND WINE.TITLE LIKE ? "
        + "AND WINE_REVIEW.RATING BETWEEN ? AND ? ")
        + "LIMIT ? "
        + "OFFSET ?";
    ObservableList<Pair<WineReview, Wine>> wineReviewPairs = FXCollections.observableArrayList();
    try (PreparedStatement statement = connection.prepareStatement(sql)) {
      if (filters != null) {
        statement.setString(1,
            filters.getUsername().isEmpty() ? "%" : "%" + filters.getUsername() + "%");
        statement.setString(2,
            filters.getWineName().isEmpty() ? "%" : "%" + filters.getWineName() + "%");
        statement.setInt(3, filters.getMinRating());
        statement.setInt(4, filters.getMaxRating());
        statement.setInt(5, end - begin);
        statement.setInt(6, begin);
      } else {
        statement.setInt(1, end - begin);
        statement.setInt(2, begin);
      }

      try (ResultSet resultSet = statement.executeQuery()) {
        while (resultSet.next()) {
          WineReview wineReview = wineReviewDao.extractWineReviewFromResultSet(resultSet,
              "wine_review_id");
          Wine wine = wineDao.extractWineFromResultSet(resultSet, "wine_id");
          wineReviewPairs.add(new Pair<>(wineReview, wine));
        }
        log.info("Successfully retrieved {} reviews with wines in range {}-{} in {}ms",
            wineReviewPairs.size(), begin, end, timer.currentOffsetMilliseconds());
        return wineReviewPairs;
      }
    } catch (SQLException e) {
      log.error("Failed to retrieve with wines in range {}-{}", begin, end, e);
    }
    return wineReviewPairs;
  }

  /**
   * Retrieves all wines which are associated with a vineyard.
   *
   * @param vineyard The Vineyard object representing the vineyard from which to retrieve wines.
   * @return An ObservableList of Wine objects associated with the specified vineyard.
   */
  public ObservableList<Wine> getWinesFromVineyard(Vineyard vineyard) {
    Timer timer = new Timer();
    String sql = "SELECT WINE.ID as wine_id, WINE.*, GEOLOCATION.LATITUDE, GEOLOCATION.LONGITUDE "
        + "FROM WINE "
        + "LEFT JOIN GEOLOCATION on lower(WINE.REGION) like lower(GEOLOCATION.NAME) "
        + "WHERE WINERY = ?";
    ObservableList<Wine> wines = FXCollections.observableArrayList();
    try (PreparedStatement statement = connection.prepareStatement(sql)) {
      statement.setString(1, vineyard.getName());

      try (ResultSet resultSet = statement.executeQuery()) {
        while (resultSet.next()) {
          Wine wine = wineDao.extractWineFromResultSet(resultSet, "wine_id");
          wines.add(wine);
        }
      }
      log.info("Successfully retrieved {} wines from vineyard {} in {}ms",
          wines.size(), vineyard.getName(), timer.currentOffsetMilliseconds());
    } catch (SQLException e) {
      log.error("Failed to retrieve wines from vineyard {}", vineyard.getName());
    }
    return wines;
  }
}
