package seng202.team6.gui.controls.container;

import java.util.HashMap;
import java.util.Map;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.geometry.Bounds;
import javafx.scene.layout.VBox;
import seng202.team6.gui.controls.card.Card;

public class CardsContainer<T> extends VBox {

  /**
   * Stores a mapping of elements to their corresponding buttons.
   */
  private final Map<T, Card> cards = new HashMap<>();

  public CardsContainer(ReadOnlyObjectProperty<Bounds> viewportBounds,
      ReadOnlyDoubleProperty scrollPaneWidth) {
    setSpacing(10);
    setFillWidth(true);

    // Listen to the viewport width and the scroll pane's width and adjust VBox width accordingly
    viewportBounds.addListener((obs, oldBounds, newBounds) -> {
      if (newBounds != null) {
        double newWidth = newBounds.getWidth();
        setPrefWidth(newWidth);
      }
    });

    scrollPaneWidth.addListener((obs, oldVal, newVal) -> {
      // Adjust the VBox width based on the ScrollPane width, accounting for possible scroll bars
      if (newVal != null) {
        setPrefWidth(newVal.doubleValue());
      }
    });
  }

  public void addCard(T element, Card cardContainer) {
    cards.put(element, cardContainer);
    getChildren().add(cardContainer);
  }

  public void remove(T element) {
    Card card = cards.remove(element);
    if (card != null) {
      getChildren().remove(card);
    }
  }

  public void removeAll() {
    cards.values().removeIf(card -> {
      getChildren().remove(card);
      return true;
    });
  }
}
