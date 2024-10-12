package seng202.team6.util;

import java.util.HashMap;
import java.util.Map;
import javafx.scene.image.Image;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import seng202.team6.enums.ColourMatch;
import seng202.team6.model.Wine;

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
  private static final Map<String, Image> WINE_IMAGES = new HashMap<>() {
    {
      put("red", RED_WINE_IMAGE);
      put("white", WHITE_WINE_IMAGE);
      put("rose", ROSE_WINE_IMAGE);
      put("rosé", ROSE_WINE_IMAGE);
      put("default", DEFAULT_WINE_IMAGE);
    }
  };

  public static Image getImage(Wine wine) {
    String color = wine.getColor();
    if (color != null && !color.isEmpty()) {
      return WINE_IMAGES.getOrDefault(color.toLowerCase(), DEFAULT_WINE_IMAGE);
    }
    return DEFAULT_WINE_IMAGE;
  }
}
