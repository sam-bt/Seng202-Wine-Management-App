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
import seng202.team6.model.VineyardTour;
import seng202.team6.model.Wine;
import seng202.team6.model.WineDatePair;
import seng202.team6.service.VineyardService;

public class VineyardDaoTest {

  private DatabaseManager databaseManager;
  VineyardDao vineyardDao;
  Vineyard testVineyard1;
  Vineyard testVineyard2;
  Vineyard testVineyard3;

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

  @AfterEach
  void teardown() throws SQLException {
    databaseManager.teardown();
  }

  @Test
  void testInit() throws SQLException {
    VineyardService vineyardService = new VineyardService(databaseManager);
    assertDoesNotThrow(vineyardService::init);
  }

  @Test
  void testGetCount() throws SQLException {

    int count = vineyardDao.getCount();

    assertEquals(count, 3); // 8 default plus 3 extras
  }

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
    VineyardTour testTour = vineyardTourDao.create(testUser, "testTour");

    vineyardTourDao.addVineyard(testTour, testVineyard1);
    vineyardTourDao.addVineyard(testTour, testVineyard2);

    List<Vineyard> vineyards = vineyardDao.getAllFromTour(testTour);

    assertNotNull(vineyards);
    assertEquals(2, vineyards.size());

    assertEquals("Test1 Vineyard", vineyards.get(0).getName());
    assertEquals("Test2 Vineyard", vineyards.get(1).getName());

  }

  @Test
  void testVineyardHasData() {

    assertTrue(vineyardDao.vineyardsTableHasData());

  }

}