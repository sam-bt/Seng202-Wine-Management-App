package seng202.team6.gui.controls;

import java.util.HashMap;
import java.util.Map;
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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.controlsfx.control.Rating;
import seng202.team6.gui.controls.card.Card;
import seng202.team6.managers.DatabaseManager;
import seng202.team6.model.Wine;
import seng202.team6.util.ImageReader;
import seng202.team6.util.WineImages;

/**
 * A class that represents a card for displaying wine information.
 */
public class WineCard extends Card {

  private WineImages wineImages = new WineImages();

  /**
   * Constructs a WineCard to display the specified wine's details.
   *
   * @param containerWidth the width of the container to which this card belongs
   * @param horizontalGap  the horizontal gap of the container.
   * @param wine           the Wine object containing details to display
   * @param showReview     a boolean indicating whether to show the review rating
   */
  public WineCard(ReadOnlyDoubleProperty containerWidth,
      DoubleProperty horizontalGap, Wine wine, boolean showReview) {
    super(containerWidth, horizontalGap);

    String varietySource = wine.getVariety();
    if (varietySource.length() == 0) {
      varietySource = wine.getColor();
    }
    Image wineImage = wineImages.getImageByVariety(varietySource);
    ImageView imageView = new ImageView(wineImage);
    imageView.setFitHeight(100);
    imageView.setPreserveRatio(true);
    HBox.setHgrow(imageView, Priority.NEVER);

    Label wineTitle = new Label();
    wineTitle.textProperty().bind(wine.titleProperty());
    wineTitle.setStyle("-fx-font-size: 16px;");
    wineTitle.setWrapText(true);

    VBox titleReview = new VBox(wineTitle);
    titleReview.setOpaqueInsets(new Insets(0, 10, 0, 10));
    titleReview.setSpacing(10);
    titleReview.setAlignment(Pos.CENTER_LEFT);

    if (showReview) {
      Rating rating = new UnmodifiableRating();
      rating.ratingProperty().bind(wine.averageRatingProperty());
      rating.getStyleClass().add("small-rating");
      titleReview.getChildren().add(rating);
    }

    HBox header = new HBox(imageView, titleReview);
    header.setAlignment(Pos.CENTER_LEFT);
    header.setSpacing(20);
    getChildren().add(header);
  }
}
