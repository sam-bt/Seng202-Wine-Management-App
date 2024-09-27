package seng202.team6.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import seng202.team6.model.GeoLocation;
import seng202.team6.model.Wine;
import seng202.team6.util.DatabaseObjectUniquer;
import seng202.team6.util.Timer;

/**
 * Data Access Object (DAO) for handling wine related database operations.
 */
public class WineDAO extends DAO {

  /**
   * Cache to store and reuse Wine objects to avoid duplication
   */
  private final DatabaseObjectUniquer<Wine> wineCache = new DatabaseObjectUniquer<>();

  /**
   * Constructs a new WineDAO with the given database connection.
   *
   * @param connection The database connection to be used for wine operations.
   */
  public WineDAO(Connection connection) {
    super(connection, WineDAO.class);
  }

  /**
   * Returns the SQL statements required to initialise the WINE table.
   *
   * @return Array of SQL statements for initialising the WINE table
   */
  @Override
  public String[] getInitialiseStatements() {
    return new String[]{
        "CREATE TABLE IF NOT EXISTS WINE (" +
            "ID             INTEGER       PRIMARY KEY," +
            "TITLE          VARCHAR(64)   NOT NULL," +
            "VARIETY        VARCHAR(32)," +
            "COUNTRY        VARCHAR(32)," +
            "REGION         VARCHAR(32)," +
            "WINERY         VARCHAR(64)," +
            "COLOR          VARCHAR(32)," +
            "VINTAGE        INTEGER," +
            "DESCRIPTION    INTEGER," +
            "SCORE_PERCENT  INTEGER," +
            "ABV            FLOAT," +
            "PRICE          FLOAT" +
            ")"
    };
  }

  /**
   * Retrieves the total number of wines in the database.
   *
   * @return The count of wines in the WINE table
   */
  public int getCount() {
    Timer timer = new Timer();
    String sql = "SELECT COUNT(*) FROM WINE";
    try (Statement statement = connection.createStatement()) {
      try (ResultSet resultSet = statement.executeQuery(sql)) {
        if (resultSet.next()) {
          int count = resultSet.getInt(1);
          log.info("Counted {} elements in the WINE table in {}ms", count, timer.stop());
          return count;
        }
      }
    } catch (SQLException error) {
      log.error("Failed to count the number of elements in WINE table", error);
    }
    return 0;
  }

  /**
   * Retrieves all wines from the WINE table.
   *
   * @return An ObservableList of all Wine objects in the database
   */
  public ObservableList<Wine> getAll() {
    Timer timer = new Timer();
    String sql = "SELECT * from WINE "
        + "LEFT JOIN GEOLOCATION ON LOWER(WINE.REGION) LIKE LOWER(GEOLOCATION.NAME)"
        + "ORDER BY WINE.ID ";
    try (Statement statement = connection.createStatement()) {
      try (ResultSet resultSet = statement.executeQuery(sql)) {
        ObservableList<Wine> wines = extractAllWinesFromResultSet(resultSet);
        log.info("Successfully retrieved all {} wines in {}ms", wines.size(), timer.stop());
        return wines;
      }
    } catch (SQLException error) {
      log.info("Failed to retrieve wines in range", error);
    }
    return FXCollections.emptyObservableList();
  }

  /**
   * Retrieves a range of wines from the WINE table.
   *
   * @param begin The start index of the range (inclusive)
   * @param end The end index of the range (exclusive)
   * @return An ObservableList of Wine objects within the specified range
   */
  public ObservableList<Wine> getRange(int begin, int end) {
    Timer timer = new Timer();
    String sql = "SELECT * from WINE "
        + "LEFT JOIN GEOLOCATION ON LOWER(WINE.REGION) LIKE LOWER(GEOLOCATION.NAME)"
        + "ORDER BY WINE.ID "
        + "LIMIT ? "
        + "OFFSET ?";
    try (PreparedStatement statement = connection.prepareStatement(sql)) {
      statement.setInt(1, end - begin);
      statement.setInt(2, begin);

      try (ResultSet resultSet = statement.executeQuery()) {
        ObservableList<Wine> wines = extractAllWinesFromResultSet(resultSet);
        log.info("Successfully retrieved {} wines in {}ms", wines.size(), timer.stop());
      }
    } catch (SQLException error) {
      log.info("Failed to retrieve wines in range", error);
    }
    return FXCollections.emptyObservableList();
  }

  /**
   * Replaces all wines in the WINE table by first removing all existing wines and then adding
   * the provided lists of wines.
   *
   * @param wines The list of wines to be added to the table
   */
  public void replaceAll(List<Wine> wines) {
    removeAll();
    addAll(wines);
  }

  /**
   * Adds a list of wines to the WINE table in batch mode to improve performance.
   * The batch is executed every 2048 wines to prevent excessive memory usage.
   *
   * @param wines The list of wines to be added to the table
   */
  public void addAll(List<Wine> wines) {
    Timer timer = new Timer();
    String sql = "INSERT INTO WINE VALUES (null, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    try {
      connection.setAutoCommit(false);
    } catch (SQLException error) {
      log.error("Failed to disable database auto committing", error);
    }

    int rowsAffected = 0;
    try (PreparedStatement statement = connection.prepareStatement(sql)) {
      for (int i = 0; i < wines.size(); i++) {
        setWineParameters(statement, wines.get(i), 1);
        statement.addBatch();

        if (i > 0 && i % 2048 == 0) {
          for (int rowsAffectedInBatch : statement.executeBatch()) {
            rowsAffected += rowsAffectedInBatch;
          }
        }
      }
      for (int rowsAffectedInBatch : statement.executeBatch()) {
        rowsAffected += rowsAffectedInBatch;
      }
      connection.commit();
    } catch (SQLException error) {
      log.error("Failed to add a batch of wines to the WINE table", error);
    }
    log.info("Successfully added {} out of {} wines to the WINE table in {}ms", rowsAffected, wines.size(), timer.stop());
  }

  /**
   * Removes all wines from the WINE table.
   */
  public void removeAll() {
    Timer timer = new Timer();
    String sql = "DELETE FROM WINE";
    try (Statement statement = connection.createStatement()) {
      int rowsAffected = statement.executeUpdate(sql);
      log.info("Successfully removed {} wines from the WINE table in {}ms", rowsAffected, timer.stop());
    } catch (SQLException error) {
      log.error("Failed to remove all wines from the WINE table", error);
    }
  }

  /**
   * Extracts all wines from the provided ResultSet and stores them in an ObservableList.
   *
   * @param resultSet The ResultSet from which wines are to be extracted
   * @return An ObservableList of Wine objects
   * @throws SQLException If an error occurs while processing the ResultSet
   */
  private ObservableList<Wine> extractAllWinesFromResultSet(ResultSet resultSet)
      throws SQLException {
    ObservableList<Wine> wines = FXCollections.observableArrayList();
    while (resultSet.next()) {
      wines.add(extractWineFromResultSet(resultSet));
    }
    return wines;
  }

  /**
   * Extracts a Wine object from the provided ResultSet. The wine cache is checked before creating
   * a new Wine instance
   *
   * @param resultSet The ResultSet from which wines are to be extracted
   * @return The extracted Wine object
   * @throws SQLException If an error occurs while processing the ResultSet
   */
  private Wine extractWineFromResultSet(ResultSet resultSet) throws SQLException {
    long id =  resultSet.getLong("ID");
    Wine cachedWine = wineCache.tryGetObject(id);
    if (cachedWine != null) {
      return cachedWine;
    }

    GeoLocation geoLocation = null; // todo - change this to grab the geolocation when required
    return new Wine(
        resultSet.getLong("ID"),
        null, // null DatabaseManager for now as this will be replaced
        resultSet.getString("TITLE"),
        resultSet.getString("VARIETY"),
        resultSet.getString("COUNTRY"),
        resultSet.getString("REGION"),
        resultSet.getString("WINERY"),
        resultSet.getString("COLOR"),
        resultSet.getInt("VINTAGE"),
        resultSet.getString("DESCRIPTION"),
        resultSet.getInt("SCORE_PERCENT"),
        resultSet.getFloat("ABV"),
        resultSet.getFloat("PRICE"),
        geoLocation
    );
  }

  /**
   * Sets the parameters for the PreparedStatement with the Wine objects data.
   *
   * @param statement The PreparedStatement to set the parameters for
   * @param wine The wine whose data will be used to set
   * @param startIndex The starting param index
   * @throws SQLException If an error occurs while setting the PreparedStatement's parameters
   */
  private void setWineParameters(PreparedStatement statement, Wine wine, int startIndex) throws SQLException {
    statement.setString(startIndex++, wine.getTitle());
    statement.setString(startIndex++, wine.getVariety());
    statement.setString(startIndex++, wine.getCountry());
    statement.setString(startIndex++, wine.getRegion());
    statement.setString(startIndex++, wine.getWinery());
    statement.setString(startIndex++, wine.getColor());
    statement.setInt(startIndex++, wine.getVintage());
    statement.setString(startIndex++, wine.getDescription());
    statement.setInt(startIndex++, wine.getScorePercent());
    statement.setFloat(startIndex++, wine.getAbv());
    statement.setFloat(startIndex++, wine.getPrice());
  }
}
