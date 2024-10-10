package seng202.team6.gui.controls;

import java.util.HashMap;
import java.util.Map;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.controlsfx.control.Rating;
import seng202.team6.gui.controls.card.Card;
import seng202.team6.model.Wine;
import seng202.team6.model.WineReview;
import seng202.team6.util.ImageReader;

/**
 * A class that represents a card for displaying wine information.
 */
public class ReviewCard extends Card {

  private static final Image RED_WINE_IMAGE = ImageReader.loadImage("/img/red_wine_cropped.png");
  private static final Image WHITE_WINE_IMAGE = ImageReader.loadImage(
      "/img/white_wine_cropped.png");
  private static final Image ROSE_WINE_IMAGE = ImageReader.loadImage("/img/rose_wine_cropped.png");
  private static final Image DEFAULT_WINE_IMAGE = ImageReader.loadImage(
      "/img/default_wine_cropped.png");
  private static final Map<String, Image> WINE_IMAGES = new HashMap<>();

  static {
    WINE_IMAGES.put("red", RED_WINE_IMAGE);
    WINE_IMAGES.put("white", WHITE_WINE_IMAGE);
    WINE_IMAGES.put("rose", ROSE_WINE_IMAGE);
    WINE_IMAGES.put("rosé", ROSE_WINE_IMAGE);
  }

  /**
   * Constructs a ReviewCar to display the specified review's details.
   *
   * @param containerWidth the width of the container to which this card belongs
   * @param horizontalGap  the horizontal gap of the container.
   * @param review           the Review object containing details to display
   * @param wine the Wine object containing details to display
   */
  public ReviewCard(ReadOnlyDoubleProperty containerWidth,
      DoubleProperty horizontalGap, WineReview review, Wine wine) {
    super(containerWidth, horizontalGap);
    Image wineImage = WINE_IMAGES.getOrDefault(wine.getColor().toLowerCase(), DEFAULT_WINE_IMAGE);
    ImageView imageView = new ImageView(wineImage);
    imageView.setFitHeight(100);
    imageView.setPreserveRatio(true);
    HBox.setHgrow(imageView, Priority.NEVER);

    Label userName = new Label();
    userName.setText("By: " + review.getUsername());
    userName.setStyle("-fx-font-size: 16px;");
    userName.setWrapText(true);

    Label wineTitle = new Label();
    wineTitle.textProperty().bind(Bindings.createStringBinding(
        () -> "For: " + wine.getTitle(),
        wine.titleProperty()
    ));
    wineTitle.setStyle("-fx-font-size: 12px;");
    wineTitle.setWrapText(true);

    VBox infoBox = new VBox();
    infoBox.setSpacing(5);
    infoBox.setAlignment(Pos.CENTER_LEFT);

    infoBox.getChildren().add(userName);

    infoBox.getChildren().add(wineTitle);

    Rating rating = new UnmodifiableRating();
    rating.ratingProperty().bind(review.ratingProperty());
    rating.getStyleClass().add("small-rating");
    infoBox.getChildren().add(rating);

    HBox header = new HBox(imageView, infoBox);
    header.setAlignment(Pos.CENTER_LEFT);
    header.setSpacing(20);
    getChildren().add(header);
  }
}
