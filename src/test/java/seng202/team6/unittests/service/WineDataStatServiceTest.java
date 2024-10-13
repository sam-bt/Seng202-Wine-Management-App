package seng202.team6.unittests.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import seng202.team6.service.WineDataStatService;

public class WineDataStatServiceTest {

  private WineDataStatService wineDataStatService;

  @BeforeEach
  void setUp() {
    wineDataStatService = new WineDataStatService();
  }

  @Test
  void initialEmptySets() {
    assertTrue(wineDataStatService.getUniqueTitles().isEmpty());
    assertTrue(wineDataStatService.getUniqueCountries().isEmpty());
    assertTrue(wineDataStatService.getUniqueColors().isEmpty());
    assertTrue(wineDataStatService.getUniqueWineries().isEmpty());
  }

  @Test
  void testReset() {
    wineDataStatService.getUniqueTitles().add("Title1");
    wineDataStatService.getUniqueCountries().add("Country1");
    wineDataStatService.setMinVintage(1990);
    wineDataStatService.setMaxVintage(2020);
    wineDataStatService.setMinScore(85);
    wineDataStatService.setMaxScore(95);
    wineDataStatService.setMinAbv(12.0f);
    wineDataStatService.setMaxAbv(15.5f);
    wineDataStatService.setMinPrice(10.0f);
    wineDataStatService.setMaxPrice(50.0f);

    // Reset
    wineDataStatService.reset();

    assertTrue(wineDataStatService.getUniqueTitles().isEmpty());
    assertTrue(wineDataStatService.getUniqueCountries().isEmpty());
    assertTrue(wineDataStatService.getUniqueColors().isEmpty());
    assertTrue(wineDataStatService.getUniqueWineries().isEmpty());

    // Check min and max values reset correctly
    assertEquals(Integer.MAX_VALUE, wineDataStatService.getMinVintage());
    assertEquals(0, wineDataStatService.getMaxVintage());
    assertEquals(100, wineDataStatService.getMinScore());
    assertEquals(0, wineDataStatService.getMaxScore());
    assertEquals(100.0f, wineDataStatService.getMinAbv());
    assertEquals(0.0f, wineDataStatService.getMaxAbv());
    assertEquals(Float.MAX_VALUE, wineDataStatService.getMinPrice());
    assertEquals(0, wineDataStatService.getMaxPrice());
  }

}
