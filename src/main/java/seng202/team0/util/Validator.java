package seng202.team0.util;

import java.util.Objects;
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
      String winery,
      String color,
      String vintage,
      String description,
      String scorePercent,
      String abv,
      String price
  ) throws ValidationException {
    try {

      // Decanter has invalid values for year :'(
      // This handles those values by setting them to zero
      int parsedVintage = 0;
      if (!Objects.equals(vintage, "")) {
        try {
          parsedVintage = Integer.parseInt(vintage);
        } catch (NumberFormatException e) { // failed parse so default to 0

          // Log manager wasn't working for me here
          // Logmanager.getLogger(Validator.class.getName()) kept returning null
          System.err.println("Invalid vintage value: " + vintage + ", defaulting to 0");
        }
      }

      return new Wine(
          title,
          variety,
          country,
          winery,
          color,
          parsedVintage,
          description,
          Objects.equals(scorePercent, "") ? 0 : Integer.parseInt(scorePercent),
          Objects.equals(abv, "") ? 0 : Float.parseFloat(abv),
          Objects.equals(price, "") ? 0 : Float.parseFloat(price)
      );
    } catch (Exception e) {
      throw new ValidationException("Failed to parse wine", e);
    }
  }
}
