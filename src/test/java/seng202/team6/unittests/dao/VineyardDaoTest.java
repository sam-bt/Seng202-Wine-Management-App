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
import javafx.collections.ObservableList;
import org.apache.xmlbeans.impl.xb.xsdschema.Attribute.Use;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
import seng202.team6.model.VineyardTour;
import seng202.team6.model.Wine;
import seng202.team6.model.WineDatePair;

public class VineyardDaoTest {

  private DatabaseManager databaseManager;
  private VineyardDao vineyardDao;
  Vineyard testVineyard1;
  Vineyard testVineyard2;
  Vineyard testVineyard3;

  @BeforeEach
  void setup() throws SQLException {
    databaseManager = new DatabaseManager();
    vineyardDao = databaseManager.getVineyardsDao();
    vineyardDao.setUseCache(false);

    testVineyard1 = new Vineyard(1, "Test1 Vineyard", "9 Maidstone Road", "Tasman",
        "www.test.com", "test", "www.test.com", new GeoLocation(-43.52017341146717, 172.57804428385361));
    testVineyard2 = new Vineyard(2, "Test2 Vineyard", "111 Test Road", "Taranaki",
        "www.fake.com", "oops", "www.dog.com", new GeoLocation(-41.636723680359104, 172.25705105084347));
    testVineyard3 = new Vineyard(2, "Test3 Vineyard", "999 Bill Street", "Northland",
        "www.lol.com", "cat", "www.uno.com", new GeoLocation(-134.643, 159.09));
    vineyardDao.addAll(List.of(testVineyard1, testVineyard2, testVineyard3));

  }

  @AfterEach
  void teardown() throws SQLException {
    databaseManager.teardown();
  }

  @Test
  void testGetCount() throws SQLException {

    int count = vineyardDao.getCount();

    assertEquals(count, 11); // 8 default plus 3 extras
  }

  @Test
  void testGetAllInRange() throws SQLException {

    int start = 5;
    int end = 100;

    ObservableList<Vineyard> result = vineyardDao.getAllInRange(start, end, null);

    assertNotNull(result);

    int size = result.size();
    assertEquals(6, size); // 8 default plus 3 extra

    assertEquals(result.get(size - 3).getName(), "Test1 Vineyard");
    assertEquals(result.get(size - 2).getName(), "Test2 Vineyard");
    assertEquals(result.get(size - 1).getName(), "Test3 Vineyard");
  }

  @Test
  void testGetByName() {

    Vineyard result1 = vineyardDao.get("Test1 Vineyard");

    assertEquals(result1.getName(), "Test1 Vineyard");

    Vineyard result2 = vineyardDao.get("Test3 Vineyard");

    assertEquals(result2.getDescription(), "cat");

  }

  @Test
  void testGetAllFromTour() {

    User testUser = new User("testUser", "testPassword1!", "user", "egsalt");
    UserDao userDao = databaseManager.getUserDao();
    userDao.add(testUser);
    VineyardTourDao vineyardTourDao = databaseManager.getVineyardTourDao();
    VineyardTour testTour = new VineyardTour(1, "testUser", "testTour", Island.SOUTH);
    vineyardTourDao.create(testUser, "testTour", Island.SOUTH);

    vineyardTourDao.addVineyard(testTour, testVineyard1);
    vineyardTourDao.addVineyard(testTour, testVineyard2);

    List<Vineyard> vineyards = vineyardDao.getAllFromTour(testTour);

    assertNotNull(vineyards);
    assertEquals(2, vineyards.size());

    assertEquals("Test1 Vineyard", vineyards.get(0).getName());
    assertEquals("Test2 Vineyard", vineyards.get(1).getName());

  }


}