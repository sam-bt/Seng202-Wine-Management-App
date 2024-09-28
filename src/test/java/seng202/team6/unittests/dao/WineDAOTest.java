package seng202.team6.unittests.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import seng202.team6.dao.WineDAO;
import seng202.team6.managers.DatabaseManager;
import seng202.team6.model.Wine;

public class WineDAOTest {
  private DatabaseManager databaseManager;
  private WineDAO wineDAO;

  @BeforeEach
  void setup() throws SQLException {
    databaseManager = new DatabaseManager();
    wineDAO = databaseManager.getWineDAO();
    wineDAO.setUseCache(false);
  }

  @AfterEach
  void teardown() throws SQLException {
    databaseManager.teardown();
  }

  @Test
  void testAddSingleWine() {
    addWines(1);
    assertEquals(1, wineDAO.getAll().size());
  }

  @Test
  void testAddingMultipleWines() {
    addWines(20);
    assertEquals(20, wineDAO.getAll().size());
  }

  @Test
  void testRemoveAllWines() {
    addWines(10);
    wineDAO.removeAll();
    assertEquals(0, wineDAO.getAll().size());
  }

  @Test
  void testReplaceAllWines() {
    addWines(10);

    List<Wine> wines = new ArrayList<>();
    for (int i = 0; i < 3; i++) {
      wines.add(
          new Wine(-1, "wine", "blue", "nz", "christchurch", "", "", 1024, "na", 99, 25.0f,
              50f, null));
    }

    wineDAO.replaceAll(wines);
    assertEquals(wines.size(), wineDAO.getAll().size());
  }

  @Test
  void testTitleUpdatesInDatabase() {
    String initial = "Initial";
    String changed = "Changed";
    Wine initialWine = createWine(initial, "blue", "nz", "christchurch",
        "bob's wine", "red", 2011, "na", 99, 25f, 10f);
    initialWine.setTitle(changed);

    Wine updatedWine = wineDAO.getAll().getFirst();
    assertEquals(changed, updatedWine.getTitle());
  }

  @Test
  void testVarietyUpdatesInDatabase() {
    String initial = "Initial";
    String changed = "Changed";
    Wine initialWine = createWine("wine", initial, "nz", "christchurch",
        "bob's wine", "red", 2011, "na", 99, 25f, 10f);
    initialWine.setVariety(changed);

    Wine updatedWine = wineDAO.getAll().getFirst();
    assertEquals(changed, updatedWine.getVariety());
  }

  @Test
  void testCountryUpdatesInDatabase() {
    String initial = "Initial";
    String changed = "Changed";
    Wine initialWine = createWine("wine", "variety", initial, "region",
        "winery", "red", 2011, "description", 99, 25f, 10f);
    initialWine.setCountry(changed);

    Wine updatedWine = wineDAO.getAll().getFirst();
    assertEquals(changed, updatedWine.getCountry());
  }

  @Test
  void testRegionUpdatesInDatabase() {
    String initial = "Initial";
    String changed = "Changed";
    Wine initialWine = createWine("wine", "variety", "country", initial,
        "winery", "red", 2011, "description", 99, 25f, 10f);
    initialWine.setRegion(changed);

    Wine updatedWine = wineDAO.getAll().getFirst();
    assertEquals(changed, updatedWine.getRegion());
  }

  @Test
  void testWineryUpdatesInDatabase() {
    String initial = "Initial";
    String changed = "Changed";
    Wine initialWine = createWine("wine", "variety", "country", "region",
        initial, "red", 2011, "description", 99, 25f, 10f);
    initialWine.setWinery(changed);

    Wine updatedWine = wineDAO.getAll().getFirst();
    assertEquals(changed, updatedWine.getWinery());
  }

  @Test
  void testColorUpdatesInDatabase() {
    String initial = "Red";
    String changed = "White";
    Wine initialWine = createWine("wine", "variety", "country", "region",
        "winery", initial, 2011, "description", 99, 25f, 10f);
    initialWine.setColor(changed);

    Wine updatedWine = wineDAO.getAll().getFirst();
    assertEquals(changed, updatedWine.getColor());
  }

  @Test
  void testVintageUpdatesInDatabase() {
    int initial = 2011;
    int changed = 2015;
    Wine initialWine = createWine("wine", "variety", "country", "region",
        "winery", "red", initial, "description", 99, 25f, 10f);
    initialWine.setVintage(changed);

    Wine updatedWine = wineDAO.getAll().getFirst();
    assertEquals(changed, updatedWine.getVintage());
  }

  @Test
  void testDescriptionUpdatesInDatabase() {
    String initial = "Initial description";
    String changed = "Changed description";
    Wine initialWine = createWine("wine", "variety", "country", "region",
        "winery", "red", 2011, initial, 99, 25f, 10f);
    initialWine.setDescription(changed);

    Wine updatedWine = wineDAO.getAll().getFirst();
    assertEquals(changed, updatedWine.getDescription());
  }

  @Test
  void testScorePercentUpdatesInDatabase() {
    int initial = 90;
    int changed = 95;
    Wine initialWine = createWine("wine", "variety", "country", "region",
        "winery", "red", 2011, "description", initial, 25f, 10f);
    initialWine.setScorePercent(changed);

    Wine updatedWine = wineDAO.getAll().getFirst();
    assertEquals(changed, updatedWine.getScorePercent());
  }

  @Test
  void testAbvUpdatesInDatabase() {
    float initial = 13.5f;
    float changed = 14.0f;
    Wine initialWine = createWine("wine", "variety", "country", "region",
        "winery", "red", 2011, "description", 99, initial, 10f);
    initialWine.setAbv(changed);

    Wine updatedWine = wineDAO.getAll().getFirst();
    assertEquals(changed, updatedWine.getAbv(), 0.001);
  }

  @Test
  void testPriceUpdatesInDatabase() {
    float initial = 25.99f;
    float changed = 29.99f;
    Wine initialWine = createWine("wine", "variety", "country", "region",
        "winery", "red", 2011, "description", 99, 13.5f, initial);
    initialWine.setPrice(changed);

    Wine updatedWine = wineDAO.getAll().getFirst();
    assertEquals(changed, updatedWine.getPrice(), 0.001);
  }

  private void addWines(int num) {
    List<Wine> wines = new ArrayList<>();
    for (int i = 0; i < num; i++) {
      wines.add(
          new Wine(-1, "wine", "blue", "nz", "christchurch", "bob's wine", "red", 2011,
              "na", 99, 25f, (float) i, null));
    }
    wineDAO.addAll(wines);
  }

  private Wine createWine(String title,
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
    Wine wine = new Wine(1, title, variety, country, region, winery, color, vintage, description,
        scorePercent, abv, price, null);
    wineDAO.add(wine);
    return wineDAO.getAll().getFirst();
  }
}
