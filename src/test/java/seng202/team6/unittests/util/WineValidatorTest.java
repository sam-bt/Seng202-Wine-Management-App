package seng202.team6.unittests.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import seng202.team6.model.GeoLocation;
import seng202.team6.model.Wine;
import seng202.team6.util.Exceptions.ValidationException;
import seng202.team6.util.WineValidator;

/**
 * Test class for the WineValidator utility. This class contains unit tests to verify the
 * functionality of the WineValidator
 */
class WineValidatorTest {

  private GeoLocation mockGeoLocation;

  /**
   * Tests the parseWine method with valid input for all fields.
   *
   * @throws ValidationException if wine parsing fails
   */
  @Test
  void testParseWineValidInput() throws ValidationException {
    Wine wine = WineValidator.parseWine(
        "Test Wine", "Merlot", "France", "Bordeaux",
        "Test Winery", "Red", "2020", "A fine wine", "95", "14.5", "50.0", mockGeoLocation
    );

    assertNotNull(wine);
    assertEquals("Test Wine", wine.getTitle());
    assertEquals("Merlot", wine.getVariety());
    assertEquals("France", wine.getCountry());
    assertEquals("Bordeaux", wine.getRegion());
    assertEquals("Test Winery", wine.getWinery());
    assertEquals("Red", wine.getColor());
    assertEquals(2020, wine.getVintage());
    assertEquals("A fine wine", wine.getDescription());
    assertEquals(95, wine.getScorePercent());
    assertEquals(14.5f, wine.getAbv(), 0.01);
    assertEquals(50.0f, wine.getPrice(), 0.01);
  }

  /**
   * Tests the parseWine method with empty strings for optional numeric fields.
   *
   * @throws ValidationException if wine parsing fails
   */
  @Test
  void testParseWineEmptyOptionalFields() throws ValidationException {
    Wine wine = WineValidator.parseWine(
        "Test Wine", "Merlot", "France", "Bordeaux",
        "Test Winery", "Red", "2020", "A fine wine", "", "", "", mockGeoLocation
    );

    assertNotNull(wine);
    assertEquals(0, wine.getScorePercent());
    assertEquals(0f, wine.getAbv(), 0.01);
    assertEquals(0f, wine.getPrice(), 0.01);
  }

  /**
   * Tests the parseWine method with an invalid vintage string.
   *
   * @throws ValidationException if wine parsing fails
   */
  @Test
  void testParseWineInvalidVintage() throws ValidationException {
    Wine wine = WineValidator.parseWine(
        "Test Wine", "Merlot", "France", "Bordeaux",
        "Test Winery", "Red", "Not a year", "A fine wine", "95", "14.5", "50.0", mockGeoLocation
    );

    assertNotNull(wine);
    assertEquals(0, wine.getVintage());
  }

  /**
   * Tests the parseWine method with invalid strings for numeric fields. Verifies that a
   * ValidationException is thrown when non-numeric strings are provided for numeric fields.
   */
  @Test
  void testParseWineInvalidNumericFields() {
    assertThrows(ValidationException.class, () -> {
      WineValidator.parseWine(
          "Test Wine", "Merlot", "France", "Bordeaux",
          "Test Winery", "Red", "2020", "A fine wine", "Not a number", "Not a number",
          "Not a number", mockGeoLocation
      );
    });
  }

  /**
   * Tests the parseWine method with null values for all fields. Verifies that a ValidationException
   * is thrown when null values are provided.
   */
  @Test
  void testParseWineNullFields() {
    assertThrows(ValidationException.class, () -> {
      WineValidator.parseWine(
          null, null, null, null,
          null, null, null, null, null, null, null, null
      );
    });
  }
}