package seng202.team6.gui.controls.cardcontent;

import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import seng202.team6.model.Vineyard;
import seng202.team6.util.ImageReader;

public class VineyardCardContent extends VBox {
  public VineyardCardContent(Vineyard vineyard, int logoWidth, int logoHeight) {
    Image logo = ImageReader.loadImageFromURL(vineyard.getLogoUrl());
    ImageView logoView = new ImageView(logo);
    HBox wrapper = new HBox(logoView);
    logoView.setFitWidth(logoWidth);
    logoView.setFitHeight(logoHeight);
    logoView.setPreserveRatio(true);
    wrapper.setAlignment(Pos.CENTER);
    wrapper.setMaxHeight(logoHeight);
    wrapper.setPrefHeight(logoHeight);
    wrapper.setMinHeight(logoHeight);
    getChildren().add(wrapper);
  }
}
