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
  private final DatabaseObjectUniquer<Wine> wineCache = new DatabaseObjectUniquer<>();

  /**
   * Constructs a new WineDAO with the given database connection.
   *
   * @param connection The database connection to be used for wine operations.
   */
  public WineDAO(Connection connection) {
    super(connection, WineDAO.class);
  }

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

  public void replaceAll(List<Wine> wines) {
    removeAll();
    addAll(wines);
  }

  public void addAll(List<Wine> wines) {
    Timer timer = new Timer();
    String sql = "INSERT INTO WINE VALUES (null, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    try {
      connection.setAutoCommit(false);
    } catch (SQLException error) {
      log.error("Failed to disable database auto committing", error);
    }

    try (PreparedStatement statement = connection.prepareStatement(sql)) {
      for (int i = 0; i < wines.size(); i++) {
        setWineParameters(statement, wines.get(i), 1);
        if (i % 2048 == 0) {
          statement.executeBatch();
        }
      }
      statement.executeBatch();
      connection.commit();
    } catch (SQLException error) {
      log.error("Failed to add a batch of wines to the WINE table", error);
    }
    log.info("Successfully added {} wines to the WINE table in {}ms", wines.size(), timer.stop());
  }

  public void removeAll() {
    Timer timer = new Timer();
    String sql = "REMOVE FROM WINE";
    try (Statement statement = connection.createStatement()) {
      int rowsAffected = statement.executeUpdate(sql);
      log.info("Successfully removed {} wines from the WINE table in {}ms", rowsAffected, timer.stop());
    } catch (SQLException error) {
      log.error("Failed to remove all wines from the WINE table", error);
    }
  }

  private ObservableList<Wine> extractAllWinesFromResultSet(ResultSet resultSet)
      throws SQLException {
    ObservableList<Wine> wines = FXCollections.observableArrayList();
    while (resultSet.next()) {
      wines.add(extractWineFromResultSet(resultSet));
    }
    return wines;
  }

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
