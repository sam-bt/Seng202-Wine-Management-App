package seng202.team6.gui.controls.card;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import seng202.team6.gui.controls.AddRemoveButton;

public class AddRemoveCard extends Card {
  public AddRemoveCard(ReadOnlyDoubleProperty containerWidth,  DoubleProperty hGap, Node content,
      boolean centerContent, boolean shouldAdd, boolean white, Runnable addClickRunnable, Runnable removeClickRunnable, String addTooltipText, String removeTooltipText) {
    super(containerWidth, hGap);

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
