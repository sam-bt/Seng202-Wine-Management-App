package seng202.team6.gui.controls.container;

import java.util.HashMap;
import java.util.Map;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.geometry.Bounds;
import javafx.scene.layout.VBox;
import seng202.team6.gui.controls.card.Card;

/**
 * A class that represents a container for managing a collection of cards.
 *
 * @param <T> the type of elements associated with the cards in this container
 */
public class CardsContainer<T> extends VBox {

  /**
   * Stores a mapping of elements to their corresponding buttons.
   */
  private final Map<T, Card> cards = new HashMap<>();

  /**
   * Constructs a CardsContainer that adjusts its width based on the specified viewport bounds and
   * scroll pane width.
   *
   * @param viewportBounds  a ReadOnlyObjectProperty representing the bounds of the viewport
   * @param scrollPaneWidth a ReadOnlyDoubleProperty representing the width of the scroll pane
   */
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

  /**
   * Adds a card associated with a specified element to the container.
   *
   * @param element       the element associated with the card
   * @param cardContainer the Card object to be added to the container
   */
  public void addCard(T element, Card cardContainer) {
    cards.put(element, cardContainer);
    getChildren().add(cardContainer);
  }

  /**
   * Removes the card associated with the specified element from the container.
   *
   * @param element the element whose associated card should be removed
   */
  public void remove(T element) {
    Card card = cards.remove(element);
    if (card != null) {
      getChildren().remove(card);
    }
  }

  /**
   * Removes all cards from the container.
   */
  public void removeAll() {
    cards.values().removeIf(card -> {
      getChildren().remove(card);
      return true;
    });
  }
}
