package seng202.team6.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import seng202.team6.model.GeoLocation;
import seng202.team6.model.Vineyard;
import seng202.team6.model.VineyardFilters;
import seng202.team6.model.VineyardTour;
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
            "DESCRIPTION    TEXT," +
            "LOGO_URL       TEXT" +
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
    String sql = "SELECT * FROM VINEYARD "
        + "LEFT JOIN GEOLOCATION ON LOWER(VINEYARD.ADDRESS) LIKE LOWER(GEOLOCATION.NAME)"
        + (vineyardFilters == null ? "" :
        "where NAME like ? "
            + "and ADDRESS like ? "
            + "and REGION like ? ")
        + "ORDER BY VINEYARD.ID "
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
        return vineyards;
      }
    } catch (SQLException error) {
      log.info("Failed to retrieve vineyards in range {}-{}", begin, end, error);
    }
    return FXCollections.emptyObservableList();
  }

  public Vineyard get(String name) {
    Timer timer = new Timer();
    String sql = "SELECT * FROM VINEYARD " +
        "LEFT JOIN GEOLOCATION ON LOWER(VINEYARD.ADDRESS) LIKE LOWER(GEOLOCATION.NAME) " +
        "WHERE VINEYARD.NAME = ?";
    try (PreparedStatement statement = connection.prepareStatement(sql)) {
      statement.setString(1, name);
      try (ResultSet resultSet = statement.executeQuery()) {
        if (resultSet.next()) {
          log.info("Successfully retrieved vineyard with name '{}' in {}ms", name,
              timer.stop());
          return extractVineyardFromResultSet(resultSet);
        }
        log.warn("Could not retrieve vineyard with name '{}' in {}ms", name,
            timer.stop());
      }
    } catch (SQLException error) {
      log.info("Failed to retrieve vineyard with name {}", name, error);
    }
    return null;
  }

  public void addAll(List<Vineyard> vineyards) {
    Timer timer = new Timer();
    String sql = "INSERT INTO VINEYARD values (null, ?, ?, ?, ?, ?, ?);";
    try (PreparedStatement statement = connection.prepareStatement(sql)) {
      for (Vineyard vineyard : vineyards) {
        statement.setString(1, vineyard.getName());
        statement.setString(2, vineyard.getAddress());
        statement.setString(3, vineyard.getRegion());
        statement.setString(4, vineyard.getWebsite());
        statement.setString(5, vineyard.getDescription());
        statement.setString(6, vineyard.getLogoUrl());
        statement.addBatch();
      }

      int rowsAffected = Arrays.stream(statement.executeBatch()).sum();
      log.info("Successfully added {} vineyards in {}ms",
          rowsAffected, rowsAffected, timer.stop());
    } catch (SQLException error) {
      log.error("Failed to add vineyards", error);
    }
  }

  public List<Vineyard> getAllFromTour(VineyardTour vineyardTour) {
    Timer timer = new Timer();
    String sql = "SELECT ID FROM VINEYARD_TOUR_ITEM " +
            "LEFT JOIN VINEYARD ON VINEYARD.ID = VINEYARD_TOUR_ITEM.VINEYARD_ID " +
            "WHERE TOUR_ID = ?";
    try (PreparedStatement statement = connection.prepareStatement(sql)) {
      statement.setLong(1, vineyardTour.getId());

      try (ResultSet resultSet = statement.executeQuery()) {
        ObservableList<Vineyard> vineyards = extractAllVineyardsFromResultSet(resultSet);
        log.info("Successfully retrieved all {} vineyards in tour '{}' in {}ms",
                vineyards.size(),  vineyardTour.getName(), timer.stop());
        return vineyards;
      }
    } catch (SQLException error) {
      log.info("Failed to retrieve vineyards in tour '{}'", vineyardTour.getName(), error);
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
        resultSet.getString("LOGO_URL"),
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

  /**
   * Checks if the GEOLOCATION table already has data to avoid re-adding default geolocations.
   *
   * @return true if the GEOLOCATION table has data, false otherwise.
   */
  public boolean vineyardsTableHasData() {
    String sql = "SELECT 1 FROM VINEYARD";
    try (Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(sql)) {
      return resultSet.next();
    } catch (SQLException error) {
      log.error("Failed to check if default vineyard have been added", error);
      return false;
    }
  }

  /**
   * Inserts the default vineyards data in batches from the CSV file into the database.
   *
   * @param sql  The SQL insert statement for geolocations.
   * @param rows The list of vineyard data from the CSV.
   * @return The total number of rows successfully inserted into the database.
   */
  private int batchInsertVineyards(String sql, List<String[]> rows) {
    int rowsAffected = 0;
    int batchSize = 2048;

    try (PreparedStatement statement = connection.prepareStatement(sql)) {
      for (int i = 1; i < rows.size(); i++) {
        String[] row = rows.get(i);
        String name = row[0];
        String address = row[1];
        String region = row[2];
        String website = row[3];
        String description = row[4];
        String logoUrl = row[5];

        statement.setString(1, name);
        statement.setString(2, address);
        statement.setString(3, region);
        statement.setString(4, website);
        statement.setString(5, description);
        statement.setString(6, logoUrl);
        statement.addBatch();

        if (i > 1 && i % batchSize == 0) {
          rowsAffected += executeBatch(statement);
        }
      }
      rowsAffected += executeBatch(statement);

    } catch (SQLException error) {
      log.error("Failed to add default vineyards", error);
    }
    return rowsAffected;
  }

  /**
   * Executes a batch of insert operations for geolocation data.
   *
   * @param statement The prepared statement with batched insert operations.
   * @return The total number of rows affected by the batch execution.
   * @throws SQLException If there is an error executing the batch.
   */
  private int executeBatch(PreparedStatement statement) throws SQLException {
    int rowsAffected = 0;
    for (int rowsAffectedInBatch : statement.executeBatch()) {
      rowsAffected += rowsAffectedInBatch;
    }
    return rowsAffected;
  }
}
