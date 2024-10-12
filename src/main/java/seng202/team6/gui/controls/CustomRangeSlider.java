package seng202.team6.gui.controls;

import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;
import javafx.util.StringConverter;
import org.apache.logging.log4j.LogManager;
import org.controlsfx.control.RangeSlider;

public class CustomRangeSlider extends RangeSlider {
  private final Tooltip lowerToolTip;
  private final Tooltip upperToolTip;

  public CustomRangeSlider(int layoutX, int layoutY) {
    this.lowerToolTip = new Tooltip();
    this.upperToolTip = new Tooltip();
    setLayoutX(layoutX);
    setLayoutY(layoutY);
    setupSlider();
  }

  public void configure(double min, double max, double majorTickUnit, int minorTickCount,
      StringConverter<Number> labelFormatter) {
    if (min >= max) {
      setMin(0);
      setMax(1);
      setShowTickLabels(false);
      setDisable(true);
    } else {
      setMin(min);
      setMax(max);
      setMajorTickUnit(majorTickUnit);
      setMinorTickCount(minorTickCount);
      if (labelFormatter != null)
        setLabelFormatter(labelFormatter);
    }
    resetThumbs();
  }

  public void resetThumbs() {
    setLowValue(getMin());
    setHighValue(getMax());
  }

  public void setHighLow(double low, double high) {
    setLowValue(low);
    setHighValue(high);
  }

  private void setupSlider() {
    setPrefWidth(300);
    setShowTickMarks(true);
    setShowTickLabels(true);
    setSnapToPixel(true);
    setStyle("-fx-font-size: 15px;");
    getStylesheets().add("css/range_slider.css");

    setupTooltips();
    sceneProperty().addListener((observableValue, scene, t1) -> {
      installTooltips();
    });
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