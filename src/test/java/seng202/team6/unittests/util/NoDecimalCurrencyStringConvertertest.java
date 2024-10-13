package seng202.team6.unittests.util;

import static org.junit.Assert.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import seng202.team6.util.NoDecimalCurrencyStringConverter;

public class NoDecimalCurrencyStringConvertertest {

  NoDecimalCurrencyStringConverter converter = new NoDecimalCurrencyStringConverter();

  @BeforeEach
  void setup() {
    converter = new NoDecimalCurrencyStringConverter();
  }

  /**
   * Test the converter without a decimal
   */
  @Test
  void testNormalConversion() {
    String result = converter.toString(75);
    assertEquals(result, "$75");
  }

  /**
   * Test the converter with a decimal
   */
  @Test
  void testNumberWithDecimalConversion() {
    String result = converter.toString(75.50);
    assertEquals(result, "$76");
  }

}
