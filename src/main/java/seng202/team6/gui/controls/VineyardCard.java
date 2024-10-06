package seng202.team6.gui.controls;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import seng202.team6.model.Vineyard;
import seng202.team6.util.ImageReader;

public class VineyardCard extends CardContainer {
    private final Vineyard vineyard;
    private final HBox hbox;

    public VineyardCard(ReadOnlyDoubleProperty containerWidth, DoubleProperty hGap, Vineyard vineyard, int logoWidth, int logoHeight) {
        super(containerWidth, hGap);
        this.vineyard = vineyard;

        Image logo = ImageReader.loadImageFromURL(vineyard.getLogoUrl());
        ImageView logoView = new ImageView(logo);
        this.hbox = new HBox(logoView);
        logoView.setFitWidth(logoWidth);
        logoView.setFitHeight(logoHeight);
        logoView.setPreserveRatio(true);
        hbox.setAlignment(Pos.CENTER);
        hbox.setMaxHeight(logoHeight);
        hbox.setPrefHeight(logoHeight);
        hbox.setMinHeight(logoHeight);
        HBox.setHgrow(hbox, Priority.ALWAYS);

        Label vineyardName = new Label();
        vineyardName.textProperty().bind(vineyard.nameProperty());
        vineyardName.setStyle("-fx-font-size: 16px;");
        vineyardName.setWrapText(true);

        getChildren().add(hbox);
    }

    public HBox getHbox() {
        return hbox;
    }
}
