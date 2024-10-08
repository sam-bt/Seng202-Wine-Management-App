package seng202.team6.unittests.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import seng202.team6.enums.WinePropertyName;
import seng202.team6.service.WineImportService;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class WineImportServiceTest {

  private WineImportService wineImportService;

  @BeforeEach
  void setUp() {
    wineImportService = new WineImportService();
  }

  @Test
  void testValidHashMapCreate() {
    Map<Integer, WinePropertyName> selectedWineProperties = new HashMap<>();
    selectedWineProperties.put(0, WinePropertyName.TITLE);
    selectedWineProperties.put(1, WinePropertyName.VARIETY);

    Map<WinePropertyName, Integer> result = wineImportService.validHashMapCreate(selectedWineProperties);

    assertEquals(2, result.size());
    assertEquals(0, result.get(WinePropertyName.TITLE));
    assertEquals(1, result.get(WinePropertyName.VARIETY));
  }

  @Test
  void testExtractPropertyFromRowOrDefaultValidProperty() {
    Map<WinePropertyName, Integer> valid = new HashMap<>();
    valid.put(WinePropertyName.TITLE, 0);
    String[] row = {"Red Wine", "Cabernet Sauvignon"};

    String result = wineImportService.extractPropertyFromRowOrDefault(valid, row, WinePropertyName.TITLE);

    assertEquals("Red Wine", result);
  }

  @Test
  void testExtractPropertyFromRowOrDefaultInvalidProperty() {
    Map<WinePropertyName, Integer> valid = new HashMap<>();
    valid.put(WinePropertyName.TITLE, 0);
    String[] row = {"Red Wine", "Cabernet Sauvignon"};

    String result = wineImportService.extractPropertyFromRowOrDefault(valid, row, WinePropertyName.ABV);

    assertEquals("", result);
  }

  @Test
  void testCheckDuplicatePropertiesNoDuplicates() {
    Map<Integer, WinePropertyName> selectedWineProperties = new HashMap<>();
    selectedWineProperties.put(0, WinePropertyName.TITLE);
    selectedWineProperties.put(1, WinePropertyName.VARIETY);

    Set<WinePropertyName> duplicates = wineImportService.checkDuplicateProperties(selectedWineProperties);

    assertTrue(duplicates.isEmpty());
  }

  @Test
  void testCheckDuplicatePropertiesWithDuplicates() {
    Map<Integer, WinePropertyName> selectedWineProperties = new HashMap<>();
    selectedWineProperties.put(0, WinePropertyName.TITLE);
    selectedWineProperties.put(1, WinePropertyName.TITLE);

    Set<WinePropertyName> duplicates = wineImportService.checkDuplicateProperties(selectedWineProperties);

    assertEquals(1, duplicates.size());
    assertTrue(duplicates.contains(WinePropertyName.TITLE));
  }
}
