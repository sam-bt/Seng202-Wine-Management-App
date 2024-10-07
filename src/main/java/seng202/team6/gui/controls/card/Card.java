package seng202.team6.gui.controls.card;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.geometry.Insets;
import javafx.scene.layout.HBox;

/**
 * Represents a customizable card layout that adjusts its width based on the container it resides in
 * and the specified horizontal gap.
 */
public class Card extends HBox {

  /**
   * Constructs a Card with specified container width and horizontal gap.
   *
   * @param containerWidth         The width of the container that holds the card.
   * @param containerHorizontalGap The containers horizontal gap between the card and its
   *                               neighbors.
   */
  public Card(ReadOnlyDoubleProperty containerWidth, DoubleProperty containerHorizontalGap) {
    setPadding(new Insets(10));
    setStyle("-fx-background-color: #f3f4f6; -fx-background-radius: 10px;");

    containerWidth.addListener((observableValue, before, after) ->
        setCardWidth(after.doubleValue(), containerHorizontalGap.doubleValue()));
    containerHorizontalGap.addListener((observableValue, before, after) ->
        setCardWidth(containerWidth.doubleValue(), after.doubleValue()));
    if (containerWidth.get() != 0) {
      setCardWidth(containerWidth.doubleValue(), containerHorizontalGap.doubleValue());
    }
  }

  /**
   * Sets the width of the card based on the container's width and the horizontal gap.
   *
   * @param containerWidth The width of the container.
   * @param horizontalGap  The horizontal gap of the container.
   */
  private void setCardWidth(double containerWidth, double horizontalGap) {
    if (containerWidth != 0) {
      // adjust for gaps and padding (-10)
      double tileWidth = (containerWidth - horizontalGap * 2) / 3 - 10;
      setPrefWidth(tileWidth);
    }
  }
}
