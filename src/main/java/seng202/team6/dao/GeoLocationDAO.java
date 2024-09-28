package seng202.team6.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import seng202.team6.util.ProcessCSV;
import seng202.team6.util.Timer;

/**
 * Data Access Object (DAO) for handling geolocation related database operations.
 */
public class GeoLocationDAO extends DAO {

  /**
   * Constructs a new GeoLocationDAO with the given database connection.
   *
   * @param connection The database connection to be used for geolocation operations.
   */
  public GeoLocationDAO(Connection connection) {
    super(connection, GeoLocationDAO.class);
  }

  /**
   * Returns the SQL statements required to initialise the GEOLOCATION table.
   *
   * @return Array of SQL statements for initialising the GEOLOCATION table
   */
  @Override
  public String[] getInitialiseStatements() {
    return new String[] {
        "CREATE TABLE IF NOT EXISTS GEOLOCATION (" +
            "NAME           VARCHAR(64)   PRIMARY KEY," +
            "LATITUDE       DECIMAL       NOT NULL," +
            "LONGITUDE      DECIMAL       NOT NULL"  +
            ")"
    };
  }

  /**
   * Adds default geolocation data to the database from a CSV file, if the GEOLOCATION table
   * is empty. The CSV file should contain geolocation data with name, latitude, and longitude.
   */
  public void addDefaultGeoLocations() {
    Timer timer = new Timer();
    if (geoLocationTableHasData()) {
      log.info("Skip loading default geolocations as the GEOLOCATION table is not empty in {}ms",
          timer.stop());
      return;
    }

    String sql = "INSERT INTO GEOLOCATION values (?, ?, ?);";
    List<String[]> rows = ProcessCSV.getCSVRows(
        getClass().getResourceAsStream("/nz_geolocations.csv"));

    int rowsAffected = batchInsertGeoLocations(sql, rows);
    log.info("Successfully added {} out of {} default geolocations in {}ms",
        rowsAffected, rows.size() - 1, timer.stop());
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
    int numberOfBatches = 1;

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
          numberOfBatches++;
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