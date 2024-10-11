package seng202.team6.enums;

/**
 * This enum is used to match wine varieties to their colours.
 */
public enum ColourMatch {

  //=========| STANDARD COLOURS |==========
  RED_WINE("red"),
  WHITE_WINE("white"),
  ROSE_WINE("rose"),
  DEFAULT("default"),

  //=========| The Top 20, less any that have the colour in the name |==========
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

  private final String colour;

  ColourMatch(String colour) {
    this.colour = colour;
  }

  public String getColour() {
    return colour;
  }

}
