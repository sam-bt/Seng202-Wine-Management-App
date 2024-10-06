package seng202.team6.gui.controls;

import java.util.HashMap;
import java.util.Map;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

/**
 * A generic class that creates and manages a list of buttons, each associated with a unique
 * element. The buttons are displayed within a VBox, with their width dynamically bound to the
 * parent's width.
 *
 * @param <T> the type of the elements that the buttons are associated with
 */
public class ButtonsList<T> extends VBox {

  /**
   * Stores a mapping of elements to their corresponding buttons.
   */
  private final Map<T, Button> buttons = new HashMap<>();

  public ButtonsList(ReadOnlyObjectProperty<Bounds> viewportBounds,
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
   * Adds a button to the list, associated with a specific element. The button's text is bound to
   * the provided StringProperty.
   *
   * @param element the element associated with the button
   * @param text    the StringProperty that the button's text will be bound to
   */
  public void add(T element, StringProperty text, Runnable clickAction) {
    Button button = new Button();
    button.getStyleClass().add("primary-button");
    button.setFont(Font.font(18));
    button.textProperty().bind(text);
    button.setPadding(new Insets(10));
    button.setOnMouseClicked(event -> clickAction.run());
    button.maxWidthProperty().bind(widthProperty());
    button.minWidthProperty().bind(widthProperty());
    button.prefWidthProperty().bind(widthProperty());

    Button previous = buttons.put(element, button);
    if (previous != null) {
      getChildren().remove(button);
    }
    getChildren().add(button);
  }

  /**
   * Removes the button associated with the specified element from the list.
   *
   * @param element the element whose associated button should be removed
   */
  public void remove(T element) {
    Button button = buttons.remove(element);
    if (button != null) {
      getChildren().remove(button);
    }
  }
}
