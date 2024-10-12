package seng202.team6.enums;

import javafx.scene.image.Image;
import seng202.team6.util.ImageReader;

/**
 * Enum representing different types of popups.
 * Each popup type can have an associated icon image.
 */
public enum PopupType {
  /**
   * Error popup type, with an error icon.
   */
  ERROR("/img/error_icon.png"),

  /**
   * None popup type, with no icon.
   */
  NONE();

  /**
   * The icon associated with the popup type.
   */
  private final Image icon;

  /**
   * Constructs a {@code PopupType} with a specified icon path.
   *
   * @param iconPath The file path to the icon image.
   */
  PopupType(String iconPath) {
    this.icon = ImageReader.loadImage(iconPath);
  }

  /**
   * Constructs a {@code PopupType} with no associated icon.
   */
  PopupType() {
    this.icon = null;
  }

  /**
   * Gets the icon associated with the popup type.
   *
   * @return The icon image, or null if there is no icon.
   */
  public Image getIcon() {
    return icon;
  }
}
