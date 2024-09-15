package seng202.team0.util;

import seng202.team0.database.GeoLocation;
import seng202.team0.database.Wine;
import seng202.team0.util.Exceptions.ValidationException;

/**
 * Validator is used validating and unmarshalling data
 * <p>
 * The API of this is deliberately made so you validate/construct in one call. This is to prevent
 * scenarios where one forgets to validate an input or constructs a broken object.
 * </p>
 */
public class Validator {

  /**
   * Creates a wine from a list of attributes
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
            title,
            variety,
            country,
            region,
            winery,
            color,
            parseIntegerOrDefault(vintage),
            description,
            parseIntegerOrDefault(scorePercent),
            parseFloatOrDefault(abv),
            parseFloatOrDefault(price),
            geoLocation
        );
      }  catch (Exception e) {
        throw new ValidationException("Failed to parse wine", e);
      }
  }

  private static int parseIntegerOrDefault(String string) {
    try {
      return Integer.parseInt(string);
    } catch (NumberFormatException ignored) {
      return 0;
    }
  }

  private static float parseFloatOrDefault(String string) {
    try {
      return Float.parseFloat(string);
    } catch (NumberFormatException ignored) {
      return 0.0f;
    }
  }
}
