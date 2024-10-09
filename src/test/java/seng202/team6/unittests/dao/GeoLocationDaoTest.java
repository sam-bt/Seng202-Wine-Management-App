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

public class GeoLocationDaoTest {

  private DatabaseManager databaseManager;
  private GeoLocationDao geoLocationDao;

  @BeforeEach
  void setup() throws SQLException {
    databaseManager = new DatabaseManager();
    geoLocationDao = databaseManager.getGeoLocationDao();
    geoLocationDao.setUseCache(false);
  }

  @AfterEach
  void teardown() throws SQLException {
    databaseManager.teardown();
  }

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