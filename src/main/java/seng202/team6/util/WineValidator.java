package seng202.team6.util;

import java.util.Objects;
import seng202.team6.managers.DatabaseManager;
import seng202.team6.model.GeoLocation;
import seng202.team6.model.Wine;
import seng202.team6.util.Exceptions.ValidationException;

/**
 * Validator is used validating and unmarshalling data
 * <p>
 * The API of this is deliberately made so you validate/construct in one call. This is to prevent
 * scenarios where one forgets to validate an input or constructs a broken object.
 * </p>
 */
public class WineValidator {


  /**
   * Parses a vintage
   * <p>
   * Decanter sometimes does not have a number for the vintage
   * </p>
   *
   * @param vintage vintage
   * @return vintage or null if invalid
   */
  private static int parseVintage(String vintage) {
    try {
      return Integer.parseInt(vintage);
    } catch (Exception exception) {
      return 0;
    }
  }

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
      DatabaseManager databaseManager,
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
          databaseManager,
          title,
          variety,
          country,
          region,
          winery,
          color,
          parseVintage(vintage),
          description,
          Objects.equals(scorePercent, "") ? 0 : Integer.parseInt(scorePercent),
          Objects.equals(abv, "") ? 0 : Float.parseFloat(abv),
          Objects.equals(price, "") ? 0 : Float.parseFloat(price),
          geoLocation
      );
    } catch (Exception e) {
      throw new ValidationException("Failed to parse wine", e);
    }
  }
}
