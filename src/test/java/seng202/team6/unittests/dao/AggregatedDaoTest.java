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

/**
 * Unit tests for the AggregatedDao class, which aggregates multiple DAO operations related
 * to wines, users, and reviews. These tests verify the correctness of data aggregation and
 * mapping methods used in the application, including retrieving wines, reviews, and notes.
 *
 */
public class AggregatedDaoTest {

  private DatabaseManager databaseManager;
  private seng202.team6.dao.AggregatedDao aggregatedDao;

  private UserDao userDao;
  private WineDao wineDao;
  private User testUser;
  private Wine testWine;

  /**
   * Initializes the database and sets up DAOs before each test.
   * Adds a test user and a test wine to the database for testing purposes.
   *
   * @throws SQLException if there is an error initializing the database or inserting test data.
   */
  @BeforeEach
  void setup() throws SQLException {
    databaseManager = new DatabaseManager();
    databaseManager.init();
    aggregatedDao = databaseManager.getAggregatedDao();

    userDao = databaseManager.getUserDao();
    wineDao = databaseManager.getWineDao();

    testUser = userDao.get("admin");

    Wine toAdd = new Wine(-1, "wine", "blue", "nz", "christchurch", "Test Vineyard", "", 1024, "na", 99,
        25.0f, 50f, null, 0.0);
    wineDao.add(toAdd);
    testWine = wineDao.getAll().getFirst();

  }

  /**
   * Tears down the database after each test, clearing any test data that was added.
   *
   * @throws SQLException if there is an error during database teardown.
   */
  @AfterEach
  void teardown() throws SQLException {
    databaseManager.teardown();
  }


  /**
   * Tests the functionality of fetching all wine notes mapped with wines by a given user.
   * Ensures that the notes returned are correctly associated with the appropriate wines.
   *
   * @throws SQLException if there is an error retrieving data from the database.
   */
  @Test
  void testGetAllNotesMappedWithWinesByUser() throws SQLException {
    WineNotesDao noteDao = databaseManager.getWineNotesDao();

    Note testNote = noteDao.getOrCreate(testUser,testWine);
    testNote.setNote("Yum!");

    ObservableMap<Wine, Note> result = aggregatedDao.getAllNotesMappedWithWinesByUser(testUser);

    assertNotNull(result);
    assertEquals(1, result.size());
    for (Map.Entry<Wine, Note> entry : result.entrySet()) {
      assertEquals(entry.getKey().getTitle(), "wine");
      assertEquals(entry.getValue().getNote(), "Yum!");
    }
  }

  /**
   * Tests the retrieval of wines and their associated dates from a specified wine list.
   * Verifies that the wine titles and dates match the expected values.
   *
   * @throws SQLException if there is an error retrieving data from the database.
   */
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

  /**
   * Tests the retrieval of wines from a specific wine list.
   * Ensures that the wines returned are correctly associated with the list and contain expected titles.
   *
   * @throws SQLException if there is an error retrieving data from the database.
   */
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

  /**
   * Tests the retrieval of wine reviews along with their associated wines, limited by a
   * specified range of indices (begin and end).
   * Verifies that the reviews and wines are returned as expected.
   *
   * @throws SQLException if there is an error retrieving data from the database.
   */
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

  /**
   * Tests the retrieval of wine reviews and their associated wines, applying specified filters
   * such as review rating and wine title. Ensures that only reviews matching the filters are returned.
   *
   * @throws SQLException if there is an error retrieving data from the database.
   */
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
      assertEquals(pair.getFirst().getDescription(), "Yuck!");
      assertEquals(pair.getSecond().getTitle(), "wine");
    }
  }

  /**
   * Tests the retrieval of wines from a specific vineyard.
   * Ensures that the wines associated with the given vineyard are returned correctly.
   *
   * @throws SQLException if there is an error retrieving data from the database.
   */
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
