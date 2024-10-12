package seng202.team6.controls;

import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;
import org.apache.logging.log4j.LogManager;
import org.controlsfx.control.RangeSlider;

public class RangeSliderToolTips extends RangeSlider {

  private final Tooltip lowerToolTip;
  private final Tooltip upperToolTip;

  public RangeSliderToolTips(double min, double max, double lowValue, double highValue) {
    super(min, max, lowValue, highValue);
    this.lowerToolTip = new Tooltip();
    this.upperToolTip = new Tooltip();
    setupSlider();
  }

  private void setupSlider() {
    setPrefWidth(300);
    setBlockIncrement(1);
    setShowTickMarks(true);
    setShowTickLabels(true);
    setSnapToPixel(true);
    setStyle("-fx-font-size: 15px;");
    getStylesheets().add("css/range_slider.css");

    setupTooltips();
    installTooltips();
  }

  private void setupTooltips() {
    lowerToolTip.setShowDelay(Duration.ZERO);
    lowerToolTip.setHideDelay(Duration.ZERO);
    lowerToolTip.setShowDuration(Duration.INDEFINITE);

    upperToolTip.setShowDelay(Duration.ZERO);
    upperToolTip.setHideDelay(Duration.ZERO);
    upperToolTip.setShowDuration(Duration.INDEFINITE);

    lowValueProperty().addListener((observable, oldValue, newValue) ->
        lowerToolTip.setText(String.format("%.0f", newValue.doubleValue())));
    highValueProperty().addListener((observable, oldValue, newValue) ->
        upperToolTip.setText(String.format("%.0f", newValue.doubleValue())));
  }

  private void installTooltips() {
    applyCss();
    getParent().applyCss();

    Node lowerThumb = lookup(".low-thumb");
    Node upperThumb = lookup(".high-thumb");

    if (lowerThumb != null && upperThumb != null) {
      addEventHandlersToThumb(lowerThumb, lowerToolTip);
      addEventHandlersToThumb(upperThumb, upperToolTip);

      lowerToolTip.setText(String.format("%.0f", getLowValue()));
      upperToolTip.setText(String.format("%.0f", getHighValue()));
    } else {
      LogManager.getLogger().error(
          "Thumb nodes not found. Make sure the RangeSlider is added to the scene and rendered.");
    }
  }

  private void addEventHandlersToThumb(Node thumb, Tooltip tooltip) {
    thumb.addEventHandler(MouseEvent.MOUSE_PRESSED, e -> {
      Point2D thumbLocation = thumb.localToScene(
          thumb.getBoundsInLocal().getMinX(), thumb.getBoundsInLocal().getMinY());
      tooltip.show(thumb, thumbLocation.getX() + getScene().getWindow().getX(),
          thumbLocation.getY() + getScene().getWindow().getY() - 20);
    });

    thumb.addEventHandler(MouseEvent.MOUSE_DRAGGED, e -> {
      Point2D thumbLocation = thumb.localToScene(
          thumb.getBoundsInLocal().getMinX(), thumb.getBoundsInLocal().getMinY());
      tooltip.setX(thumbLocation.getX() + getScene().getWindow().getX());
      tooltip.setY(thumbLocation.getY() + getScene().getWindow().getY() - 20);
    });

    thumb.addEventHandler(MouseEvent.MOUSE_RELEASED, e -> tooltip.hide());
  }
}