package seng202.team6.gui.controls;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.GridPane;
import javafx.scene.shape.SVGPath;
import seng202.team6.util.IconPaths;

/**
 * A custom button which toggles between an "add" and "remove" state. When the button is in an add
 * state, it will show a plus icon. When the button is in a remove state, it will show a minus icon.
 */
public class AddRemoveButton extends GridPane {

  private final Button button;
  private final Runnable addClickRunnable;
  private final Runnable removeClickRunnable;
  private final String addTooltipText;
  private final String removeTooltipText;

  /**
   * Constructs an AddRemoveButton with specified actions and tooltips.
   *
   * @param shouldAdd Indicates whether the button should represent the add action.
   * @param addClickRunnable The action to be performed when the button is clicked to add.
   * @param removeClickRunnable The action to be performed when the button is clicked to remove.
   * @param addTooltipText The tooltip text displayed when the button is in add mode.
   * @param removeTooltipText The tooltip text displayed when the button is in remove mode.
   */
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

  /**
   * Updates the icon of the button based on the current action (add or remove).
   *
   * @param shouldAdd Indicates whether to display the add icon (true) or the remove icon (false).
   */
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
