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

  ColourMatch(String colour) {
    this.variety = name().toLowerCase().replace("_", "");
    this.colour = colour;
  }

  public String getVariety() {
    return variety;
  }

  public String getColour() {
    return colour;
  }

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
