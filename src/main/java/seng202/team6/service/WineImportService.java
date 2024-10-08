package seng202.team6.service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import seng202.team6.enums.WinePropertyName;


/**
 * The Wine import service class provides methods to the wine importing functionality.
 */
public class WineImportService {

  /**
   * The creator for the hashmap of valid wine properties.
   *
   * @param selectedWineProperties the properties that been selected
   * @return the hashmap of valid wine properties
   */
  public Map<WinePropertyName, Integer> validHashMapCreate(
      Map<Integer, WinePropertyName> selectedWineProperties) {
    return new HashMap<>() {
      {
        selectedWineProperties.forEach(((integer, winePropertyName) ->
            put(winePropertyName, integer)));
      }
    };
  }

  /**
   * Gets the string at a row if it is valid or empty string.
   *
   * @param valid            map of valid wine property names
   * @param row              row of table to import
   * @param winePropertyName name of property
   * @return string from row or default
   */
  public String extractPropertyFromRowOrDefault(Map<WinePropertyName, Integer> valid, String[] row,
      WinePropertyName winePropertyName) {
    return valid.containsKey(winePropertyName) ? row[valid.get(winePropertyName)] : "";
  }

  /**
   * Check for duplicate attribute names.
   *
   * @return the duplicateProperties
   */
  public Set<WinePropertyName> checkDuplicateProperties(
      Map<Integer, WinePropertyName> selectedWineProperties) {
    Set<WinePropertyName> duplicatedProperties = new HashSet<>();
    Set<WinePropertyName> selectedProperties = new HashSet<>();
    selectedWineProperties.forEach((index, winePropertyName) -> {
      if (!selectedProperties.add(winePropertyName)) {
        duplicatedProperties.add(winePropertyName);
      }
    });
    return duplicatedProperties;
  }


}
