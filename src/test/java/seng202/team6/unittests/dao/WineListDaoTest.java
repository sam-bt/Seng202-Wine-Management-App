package seng202.team6.unittests.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.SQLException;
import javafx.collections.ObservableList;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import seng202.team6.dao.UserDao;
import seng202.team6.dao.VineyardTourDao;
import seng202.team6.dao.WineDao;
import seng202.team6.dao.WineListDao;
import seng202.team6.managers.DatabaseManager;
import seng202.team6.model.GeoLocation;
import seng202.team6.model.User;
import seng202.team6.model.Wine;
import seng202.team6.model.WineList;

public class WineListDaoTest {

  private DatabaseManager databaseManager;

  WineListDao wineListDao;

  User testUser;
  UserDao userDao;

  @BeforeEach
  void setup() throws SQLException {
    databaseManager = new DatabaseManager();
    wineListDao = databaseManager.getWineListDao();
    wineListDao.setUseCache(false);

    testUser = new User("testUser", "testPassword1!", "user", "egsalt");
    userDao = databaseManager.getUserDao();
    userDao.add(testUser);

  }

  @Test
  void testCreateList() {

    WineList testList = wineListDao.create(testUser, "testList");

    assertEquals(testList.name(), "testList");

  }

  @Test
  void testGetLists() {

    WineList testList1 = wineListDao.create(testUser, "testList1");
    WineList testList2 = wineListDao.create(testUser, "testList2");

    ObservableList<WineList> result = wineListDao.getAll(testUser);

    assertEquals(4, result.size());
    assertEquals(testList1.name(), result.get(2).name());
    assertEquals(testList2.name(), result.get(3).name());

  }

  @Test
  void testDeleteList() {

    WineList testList1 = wineListDao.create(testUser, "testList1");
    WineList testList2 = wineListDao.create(testUser, "testList2");

    ObservableList<WineList> result1 = wineListDao.getAll(testUser);

    assertEquals(4, result1.size());
    assertEquals(testList1.name(), result1.get(2).name());
    assertEquals(testList2.name(), result1.get(3).name());

    wineListDao.delete(testList1);

    ObservableList<WineList> result2 = wineListDao.getAll(testUser);

    assertEquals(3, result2.size());
    assertEquals(testList2.name(), result2.getLast().name());

  }

  @Test
  void testAddWine() {

    Wine testWine = new Wine(10, "wine", "pinot gris", "nz", "christchurch",
        "bob's wine", "red", 2011, "na", 99, 25f, 10f,
        new GeoLocation(10,10), 5.0);

    WineDao wineDao = databaseManager.getWineDao();
    wineDao.add(testWine);

    WineList testList = wineListDao.create(testUser, "testList1");

    wineListDao.addWine(testList, testWine);

    assertTrue(wineListDao.isWineInList(testList, testWine));

  }

  @Test
  void testRemoveWine() {

    Wine testWine = new Wine(10, "wine", "pinot gris", "nz", "christchurch",
        "bob's wine", "red", 2011, "na", 99, 25f, 10f,
        new GeoLocation(10,10), 5.0);

    WineDao wineDao = databaseManager.getWineDao();
    wineDao.add(testWine);

    WineList testList = wineListDao.create(testUser, "testList1");

    wineListDao.addWine(testList, testWine);

    assertTrue(wineListDao.isWineInList(testList, testWine));

    wineListDao.removeWine(testList, testWine);

    assertFalse(wineListDao.isWineInList(testList, testWine));

  }





  @AfterEach
  void teardown() throws SQLException {
    databaseManager.teardown();
  }

}
