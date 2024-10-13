package seng202.team6.gui;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import seng202.team6.gui.controls.WineCard;
import seng202.team6.managers.ManagerContext;
import seng202.team6.model.Vineyard;
import seng202.team6.model.Wine;
import seng202.team6.util.ImageReader;

/**
 * The DetailedVineyardViewController is responsible for managing the detailed vineyard view within
 * the GUI.
 */
public class DetailedVineyardViewController extends Controller {

  private final Vineyard vineyard;
  private final Runnable backButtonAction;
  @FXML
  TitledPane viewingVineyardTitledPane;
  @FXML
  ImageView imageView;
  @FXML
  VBox winesContainer;
  @FXML
  TextField addressTextbox;
  @FXML
  TextField websiteTextbox;
  @FXML
  TextArea descriptionTextbox;
  @FXML
  Button openToursButton;
  @FXML
  HBox buttonsContainer;

  /**
   * Constructs the Detailed Vineyard View Controller.
   *
   * @param context The manager context
   */
  public DetailedVineyardViewController(ManagerContext context, Vineyard vineyard,
      Runnable backButtonAction) {
    super(context);
    this.vineyard = vineyard;
    this.backButtonAction = backButtonAction;
  }

  @Override
  public void init() {
    viewingVineyardTitledPane.setText("Viewing Vineyard: " + vineyard.getName());
    addressTextbox.textProperty().bind(vineyard.addressProperty());
    websiteTextbox.textProperty().bind(vineyard.websiteProperty());
    descriptionTextbox.textProperty().bind(vineyard.descriptionProperty());

    if (!managerContext.getAuthenticationManager().isAuthenticated()) {
      buttonsContainer.getChildren().remove(openToursButton);
    }

    Image image = ImageReader.loadImageFromUrl(vineyard.getLogoUrl());
    imageView.setImage(image);
    imageView.setPreserveRatio(true);

    ObservableList<Wine> wines = managerContext.getDatabaseManager().getAggregatedDao()
        .getWinesFromVineyard(vineyard);
    wines.forEach(this::createWineCard);
  }

  /**
   * Handles when the "Back" button is clicked. It runs the provided back button runnable.
   */
  @FXML
  void onBackButtonClick() {
    backButtonAction.run();
  }

  @FXML
  void onOpenToursClick() {
    managerContext.getGuiManager().openAddToTourPopup(vineyard);
  }

  /**
   * Creates a wine card and adds it to the wine cards container.
   *
   * @param wine The wine to add to the card.
   */
  public void createWineCard(Wine wine) {
    WineCard card = new WineCard(winesContainer.widthProperty(),
        new SimpleDoubleProperty(), wine, true);
    card.setOnMouseClicked(event -> {
      if (event.getClickCount() == 2) {
        openDetailedWineView(wine);
      }
    });
    winesContainer.getChildren().add(card);
  }

  private void openDetailedWineView(Wine wine) {
    Runnable backAction = () -> managerContext.getGuiManager().openVineyardsScreen();
    managerContext.getGuiManager().openDetailedWineView(wine, backAction);
  }
}
