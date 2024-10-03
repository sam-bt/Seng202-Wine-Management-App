package seng202.team6.gui.controls;

import javafx.geometry.Insets;
import javafx.scene.layout.VBox;

public class CardContainer extends VBox {
  public CardContainer() {
    setPadding(new Insets(10));
    setStyle("-fx-background-color: #f3f4f6; -fx-background-radius: 10px;");
  }

  public void setCardWidth(double containerWidth, double hGap) {
    if (containerWidth != 0) {
      double tileWidth = (containerWidth - hGap * 2) / 3 - 10; // Adjust for gaps and padding
      setPrefWidth(tileWidth);
    }
  }
}
