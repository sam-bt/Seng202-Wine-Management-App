package seng202.team6.unittests.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.sql.SQLException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import seng202.team6.model.Wine;

/**
 * Tests wine class
 */
class WineTest {

  private Wine wine;

  /**
   * Creates a test database to test
   *
   * @throws SQLException if error
   */
  @BeforeEach
  void setUp() throws SQLException {
    wine = new Wine(-1, "", "", "", "", "", "", -1, "", 0, 0f, 0f, null, 0.0);
  }

  /**
   * Tests setting the title
   */
  @Test
  void setTitle() {
    assertEquals("", wine.getTitle());
    wine.setTitle("test");
    assertEquals("test", wine.getTitle());
  }

  /**
   * Tests setting the variety
   */
  @Test
  void setVariety() {
    assertEquals("", wine.getVariety());
    wine.setVariety("test");
    assertEquals("test", wine.getVariety());
  }

  /**
   * Tests setting the country
   */
  @Test
  void setCountry() {
    assertEquals("", wine.getCountry());
    wine.setCountry("test");
    assertEquals("test", wine.getCountry());
  }

  /**
   * Tests setting the color
   */
  @Test
  void setColor() {
    assertEquals("", wine.getColor());
    wine.setColor("test");
    assertEquals("test", wine.getColor());
  }

  /**
   * Tests setting the region
   */
  @Test
  void setVintage() {
    assertEquals(-1, wine.getVintage());
    wine.setVintage(25);
    assertEquals(25, wine.getVintage());
  }

  /**
   * Tests setting the region
   */
  @Test
  void setRegion() {
    assertEquals("", wine.getRegion());
    wine.setRegion("test");
    assertEquals("test", wine.getRegion());
  }

  /**
   * Tests setting the winery
   */
  @Test
  void setWinery() {
    assertEquals("", wine.getWinery());
    wine.setWinery("test");
    assertEquals("test", wine.getWinery());
  }

  /**
   * Tests setting the description
   */
  @Test
  void setDescription() {
    assertEquals("", wine.getDescription());
    wine.setDescription("test");
    assertEquals("test", wine.getDescription());
  }

  /**
   * Tests setting the score
   */
  @Test
  void setScorePercent() {
    assertEquals(0, wine.getScorePercent());
    wine.setScorePercent(20);
    assertEquals(20, wine.getScorePercent());
  }

  /**
   * Tests setting the ABV
   */
  @Test
  void setAbv() {
    assertEquals(0f, wine.getAbv());
    wine.setAbv(21f);
    assertEquals(21f, wine.getAbv());
  }

  /**
   * Tests setting the price
   */
  @Test
  void setPrice() {
    assertEquals(0f, wine.getPrice());
    wine.setPrice(22f);
    assertEquals(22f, wine.getPrice());
  }
}