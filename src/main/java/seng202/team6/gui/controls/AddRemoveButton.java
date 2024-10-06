package seng202.team6.gui.controls;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.GridPane;
import javafx.scene.shape.SVGPath;
import seng202.team6.util.IconPaths;

public class AddRemoveButton extends GridPane {

  private final Button button;
  private final Runnable addClickRunnable;
  private final Runnable removeClickRunnable;
  private final String addTooltipText;
  private final String removeTooltipText;

  public AddRemoveButton(boolean shouldAdd, Runnable addClickRunnable, Runnable removeClickRunnable,
      String addTooltipText, String removeTooltipText) {
    this.addTooltipText = addTooltipText;
    this.removeTooltipText = removeTooltipText;
    this.button = new Button();
    this.addClickRunnable = addClickRunnable;
    this.removeClickRunnable = removeClickRunnable;
    updateActionIcon(shouldAdd);
    button.setPrefSize(28, 32);
    button.setMaxSize(28, 32);
    button.setMinSize(28, 32);
    button.getStylesheets().add("css/add_remove_buttons.css");
    setPrefSize(32, 32);
    setMinSize(32, 32);
    setMaxSize(32, 32);
    add(button, 1, 0);
    setOpaqueInsets(new Insets(0, 10, 0, 10));
    GridPane.setHalignment(button, HPos.CENTER);
  }

  private void updateActionIcon(boolean shouldAdd) {
    SVGPath svgPath = new SVGPath();
    svgPath.getStyleClass().add("icon");
    svgPath.setContent(shouldAdd ? IconPaths.ADD_PATH : IconPaths.REMOVE_PATH);
    svgPath.setScaleX(0.05);
    svgPath.setScaleY(0.05);
    button.setGraphic(svgPath);
    button.setOnMouseClicked((event) -> {
      if (shouldAdd) {
        addClickRunnable.run();
      } else {
        removeClickRunnable.run();
      }
      updateActionIcon(!shouldAdd);
    });
    button.setTooltip(new Tooltip(shouldAdd ? addTooltipText : removeTooltipText));
  }
}
