package seng202.team6.unittests.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javafx.collections.ObservableList;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import seng202.team6.dao.WineDao;
import seng202.team6.managers.DatabaseManager;
import seng202.team6.model.GeoLocation;
import seng202.team6.model.Wine;
import seng202.team6.model.WineFilters;
import seng202.team6.service.WineDataStatService;

/**
 * Unit tests for the WineDao class, which manages the storage, retrieval, and modification of wines in the database.
 * These tests verify adding, removing, updating, and querying wines, as well as applying filters.
 */
public class WineDaoTest {

  private DatabaseManager databaseManager;
  private WineDao wineDao;

  /**
   * Sets up the database manager and WineDao before each test, disabling caching to ensure fresh data.
   *
   * @throws SQLException if an error occurs during database setup.
   */
  @BeforeEach
  void setup() throws SQLException {
    databaseManager = new DatabaseManager();
    wineDao = databaseManager.getWineDao();
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
   * Tests adding a single wine to the database and verifies that the wine count is updated accordingly.
   */
  @Test
  void testAddSingleWine() throws SQLException {
    addWines(1);
    assertEquals(1, wineDao.getAll().size());
  }

  /**
   * Tests adding multiple wines to the database and verifies that the correct number of wines is added.
   */
  @Test
  void testAddingMultipleWines() throws SQLException {
    addWines(20);
    assertEquals(20, wineDao.getAll().size());
  }

  /**
   * Tests retrieving the total number of wines, verifying the correct count is returned.
   */
  @Test
  void testGettingWineCount() throws SQLException {
    addWines(20);
    assertEquals(20, wineDao.getCount());
  }

  /**
   * Tests removing all wines from the database and verifies that the wine count is zero afterward.
   */
  @Test
  void testRemoveAllWines() throws SQLException {
    addWines(10);
    wineDao.removeAll();
    assertEquals(0, wineDao.getAll().size());
  }

  /**
   * Tests replacing all existing wines in the database with a new list of wines.
   * Verifies that the new wines replace the old ones correctly.
   */
  @Test
  void testReplaceAllWines() throws SQLException {
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


  /**
   * Tests retrieving the wine count with filters applied (e.g., by vintage), ensuring the count matches the filter criteria.
   */
  @Test
  void testGetCountWithFilters() throws SQLException {
    Wine testWine1 = createWine("wine1", "variety", "country", "region",
        "winery", "red", 2020, "description", 99, 25f, 10f);
    Wine testWine2 = createWine("wine2", "blue", "nz", "christchurch",
        "bob's wine", "red", 1999, "na", 99, 25f, 10f);

    WineFilters testFilters = new WineFilters();
    testFilters.setMaxVintage(2000);

    int count = wineDao.getCount(testFilters);

    assertEquals(1, count);

  }

  /**
   * Tests retrieving wines within a specified range and applying filters (e.g., by vintage).
   * Verifies that the wines returned match the filter criteria.
   */
  @Test
  void testGetAllInRangeWithFilters() throws SQLException {
    Wine testWine1 = createWine("wine1", "variety", "country", "region",
        "winery", "red", 2020, "description", 99, 25f, 10f);
    Wine testWine2 = createWine("wine2", "blue", "nz", "christchurch",
        "bob's wine", "red", 1999, "na", 99, 25f, 10f);

    WineFilters testFilters = new WineFilters();
    testFilters.setMinVintage(1980);
    testFilters.setMaxVintage(2000);

    ObservableList<Wine> result = wineDao.getAllInRange(0, 100, testFilters);

    assertEquals(1, result.size());
    assertEquals(testWine2.getVintage(), result.getFirst().getVintage());

  }

  /**
   * Tests updating the title of a wine in the database.
   * Verifies that the title change is correctly persisted.
   */
  @Test
  void testTitleUpdatesInDatabase() throws SQLException {
    String initial = "Initial";
    String changed = "Changed";
    Wine initialWine = createWine(initial, "blue", "nz", "christchurch",
        "bob's wine", "red", 2011, "na", 99, 25f, 10f);
    initialWine.setTitle(changed);

    Wine updatedWine = wineDao.getAll().getFirst();
    assertEquals(changed, updatedWine.getTitle());
  }

  /**
   * Tests updating the variety of a wine in the database.
   * Verifies that the variety change is correctly persisted.
   */
  @Test
  void testVarietyUpdatesInDatabase() throws SQLException {
    String initial = "Initial";
    String changed = "Changed";
    Wine initialWine = createWine("wine", initial, "nz", "christchurch",
        "bob's wine", "red", 2011, "na", 99, 25f, 10f);
    initialWine.setVariety(changed);

    Wine updatedWine = wineDao.getAll().getFirst();
    assertEquals(changed, updatedWine.getVariety());
  }

  /**
   * Tests updating the country of a wine in the database.
   * Verifies that the country change is correctly persisted.
   */
  @Test
  void testCountryUpdatesInDatabase() throws SQLException {
    String initial = "Initial";
    String changed = "Changed";
    Wine initialWine = createWine("wine", "variety", initial, "region",
        "winery", "red", 2011, "description", 99, 25f, 10f);
    initialWine.setCountry(changed);

    Wine updatedWine = wineDao.getAll().getFirst();
    assertEquals(changed, updatedWine.getCountry());
  }

  /**
   * Tests updating the region of a wine in the database.
   * Verifies that the region change is correctly persisted.
   */
  @Test
  void testRegionUpdatesInDatabase() throws SQLException {
    String initial = "Initial";
    String changed = "Changed";
    Wine initialWine = createWine("wine", "variety", "country", initial,
        "winery", "red", 2011, "description", 99, 25f, 10f);
    initialWine.setRegion(changed);

    Wine updatedWine = wineDao.getAll().getFirst();
    assertEquals(changed, updatedWine.getRegion());
  }


  /**
   * Tests updating the winery of a wine in the database.
   * Verifies that the winery change is correctly persisted.
   */
  @Test
  void testWineryUpdatesInDatabase() throws SQLException {
    String initial = "Initial";
    String changed = "Changed";
    Wine initialWine = createWine("wine", "variety", "country", "region",
        initial, "red", 2011, "description", 99, 25f, 10f);
    initialWine.setWinery(changed);

    Wine updatedWine = wineDao.getAll().getFirst();
    assertEquals(changed, updatedWine.getWinery());
  }

  /**
   * Tests updating the color of a wine in the database.
   * Verifies that the color change is correctly persisted.
   */
  @Test
  void testColorUpdatesInDatabase() throws SQLException {
    String initial = "Red";
    String changed = "White";
    Wine initialWine = createWine("wine", "variety", "country", "region",
        "winery", initial, 2011, "description", 99, 25f, 10f);
    initialWine.setColor(changed);

    Wine updatedWine = wineDao.getAll().getFirst();
    assertEquals(changed, updatedWine.getColor());
  }

  /**
   * Tests updating the vintage of a wine in the database.
   * Verifies that the vintage change is correctly persisted.
   */
  @Test
  void testVintageUpdatesInDatabase() throws SQLException {
    int initial = 2011;
    int changed = 2015;
    Wine initialWine = createWine("wine", "variety", "country", "region",
        "winery", "red", initial, "description", 99, 25f, 10f);
    initialWine.setVintage(changed);

    Wine updatedWine = wineDao.getAll().getFirst();
    assertEquals(changed, updatedWine.getVintage());
  }

  /**
   * Tests updating the description of a wine in the database.
   * Verifies that the description change is correctly persisted.
   */
  @Test
  void testDescriptionUpdatesInDatabase() throws SQLException {
    String initial = "Initial description";
    String changed = "Changed description";
    Wine initialWine = createWine("wine", "variety", "country", "region",
        "winery", "red", 2011, initial, 99, 25f, 10f);
    initialWine.setDescription(changed);

    Wine updatedWine = wineDao.getAll().getFirst();
    assertEquals(changed, updatedWine.getDescription());
  }

  /**
   * Tests updating the score percent of a wine in the database.
   * Verifies that the score percent change is correctly persisted.
   */
  @Test
  void testScorePercentUpdatesInDatabase() throws SQLException {
    int initial = 90;
    int changed = 95;
    Wine initialWine = createWine("wine", "variety", "country", "region",
        "winery", "red", 2011, "description", initial, 25f, 10f);
    initialWine.setScorePercent(changed);

    Wine updatedWine = wineDao.getAll().getFirst();
    assertEquals(changed, updatedWine.getScorePercent());
  }

  /**
   * Tests updating the alcohol by volume (ABV) of a wine in the database.
   * Verifies that the ABV change is correctly persisted.
   */
  @Test
  void testAbvUpdatesInDatabase() throws SQLException {
    float initial = 13.5f;
    float changed = 14.0f;
    Wine initialWine = createWine("wine", "variety", "country", "region",
        "winery", "red", 2011, "description", 99, initial, 10f);
    initialWine.setAbv(changed);

    Wine updatedWine = wineDao.getAll().getFirst();
    assertEquals(changed, updatedWine.getAbv(), 0.001);
  }


  /**
   * Tests updating the price of a wine in the database.
   * Verifies that the price change is correctly persisted.
   */
  @Test
  void testPriceUpdatesInDatabase() throws SQLException {
    float initial = 25.99f;
    float changed = 29.99f;
    Wine initialWine = createWine("wine", "variety", "country", "region",
        "winery", "red", 2011, "description", 99, 13.5f, initial);
    initialWine.setPrice(changed);

    Wine updatedWine = wineDao.getAll().getFirst();
    assertEquals(changed, updatedWine.getPrice(), 0.001);
  }

  /**
   * Test to check if the unique updater works as expected for the country field
   */
  @Test
  public void testUpdateUniquesForCountry() throws SQLException {

    WineDataStatService wineDataStatService = wineDao.getWineDataStatService();
    Set<String> initialUniques = wineDataStatService.getUniqueCountries();

    assertFalse(initialUniques.contains("Namibia"));

    Wine testWine = createWine("wine", "variety", "Namibia", "region",
        "winery", "red", 2011, "description", 99, 13.5f, 10);

    wineDao.updateUniques();

    Set<String> newUniques = wineDataStatService.getUniqueCountries();

    assertTrue(newUniques.contains("Namibia"));

  }

  /**
   * Test to check if the unique updater works as expected for the minimum vintage field
   */
  @Test
  public void testUpdateUniquesForMinVintage() throws SQLException {

    WineDataStatService wineDataStatService = wineDao.getWineDataStatService();

    Wine initialWine1= createWine("wine", "variety", "Namibia", "region",
        "winery", "red", 2011, "description", 99, 13.5f, 10);

    Wine initialWine2 = createWine("wine", "variety", "Namibia", "region",
        "winery", "red", 2002, "description", 99, 13.5f, 10);


    wineDao.updateUniques();

    int initialMinVintage = wineDataStatService.getMinVintage();

    assertEquals(initialMinVintage, 2002);

    Wine finalWine = createWine("wine", "variety", "Namibia", "region",
        "winery", "red", 1999, "description", 99, 13.5f, 10);

    wineDao.updateUniques();

    int finalMinVintage = wineDataStatService.getMinVintage();

    assertEquals(finalMinVintage, 1999);

  }

  /**
   * Test to check if the unique updater works as expected for the maximum vintage field
   */
  @Test
  public void testUpdateUniquesForMaxVintage() throws SQLException {

    WineDataStatService wineDataStatService = wineDao.getWineDataStatService();

    Wine initialWine1= createWine("wine", "variety", "Namibia", "region",
        "winery", "red", 2011, "description", 99, 13.5f, 10);

    Wine initialWine2 = createWine("wine", "variety", "Namibia", "region",
        "winery", "red", 2002, "description", 99, 13.5f, 10);


    wineDao.updateUniques();

    int initialMaxVintage = wineDataStatService.getMaxVintage();

    assertEquals(initialMaxVintage, 2011);

    Wine finalWine = createWine("wine", "variety", "Namibia", "region",
        "winery", "red", 2024, "description", 99, 13.5f, 10);

    wineDao.updateUniques();

    int finalMaxVintage = wineDataStatService.getMaxVintage();

    assertEquals(finalMaxVintage, 2024);

  }

  /**
   * Test to check if the unique updater works as expected for the minimum score field
   */
  @Test
  public void testUpdateUniquesForMinScore() throws SQLException {

    WineDataStatService wineDataStatService = wineDao.getWineDataStatService();

    Wine initialWine1= createWine("wine", "variety", "Namibia", "region",
        "winery", "red", 2011, "description", 70, 13.5f, 10);

    Wine initialWine2 = createWine("wine", "variety", "Namibia", "region",
        "winery", "red", 2002, "description", 30, 13.5f, 10);


    wineDao.updateUniques();

    int initialMinScore = wineDataStatService.getMinScore();

    assertEquals(initialMinScore, 30);

    Wine finalWine = createWine("wine", "variety", "Namibia", "region",
        "winery", "red", 2024, "description", 5, 13.5f, 10);

    wineDao.updateUniques();

    int finalMinScore = wineDataStatService.getMinScore();

    assertEquals(finalMinScore, 5);

  }

  /**
   * Test to check if the unique updater works as expected for the maximum score field
   */
  @Test
  public void testUpdateUniquesForMaxScore() throws SQLException {

    WineDataStatService wineDataStatService = wineDao.getWineDataStatService();

    Wine initialWine1= createWine("wine", "variety", "Namibia", "region",
        "winery", "red", 2011, "description", 70, 13.5f, 10);

    Wine initialWine2 = createWine("wine", "variety", "Namibia", "region",
        "winery", "red", 2002, "description", 10, 13.5f, 10);


    wineDao.updateUniques();

    int initialMaxScore = wineDataStatService.getMaxScore();

    assertEquals(initialMaxScore, 70);

    Wine finalWine = createWine("wine", "variety", "Namibia", "region",
        "winery", "red", 2024, "description", 99, 13.5f, 10);

    wineDao.updateUniques();

    int finalMaxScore = wineDataStatService.getMaxScore();

    assertEquals(finalMaxScore, 99);

  }

  /**
   * Test to check if the unique updater works as expected for the minimum abv field
   */
  @Test
  public void testUpdateUniquesForMinAbv() throws SQLException {

    WineDataStatService wineDataStatService = wineDao.getWineDataStatService();

    Wine initialWine1= createWine("wine", "variety", "Namibia", "region",
        "winery", "red", 2011, "description", 70, 14.0f, 10);

    Wine initialWine2 = createWine("wine", "variety", "Namibia", "region",
        "winery", "red", 2002, "description", 30, 13.5f, 10);


    wineDao.updateUniques();

    float initialMinAbv = wineDataStatService.getMinAbv();

    assertEquals(initialMinAbv, 13.5f);

    Wine finalWine = createWine("wine", "variety", "Namibia", "region",
        "winery", "red", 2024, "description", 5, 12.5f, 10);

    wineDao.updateUniques();

    float finalMinAbv = wineDataStatService.getMinAbv();

    assertEquals(finalMinAbv, 12.5f);

  }

  /**
   * Test to check if the unique updater works as expected for the maximum abv field
   */
  @Test
  public void testUpdateUniquesForMaxAbv() throws SQLException {

    WineDataStatService wineDataStatService = wineDao.getWineDataStatService();

    Wine initialWine1= createWine("wine", "variety", "Namibia", "region",
        "winery", "red", 2011, "description", 70, 13.2f, 10);

    Wine initialWine2 = createWine("wine", "variety", "Namibia", "region",
        "winery", "red", 2002, "description", 30, 13.5f, 10);


    wineDao.updateUniques();

    float initialMaxAbv = wineDataStatService.getMaxAbv();

    assertEquals(initialMaxAbv, 13.5);

    Wine finalWine = createWine("wine", "variety", "Namibia", "region",
        "winery", "red", 2024, "description", 5, 14.0f, 10);

    wineDao.updateUniques();

    float finalMaxAbv = wineDataStatService.getMaxAbv();

    assertEquals(finalMaxAbv, 14.0);

  }

  /**
   * Test to check if the unique updater works as expected for the minimum price field
   */
  @Test
  public void testUpdateUniquesForMinPrice() throws SQLException {

    WineDataStatService wineDataStatService = wineDao.getWineDataStatService();

    Wine initialWine1= createWine("wine", "variety", "Namibia", "region",
        "winery", "red", 2011, "description", 70, 13.2f, 100f);
    Wine initialWine2 = createWine("wine", "variety", "Namibia", "region",
        "winery", "red", 2002, "description", 30, 13.5f, 50f);



    wineDao.updateUniques();

    float initialMinPrice = wineDataStatService.getMinPrice();

    assertEquals(initialMinPrice, 50f);

    Wine finalWine = createWine("wine", "variety", "Namibia", "region",
        "winery", "red", 2024, "description", 5, 14.0f, 5f);

    wineDao.updateUniques();

    float finalMinPrice = wineDataStatService.getMinPrice();

    assertEquals(finalMinPrice, 5f);

  }

  /**
  * Test to check if the unique updater works as expected for the maximum price field
  */
  @Test
  public void testUpdateUniquesForMaxPrice() throws SQLException {

    WineDataStatService wineDataStatService = wineDao.getWineDataStatService();

    Wine initialWine1= createWine("wine", "variety", "Namibia", "region",
        "winery", "red", 2011, "description", 70, 13.2f, 5);

    Wine initialWine2 = createWine("wine", "variety", "Namibia", "region",
        "winery", "red", 2002, "description", 30, 13.5f, 100);


    wineDao.updateUniques();

    float initialMaxPrice = wineDataStatService.getMaxPrice();

    assertEquals(initialMaxPrice, 100);

    Wine finalWine = createWine("wine", "variety", "Namibia", "region",
        "winery", "red", 2024, "description", 5, 14.0f, 350);

    wineDao.updateUniques();

    float finalMaxPrice = wineDataStatService.getMaxPrice();

    assertEquals(finalMaxPrice, 350);

  }

  /**
   * Test the exact title matching
   * @throws SQLException
   */
  @Test
  void testGetByExactTitle() throws SQLException {
    addWines(1);
    Wine result = wineDao.getByExactTitle("wine");
    assertEquals(result.getTitle(), "wine");
  }

  /**
   * Test the exact title matching with an invalid title
   * @throws SQLException
   */
  @Test
  void testExactWinesWithInvalidTitle() throws SQLException {
    addWines(1);
    Wine result = wineDao.getByExactTitle("No Wine");
    assertNull(result);
  }


  /**
   * Helper method to create a new Wine object with the given properties and add it to the database.
   *
   * @param title the wine's title.
   * @param variety the variety of the wine.
   * @param country the country of origin.
   * @param region the region of origin.
   * @param winery the winery that produced the wine.
   * @param color the color of the wine (e.g., red, white).
   * @param vintage the vintage year of the wine.
   * @param description a description of the wine.
   * @param scorePercent the score percentage of the wine.
   * @param abv the alcohol by volume (ABV) of the wine.
   * @param price the price of the wine.
   * @return the Wine object that was added to the database.
   */
  private Wine createWine(
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
      float price) throws SQLException {
    Wine wine = new Wine(-1, title, variety, country, region, winery, color, vintage, description,
        scorePercent, abv, price, null, 0.0);
    wineDao.add(wine);
    return wineDao.get(wine.getKey());
  }

  /**
   * Helper method to add a specified number of wines to the database for testing purposes.
   *
   * @param num the number of wines to add.
   */
  private void addWines(int num) throws SQLException {
    List<Wine> wines = new ArrayList<>();
    for (int i = 0; i < num; i++) {
      wines.add(
          new Wine(-1, "wine", "blue", "nz", "christchurch", "bob's wine", "red", 2011,
              "na", 99, 25f, (float) i, null, 0.0));
    }
    wineDao.addAll(wines);
  }

}
