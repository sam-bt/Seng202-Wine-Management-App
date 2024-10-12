package seng202.team6.unittests.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.sql.Date;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import kotlin.Pair;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import seng202.team6.dao.AggregatedDao;
import seng202.team6.dao.UserDao;
import seng202.team6.dao.VineyardDao;
import seng202.team6.dao.WineDao;
import seng202.team6.dao.WineListDao;
import seng202.team6.dao.WineNotesDao;
import seng202.team6.dao.WineReviewDao;
import seng202.team6.managers.DatabaseManager;
import seng202.team6.model.GeoLocation;
import seng202.team6.model.Note;
import seng202.team6.model.ReviewFilters;
import seng202.team6.model.User;
import seng202.team6.model.Vineyard;
import seng202.team6.model.Wine;
import seng202.team6.model.WineDatePair;
import seng202.team6.model.WineList;
import seng202.team6.model.WineReview;

public class AggregatedDaoTest {

  private DatabaseManager databaseManager;
  private seng202.team6.dao.AggregatedDao aggregatedDao;

  private UserDao userDao;
  private WineDao wineDao;
  private User testUser;
  private Wine testWine;

  @BeforeEach
  void setup() throws SQLException {
    databaseManager = new DatabaseManager();
    databaseManager.init();
    aggregatedDao = databaseManager.getAggregatedDao();

    userDao = databaseManager.getUserDao();
    wineDao = databaseManager.getWineDao();

    testUser = new User("testUser", "testPassword1!", "user", "egsalt");
    userDao.add(testUser);

    testWine = new Wine(-1, "wine", "blue", "nz", "christchurch", "Test Vineyard", "", 1024, "na", 99,
        25.0f, 50f, null, 0.0);
    wineDao.add(testWine);

  }

  @AfterEach
  void teardown() throws SQLException {
    databaseManager.teardown();
  }

  @Test
  void testGetAllNotesMappedWithWinesByUser() throws SQLException {
    WineNotesDao noteDao = databaseManager.getWineNotesDao();

    Note testNote = noteDao.get(testUser,testWine);
    testNote.setNote("Yum!");

    ObservableMap<Wine, Note> result = aggregatedDao.getAllNotesMappedWithWinesByUser(testUser);

    assertNotNull(result);
    assertEquals(1, result.size());
    for (Map.Entry<Wine, Note> entry : result.entrySet()) {
      assertEquals(entry.getKey().getTitle(), "wine");
      assertEquals(entry.getValue().getNote(), "Yum!");
    }
  }

  @Test
  void testGetWinesMappedWithDatesFromList() throws SQLException {
    WineListDao wineListDao = databaseManager.getWineListDao();

    WineList testWineList = new WineList(1L, "Test Wine List");
    wineListDao.create(testUser, "Test Wine List");
    wineListDao.addWine(testWineList, testWine);

    ObservableList<WineDatePair> result = aggregatedDao.getWinesMappedWithDatesFromList(testWineList);

    assertNotNull(result);
    assertEquals(1, result.size());
    for (WineDatePair pair : result) {
      assertEquals(pair.wine().getTitle(), "wine");
      assertNotNull(pair.date());
    }
  }

  @Test
  void testGetWinesInList() throws SQLException {
    WineListDao wineListDao = databaseManager.getWineListDao();

    WineList testWineList = new WineList(1L, "Test Wine List");
    wineListDao.create(testUser, "Test Wine List");
    wineListDao.addWine(testWineList, testWine);

    ObservableList<Wine> result = aggregatedDao.getWinesInList(testWineList);

    assertNotNull(result);
    assertEquals(1, result.size());
    for (Wine wine : result) {
      assertEquals(wine.getTitle(), "wine");
    }
  }

  @Test
  void testGetWineReviewsAndWines() throws SQLException {
    int begin = 0;
    int end = 5;

    WineReviewDao reviewDao = databaseManager.getWineReviewDao();

    reviewDao.add(testUser, testWine, 5.0, "Yum!",
        new Date(1728366112972L));

    ObservableList<Pair<WineReview, Wine>> result = aggregatedDao.getWineReviewsAndWines(begin, end, null);

    assertNotNull(result);
    assertEquals(1, result.size());
    for (Pair<WineReview, Wine> pair : result) {
      assertEquals(pair.getFirst().getDescription(), "Yum!");
      assertEquals(pair.getSecond().getTitle(), "wine");
    }
  }


  @Test
  void testGetWineReviewsAndWinesWithFilters() throws SQLException {
    int begin = 0;
    int end = 5;

    WineReviewDao reviewDao = databaseManager.getWineReviewDao();

    reviewDao.add(testUser, testWine, 5.0, "Yum!",
        new Date(1728366112972L));

    reviewDao.add(testUser, testWine, 2.0, "Yuck!",
        new Date(1728366112972L));

    ReviewFilters testFilters = new ReviewFilters("", "win", 2, 4);

    ObservableList<Pair<WineReview, Wine>> result = aggregatedDao.getWineReviewsAndWines(begin, end, testFilters);

    assertNotNull(result);
    assertEquals(1, result.size());
    for (Pair<WineReview, Wine> pair : result) {
      assertEquals("Yuck!", pair.getFirst().getDescription());
      assertEquals("wine", pair.getSecond().getTitle());
    }
  }

  @Test
  void testGetWinesFromVineyard() throws SQLException {

    VineyardDao vineyardDao = databaseManager.getVineyardsDao();
    Vineyard testVineyard = new Vineyard(1,"Test Vineyard", "9 Maidstone Road", "Tasman", "www.test.com", "test", "www.test.com", new GeoLocation(1,1));
    vineyardDao.addAll(List.of(testVineyard));

    ObservableList<Wine> result = aggregatedDao.getWinesFromVineyard(testVineyard);

    assertNotNull(result);
    assertEquals(1, result.size());
    for (Wine wine : result) {
      assertEquals(wine.getTitle(), "wine");
    }
  }

}
