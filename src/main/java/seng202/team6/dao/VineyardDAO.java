package seng202.team6.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import seng202.team6.model.GeoLocation;
import seng202.team6.model.Vineyard;
import seng202.team6.model.VineyardFilters;
import seng202.team6.util.DatabaseObjectUniquer;
import seng202.team6.util.Timer;

public class VineyardDAO extends DAO {

  /**
   * Cache to store and reuse Vineyard objects to avoid duplication
   */
  private final DatabaseObjectUniquer<Vineyard> vineyardCache = new DatabaseObjectUniquer<>();

  /**
   * Constructs a new VineyardDAO with the given database connection.
   *
   * @param connection The database connection to be used for vineyard operations
   */
  public VineyardDAO(Connection connection) {
    super(connection, VineyardDAO.class);
  }

  /**
   * Returns the SQL statements required to initialise the VINEYARD table.
   *
   * @return Array of SQL statements for initialising the VINEYARD table
   */
  @Override
  public String[] getInitialiseStatements() {
    return new String[]{
        "CREATE TABLE IF NOT EXISTS VINEYARD (" +
            "ID             INTEGER       PRIMARY KEY," +
            "NAME           VARCHAR(64)   NOT NULL," +
            "ADDRESS        VARCHAR(64)   NOT NULL," +
            "REGION         VARCHAR(32)   NOT NULL," +
            "WEBSITE        TEXT," +
            "DESCRIPTION    TEXT" +
            ")"
    };
  }

  /**
   * Retrieves the total number of vineyards in the database.
   *
   * @return The count of vineyards in the VINEYARD table
   */
  public int getCount() {
    Timer timer = new Timer();
    String sql = "SELECT COUNT(*) FROM VINEYARD";
    try (Statement statement = connection.createStatement()) {
      try (ResultSet resultSet = statement.executeQuery(sql)) {
        if (resultSet.next()) {
          int count = resultSet.getInt(1);
          log.info("Counted {} vineyards in {}ms", count, timer.stop());
          return count;
        }
      }
    } catch (SQLException error) {
      log.error("Failed to count the number of vineyards", error);
    }
    return 0;
  }

  /**
   * Retrieves a range of vineyards from the VINEYARD table.
   *
   * @param begin The start index of the range (inclusive)
   * @param end   The end index of the range (exclusive)
   * @param vineyardFilters The vineyard filters to be applied
   * @return An ObservableList of Vineyard objects within the specified range
   */
  public ObservableList<Vineyard> getAllInRange(int begin, int end,
      VineyardFilters vineyardFilters) {
    Timer timer = new Timer(); // todo - make query not use limit and offset
    String sql = "SELECT * FROM VINEYARD"
        + "LEFT JOIN GEOLOCATION ON LOWER(WINE.REGION) LIKE LOWER(GEOLOCATION.NAME)"
        + (vineyardFilters == null ? "" :
        "where NAME like ? "
            + "and ADDRESS like ? "
            + "and REGION like ? ")
        + "ORDER BY WINE.ID "
        + "LIMIT ? "
        + "OFFSET ?";
    try (PreparedStatement statement = connection.prepareStatement(sql)) {
      int paramIndex = 1;
      if (vineyardFilters != null) {
        statement.setString(paramIndex++,
            vineyardFilters.getName().isEmpty() ? "%" : "%" + vineyardFilters.getName() + "%");
        statement.setString(paramIndex++,
            vineyardFilters.getAddress().isEmpty() ? "%" : "%" + vineyardFilters.getAddress() + "%");
        statement.setString(paramIndex++,
            vineyardFilters.getRegion().isEmpty() ? "%" : "%" + vineyardFilters.getRegion() + "%");
      }
      statement.setInt(paramIndex++, end - begin);
      statement.setInt(paramIndex, begin);

      try (ResultSet resultSet = statement.executeQuery()) {
        ObservableList<Vineyard> vineyards = extractAllVineyardsFromResultSet(resultSet);
        log.info("Successfully retrieved {} vineyards in range {}-{} in {}ms",
            vineyards.size(), begin, end, timer.stop());
      }
    } catch (SQLException error) {
      log.info("Failed to retrieve vineyards in range {}-{}", begin, end, error);
    }
    return FXCollections.emptyObservableList();
  }

  /**
   * Extracts all vineyards from the provided ResultSet and stores them in an ObservableList.
   *
   * @param resultSet The ResultSet from which vineyards are to be extracted
   * @return An ObservableList of Vineyard objects
   * @throws SQLException If an error occurs while processing the ResultSet
   */
  private ObservableList<Vineyard> extractAllVineyardsFromResultSet(ResultSet resultSet)
      throws SQLException {
    ObservableList<Vineyard> vineyards = FXCollections.observableArrayList();
    while (resultSet.next()) {
      vineyards.add(extractVineyardFromResultSet(resultSet));
    }
    return vineyards;
  }

  /**
   * Extracts a Vineyard object from the provided ResultSet. The wine cache is checked before
   * creating a new Vineyard instance
   *
   * @param resultSet The ResultSet from which vineyards are to be extracted
   * @return The extracted Vineyard object
   * @throws SQLException If an error occurs while processing the ResultSet
   */
  private Vineyard extractVineyardFromResultSet(ResultSet resultSet) throws SQLException {
    long id = resultSet.getLong("ID");
    if (useCache()) {
      Vineyard cachedVineyard = vineyardCache.tryGetObject(id);
      if (cachedVineyard != null) {
        return cachedVineyard;
      }
    }

    GeoLocation geoLocation = createGeoLocation(resultSet);
    Vineyard vineyard = new Vineyard(
        id,
        resultSet.getString("NAME"),
        resultSet.getString("ADDRESS"),
        resultSet.getString("REGION"),
        resultSet.getString("WEBSITE"),
        resultSet.getString("DESCRIPTION"),
        geoLocation
    );
    if (useCache()) {
      vineyardCache.addObject(id, vineyard);
    }
    return vineyard;
  }

  /**
   * Extracts the latitude and longitude from the provided ResultSet and creates a new GeoLocation
   * object
   *
   * @param set The ResultSet from which geolocations are to be extracted
   * @return The extract Geolocation if available, otherwise null if either the latitude or
   * longitude were null
   * @throws SQLException If an error occurs while processing the ResultSet
   */
  private GeoLocation createGeoLocation(ResultSet set) throws SQLException {
    double latitude = set.getDouble("LATITUDE");
    if (set.wasNull()) {
      return null;
    }
    double longitude = set.getDouble("LONGITUDE");
    if (set.wasNull()) {
      return null;
    }
    return new GeoLocation(latitude, longitude);
  }
}
