package seng202.team6.unittests.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.SQLException;
import javafx.collections.ObservableList;
import org.junit.Assert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import seng202.team6.dao.UserDao;
import seng202.team6.dao.VineyardDao;
import seng202.team6.dao.VineyardTourDao;
import seng202.team6.enums.Island;
import seng202.team6.managers.DatabaseManager;
import seng202.team6.model.GeoLocation;
import seng202.team6.model.User;
import seng202.team6.model.Vineyard;
import seng202.team6.model.VineyardTour;

/**
 * Unit tests for the VineyardTourDao class, which manages the creation and modification of vineyard tours.
 * These tests verify the creation of vineyard tours, the addition and removal of vineyards to tours,
 * and querying whether a vineyard belongs to a tour.
 */
public class VineyardTourDaoTest {

  private DatabaseManager databaseManager;

  VineyardTourDao vineyardTourDao;

  User testUser;
  UserDao userDao;

  /**
   * Sets up the database manager and the VineyardTourDao before each test.
   * Also adds a test user for use in the tests.
   *
   * @throws SQLException if an error occurs during database setup.
   */
  @BeforeEach
  void setup() throws SQLException {
    databaseManager = new DatabaseManager();
    vineyardTourDao = databaseManager.getVineyardTourDao();
    vineyardTourDao.setUseCache(false);

    testUser = new User("testUser", "testPassword1!", "user", "egsalt");
    userDao = databaseManager.getUserDao();
    userDao.add(testUser);

  }

  /**
   * Tears down the database after each test, removing any added data and resetting the state.
   *
   * @throws SQLException if an error occurs during database teardown.
   */
  @AfterEach
  void teardown() throws SQLException {
    databaseManager.teardown();
  }

  /**
   * Tests the creation of a vineyard tour.
   * Verifies that a vineyard tour can be created successfully and that the name is correctly set.
   */
  @Test
  void testCreateTour() {

    User testUser = new User("testUser", "testPassword1!", "user", "egsalt");
    UserDao userDao = databaseManager.getUserDao();
    userDao.add(testUser);
    VineyardTourDao vineyardTourDao = databaseManager.getVineyardTourDao();

    VineyardTour testVineyardTour = vineyardTourDao.create(testUser, "testTour", Island.SOUTH);
    assertEquals(testVineyardTour.getName(), "testTour");

  }

  /**
   * Tests retrieving all vineyard tours for a specific user.
   * Verifies that the correct number of vineyard tours is retrieved and their names match the expected values.
   */
  @Test
  void testGetToursFromUser() {

    VineyardTourDao vineyardTourDao = databaseManager.getVineyardTourDao();

    VineyardTour testVineyardTour1 = vineyardTourDao.create(testUser, "test2Tour", Island.SOUTH);
    VineyardTour testVineyardTour2 = vineyardTourDao.create(testUser, "test2Tour", Island.SOUTH);

    ObservableList<VineyardTour> vineyardTours = vineyardTourDao.getAll(testUser);

    assertEquals(2, vineyardTours.size());
    assertEquals(testVineyardTour1.getName(), vineyardTours.get(0).getName());
    assertEquals(testVineyardTour2.getName(), vineyardTours.get(1).getName());

  }

  /**
   * Tests adding a vineyard to a vineyard tour.
   * Verifies that the vineyard is successfully added to the tour.
   */
  @Test
  void testAddVineyard() {
    VineyardDao vineyardDao = databaseManager.getVineyardsDao();
    Vineyard testVineyard = vineyardDao.add("Test1 Vineyard", "Christchurch", "Canterbury",
        "www.test.com", "test", "www.test.com", new GeoLocation(-43.532, 172.6306));

    VineyardTour testVineyardTour = vineyardTourDao.create(testUser, "test2Tour", Island.SOUTH);

    vineyardTourDao.addVineyard(testVineyardTour, testVineyard);

    assertTrue(vineyardTourDao.isVineyardInTour(testVineyardTour, testVineyard));

  }

  /**
   * Tests whether a vineyard is part of a specific vineyard tour.
   * Verifies that the vineyard is correctly recognized as being part of the tour.
   */
  @Test
  void testIsVineyardInTourTrue() {

    VineyardDao vineyardDao = databaseManager.getVineyardsDao();
    Vineyard testVineyard1 = vineyardDao.add("Test1 Vineyard", "Christchurch", "Canterbury",
        "www.test.com", "test", "www.test.com", new GeoLocation(-43.532, 172.6306));
    Vineyard testVineyard2 = vineyardDao.add("Test2 Vineyard", "Dunedin", "Otago",
        "www.fake.com", "oops", "www.dog.com", new GeoLocation(-45.8788,170.5028));


    VineyardTour testVineyardTour = vineyardTourDao.create(testUser, "test2Tour", Island.SOUTH);

    vineyardTourDao.addVineyard(testVineyardTour, testVineyard1);
    vineyardTourDao.addVineyard(testVineyardTour, testVineyard2);

    assertTrue(vineyardTourDao.isVineyardInTour(testVineyardTour, testVineyard1));
    assertTrue(vineyardTourDao.isVineyardInTour(testVineyardTour, testVineyard2));

  }

  /**
   * Tests whether a vineyard is not part of a specific vineyard tour.
   * Verifies that the vineyard is correctly recognized as not being part of the tour.
   */
  @Test
  void testIsVineyardInTourFalse() {

    VineyardDao vineyardDao = databaseManager.getVineyardsDao();
    Vineyard testVineyard1 = vineyardDao.add("Test1 Vineyard", "Christchurch", "Canterbury",
        "www.test.com", "test", "www.test.com", new GeoLocation(-43.532, 172.6306));
    Vineyard testVineyard2 = vineyardDao.add("Test2 Vineyard", "Dunedin", "Otago",
        "www.fake.com", "oops", "www.dog.com", new GeoLocation(-45.8788,170.5028));


    VineyardTour testVineyardTour = vineyardTourDao.create(testUser, "test2Tour", Island.SOUTH);

    vineyardTourDao.addVineyard(testVineyardTour, testVineyard1);

    assertTrue(vineyardTourDao.isVineyardInTour(testVineyardTour, testVineyard1));
    assertFalse(vineyardTourDao.isVineyardInTour(testVineyardTour, testVineyard2));

  }

  /**
   * Tests removing a vineyard from a vineyard tour.
   * Verifies that the vineyard is correctly removed from the tour and that the other vineyards remain in the tour.
   */
  @Test
  void testRemoveVineyardFromTour() {

    VineyardDao vineyardDao = databaseManager.getVineyardsDao();
    Vineyard testVineyard1 = vineyardDao.add("Test1 Vineyard", "Christchurch", "Canterbury",
        "www.test.com", "test", "www.test.com", new GeoLocation(-43.532, 172.6306));
    Vineyard testVineyard2 = vineyardDao.add("Test2 Vineyard", "Dunedin", "Otago",
        "www.fake.com", "oops", "www.dog.com", new GeoLocation(-45.8788,170.5028));


    VineyardTour testVineyardTour = vineyardTourDao.create(testUser, "test2Tour", Island.SOUTH);

    vineyardTourDao.addVineyard(testVineyardTour, testVineyard1);
    vineyardTourDao.addVineyard(testVineyardTour, testVineyard2);

    assertTrue(vineyardTourDao.isVineyardInTour(testVineyardTour, testVineyard1));
    assertTrue(vineyardTourDao.isVineyardInTour(testVineyardTour, testVineyard2));

    vineyardTourDao.removeVineyard(testVineyardTour, testVineyard1);

    assertFalse(vineyardTourDao.isVineyardInTour(testVineyardTour, testVineyard1));
    assertTrue(vineyardTourDao.isVineyardInTour(testVineyardTour, testVineyard2));


  }


}
