package seng202.team0.util;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class RegexProcessorTest {
    RegexProcessor regexProcessor = new RegexProcessor();

    @Test
    public void testExtractYear() {
        String twentyfirst = "Pinot Noir made in 2005";
        String twentieth = "Red made in 1984";
        String nineteenth = "White made in 1896";
        String noYear = "Rose of unknown origin";
        String nonYearNUmber = "Rose number 1178";

        String output = regexProcessor.extractYearFromString(twentyfirst);
        assertEquals(output, "2005");

        output = regexProcessor.extractYearFromString(twentieth);
        assertEquals(output, "1984");

        output = regexProcessor.extractYearFromString(nineteenth);
        assertEquals(output, "1896");

        output = regexProcessor.extractYearFromString(noYear);
        assertEquals(output, "No match!");

        output = regexProcessor.extractYearFromString(nonYearNUmber);
        assertEquals(output, "No match!");

    }
}
