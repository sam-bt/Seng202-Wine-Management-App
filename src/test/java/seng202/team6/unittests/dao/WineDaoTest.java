package seng202.team6.unittests.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javafx.collections.ObservableList;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import seng202.team6.dao.WineDao;
import seng202.team6.managers.DatabaseManager;
import seng202.team6.model.Wine;
import seng202.team6.model.WineFilters;

public class WineDaoTest {

  private DatabaseManager databaseManager;
  private WineDao wineDao;

  @BeforeEach
  void setup() throws SQLException {
    databaseManager = new DatabaseManager();
    wineDao = databaseManager.getWineDao();
    wineDao.setUseCache(false);
  }

  @AfterEach
  void teardown() throws SQLException {
    databaseManager.teardown();
  }

  @Test
  void testAddSingleWine() {
    addWines(1);
    assertEquals(1, wineDao.getAll().size());
  }

  @Test
  void testAddingMultipleWines() {
    addWines(20);
    assertEquals(20, wineDao.getAll().size());
  }

  @Test
  void testGettingWineCount() {
    addWines(20);
    WineFilters filter = new WineFilters();
    assertEquals(20, wineDao.getCount(filter));
  }

  @Test
  void testRemoveAllWines() {
    addWines(10);
    wineDao.removeAll();
    assertEquals(0, wineDao.getAll().size());
  }

  @Test
  void testReplaceAllWines() {
    addWines(10);

    List<Wine> wines = new ArrayList<>();
    for (int i = 0; i < 3; i++) {
      wines.add(
          new Wine(-1, "wine", "blue", "nz", "christchurch", "", "", 1024, "na", 99, 25.0f,
              50f, null, 0.0));
    }

    wineDao.replaceAll(wines);
    assertEquals(wines.size(), wineDao.getAll().size());
  }

  @Test
  void testGetCountWithFilters() {
    Wine testWine1 = createWine(1,"wine1", "variety", "country", "region",
        "winery", "red", 2020, "description", 99, 25f, 10f);
    Wine testWine2 = createWine(2,"wine2", "blue", "nz", "christchurch",
        "bob's wine", "red", 1999, "na", 99, 25f, 10f);

    WineFilters testFilters = new WineFilters();
    testFilters.setMaxVintage(2000);

    int count = wineDao.getCount(testFilters);

    assertEquals(1, count);

  }

  @Test
  void testGetAllInRangeWithFilters() {
    Wine testWine1 = createWine(1,"wine1", "variety", "country", "region",
        "winery", "red", 2020, "description", 99, 25f, 10f);
    Wine testWine2 = createWine(2,"wine2", "blue", "nz", "christchurch",
        "bob's wine", "red", 1999, "na", 99, 25f, 10f);

    WineFilters testFilters = new WineFilters();
    testFilters.setMinVintage(1980);
    testFilters.setMaxVintage(2000);

    ObservableList<Wine> result = wineDao.getAllInRange(0, 100, testFilters);

    assertEquals(1, result.size());
    assertEquals(testWine2.getVintage(), result.getFirst().getVintage());

  }

  @Test
  void testTitleUpdatesInDatabase() {
    String initial = "Initial";
    String changed = "Changed";
    Wine initialWine = createWine(1, initial, "blue", "nz", "christchurch",
        "bob's wine", "red", 2011, "na", 99, 25f, 10f);
    initialWine.setTitle(changed);

    Wine updatedWine = wineDao.getAll().getFirst();
    assertEquals(changed, updatedWine.getTitle());
  }

  @Test
  void testVarietyUpdatesInDatabase() {
    String initial = "Initial";
    String changed = "Changed";
    Wine initialWine = createWine(1,"wine", initial, "nz", "christchurch",
        "bob's wine", "red", 2011, "na", 99, 25f, 10f);
    initialWine.setVariety(changed);

    Wine updatedWine = wineDao.getAll().getFirst();
    assertEquals(changed, updatedWine.getVariety());
  }

  @Test
  void testCountryUpdatesInDatabase() {
    String initial = "Initial";
    String changed = "Changed";
    Wine initialWine = createWine(1,"wine", "variety", initial, "region",
        "winery", "red", 2011, "description", 99, 25f, 10f);
    initialWine.setCountry(changed);

    Wine updatedWine = wineDao.getAll().getFirst();
    assertEquals(changed, updatedWine.getCountry());
  }

  @Test
  void testRegionUpdatesInDatabase() {
    String initial = "Initial";
    String changed = "Changed";
    Wine initialWine = createWine(1,"wine", "variety", "country", initial,
        "winery", "red", 2011, "description", 99, 25f, 10f);
    initialWine.setRegion(changed);

    Wine updatedWine = wineDao.getAll().getFirst();
    assertEquals(changed, updatedWine.getRegion());
  }

  @Test
  void testWineryUpdatesInDatabase() {
    String initial = "Initial";
    String changed = "Changed";
    Wine initialWine = createWine(1,"wine", "variety", "country", "region",
        initial, "red", 2011, "description", 99, 25f, 10f);
    initialWine.setWinery(changed);

    Wine updatedWine = wineDao.getAll().getFirst();
    assertEquals(changed, updatedWine.getWinery());
  }

  @Test
  void testColorUpdatesInDatabase() {
    String initial = "Red";
    String changed = "White";
    Wine initialWine = createWine(1,"wine", "variety", "country", "region",
        "winery", initial, 2011, "description", 99, 25f, 10f);
    initialWine.setColor(changed);

    Wine updatedWine = wineDao.getAll().getFirst();
    assertEquals(changed, updatedWine.getColor());
  }

  @Test
  void testVintageUpdatesInDatabase() {
    int initial = 2011;
    int changed = 2015;
    Wine initialWine = createWine(1,"wine", "variety", "country", "region",
        "winery", "red", initial, "description", 99, 25f, 10f);
    initialWine.setVintage(changed);

    Wine updatedWine = wineDao.getAll().getFirst();
    assertEquals(changed, updatedWine.getVintage());
  }

  @Test
  void testDescriptionUpdatesInDatabase() {
    String initial = "Initial description";
    String changed = "Changed description";
    Wine initialWine = createWine(1,"wine", "variety", "country", "region",
        "winery", "red", 2011, initial, 99, 25f, 10f);
    initialWine.setDescription(changed);

    Wine updatedWine = wineDao.getAll().getFirst();
    assertEquals(changed, updatedWine.getDescription());
  }

  @Test
  void testScorePercentUpdatesInDatabase() {
    int initial = 90;
    int changed = 95;
    Wine initialWine = createWine(1,"wine", "variety", "country", "region",
        "winery", "red", 2011, "description", initial, 25f, 10f);
    initialWine.setScorePercent(changed);

    Wine updatedWine = wineDao.getAll().getFirst();
    assertEquals(changed, updatedWine.getScorePercent());
  }

  @Test
  void testAbvUpdatesInDatabase() {
    float initial = 13.5f;
    float changed = 14.0f;
    Wine initialWine = createWine(1,"wine", "variety", "country", "region",
        "winery", "red", 2011, "description", 99, initial, 10f);
    initialWine.setAbv(changed);

    Wine updatedWine = wineDao.getAll().getFirst();
    assertEquals(changed, updatedWine.getAbv(), 0.001);
  }

  @Test
  void testPriceUpdatesInDatabase() {
    float initial = 25.99f;
    float changed = 29.99f;
    Wine initialWine = createWine(1,"wine", "variety", "country", "region",
        "winery", "red", 2011, "description", 99, 13.5f, initial);
    initialWine.setPrice(changed);

    Wine updatedWine = wineDao.getAll().getFirst();
    assertEquals(changed, updatedWine.getPrice(), 0.001);
  }

  private void addWines(int num) {
    List<Wine> wines = new ArrayList<>();
    for (int i = 0; i < num; i++) {
      wines.add(
          new Wine(-1, "wine", "blue", "nz", "christchurch", "bob's wine", "red", 2011,
              "na", 99, 25f, (float) i, null, 0.0));
    }
    wineDao.addAll(wines);
  }



  private Wine createWine(
      int key,
      String title,
      String variety,
      String country,
      String region,
      String winery,
      String color,
      int vintage,
      String description,
      int scorePercent,
      float abv,
      float price) {
    Wine wine = new Wine(key, title, variety, country, region, winery, color, vintage, description,
        scorePercent, abv, price, null, 0.0);
    wineDao.add(wine);
    return wineDao.get(key);
  }
}
