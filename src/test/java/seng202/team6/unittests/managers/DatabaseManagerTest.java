package seng202.team6.unittests.managers;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import seng202.team6.managers.DatabaseManager;
import seng202.team6.model.Filters;
import seng202.team6.model.Wine;
import seng202.team6.model.WineList;

class DatabaseManagerTest {

  private DatabaseManager manager;

  /**
   * Initializes the database
   */
  @BeforeEach
  void setup() {
    assertDoesNotThrow(() -> {
      manager = new DatabaseManager();
    });
  }

  /**
   * Closes the database
   */
  @AfterEach
  void close() {
    manager.close();
  }

  /**
   * Tests getting wines in a certain range
   *
   * @throws SQLException if error
   */
  @Test
  void getWinesInRange() throws SQLException {
    addWines(10);
    assertEquals(3, manager.getWinesInRange(0, 3).size());
  }

  /**
   * Tests setting the wine attribute
   *
   * @throws SQLException if error
   */
  @Test
  void setWineAttribute() throws SQLException {
    addWines(2);
    assertEquals(2, manager.getWinesSize());
    long id = manager.getWinesInRange(1, 2).getFirst().getKey();
    assertNotEquals(id, -1);
    manager.setWineAttribute(id, "TITLE", statement -> {
      statement.setString(1, "TEST_TITLE");
    });
    // ordering dependant
    assertEquals("TEST_TITLE", manager.getWinesInRange(1, 2).getFirst().getTitle());


  }


  /**
   * Tests getting wine size
   *
   * @throws SQLException if error
   */
  @Test
  void getWinesSize() throws SQLException {
    addWines(10);
    assertEquals(10, manager.getWinesSize());
  }

  /**
   * Sometimes all the primary keys are 0
   */
  @Test
  void testBrokenIDs() throws SQLException {
    addWines(2);
    manager.getWinesInRange(0, 1);
    assertNotEquals(manager.getWinesInRange(0, 1).getFirst().getKey(),
        manager.getWinesInRange(1, 2).getFirst().getKey());
  }


  /**
   * Tests replacing wine
   *
   * @throws SQLException if error
   */
  @Test
  void replaceAllWines() throws SQLException {
    addWines(10);

    ArrayList<Wine> wines = new ArrayList<>();
    for (int i = 0; i < 3; i++) {
      wines.add(
          new Wine(-1, manager, "wine", "blue", "nz", "christchurch", "", "", 1024, "na", 99, 25.0f,
              50f, null));
    }
    manager.replaceAllWines(wines);
    assertEquals(3, manager.getWinesSize());
  }

  @Test
  void testNameFilter() throws SQLException {
    Filters filter = new Filters("wine",
        "",
        "",
        "",
        0,
        10000,
        0,
        100,
        0,
        100,
        0,
        100);
    assertDoesNotThrow(this::addFilterableWines);
    assertEquals(5, manager.getWinesInRange(0, 5, filter).size());
    for (Wine wine : manager.getWinesInRange(0, 4, filter)) {
      assertTrue(wine.getTitle().toLowerCase().contains("wine"));
    }
  }

  @Test
  void testCountryFilter() throws SQLException {
    Filters filter = new Filters("",
        "us",
        "",
        "",
        0,
        10000,
        0,
        100,
        0,
        100,
        0,
        100);
    assertDoesNotThrow(this::addFilterableWines);
    assertEquals(2, manager.getWinesInRange(0, 5, filter).size());
    for (Wine wine : manager.getWinesInRange(0, 5, filter)) {
      assertEquals("us", wine.getCountry());
    }
  }

  @Test
  void testWineryFilter() throws SQLException {
    Filters filter = new Filters("",
        "",
        "bob's wine",
        "",
        0,
        10000,
        0,
        100,
        0,
        100,
        0,
        100);
    assertDoesNotThrow(this::addFilterableWines);
    assertEquals(1, manager.getWinesInRange(0, 5, filter).size());
    for (Wine wine : manager.getWinesInRange(0, 5, filter)) {
      assertEquals("bob's wine", wine.getWinery());
    }
  }

  @Test
  void testScoreFilter() throws SQLException {
    Filters filter = new Filters("",
        "",
        "",
        "",
        0,
        10000,
        0,
        90,
        0,
        100,
        0,
        100);
    assertDoesNotThrow(this::addFilterableWines);
    assertEquals(4, manager.getWinesInRange(0, 5, filter).size());
    for (Wine wine : manager.getWinesInRange(0, 5, filter)) {
      assertTrue(wine.getScorePercent() >= 0 && wine.getScorePercent() <= 90);
    }
  }

  @Test
  void testAbvFilter() throws SQLException {
    Filters filter = new Filters("",
        "",
        "",
        "",
        0,
        10000,
        0,
        100,
        21,
        100,
        0,
        100);
    assertDoesNotThrow(this::addFilterableWines);
    assertEquals(3, manager.getWinesInRange(0, 5, filter).size());
    for (Wine wine : manager.getWinesInRange(0, 5, filter)) {
      assertTrue(wine.getAbv() >= 21 && wine.getAbv() <= 100);
    }
  }

  @Test
  void testPriceFilter() throws SQLException {
    Filters filter = new Filters("",
        "",
        "",
        "",
        0,
        10000,
        0,
        100,
        0,
        100,
        40,
        100);
    assertDoesNotThrow(this::addFilterableWines);
    assertEquals(2, manager.getWinesInRange(0, 5, filter).size());
    for (Wine wine : manager.getWinesInRange(0, 5, filter)) {
      assertTrue(wine.getPrice() >= 40 && wine.getPrice() <= 100);
    }
  }

  @Test
  void testColorFilter() throws SQLException {
    Filters filter = new Filters("",
        "",
        "",
        "red",
        0,
        10000,
        0,
        100,
        0,
        100,
        0,
        100);
    assertDoesNotThrow(this::addFilterableWines);
    assertEquals(3, manager.getWinesInRange(0, 5, filter).size());
    for (Wine wine : manager.getWinesInRange(0, 5, filter)) {
      assertEquals("red", wine.getColor());
    }
  }

  @Test
  void testVintageFilter() throws SQLException {
    Filters filter = new Filters("",
        "",
        "",
        "",
        2018,
        10000,
        0,
        100,
        0,
        100,
        0,
        100);
    assertDoesNotThrow(this::addFilterableWines);
    assertEquals(3, manager.getWinesInRange(0, 5, filter).size());
    for (Wine wine : manager.getWinesInRange(0, 5, filter)) {
      assertTrue(wine.getVintage() >= 2018 && wine.getVintage() <= 10000);
    }
  }

  private void addFilterableWines() throws SQLException {
    ArrayList<Wine> wines = new ArrayList<>();
    wines.add(
        new Wine(-1, manager, "wine", "blue", "nz", "christchurch",
            "bob's wine", "red", 2011, "na", 99, 25f,
            10f, null));
    wines.add(
        new Wine(-1, manager, "Big wine", "green", "us", "christchurch",
            "joes's wine", "white", 2020, "na", 65,
            20f, 20f, null));
    wines.add(
        new Wine(-1, manager, "Funny wine", "blue", "us", "christchurch",
            "joes's wine", "red", 2019, "na", 85,
            24f, 50f, null));
    wines.add(
        new Wine(-1, manager, "Small wine", "red", "nz", "christchurch",
            "jill's wine", "white", 2012, "na", 88,
            18f, 25f, null));
    wines.add(
        new Wine(-1, manager, "Cool wine", "green", "nz", "christchurch",
            "jill's wine", "red", 2018, "na", 90,
            23f, 40f, null));
    manager.addWines(wines);
  }

  /**
   * Adds wine
   *
   * @throws SQLException if error
   */
  private void addWines(int num) throws SQLException {
    ArrayList<Wine> wines = new ArrayList<>();
    for (int i = 0; i < num; i++) {
      wines.add(
          new Wine(-1, manager, "wine", "blue", "nz", "christchurch", "bob's wine", "red", 2011,
              "na", 99, 25f,
              (float) i, null));
    }
    manager.addWines(wines);
  }

  @Test
  void adminHasFavourite() {
    assertTrue(manager.getUserLists("admin").stream().anyMatch(wineList -> wineList.name().equals("Favourites")));
  }

  @Test
  void createdListIsAddedToDatabase() {
    String listName = "2024 Wines";
    manager.createList("admin", listName);

    List<WineList> wineLists = manager.getUserLists("admin");
    assertTrue(wineLists.stream().anyMatch(wineList -> wineList.name().equals(listName)));
  }

  @Test
  void historyExists() {
    WineList favouritesList = manager.getUserLists("admin").stream()
        .filter(wineList -> wineList.name().equals("History"))
        .findFirst()
        .orElse(null);
    assertNotNull(favouritesList);
  }

  @Test
  void addedWineSavesToListOnDatabase() throws SQLException {
    WineList favouritesList = manager.getUserLists("admin").stream()
        .filter(wineList -> wineList.name().equals("Favourites"))
        .findFirst()
        .orElse(null);
    assertNotNull(favouritesList);

    Wine wine = new Wine(1, manager, "wine", "blue", "nz", "christchurch", "bob's wine", "red", 2011,
        "na", 99, 25f,
        (float) 1, null);
    manager.addWines(List.of(wine));

    // ensure the size of the list is initially 0
    List<Wine> wines = manager.getWinesInList(favouritesList);
    assertEquals(0, wines.size());

    // ensure a wine added to the list is saved and can be retrieved
    manager.addWineToList(favouritesList, wine);
    List<Wine> updatedWines = manager.getWinesInList(favouritesList);
    assertEquals(1, updatedWines.size());
    assertEquals(updatedWines.getFirst(), wine);
  }
}