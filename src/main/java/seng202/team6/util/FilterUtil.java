package seng202.team6.util;

import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;
import org.apache.logging.log4j.LogManager;
import org.controlsfx.control.RangeSlider;
import seng202.team6.gui.controls.AutoCompletionTextField;


/**
 * Class to handle range slider and autocomplete filters.
 */
public class FilterUtil {

  /**
   * Adds required tooltip logic through event handlers.
   *
   * @param thumb   the thumb node to attach the tool tip too
   * @param tooltip tool tip to attach
   */
  public static void addEventHandlersToThumb(Node thumb, Tooltip tooltip) {

    // Attach tooltip on click
    thumb.addEventHandler(MouseEvent.MOUSE_PRESSED, e -> {

          Point2D thumbLocation = thumb.localToScene(
              thumb.getBoundsInLocal().getMinX(), thumb.getBoundsInLocal().getMinY());

          // Using getWindow().getX() to adjust for window postion so tool tip is located correctly
          tooltip.show(thumb, thumbLocation.getX() + thumb.getScene().getWindow().getX(),
              thumbLocation.getY() + thumb.getScene().getWindow().getY() - 20);
        }
    );

    // Update tooltip as its dragged
    thumb.addEventHandler(MouseEvent.MOUSE_DRAGGED, e -> {

          Point2D thumbLocation = thumb.localToScene(
              thumb.getBoundsInLocal().getMinX(), thumb.getBoundsInLocal().getMinY());

          tooltip.setX(thumbLocation.getX() + thumb.getScene().getWindow().getX());
          tooltip.setY(thumbLocation.getY() + thumb.getScene().getWindow().getY() - 20);
        }
    );

    // Hide on mouse release
    thumb.addEventHandler(MouseEvent.MOUSE_RELEASED, e -> tooltip.hide());
  }


  /**
   * Adds a tool tips to each thumb of the range slider to indicate values.
   *
   * @param rangeSlider range slider to add tooltips too
   */
  public static void installRangeSliderTooltip(RangeSlider rangeSlider) {
    rangeSlider.applyCss();
    rangeSlider.getParent().applyCss();
    final Tooltip lowerToolTip = new Tooltip();
    final Tooltip upperToolTip = new Tooltip();

    // Ensures that tooltips display instantly
    lowerToolTip.setShowDelay(Duration.ZERO);
    lowerToolTip.setHideDelay(Duration.ZERO);
    lowerToolTip.setShowDuration(Duration.INDEFINITE);

    upperToolTip.setShowDelay(Duration.ZERO);
    upperToolTip.setHideDelay(Duration.ZERO);
    upperToolTip.setShowDuration(Duration.INDEFINITE);

    // Get thumbs
    Node lowerThumb = rangeSlider.lookup(".low-thumb");
    Node upperThumb = rangeSlider.lookup(".high-thumb");

    if (lowerThumb != null && upperThumb != null) {
      // add handlers for tooltip logic
      addEventHandlersToThumb(lowerThumb, lowerToolTip);
      addEventHandlersToThumb(upperThumb, upperToolTip);

      // Set initial values
      lowerToolTip.setText(String.format("%.0f", rangeSlider.getLowValue()));
      upperToolTip.setText(String.format("%.0f", rangeSlider.getHighValue()));

      // Add listeners
      rangeSlider.lowValueProperty().addListener((observable, oldValue, newValue) ->
          lowerToolTip.setText(String.format("%.0f", newValue.doubleValue())));
      rangeSlider.highValueProperty().addListener((observable, oldValue, newValue) ->
          upperToolTip.setText(String.format("%.0f", newValue.doubleValue())));

    } else {
      LogManager.getLogger().error(
          "Thumb nodes not found. Make sure the RangeSlider is added to the scene and rendered.");
    }

  }

  /**
   * Creates a range slider element and displays it on the filters pane at the given layout
   * coordinates.
   *
   * @param layoutX         The layout X position
   * @param layoutY         The layout Y position
   * @param min             The minimum value displayed on the slider
   * @param max             The maximum value displayed on the slider
   * @param blockIncrements The gap between tick marks
   */
  public static RangeSlider createSlider(int layoutX, int layoutY, int min, int max,
      int blockIncrements) {
    RangeSlider rangeSlider = new RangeSlider(min, max, min, max);
    rangeSlider.setLayoutX(layoutX);
    rangeSlider.setLayoutY(layoutY);
    rangeSlider.setPrefWidth(300);
    rangeSlider.setBlockIncrement(blockIncrements);
    rangeSlider.setShowTickMarks(true);
    rangeSlider.setShowTickLabels(true);
    rangeSlider.setSnapToPixel(true);
    // by default the font size matches the parent font size which is the filters title
    rangeSlider.setStyle("-fx-font-size: 15px;");
    rangeSlider.getStylesheets().add("css/range_slider.css");

    return rangeSlider;
  }

  /**
   * Creates an auto complete field at a location.
   *
   * @param layoutX x location
   * @param layoutY y location
   * @return auto complete field
   */
  public static AutoCompletionTextField createAutoCompleteTextField(double layoutX,
      double layoutY) {
    AutoCompletionTextField autoCompleteTextField = new AutoCompletionTextField();
    autoCompleteTextField.setLayoutX(layoutX);
    autoCompleteTextField.setLayoutY(layoutY);
    autoCompleteTextField.prefHeight(33.0);
    autoCompleteTextField.setPrefWidth(300);
    autoCompleteTextField.setStyle("-fx-font-size: 15px;");
    return autoCompleteTextField;
  }

}
