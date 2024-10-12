package seng202.team6.enums;

/**
 * This enum is used to match wine varieties to their colours.
 */
public enum ColourMatch {
  // standard colours
  RED("red"),
  WHITE("white"),
  ROSE("rose"),
  DEFAULT("default"),

  // the top 20
  PINOT_GRIS("white"),
  PINOT_NOIR("red"),
  CHARDONNAY("white"),
  CAB_SAV("red"),
  RIESLING("white"),
  SAV_BLANC("white"),
  SYRAH("red"),
  MERLOT("red"),
  NEBBIOLO("red"),
  ZINFANDEL("red"),
  SANGIOVESE("red"),
  MALBEC("red"),
  SPARKLING("white"),
  TEMPRANILLO("red");

  private final String variety;
  private final String colour;

  /**
   * Constructor to assign the wine colour and derive the variety name.
   *
   * @param colour the colour associated with the wine variety (e.g., red, white)
   */
  ColourMatch(String colour) {
    this.variety = name().toLowerCase().replace("_", " ");
    this.colour = colour;
  }

  /**
   * Gets the variety name in lowercase format.
   *
   * @return the wine variety name
   */
  public String getVariety() {
    return variety;
  }

  /**
   * Gets the colour associated with the wine variety.
   *
   * @return the colour of the wine (e.g., "red", "white", "rose")
   */
  public String getColour() {
    return colour;
  }

  /**
   * Matches a given variety string to a corresponding ColourMatch enum value. The method performs
   * a case-insensitive search.
   *
   * @param variety the wine variety as a string
   * @return the matching ColourMatch value, or null if no match is found
   */
  public static ColourMatch match(String variety) {
    variety = variety.toLowerCase();
    // special case for rosé which contains special character
    if (variety.contains("rosé")) {
      return ROSE;
    }
    for (ColourMatch value : values()) {
      if (variety.contains(value.getVariety())) {
        return value;
      }
    }
    return null;
  }
}
