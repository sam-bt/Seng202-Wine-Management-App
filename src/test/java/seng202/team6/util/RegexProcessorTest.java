package seng202.team6.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class RegexProcessorTest {
    RegexProcessor regexProcessor = new RegexProcessor();
    String twentyfirst = "Pinot Noir made in 2005";
    String twentieth = "Red made in 1984";
    String nineteenth = "White made in 1896";
    String noYear = "Rose of unknown origin";
    String nonYearNUmber = "Rose number 1178";

    /**
     * Test extracting a 21st-century year from a string
     */
    @Test
    public void testExtract21stCentury() {
        String year = regexProcessor.extractYearFromString(twentyfirst);
        assertEquals(year, "2005");
    }

    /**
     * Test extracting a 20th-century year from a string
     */
    @Test
    public void testExtract20thCentury() {
        String year = regexProcessor.extractYearFromString(twentieth);
        assertEquals(year, "1984");
    }

    /**
     * Test extracting a 19th-century year from a string
     */
    @Test
    public void testExtract19thCentury() {
        String year = regexProcessor.extractYearFromString(nineteenth);
        assertEquals(year, "1896");
    }

    /**
     * Test processing a string with no numbers in it
     */
    @Test
    public void testNoYearInString() {
        String year = regexProcessor.extractYearFromString(noYear);
        assertEquals(year, "-1");
    }

    /**
     * Test processing of an invalid year / number which doesn't meet the year requirements (1800-2099)
     */
    @Test
    public void testNonYearNumberInString() {
        String year = regexProcessor.extractYearFromString(nonYearNUmber);
        assertEquals(year, "-1");
    }
}
