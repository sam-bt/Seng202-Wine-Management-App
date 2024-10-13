package seng202.team6.gui.controls.cardcontent;

import javafx.beans.value.ChangeListener;
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

  private final ImageView logoView;

  /**
   * Constructs a VineyardCardContent with the specified vineyard and logo dimensions.
   *
   * @param vineyard   the Vineyard object containing the logo URL to display
   * @param logoWidth  the desired width for the logo image
   * @param logoHeight the desired height for the logo image
   */
  public VineyardCardContent(Vineyard vineyard, int logoWidth, int logoHeight) {
    logoView = new ImageView();
    logoView.setFitWidth(logoWidth);
    logoView.setFitHeight(logoHeight);
    logoView.setPreserveRatio(true);

    HBox wrapper = new HBox(logoView);
    wrapper.setMaxHeight(logoHeight);
    wrapper.setPrefHeight(logoHeight);
    wrapper.setMinHeight(logoHeight);
    wrapper.setAlignment(Pos.CENTER);
    getChildren().add(wrapper);

    // set up the listener for logo URL changes
    ChangeListener<String> logoUrlListener = (observable, oldValue, newValue) ->
        updateLogo(newValue);
    vineyard.logoUrlProperty().addListener(logoUrlListener);

    // initial update
    updateLogo(vineyard.getLogoUrl());
  }

  /**
   * Updates the logo image with the given URL.
   *
   * @param url the URL of the new logo image
   */
  private void updateLogo(String url) {
    Image logo = ImageReader.loadImageFromUrl(url);
    logoView.setImage(logo);
  }
}