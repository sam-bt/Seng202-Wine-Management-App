package seng202.team6.gui.controls.cardcontent;

import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import seng202.team6.model.Vineyard;
import seng202.team6.util.ImageReader;

/**
 * A class that represents the content of a vineyard card, displaying a vineyard's logo.
 */
public class VineyardCardContent extends VBox {

  /**
   * Constructs a VineyardCardContent with the specified vineyard and logo dimensions.
   *
   * @param vineyard   the Vineyard object containing the logo URL to display
   * @param logoWidth  the desired width for the logo image
   * @param logoHeight the desired height for the logo image
   */
  public VineyardCardContent(Vineyard vineyard, int logoWidth, int logoHeight) {
    Image logo = ImageReader.loadImageFromUrl(vineyard.getLogoUrl());
    ImageView logoView = new ImageView(logo);
    logoView.setFitWidth(logoWidth);
    logoView.setFitHeight(logoHeight);
    logoView.setPreserveRatio(true);

    HBox wrapper = new HBox(logoView);
    wrapper.setAlignment(Pos.CENTER);
    wrapper.setMaxHeight(logoHeight);
    wrapper.setPrefHeight(logoHeight);
    wrapper.setMinHeight(logoHeight);
    getChildren().add(wrapper);
  }
}
