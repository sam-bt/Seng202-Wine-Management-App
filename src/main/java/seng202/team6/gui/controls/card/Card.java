package seng202.team6.gui.controls.card;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.geometry.Insets;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class Card extends HBox {
  public Card(ReadOnlyDoubleProperty containerWidth, DoubleProperty hGap) {
    setPadding(new Insets(10));
    setStyle("-fx-background-color: #f3f4f6; -fx-background-radius: 10px;");

    containerWidth.addListener((observableValue, before, after) ->
        setCardWidth(after.doubleValue(), hGap.doubleValue()));
    hGap.addListener((observableValue, before, after) ->
        setCardWidth(containerWidth.doubleValue(), after.doubleValue()));
    if (containerWidth.get() != 0) {
      setCardWidth(containerWidth.doubleValue(), hGap.doubleValue());
    }
  }

  private void setCardWidth(double containerWidth, double hGap) {
    if (containerWidth != 0) {
      double tileWidth = (containerWidth - hGap * 2) / 3 - 10; // Adjust for gaps and padding
      setPrefWidth(tileWidth);
    }
  }
}
