package seng202.team6.gui.controls;

import javafx.event.Event;
import org.controlsfx.control.Rating;

/**
 * An extension of the Rating control that creates an unmodifiable rating component. This class
 * disables user interaction with the rating control, making it display-only.
 */
public class UnmodifiableRating extends Rating {

  /**
   * Constructs a new UnmodifiableRating control. This rating control is set to be non-interactive,
   * meaning users cannot modify the rating by clicking or hovering over it.
   */
  public UnmodifiableRating() {
    setUpdateOnHover(false);
    setMouseTransparent(true);
    setOnMouseClicked(Event::consume);
    setOnMouseDragEntered(Event::consume);
    setPartialRating(true);
  }
}
