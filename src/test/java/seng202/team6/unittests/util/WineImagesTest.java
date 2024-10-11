package seng202.team6.unittests.util;

import static org.junit.Assert.assertEquals;

import org.junit.jupiter.api.Test;
import seng202.team6.enums.ColourMatch;
import seng202.team6.util.WineImages;

public class WineImagesTest {
  private WineImages wineImages = new WineImages();

  @Test
  void testMatchRed() {
    ColourMatch result = wineImages.matchVarieties("Red Blend");
    assertEquals("red", result.getColour());
  }

  @Test
  void testMatchRose() {
    ColourMatch result = wineImages.matchVarieties("Rosé");
    assertEquals("rose", result.getColour());
  }

  @Test
  void testMatchPinotGris() {
    ColourMatch result = wineImages.matchVarieties("Pinot Gris");
    assertEquals("white", result.getColour());
  }
}
