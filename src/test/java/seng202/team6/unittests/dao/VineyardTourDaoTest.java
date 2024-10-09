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

public class VineyardTourDaoTest {

  private DatabaseManager databaseManager;

  VineyardTourDao vineyardTourDao;

  User testUser;
  UserDao userDao;

  @BeforeEach
  void setup() throws SQLException {
    databaseManager = new DatabaseManager();
    vineyardTourDao = databaseManager.getVineyardTourDao();
    vineyardTourDao.setUseCache(false);

    testUser = new User("testUser", "testPassword1!", "user", "egsalt");
    userDao = databaseManager.getUserDao();
    userDao.add(testUser);

  }

  @AfterEach
  void teardown() throws SQLException {
    databaseManager.teardown();
  }

  @Test
  void testCreateTour() {

    User testUser = new User("testUser", "testPassword1!", "user", "egsalt");
    UserDao userDao = databaseManager.getUserDao();
    userDao.add(testUser);
    VineyardTourDao vineyardTourDao = databaseManager.getVineyardTourDao();

    VineyardTour testVineyardTour = vineyardTourDao.create(testUser, "testTour", Island.SOUTH);
    assertEquals(testVineyardTour.getName(), "testTour");

  }

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

  @Test
  void testAddVineyard() {
    VineyardDao vineyardDao = databaseManager.getVineyardsDao();
    Vineyard testVineyard = vineyardDao.add("Test1 Vineyard", "Christchurch", "Canterbury",
        "www.test.com", "test", "www.test.com", new GeoLocation(-43.532, 172.6306));

    VineyardTour testVineyardTour = vineyardTourDao.create(testUser, "test2Tour", Island.SOUTH);

    vineyardTourDao.addVineyard(testVineyardTour, testVineyard);

    assertTrue(vineyardTourDao.isVineyardInTour(testVineyardTour, testVineyard));

  }

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
