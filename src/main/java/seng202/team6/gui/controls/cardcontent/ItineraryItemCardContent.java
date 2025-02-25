package seng202.team6.gui.controls.cardcontent;

import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import seng202.team6.model.Vineyard;

/**
 * A class that represents the content of an itinerary item card, displaying information about a
 * vineyard, including its name and address.
 */
public class ItineraryItemCardContent extends VBox {

  /**
   * Constructs an ItineraryItemCardContent with the specified vineyard.
   *
   * @param vineyard the Vineyard object containing the information to display on the itinerary item
   *                 card.
   */
  public ItineraryItemCardContent(Vineyard vineyard) {
    Label name = new Label();
    name.textProperty().bind(vineyard.nameProperty());
    name.setWrapText(true);
    name.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

    Label address = new Label();
    address.textProperty().bind(vineyard.addressProperty());
    address.setWrapText(true);

    getChildren().addAll(name, address);
    setSpacing(10);
  }
}
