package seng202.team6.unittests.dao;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javafx.collections.ObservableList;
import org.apache.xmlbeans.impl.xb.xsdschema.Attribute.Use;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import seng202.team6.dao.GeoLocationDao;
import seng202.team6.dao.UserDao;
import seng202.team6.dao.VineyardDao;
import seng202.team6.dao.VineyardTourDao;
import seng202.team6.dao.WineDao;
import seng202.team6.enums.Island;
import seng202.team6.managers.DatabaseManager;
import seng202.team6.model.GeoLocation;
import seng202.team6.model.User;
import seng202.team6.model.Vineyard;
import seng202.team6.model.VineyardFilters;
import seng202.team6.model.VineyardTour;
import seng202.team6.model.Wine;
import seng202.team6.model.WineDatePair;
import seng202.team6.model.WineFilters;
import seng202.team6.service.VineyardService;

/**
 * Unit tests for the VineyardDao class, which handles database operations related to vineyard
 * management. These tests verify the correct addition, retrieval, and counting of vineyards,
 * as well as ensuring the integrity of vineyard tours.
 *
 */
public class VineyardDaoTest {

  private DatabaseManager databaseManager;
  VineyardDao vineyardDao;
  Vineyard testVineyard1;
  Vineyard testVineyard2;
  Vineyard testVineyard3;

  /**
   * Initializes the database manager and VineyardDao before each test.
   * Also adds three test vineyards for use in the tests.
   *
   * @throws SQLException if there is an error initializing the database.
   */
  @BeforeEach
  void setup() throws SQLException {
    databaseManager = new DatabaseManager();
    vineyardDao = databaseManager.getVineyardsDao();
    vineyardDao.setUseCache(false);

    testVineyard1 = vineyardDao.add("Test1 Vineyard", "Christchurch", "Canterbury",
        "www.test.com", "test", "www.test.com", new GeoLocation(-43.532,172.6306));
    testVineyard2 = vineyardDao.add("Test2 Vineyard", "Dunedin", "Otago",
        "www.fake.com", "oops", "www.dog.com", new GeoLocation(-45.8788,170.5028));
    testVineyard3 = vineyardDao.add("Test3 Vineyard", "999 Bill Street", "Northland",
        "www.lol.com", "cat", "www.uno.com", new GeoLocation(-134.643, 159.09));
  }

  /**
   * Tears down the database after each test, removing the added test vineyards and cleaning up.
   *
   * @throws SQLException if there is an error during database teardown.
   */
  @AfterEach
  void teardown() throws SQLException {
    databaseManager.teardown();
  }

  /**
   * Tests the initialization of the VineyardService.
   * Ensures that the service can be initialized without throwing any exceptions.
   */
  @Test
  void testInit() throws SQLException {
    VineyardService vineyardService = new VineyardService(databaseManager);
    assertDoesNotThrow(vineyardService::init);
  }

  /**
   * Tests the vineyard count in the database.
   * Verifies that the correct number of vineyards (including default and added) is returned.
   */
  @Test
  void testGetCount() throws SQLException {

    int count = vineyardDao.getCount();

    assertEquals(count, 3); // 8 default plus 3 extras
  }

  /**
   * Tests retrieving all vineyards within a specified range.
   * Verifies that the retrieved vineyards match the expected number and details.
   */
  @Test
  void testGetAllInRange() throws SQLException {
    ObservableList<Vineyard> result = vineyardDao.getAllInRange(0, Integer.MAX_VALUE, null);

    assertNotNull(result);

    int size = result.size();
    assertEquals(3, size); // 8 default plus 3 extra

    assertEquals(result.get(0).getName(), "Test1 Vineyard");
    assertEquals(result.get(1).getName(), "Test2 Vineyard");
    assertEquals(result.get(2).getName(), "Test3 Vineyard");
  }

  /**
   * Tests retrieving all vineyards within a specified range with filters.
   * Verifies that the retrieved vineyards match the expected number and details.
   */
  @Test
  void testGetAllInRangeWithFilters() throws SQLException {

    VineyardFilters filters = new VineyardFilters("Test", "", "Nor");

    ObservableList<Vineyard> result = vineyardDao.getAllInRange(0, Integer.MAX_VALUE, filters);

    assertNotNull(result);



    int size = result.size();
    assertEquals(1, size);

    assertEquals(result.get(0).getName(), "Test3 Vineyard");
  }

  /**
   * Tests retrieving a vineyard by name.
   * Verifies that the correct vineyard is returned and the data matches the expected values.
   */
  @Test
  void testGetByName() {

    Vineyard result1 = vineyardDao.get("Test1 Vineyard");

    assertEquals(result1.getName(), "Test1 Vineyard");

    Vineyard result2 = vineyardDao.get("Test3 Vineyard");

    assertEquals(result2.getDescription(), "cat");

  }

  /**
   * Tests retrieving all vineyards from a vineyard tour.
   * Verifies that the vineyards associated with a tour are correctly retrieved.
   */
  @Test
  void testGetAllFromTour() {

    User testUser = new User("testUser", "testPassword1!", "user", "egsalt");
    UserDao userDao = databaseManager.getUserDao();
    userDao.add(testUser);
    VineyardTourDao vineyardTourDao = databaseManager.getVineyardTourDao();
    VineyardTour testTour = vineyardTourDao.create(testUser, "testTour");

    vineyardTourDao.addVineyard(testTour, testVineyard1);
    vineyardTourDao.addVineyard(testTour, testVineyard2);

    List<Vineyard> vineyards = vineyardDao.getAllFromTour(testTour);

    assertNotNull(vineyards);
    assertEquals(2, vineyards.size());

    assertEquals("Test1 Vineyard", vineyards.get(0).getName());
    assertEquals("Test2 Vineyard", vineyards.get(1).getName());

  }

  /**
   * Tests whether the vineyard table has data.
   * Verifies that the database contains vineyards and that the data is correctly loaded.
   */
  @Test
  void testVineyardHasData() {

    assertTrue(vineyardDao.vineyardsTableHasData());

  }

}