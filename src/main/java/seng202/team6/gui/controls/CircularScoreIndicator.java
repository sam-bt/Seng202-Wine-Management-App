package seng202.team6.gui.controls;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Pos;
import javafx.scene.control.Control;
import javafx.scene.control.SkinBase;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class CircularScoreIndicator extends Control {
  private final DoubleProperty score = new SimpleDoubleProperty(0);

  public CircularScoreIndicator() {
    setSkin(new CircularWineScoreIndicatorSkin(this));
  }

  public double getScore() {
    return score.get();
  }

  public void setScore(double score) {
    this.score.set(Math.min(100, Math.max(0, score)));
  }

  public DoubleProperty scoreProperty() {
    return score;
  }

  private static class CircularWineScoreIndicatorSkin extends SkinBase<CircularScoreIndicator> {
    private final Pane pane;
    private final Circle background;
    private final VBox scoreTextWrapper;
    private final Text scoreText;

    public CircularWineScoreIndicatorSkin(CircularScoreIndicator control) {
      super(control);

      // pane is the wrapper for the circle, arc and text
      pane = new Pane();

      // make the circle transparent but give it a light gray stoke, so it appears
      // as a circle outline
      background = new Circle();
      background.setFill(Color.TRANSPARENT);
      background.setStroke(Color.LIGHTGRAY);

      scoreText = new Text();
      scoreText.setFill(Color.BLACK);
      scoreText.setFont(Font.font(28));

      Text scoreMax = new Text();
      scoreMax.setText("/100");
      scoreMax.setFont(Font.font(18));
      scoreTextWrapper = new VBox(scoreText, scoreMax);
      scoreTextWrapper.setAlignment(Pos.CENTER);

      pane.getChildren().addAll(background, scoreTextWrapper);
      getChildren().add(pane);

      control.scoreProperty().addListener((obs, oldVal, newVal) -> updateScore(newVal.doubleValue()));
      control.widthProperty().addListener((obs, oldVal, newVal) -> resize());
      control.heightProperty().addListener((obs, oldVal, newVal) -> resize());

      updateScore(control.getScore());
    }

    private void resize() {
      // maintain aspect ratio of 1
      double size = Math.min(getSkinnable().getWidth(), getSkinnable().getHeight());
      pane.setPrefSize(size, size);

      scoreTextWrapper.setPrefSize(size, size);

      double centerX = size / 2;
      double centerY = size / 2;
      double radius = size / 2 - 5; // 5 pixels padding

      background.setCenterX(centerX);
      background.setCenterY(centerY);
      background.setRadius(radius);
      background.setStrokeWidth(radius * 0.1);

      updateScore(getSkinnable().getScore());
    }

    private void updateScore(double score) {
      scoreText.setText(String.format("%.0f", score)); // don't show decimal place of score

      // calculate the center (x, y coordinate) of the circle which is half the diameter
//      double size = Math.min(getSkinnable().getWidth(), getSkinnable().getHeight());
//      double centerX = size / 2;
//      double centerY = size / 2;
//      scoreTextWrapper.setLayoutX(centerX - scoreText.getBoundsInLocal().getWidth() / 2);
//      scoreTextWrapper.setLayoutY(centerY + scoreText.getBoundsInLocal().getHeight() / 4);
    }
  }
}