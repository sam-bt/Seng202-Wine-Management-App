package seng202.team6.unittests.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;
import seng202.team6.util.YearStringConverter;

/**
 * Test class for the YearStringConverter. This class contains unit tests to verify the
 * functionality of the YearStringConverter
 */
public class YearStringConverterTest {

  private final YearStringConverter converter = new YearStringConverter();

  /**
   * Tests that the to string method works with valid numbers
   */
  @Test
  public void testToStringWithValidNumbers() {
    assertEquals("0005", converter.toString(5));
    assertEquals("1985", converter.toString(1985));
    assertEquals("0000", converter.toString(0));
  }

  /**
   * Tests that the to string method works for null values
   */
  @Test
  public void testToStringWithNull() {
    assertEquals("", converter.toString(null));
  }

  /**
   * Tests that the from string method works with valid numbers
   */
  @Test
  public void testFromStringWithValidStrings() {
    assertEquals(1985, converter.fromString("1985"));
    assertEquals(0, converter.fromString("0000"));
    assertEquals(5, converter.fromString("0005"));
  }

  /**
   * Tests that the from string handles invalid strings
   */
  @Test
  public void testFromStringWithInvalidStrings() {
    assertNull(converter.fromString(""));
    assertNull(converter.fromString(null));
    assertEquals(-1, converter.fromString("abcd"));
    assertEquals(-1, converter.fromString("1985.5"));
  }

  /**
   * Tests that the from string works as expected for edge cases
   */
  @Test
  public void testFromStringWithEdgeCases() {
    assertEquals(9999, converter.fromString("9999"));
    assertEquals(-1234, converter.fromString("-1234"));
  }
}

