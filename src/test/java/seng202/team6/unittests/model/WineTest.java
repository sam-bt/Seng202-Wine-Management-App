package seng202.team6.unittests.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.sql.SQLException;
import java.util.ArrayList;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import seng202.team6.managers.OldDatabaseManager;
import seng202.team6.model.Wine;

/**
 * Tests wine class
 */
class WineTest {

  OldDatabaseManager database;

  /**
   * Creates a test database to test
   *
   * @throws SQLException if error
   */
  @BeforeEach
  void setUp() throws SQLException {
    database = new OldDatabaseManager();
    addWine();
  }

  /**
   * @throws SQLException if error
   */
  void addWine() throws SQLException {
    ArrayList<Wine> list = new ArrayList<>();
    list.add(new Wine(-1, database, "", "", "", "", "", "", -1, "", 0, 0f, 0f, null));
    database.addWines(list);
  }

  /**
   * Gets a test wine in the database
   *
   * @return test wine
   */
  Wine getTestWine() {
    return database.getWinesInRange(0, 1).getFirst();
  }

  /**
   * Frees the database
   */
  @AfterEach
  void tearDown() {
    database.close();
  }

  /**
   * Tests setting the title
   */
  @Test
  void setTitle() {
    assertEquals("", getTestWine().getTitle());
    getTestWine().setTitle("test");
    assertEquals("test", getTestWine().getTitle());
  }

  /**
   * Tests setting the variety
   */
  @Test
  void setVariety() {
    assertEquals("", getTestWine().getVariety());
    getTestWine().setVariety("test");
    assertEquals("test", getTestWine().getVariety());
  }

  /**
   * Tests setting the country
   */
  @Test
  void setCountry() {
    assertEquals("", getTestWine().getCountry());
    getTestWine().setCountry("test");
    assertEquals("test", getTestWine().getCountry());
  }

  /**
   * Tests setting the color
   */
  @Test
  void setColor() {
    assertEquals("", getTestWine().getColor());
    getTestWine().setColor("test");
    assertEquals("test", getTestWine().getColor());
  }

  /**
   * Tests setting the region
   */
  @Test
  void setVintage() {
    assertEquals(-1, getTestWine().getVintage());
    getTestWine().setVintage(25);
    assertEquals(25, getTestWine().getVintage());
  }

  /**
   * Tests setting the region
   */
  @Test
  void setRegion() {
    assertEquals("", getTestWine().getRegion());
    getTestWine().setRegion("test");
    assertEquals("test", getTestWine().getRegion());
  }

  /**
   * Tests setting the winery
   */
  @Test
  void setWinery() {
    assertEquals("", getTestWine().getWinery());
    getTestWine().setWinery("test");
    assertEquals("test", getTestWine().getWinery());
  }

  /**
   * Tests setting the description
   */
  @Test
  void setDescription() {
    assertEquals("", getTestWine().getDescription());
    getTestWine().setDescription("test");
    assertEquals("test", getTestWine().getDescription());
  }

  /**
   * Tests setting the score
   */
  @Test
  void setScorePercent() {
    assertEquals(0, getTestWine().getScorePercent());
    getTestWine().setScorePercent(20);
    assertEquals(20, getTestWine().getScorePercent());
  }

  /**
   * Tests setting the ABV
   */
  @Test
  void setAbv() {
    assertEquals(0f, getTestWine().getAbv());
    getTestWine().setAbv(21f);
    assertEquals(21f, getTestWine().getAbv());
  }

  /**
   * Tests setting the price
   */
  @Test
  void setPrice() {
    assertEquals(0f, getTestWine().getPrice());
    getTestWine().setPrice(22f);
    assertEquals(22f, getTestWine().getPrice());
  }
}