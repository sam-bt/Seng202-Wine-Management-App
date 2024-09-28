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

  public void addDefaultGeoLocations() {
    Timer timer = new Timer();
    String sql = "SELECT 1 FROM GEOLOCATION";
    try (Statement statement = connection.createStatement()) {
      ResultSet set = statement.executeQuery(sql);
      if (set.next()) {
        log.info("Skip loading default geolocations as the GEOLOCATION table is not empty in {}ms",
            timer.stop());
        return;
      }
    } catch (SQLException error) {
      log.error("Failed to check if default geolocations have been added", error);
    }

    sql = "INSERT INTO GEOLOCATION values (?, ?, ?);";
    List<String[]> rows = ProcessCSV.getCSVRows(
        getClass().getResourceAsStream("/nz_geolocations.csv"));

    int rowsAffected = 0;
    int numberOfBatches = 1;
    try (PreparedStatement statement = connection.prepareStatement(sql)) {
      for (int i = 1; i < rows.size(); i++) {
        String[] row = rows.get(i);
        String name = row[0];
        double latitude = Double.parseDouble(row[1]);
        double longitude = Double.parseDouble(row[2]);
        int queryIndex = 1;
        statement.setString(queryIndex++, name);
        statement.setDouble(queryIndex++, latitude);
        statement.setDouble(queryIndex++, longitude);
        statement.addBatch();

        if (i > 1 && i % 2048 == 0) {
          for (int rowsAffectedInBatch : statement.executeBatch()) {
            rowsAffected += rowsAffectedInBatch;
          }
          numberOfBatches++;
        }
      }
      for (int rowsAffectedInBatch : statement.executeBatch()) {
        rowsAffected += rowsAffectedInBatch;
      }

    } catch (SQLException error) {
      log.error("Failed to add default geolocations", error);
      return;
    }
    log.info("Successfully added {} out of {} default geolocations using {} batches in {}ms", rowsAffected,
        rows.size() - 1, numberOfBatches, timer.stop());
  }
}
