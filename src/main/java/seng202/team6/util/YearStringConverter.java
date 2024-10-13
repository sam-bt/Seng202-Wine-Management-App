package seng202.team6.util;

import java.text.DecimalFormat;
import javafx.util.StringConverter;

/**
 * This class will format the number as a year (DDDD). Used in setting the tick labels of the
 * vintage slider.
 */
public class YearStringConverter extends StringConverter<Number> {

  private final DecimalFormat yearFormat = new DecimalFormat("0000");

  /**
   * Convert the value to a string.
   *
   * @param value value
   * @return string in the form DDDD
   */
  @Override
  public String toString(Number value) {
    if (value == null) {
      return "";
    }
    return yearFormat.format(value);
  }

  /**
   * Converts the given string into an int. Unused in this program.
   *
   * @param string string
   * @return integer
   */
  @Override
  public Integer fromString(String string) {
    if (string == null || string.isEmpty()) {
      return null;
    }
    try {
      return Integer.parseInt(string);
    } catch (NumberFormatException e) {
      return -1;
    }
  }
}
