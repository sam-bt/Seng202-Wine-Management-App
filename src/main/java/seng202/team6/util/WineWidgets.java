package seng202.team6.util;

import java.util.HashMap;
import java.util.Map;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import seng202.team6.managers.DatabaseManager;
import seng202.team6.model.Wine;

/**
 * Reusable wine GUI components.
 */
public class WineWidgets {

  private static final Logger log = LogManager.getLogger(WineWidgets.class);
  private static WineImages wineImages = new WineImages();

  /**
   * Creates a card for a wine.
   *
   * @param wine wine
   */
  public static Pane createWineCard(Wine wine) {
    VBox wrapper = new VBox();
    wrapper.setPadding(new Insets(10));
    wrapper.setStyle("-fx-background-color: #f3f4f6; -fx-background-radius: 10px;");

    String varietySource = wine.getVariety();
    if(varietySource.length() == 0) {
      varietySource = wine.getColor();
      log.info("Using Colour field for wine colour");
    } else {
      log.info("Using Variety field for colour");
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

    HBox header = new HBox(imageView, wineTitle);
    header.setAlignment(Pos.CENTER_LEFT);
    header.setSpacing(20);
    wrapper.getChildren().add(header);
    return wrapper;
  }


}
