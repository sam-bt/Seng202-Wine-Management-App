package seng202.team0.util;

import seng202.team0.database.Wine;
import seng202.team0.util.Exceptions.ValidationException;

public class Validator {

  /**
   * Creates a wine from a list of attributes
   *
   * @param title title
   * @param variety variety
   * @param country country
   * @param winery winery
   * @param description description
   * @param scorePercent percent score
   * @param abv abv
   * @param price price
   * @return wine
   */
  Wine parseWine(
      String title,
      String variety,
      String country,
      String winery,
      String description,
      String scorePercent,
      String abv,
      String price
      ) throws ValidationException {
      try {
        return new Wine(
            title,
            variety,
            country,
            winery,
            description,
            Integer.parseInt(scorePercent),
            Float.parseFloat(abv),
            Float.parseFloat(price)
        );
      }  catch (Exception e) {
        throw new ValidationException("Failed to parse wine", e);
      }
  }
}
