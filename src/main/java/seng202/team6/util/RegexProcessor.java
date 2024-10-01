package seng202.team6.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexProcessor {

  /**
   * Extracts a year from 1800-2099 from the given string.
   *
   * @param inputString <- The string which contains the year.
   * @return The extracted year, or -1 if there is none.
   */
  public String extractYearFromString(String inputString) {
    Pattern yearPattern = Pattern.compile("(18|19|20)(\\d\\d)", Pattern.CASE_INSENSITIVE);
    Matcher yearMatcher = yearPattern.matcher(inputString);
    if (yearMatcher.find()) {
      return yearMatcher.group();
    } else {
      return "-1";
    }
  }
}
