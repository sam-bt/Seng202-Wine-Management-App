package seng202.team6.gui.controls.card;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import seng202.team6.gui.controls.AddRemoveButton;

/**
 * Represents a card with an add/remove functionality, allowing the user to dynamically add or
 * remove an item.
 */
public class AddRemoveCard extends Card {

  /**
   * Constructs an AddRemoveCard with specified parameters.
   *
   * @param containerWidth      The width of the container that holds the card.
   * @param horizontalGap       The horizontal gap of the container.
   * @param content             The content to be displayed in the card.
   * @param centerContent       Indicates whether the content should be centered.
   * @param shouldAdd           Indicates whether the button should represent the add action.
   * @param addClickRunnable    The action to be performed when the button is clicked to add.
   * @param removeClickRunnable The action to be performed when the button is clicked to remove.
   * @param addTooltipText      The tooltip text displayed when the button is in add mode.
   * @param removeTooltipText   The tooltip text displayed when the button is in remove mode.
   */
  public AddRemoveCard(ReadOnlyDoubleProperty containerWidth, DoubleProperty horizontalGap,
      Node content, boolean centerContent, boolean shouldAdd,
      Runnable addClickRunnable, Runnable removeClickRunnable, String addTooltipText,
      String removeTooltipText) {
    super(containerWidth, horizontalGap);

    HBox contentWrapper = new HBox(content);
    contentWrapper.setAlignment(centerContent ? Pos.CENTER : Pos.CENTER_LEFT);
    HBox.setHgrow(contentWrapper, Priority.ALWAYS);
    HBox.setHgrow(content, Priority.ALWAYS);

    AddRemoveButton addRemoveButton = new AddRemoveButton(shouldAdd, addClickRunnable,
        removeClickRunnable, addTooltipText, removeTooltipText);
    setAlignment(Pos.CENTER);
    getChildren().addAll(contentWrapper, addRemoveButton);
  }
}
