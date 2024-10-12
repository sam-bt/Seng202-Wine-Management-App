package seng202.team6.unittests.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import seng202.team6.dao.GeoLocationDao;
import seng202.team6.dao.WineDao;
import seng202.team6.managers.DatabaseManager;
import seng202.team6.model.GeoLocation;
import seng202.team6.model.Wine;


/**
 * Unit tests for the GeoLocationDao class, which handles database operations
 * related to geographic locations. These tests verify the functionality of adding
 * multiple geolocations to the database and checking the existence of location names.
 *
 */
public class GeoLocationDaoTest {

  private DatabaseManager databaseManager;
  private GeoLocationDao geoLocationDao;

  /**
   * Initializes the database manager and GeoLocationDao before each test.
   * Disables caching for geolocation operations to ensure consistency in tests.
   *
   * @throws SQLException if there is an error initializing the database.
   */
  @BeforeEach
  void setup() throws SQLException {
    databaseManager = new DatabaseManager();
    geoLocationDao = databaseManager.getGeoLocationDao();
  }

  /**
   * Tears down the database after each test, cleaning up any added test data.
   *
   * @throws SQLException if there is an error during database teardown.
   */
  @AfterEach
  void teardown() throws SQLException {
    databaseManager.teardown();
  }

  /**
   * Tests the functionality of adding multiple geolocations to the database.
   * Verifies that the correct number of geolocations can be successfully retrieved
   * by name, ensuring that locations were added correctly.
   *
   * @throws SQLException if there is an error adding or retrieving data from the database.
   */
  @Test
  void testAddAllGeoLocations() throws SQLException {
    Map<String, GeoLocation> geoLocations = new HashMap<>();
    geoLocations.put("Location1", new GeoLocation(-45.0312, 168.6626));
    geoLocations.put("Location2", new GeoLocation(40.7128, -74.0060));
    geoLocations.put("Location3", new GeoLocation(48.8566, 2.3522));

    geoLocationDao.addAll(geoLocations);

    Set<String> locationNamesToCheck = new HashSet<>();
    locationNamesToCheck.add("Location1");
    locationNamesToCheck.add("Location2");
    locationNamesToCheck.add("Location3");
    locationNamesToCheck.add("NonExistentLocation");

    Set<String> existingLocationNames = geoLocationDao.getExistingLocationNames(locationNamesToCheck);

    assertNotNull(existingLocationNames);
    assertEquals(3, existingLocationNames.size());
  }

  /**
   * Tests the retrieval of existing geolocations from the database by a set of location names.
   * Verifies that the correct geolocations are returned for valid names and that non-existent
   * locations are handled correctly.
   *
   * @throws SQLException if there is an error retrieving data from the database.
   */
  @Test
  void testGetExistingGeoLocations() throws SQLException {
    Map<String, GeoLocation> geoLocations = new HashMap<>();
    geoLocations.put("Location1", new GeoLocation(-45.0312, 168.6626));
    geoLocations.put("Location2", new GeoLocation(40.7128, -74.0060));
    geoLocations.put("Location3", new GeoLocation(48.8566, 2.3522));

    geoLocationDao.addAll(geoLocations);

    Set<String> locationNamesToCheck = new HashSet<>();
    locationNamesToCheck.add("Location1");
    locationNamesToCheck.add("Location2");
    locationNamesToCheck.add("Location3");
    locationNamesToCheck.add("NonExistentLocation");

    Set<String> existingLocationNames = geoLocationDao.getExistingLocationNames(locationNamesToCheck);

    assertTrue(existingLocationNames.contains("Location1"));
    assertTrue(existingLocationNames.contains("Location2"));
    assertTrue(existingLocationNames.contains("Location3"));

    assertFalse(existingLocationNames.contains("NonExistentLocation"));

  }

}