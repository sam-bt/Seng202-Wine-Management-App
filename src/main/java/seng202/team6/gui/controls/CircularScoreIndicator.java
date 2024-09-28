package seng202.team6.gui.controls;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.control.Control;
import javafx.scene.control.SkinBase;
import javafx.scene.layout.Pane;
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
      scoreText.setFont(Font.font(16));

      pane.getChildren().addAll(background, scoreText);
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
      scoreText.setText(String.format("%.1f", score)); // only to 1 decimal place

      // calculate the center (x, y coordinate) of the circle which is half the diameter
      double size = Math.min(getSkinnable().getWidth(), getSkinnable().getHeight());
      double centerX = size / 2;
      double centerY = size / 2;
      scoreText.setX(centerX - scoreText.getBoundsInLocal().getWidth() / 2);
      scoreText.setY(centerY + scoreText.getBoundsInLocal().getHeight() / 4);
    }
  }
}