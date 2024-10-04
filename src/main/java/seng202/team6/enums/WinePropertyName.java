package seng202.team6.enums;

import java.util.HashMap;
import java.util.Map;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public enum WinePropertyName {
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
  NZD("NZD");

  private static Map<String, WinePropertyName> PROPERTY_BY_NAME = new HashMap<>() {{
    for (WinePropertyName winePropertyName : WinePropertyName.values()) {
      put(winePropertyName.name.toLowerCase(), winePropertyName);
      for (String alias : winePropertyName.aliases) {
        put(alias.toLowerCase(), winePropertyName);
      }
    }
  }};
  public static final ObservableList<WinePropertyName> VALUES = FXCollections.observableArrayList(values());

  private final String name;
  private final String[] aliases;

  WinePropertyName(String prettyName, String... aliases) {
    this.name = prettyName;
    this.aliases = aliases;
  }

  public String getName() {
    return name;
  }

  public static WinePropertyName tryMatch(String text) {
    return PROPERTY_BY_NAME.get(text.toLowerCase());
  }
}
