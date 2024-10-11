package seng202.team6.util;

import java.util.HashMap;
import java.util.Map;
import javafx.scene.image.Image;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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



}
