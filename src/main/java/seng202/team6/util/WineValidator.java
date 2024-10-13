package seng202.team6.util;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import seng202.team6.enums.ColourMatch;
import seng202.team6.model.GeoLocation;
import seng202.team6.model.Wine;
import seng202.team6.util.exceptions.ValidationException;

/**
 * Validator is used validating and unmarshalling data.
 * <p>
 * The API of this is deliberately made so you validate/construct in one call. This is to prevent
 * scenarios where one forgets to validate an input or constructs a broken object.
 * </p>
 */
public class WineValidator {
  private static final Pattern VINTAGE_PATTERN = Pattern.compile("(18|19|20)(\\d\\d)");

  /**
   * Creates a wine from a list of attributes.
   *
   * @param title        title
   * @param variety      variety
   * @param country      country
   * @param winery       winery
   * @param color        color
   * @param vintage      vintage
   * @param description  description
   * @param scorePercent percent score
   * @param abv          abv
   * @param price        price
   * @return wine
   */
  public static Wine parseWine(
      String title,
      String variety,
      String country,
      String region,
      String winery,
      String color,
      String vintage,
      String description,
      String scorePercent,
      String abv,
      String price,
      GeoLocation geoLocation
  ) throws ValidationException {
    try {
      return new Wine(
          -1,
          title,
          variety,
          country,
          region,
          winery,
          getOrExtractColourFromVariety(variety, color),
          getOrExtractVintageFromTitle(title, vintage),
          description,
          Objects.equals(scorePercent, "") ? 0 : Integer.parseInt(scorePercent),
          Objects.equals(abv, "") ? 0 : Float.parseFloat(abv),
          Objects.equals(price, "") ? 0 : Float.parseFloat(price),
          geoLocation,
          0.0
      );
    } catch (Exception e) {
      throw new ValidationException("Failed to parse wine", e);
    }
  }

  /**
   * Extracts or returns the vintage year from the given title or vintage string. If the
   * vintage string is a valid integer, it is returned. Otherwise, it attempts to extract the
   * vintage from the title using a regex pattern.
   *
   * @param title         the title containing a possible vintage year
   * @param vintageString the string that may contain the vintage year
   * @return the vintage year if valid, 0 if no valid vintage found
   */
  public static int getOrExtractVintageFromTitle(String title, String vintageString) {
    try {
      return Integer.parseInt(vintageString);
    } catch (NumberFormatException ignored) {
      Matcher matcher = VINTAGE_PATTERN.matcher(title);
      return matcher.find() ? Integer.parseInt(matcher.group()) : 0;
    }
  }

  /**
   * Returns the colour associated with a wine variety, or uses the provided colour if available.
   * If the given colour is not null or empty, it is returned as the result. Otherwise, the method
   * attempts to determine the colour based on the wine variety.
   *
   * @param variety the wine variety
   * @param colour the pre-determined colour of the wine
   * @return the wine colour, either from the provided colour or determined from the variety
   */
  public static String getOrExtractColourFromVariety(String variety, String colour) {
    if (colour != null && !colour.isEmpty()) {
      return colour;
    }
    return ColourMatch.match(variety).getColour();
  }
}
