package seng202.team6.gui.controls;

import javafx.event.Event;
import org.controlsfx.control.Rating;

public class UnmodifiableRating extends Rating {
  public UnmodifiableRating() {
    setUpdateOnHover(false);
    setMouseTransparent(true);
    setOnMouseClicked(Event::consume);
    setOnMouseDragEntered(Event::consume);
    setPartialRating(true);
  }
}
