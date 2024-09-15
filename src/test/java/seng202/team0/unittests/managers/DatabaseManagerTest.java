package seng202.team0.unittests.managers;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.sql.SQLException;
import java.util.ArrayList;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import seng202.team0.database.Wine;
import seng202.team0.managers.DatabaseManager;
import seng202.team0.util.Filters;

class DatabaseManagerTest {

  DatabaseManager manager;

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
    assertEquals(3, manager.getWinesInRange(1, 4).size());
  }

  @Test
  void getWinesInRangeFiltered() throws SQLException {
    addFilterableWines();

    // Name filter
    Filters filter = new Filters(
        "wine",
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
        100
    );

    // Should return all wines
    assertEquals(5, manager.getWinesInRange(0, 5, filter).size());

    // Should all contain "wine"
    for (Wine wine : manager.getWinesInRange(0, 4, filter)) {
      Assertions.assertTrue(wine.getTitle().contains("wine"));
    }

    // Country filter
    filter = new Filters(
        "",
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
        100
    );

    // Should only return 2 wines
    assertEquals(2, manager.getWinesInRange(0, 5, filter).size());

    // Should all have country as "us"
    for (Wine wine : manager.getWinesInRange(0, 5, filter)) {
      assertEquals("us", wine.getCountry());
    }

    // Winery filter
    filter = new Filters(
        "",
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
        100
    );

    // Should only return 1 wine
    assertEquals(1, manager.getWinesInRange(0, 5, filter).size());

    // Should all have winery as "bob's wine"
    for (Wine wine : manager.getWinesInRange(0, 5, filter)) {
      assertEquals("bob's wine", wine.getWinery());
    }

    // Score filter
    filter = new Filters(
        "",
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
        100
    );

    // Should return 4 wines
    assertEquals(4, manager.getWinesInRange(0, 5, filter).size());

    // Should have a score between 0 and 90
    for (Wine wine : manager.getWinesInRange(0, 5, filter)) {
      Assertions.assertTrue(wine.getScorePercent() >= 0 && wine.getScorePercent() <= 90);
    }

    // Abv filter
    filter = new Filters(
        "",
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
        100
    );

    // Should return 3 wines
    assertEquals(3, manager.getWinesInRange(0, 5, filter).size());

    // Abv should be between 21 and 100
    for (Wine wine : manager.getWinesInRange(0, 5, filter)) {
      Assertions.assertTrue(wine.getAbv() >= 21 && wine.getAbv() <= 100);
    }

    // Price filter
    filter = new Filters(
        "",
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
        100
    );

    // Should get two wines
    assertEquals(2, manager.getWinesInRange(0, 5, filter).size());

    // Price should be between 40 and 100
    for (Wine wine : manager.getWinesInRange(0, 5, filter)) {
      Assertions.assertTrue(wine.getPrice() >= 40 && wine.getPrice() <= 100);
    }

    // Color filter
    filter = new Filters(
        "",
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
        100
    );

    // Should return 3 red wines
    assertEquals(3, manager.getWinesInRange(0, 5, filter).size());

    // Ensure all red
    for (Wine wine : manager.getWinesInRange(0, 5, filter)) {
      assertEquals("red", wine.getColor());
    }

    // Vintage filter
    filter = new Filters(
        "",
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
        100
    );

    // Should be 3 wines above 2018
    assertEquals(3, manager.getWinesInRange(0, 5, filter).size());

    // Assert all in range
    for (Wine wine : manager.getWinesInRange(0, 5, filter)) {
      Assertions.assertTrue(wine.getVintage() >= 2018 && wine.getVintage() <= 10000);
    }

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
   * Tests replacing wine
   *
   * @throws SQLException if error
   */
  @Test
  void replaceAllWines() throws SQLException {
    addWines(10);

    ArrayList<Wine> wines = new ArrayList<>();
    for (int i = 0; i < 3; i++) {
      wines.add(new Wine("wine", "blue", "nz", "christchurch", "bob's wine", "red", 2011, "na", 99, 25.0f, 50f));
    }
    manager.replaceAllWines(wines);
    assertEquals(3, manager.getWinesSize());
  }

  /**
   * Adds wine
   *
   * @throws SQLException if error
   */
  void addWines(int num) throws SQLException {
    ArrayList<Wine> wines = new ArrayList<>();
    for (int i = 0; i < num; i++) {
      wines.add(new Wine("wine", "blue", "nz", "christchurch", "bob's wine", "red", 2011, "na", 99, 25f, (float) i));
    }
    manager.addWines(wines);


  }

  /**
   * Adds a selection of preset wines to be used for testing filters
   *
   * @throws SQLException if adding wines fails
   */
  void addFilterableWines() throws SQLException {
    ArrayList<Wine> wines = new ArrayList<>();
    wines.add(new Wine("wine", "blue", "nz", "christchurch", "bob's wine", "red", 2011, "na", 99, 25f, 10f));
    wines.add(
        new Wine("Big wine", "green", "us", "christchurch", "joes's wine", "white", 2020, "na", 65, 20f, 20f));
    wines.add(new Wine("Funny wine", "blue", "us", "christchurch", "joes's wine", "red", 2019, "na", 85, 24f, 50f));
    wines.add(
        new Wine("Small wine", "red", "nz", "christchurch", "jill's wine", "white", 2012, "na", 88, 18f, 25f));
    wines.add(new Wine("Cool wine", "green", "nz", "christchurch", "jill's wine", "red", 2018, "na", 90, 23f, 40f));
    manager.addWines(wines);
  }
}