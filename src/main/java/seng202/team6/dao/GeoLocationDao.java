package seng202.team6.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import seng202.team6.model.GeoLocation;
import seng202.team6.util.ProcessCsv;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import seng202.team6.model.GeoLocation;
import seng202.team6.util.ProcessCsv;
import seng202.team6.util.Timer;

/**
 * Data Access Object (DAO) for handling geolocation related database operations.
 */
public class GeoLocationDao extends Dao {

  /**
   * Constructs a new GeoLocationDAO with the given database connection.
   *
   * @param connection The database connection to be used for geolocation operations.
   */
  public GeoLocationDao(Connection connection) {
    super(connection, GeoLocationDao.class);
  }

  /**
   * Returns the SQL statements required to initialise the GEOLOCATION table.
   *
   * @return Array of SQL statements for initialising the GEOLOCATION table
   */
  @Override
  public String[] getInitialiseStatements() {
    return new String[]{
        "CREATE TABLE IF NOT EXISTS GEOLOCATION ("
            + "NAME           VARCHAR(64)   PRIMARY KEY,"
            + "LATITUDE       DECIMAL       NOT NULL,"
            + "LONGITUDE      DECIMAL       NOT NULL"
            + ")"
    };
  }

  /**
   * Adds default geolocation data to the database from a CSV file, if the GEOLOCATION table is
   * empty. The CSV file should contain geolocation data with name, latitude, and longitude.
   */
  public void addDefaultGeoLocations() {
    Timer timer = new Timer();
    if (geoLocationTableHasData()) {
      log.info("Skip loading default geolocations as the GEOLOCATION table is not empty in {}ms",
          timer.currentOffsetMilliseconds());
      return;
    }

    String sql = "INSERT INTO GEOLOCATION values (?, ?, ?);";
    List<String[]> rows = ProcessCsv.getCsvRows(
        getClass().getResourceAsStream("/data/nz_geolocations.csv"));

    int rowsAffected = batchInsertGeoLocations(sql, rows);
    log.info("Successfully added {} out of {} default geolocations in {}ms",
        rowsAffected, rows.size(), timer.currentOffsetMilliseconds());
  }

  public void addAll(Map<String, GeoLocation> geoLocations) {
    Timer timer = new Timer();
    String sql = "INSERT INTO GEOLOCATION values (?, ?, ?);";
    try (PreparedStatement statement = connection.prepareStatement(sql)) {
      for (Entry<String, GeoLocation> entry : geoLocations.entrySet()) {
        statement.setString(1, entry.getKey());
        statement.setDouble(2, entry.getValue().getLatitude());
        statement.setDouble(3, entry.getValue().getLongitude());
        statement.addBatch();
      }

      int rowsAffected = Arrays.stream(statement.executeBatch()).sum();
      log.info("Successfully added {} geolocations in {}ms",
          rowsAffected, rowsAffected, timer.currentOffsetMilliseconds());
    } catch (SQLException error) {
      log.error("Failed to add geolocations", error);
    }
  }

  public Set<String> getExistingLocationNames(Set<String> locationNames) {
    Timer timer = new Timer();
    // Collections.nCopies just repeats '?' n times
    String sql = "SELECT NAME FROM GEOLOCATION WHERE NAME IN (" +
        String.join(",", Collections.nCopies(locationNames.size(), "?")) + ")";
    Set<String> existingLocationNames = new HashSet<>();

    try (PreparedStatement statement = connection.prepareStatement(sql)) {
      int paramIndex = 1;
      for (String locationName : locationNames) {
        statement.setString(paramIndex++, locationName);
      }

      try (ResultSet resultSet = statement.executeQuery()) {
        while (resultSet.next()) {
          existingLocationNames.add(resultSet.getString("NAME"));
        }
      }
      log.info("Successfully found {} out of {} location names in {}ms",
          existingLocationNames.size(), locationNames.size(), timer.currentOffsetMilliseconds());
    } catch (SQLException error) {
      log.error("Failed to retrieve locations names that match", error);
    }
    return existingLocationNames;
  }

  /**
   * Checks if the GEOLOCATION table already has data to avoid re-adding default geolocations.
   *
   * @return true if the GEOLOCATION table has data, false otherwise.
   */
  private boolean geoLocationTableHasData() {
    String sql = "SELECT 1 FROM GEOLOCATION";
    try (Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(sql)) {
      return resultSet.next();
    } catch (SQLException error) {
      log.error("Failed to check if default geolocations have been added", error);
      return false;
    }
  }

  /**
   * Inserts the default geolocation data in batches from the CSV file into the database.
   *
   * @param sql  The SQL insert statement for geolocations.
   * @param rows The list of geolocation data (name, latitude, longitude) from the CSV.
   * @return The total number of rows successfully inserted into the database.
   */
  private int batchInsertGeoLocations(String sql, List<String[]> rows) {
    int rowsAffected = 0;
    int batchSize = 2048;

    try (PreparedStatement statement = connection.prepareStatement(sql)) {
      for (int i = 1; i < rows.size(); i++) {
        String[] row = rows.get(i);
        String name = row[0];
        double latitude = Double.parseDouble(row[1]);
        double longitude = Double.parseDouble(row[2]);

        statement.setString(1, name);
        statement.setDouble(2, latitude);
        statement.setDouble(3, longitude);
        statement.addBatch();

        if (i > 1 && i % batchSize == 0) {
          rowsAffected += executeBatch(statement);
        }
      }
      rowsAffected += executeBatch(statement);

    } catch (SQLException error) {
      log.error("Failed to add default geolocations", error);
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