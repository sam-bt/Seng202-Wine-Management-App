package seng202.team6.unittests.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import seng202.team6.enums.ColourMatch;

public class ColourMatchTest {
  @Test
  void testMatchRed() {
    ColourMatch result = ColourMatch.match("Red Blend");
    assertEquals(ColourMatch.RED, result);
  }

  @Test
  void testMatchRose() {
    ColourMatch result = ColourMatch.match("Rosé");
    assertEquals(ColourMatch.ROSE, result);
  }

  @Test
  void testMatchPinotGris() {
    ColourMatch result = ColourMatch.match("Pinot Gris");
    assertEquals(ColourMatch.PINOT_GRIS, result);
  }
}
