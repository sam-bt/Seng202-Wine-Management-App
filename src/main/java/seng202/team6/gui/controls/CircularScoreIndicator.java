package seng202.team6.gui.controls;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Pos;
import javafx.scene.control.Control;
import javafx.scene.control.SkinBase;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

/**
 * A custom JavaFX control which displays a circular score indicator.
 */
public class CircularScoreIndicator extends Control {

  /**
   * The score property representing the current score value.
   */
  private final DoubleProperty score = new SimpleDoubleProperty(0);

  /**
   * Constructs a new CircularScoreIndicator with a default skin which is used for styling the
   * control.
   */
  public CircularScoreIndicator() {
    setSkin(new CircularWineScoreIndicatorSkin(this));
  }

  /**
   * Gets the current score.
   *
   * @return The current score
   */
  public double getScore() {
    return score.get();
  }

  /**
   * Sets the score value. The score is clamped between 0 and 100.
   *
   * @param score The new score value to set
   */
  public void setScore(double score) {
    this.score.set(Math.min(100, Math.max(0, score)));
  }

  /**
   * Gets the score property.
   *
   * @return The DoubleProperty representing the score
   */
  public DoubleProperty scoreProperty() {
    return score;
  }

  /**
   * The skin for the CircularScoreIndicator which is responsible for the UI elements of the score
   * indicator.
   */
  private static class CircularWineScoreIndicatorSkin extends SkinBase<CircularScoreIndicator> {

    /**
     * The main pane which contains the elements which make up the circular wine score indicator.
     */
    private final Pane pane;

    /**
     * A circle which is used as the background of the score indicator. The circle has a transparent
     * background with a light gray stroke.
     */
    private final Arc backgroundArc;

    /**
     * An arc which is used to display a coloured arc on top of the circle background.
     */
    private final Arc arc;

    /**
     * A vertical box which wraps the score text and the max score text.
     */
    private final VBox scoreTextWrapper;

    /**
     * Text which shows the score.
     */
    private final Text scoreText;

    /**
     * Constructs a new CircularWineScoreIndicatorSkin for the specified CircularScoreIndicator and
     * set up the styling for the control.
     *
     * @param control The CircularScoreIndicator control this skin is associated with
     */
    public CircularWineScoreIndicatorSkin(CircularScoreIndicator control) {
      super(control);

      // pane is the wrapper for the circle, arc and text
      pane = new Pane();

      // make the circle transparent but give it a light gray stoke, so it appears
      // as a circle outline
      backgroundArc = new Arc();
      backgroundArc.setFill(Color.TRANSPARENT);
      backgroundArc.setStroke(Color.LIGHTGRAY);
      backgroundArc.setLength(360);

      // create the arc which will be used to display the score on top of the circle with a stroke
      arc = new Arc();
      arc.setFill(Color.TRANSPARENT);
      arc.setStroke(Color.GREEN);
      arc.setType(ArcType.OPEN);

      scoreText = new Text();
      scoreText.setFill(Color.BLACK);
      scoreText.setFont(Font.font(28));

      Text scoreMax = new Text();
      scoreMax.setText("/100");
      scoreMax.setFont(Font.font(18));
      scoreTextWrapper = new VBox(scoreText, scoreMax);
      scoreTextWrapper.setAlignment(Pos.CENTER);

      pane.getChildren().addAll(backgroundArc, arc, scoreTextWrapper);
      getChildren().add(pane);

      control.scoreProperty()
          .addListener((obs, oldVal, newVal) -> updateScore(newVal.doubleValue()));
      control.widthProperty().addListener((obs, oldVal, newVal) -> resize());
      control.heightProperty().addListener((obs, oldVal, newVal) -> resize());

      updateScore(control.getScore());
    }

    /**
     * Resizes the controls elements based on the current size of the control.
     */
    private void resize() {
      // maintain aspect ratio of 1
      double size = Math.min(getSkinnable().getWidth(), getSkinnable().getHeight());
      pane.setPrefSize(size, size);
      scoreTextWrapper.setPrefSize(size, size);

      double centerX = size / 2;
      double centerY = size / 2;
      double radius = size / 2 - 5; // 5 pixels padding

      setArcPositionAndSize(arc, centerX, centerY, radius);
      setArcPositionAndSize(arc, centerX, centerY, radius);
      backgroundArc.setCenterX(centerX);
      backgroundArc.setCenterY(centerY);
      backgroundArc.setRadiusX(radius);
      backgroundArc.setRadiusY(radius);
      backgroundArc.setStrokeWidth(radius * 0.1);

      arc.setStartAngle(90); // starts the arc at the top
      arc.setCenterX(centerX);
      arc.setCenterY(centerY);
      arc.setRadiusX(radius);
      arc.setRadiusY(radius);
      arc.setStrokeWidth(radius * 0.1);

      updateScore(getSkinnable().getScore());
    }

    private void setArcPositionAndSize(Arc arc, double centerX, double centerY, double radius) {
      arc.setCenterX(centerX);
      arc.setCenterY(centerY);
      arc.setRadiusX(radius);
      arc.setRadiusY(radius);
      arc.setStrokeWidth(radius * 0.1);
    }

    /**
     * Updates the UI of the score when the score is changed. The text, arc length, and arc colour
     * is adjusted depending on the provided score.
     *
     * @param score The new score value to display
     */
    private void updateScore(double score) {
      double angle = score * 3.6; // convert percentage to degrees (100% = 360 degrees)
      scoreText.setText(String.format("%.0f", score)); // don't show decimal place of score
      arc.setLength(-angle);

      // change the colour of the arc depending on the score of the wine
      if (score >= 90) {
        arc.setStroke(Color.GREEN);
      } else if (score >= 70) {
        arc.setStroke(Color.YELLOWGREEN);
      } else if (score >= 50) {
        arc.setStroke(Color.ORANGE);
      } else {
        arc.setStroke(Color.RED);
      }
    }
  }
}