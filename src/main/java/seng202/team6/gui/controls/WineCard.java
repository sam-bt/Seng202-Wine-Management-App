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
import org.controlsfx.control.Rating;
import seng202.team6.gui.controls.card.Card;
import seng202.team6.model.Wine;
import seng202.team6.util.ImageReader;

public class WineCard extends Card {
  private static final Image RED_WINE_IMAGE = ImageReader.loadImage("/img/red_wine_cropped.png");
  private static final Image WHITE_WINE_IMAGE = ImageReader.loadImage(
      "/img/white_wine_cropped.png");
  private static final Image ROSE_WINE_IMAGE = ImageReader.loadImage("/img/rose_wine_cropped.png");
  private static final Image DEFAULT_WINE_IMAGE = ImageReader.loadImage(
      "/img/default_wine_cropped.png");
  private static final Map<String, Image> WINE_IMAGES = new HashMap<>() {{
    put("red", RED_WINE_IMAGE);
    put("white", WHITE_WINE_IMAGE);
    put("rose", ROSE_WINE_IMAGE);
    put("rosé", ROSE_WINE_IMAGE);
  }};

  public WineCard(ReadOnlyDoubleProperty containerWidth,
      DoubleProperty hGap, Wine wine, boolean showReview) {
    super(containerWidth, hGap);
    Image wineImage = WINE_IMAGES.getOrDefault(wine.getColor().toLowerCase(), DEFAULT_WINE_IMAGE);
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
      rating.setRating(0.0);
      rating.getStyleClass().add("small-rating");
      titleReview.getChildren().add(rating);
    }

    HBox header = new HBox(imageView, titleReview);
    header.setAlignment(Pos.CENTER_LEFT);
    header.setSpacing(20);
    getChildren().add(header);
  }
}
