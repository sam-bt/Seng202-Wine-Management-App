package seng202.team6.util;

import java.util.HashMap;
import java.util.Map;
import javafx.scene.image.Image;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import seng202.team6.enums.ColourMatch;

/**
 * Class for handling the loading of wine images, since this code was duplicated over and over.
 * Extended to also handle variety matching.
 */
public class WineImages {

  private static final Logger log = LogManager.getLogger(WineImages.class);
  private static final Image RED_WINE_IMAGE = ImageReader.loadImage(
      "/img/red_wine_cropped.png");
  private static final Image WHITE_WINE_IMAGE = ImageReader.loadImage(
      "/img/white_wine_cropped.png");
  private static final Image ROSE_WINE_IMAGE = ImageReader.loadImage(
      "/img/rose_wine_cropped.png");
  private static final Image DEFAULT_WINE_IMAGE = ImageReader.loadImage(
      "/img/default_wine_cropped.png");
  private static final Map<String, Image> wineImages = new HashMap<>() {
    {
      put("red", RED_WINE_IMAGE);
      put("white", WHITE_WINE_IMAGE);
      put("rose", ROSE_WINE_IMAGE);
      put("rosé", ROSE_WINE_IMAGE);
      put("default", DEFAULT_WINE_IMAGE);
    }
  };

  /**
   * Gets the wine image from the HashMap above. If the string doesn't match, you get
   * a default image instead.
   *
   * @param imageName The name of the image.
   * @return an Image of the requested wine.
   */
  public static Image getWineImage(String imageName) {
    if (!wineImages.containsKey(imageName)) {
      log.info("No image with name " + imageName + "; Returning default image");
    }
    return wineImages.getOrDefault(imageName, DEFAULT_WINE_IMAGE);
  }

  /**
   * Combines the other two functions to retrieve an Image from the specified variety string.
   *
   * @param variety a String of the wine variety
   * @return an Image of the wine. if no match was found, return DEFAULT_WINE_IMAGE
   */
  public static Image getImageByVariety(String variety) {
    ColourMatch colourMatch = matchVarieties(variety);
    return getWineImage(colourMatch.getColour());
  }

  /**
   * Will attempt to match the provided string to a colour.
   *
   * @return the colour string.
   */
  public static ColourMatch matchVarieties(String variety) {
    if (variety.toUpperCase().contains("RED")) {
      return ColourMatch.RED_WINE;
    } else if (variety.toUpperCase().contains("WHITE")) {
      return ColourMatch.WHITE_WINE;
    } else if (variety.toUpperCase().contains("ROSE")) {
      return ColourMatch.ROSE_WINE;
    } else if (variety.contains("Rosé")) {
      return ColourMatch.ROSE_WINE;
    }

    variety = variety.replace(" ", "_");

    for (ColourMatch colourMatch : ColourMatch.values()) {
      if (colourMatch.name().toUpperCase().contains(variety.toUpperCase())) {
        return colourMatch;
      }
    }

    return ColourMatch.DEFAULT;

  }



}
