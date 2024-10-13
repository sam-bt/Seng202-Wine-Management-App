package seng202.team6.enums;

import java.util.HashMap;
import java.util.Map;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Mapping of property to column name.
 */
public enum WinePropertyName {
  NONE(""),
  TITLE("Title", "Name", "Wine Name"),
  VARIETY("Variety"),
  COUNTRY("Country"),
  REGION("Region"),
  WINERY("Winery", "Producer"),
  COLOUR("Colour", "Color"),
  VINTAGE("Vintage", "Year"),
  DESCRIPTION("Description"),
  SCORE("Score"),
  ABV("ABV"),
  PRICE("Price");

  public static final ObservableList<WinePropertyName> VALUES = FXCollections.observableArrayList(
      values());
  private static final Map<String, WinePropertyName> PROPERTY_BY_NAME = new HashMap<>() {
    {
      for (WinePropertyName winePropertyName : WinePropertyName.values()) {
        put(winePropertyName.name.toLowerCase(), winePropertyName);
        for (String alias : winePropertyName.aliases) {
          put(alias.toLowerCase(), winePropertyName);
        }
      }
    }
  };
  private final String name;
  private final String[] aliases;

  /**
   * Constructor.
   *
   * @param prettyName real name of column
   * @param aliases    possible names the dataset may have
   */
  WinePropertyName(String prettyName, String... aliases) {
    this.name = prettyName;
    this.aliases = aliases;
  }

  /**
   * Tries to get a possible column name.
   *
   * @param text possible name
   * @return name if valid
   */
  public static WinePropertyName tryMatch(String text) {
    return PROPERTY_BY_NAME.getOrDefault(text.toLowerCase(), NONE);
  }

  /**
   * Gets name.
   *
   * @return name
   */
  public String getName() {
    return name;
  }

  @Override
  public String toString() {
    return name;
  }
}
