package seng202.team6.util;

import java.io.IOException;
import java.io.InputStream;
import javafx.scene.image.Image;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ImageReader {
  private static final Logger log = LogManager.getLogger(ImageReader.class);

  public static Image loadImage(String path) {
    try (InputStream inputStream = ImageReader.class.getResourceAsStream(path)) {
      if (inputStream != null)
        return new Image(inputStream);
      log.error("Could not load image at path %s".formatted(path));
    } catch (IOException error) {
      log.error("Could load image", error);
    }
    return null;
  }
}
