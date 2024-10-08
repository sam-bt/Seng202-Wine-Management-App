package seng202.team6.unittests.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import seng202.team6.util.RegexProcessor;

public class RegexProcessorTest {

  RegexProcessor regexProcessor = new RegexProcessor();

  /**
   * Test extracting a 21st-century year from a string
   */
  @Test
  void testExtract21stCentury() {
    String year = regexProcessor.extractYearFromString("Pinot Noir made in 2005");
    assertEquals(year, "2005");
  }

  /**
   * Test extracting a 20th-century year from a string
   */
  @Test
  void testExtract20thCentury() {
    String year = regexProcessor.extractYearFromString("Red made in 1984");
    assertEquals(year, "1984");
  }

  /**
   * Test extracting a 19th-century year from a string
   */
  @Test
  void testExtract19thCentury() {
    String year = regexProcessor.extractYearFromString("White made in 1896");
    assertEquals(year, "1896");
  }

  /**
   * Test processing a string with no numbers in it
   */
  @Test
  void testNoYearInString() {
    String year = regexProcessor.extractYearFromString("Rose of unknown origin");
    assertEquals(year, "-1");
  }

  /**
   * Test processing of an invalid year / number which doesn't meet the year requirements
   * (1800-2099)
   */
  @Test
  void testNonYearNumberInString() {
    String year = regexProcessor.extractYearFromString("Rose number 1178");
    assertEquals(year, "-1");
  }
}
