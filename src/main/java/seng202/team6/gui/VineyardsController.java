package seng202.team6.gui;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.TilePane;
import javafx.scene.web.WebView;
import seng202.team6.gui.controls.AutoCompletionTextField;
import seng202.team6.gui.controls.CardContainer;
import seng202.team6.managers.ManagerContext;
import seng202.team6.model.Vineyard;
import seng202.team6.service.VineyardService;
import seng202.team6.util.ImageReader;

public class VineyardsController extends Controller {
  @FXML
  AnchorPane filtersPane;
  @FXML
  TilePane vineyardsViewContainer;
  @FXML
  WebView webView;
  private AutoCompletionTextField nameTextField;
  private AutoCompletionTextField addressTextField;
  private AutoCompletionTextField regionTextField;
  private LeafletOSMController mapController;

  private final ObservableMap<Vineyard, CardContainer> vineyardCards = FXCollections.observableHashMap();
  private final VineyardService vineyardService;

  /**
   * Constructs the Vineyards Controller
   *
   * @param context The manager context
   */
  public VineyardsController(ManagerContext context) {
    super(context);
    this.vineyardService = new VineyardService(context.databaseManager);
    bindToVineyardService();
  }

  @Override
  public void init() {
    vineyardService.init();
    mapController = new LeafletOSMController(webView.getEngine());
    mapController.initMap();
    nameTextField = createAutoCompleteTextField(9, 45);
    addressTextField = createAutoCompleteTextField(9, 105);
    regionTextField = createAutoCompleteTextField(9, 165);
  }

  private void bindToVineyardService() {
    ObservableList<Vineyard> vineyards = vineyardService.get();
    vineyards.addListener((ListChangeListener<Vineyard>) change -> {
      while (change.next()) {
        if (change.wasAdded()) {
          change.getAddedSubList().forEach(vineyard -> {
            CardContainer card = createVineyardCard(vineyard);
            vineyardCards.put(vineyard, card);
            vineyardsViewContainer.getChildren().add(card);
          });
        }
        if (change.wasRemoved()) {
          change.getRemoved().forEach(vineyard -> {
            CardContainer card = vineyardCards.get(vineyard);
            vineyardsViewContainer.getChildren().remove(card);
            vineyardCards.remove(vineyard);
          });
        }
      }
    });
  }

  private CardContainer createVineyardCard(Vineyard vineyard) {
    CardContainer card = new CardContainer(vineyardsViewContainer.widthProperty(),
        vineyardsViewContainer.hgapProperty());

    int logoWidth = 200, logoHeight = 150;
    Image logo = ImageReader.loadImageFromURL(vineyard.getLogoUrl());
    ImageView logoView = new ImageView(logo);
    HBox imageContainer = new HBox(logoView);
    logoView.setFitWidth(logoWidth);
    logoView.setFitHeight(logoHeight);
    logoView.setPreserveRatio(true);
    imageContainer.setAlignment(Pos.CENTER);
    imageContainer.setMaxHeight(logoHeight);
    imageContainer.setPrefHeight(logoHeight);
    imageContainer.setMinHeight(logoHeight);
    HBox.setHgrow(imageContainer, Priority.ALWAYS);

    Label vineyardName = new Label();
    vineyardName.textProperty().bind(vineyard.nameProperty());
    vineyardName.setStyle("-fx-font-size: 16px;");
    vineyardName.setWrapText(true);

    card.getChildren().add(imageContainer);
    return card;
  }

  private AutoCompletionTextField createAutoCompleteTextField(double layoutX, double layoutY) {
    AutoCompletionTextField autoCompleteTextField = new AutoCompletionTextField();
    autoCompleteTextField.setLayoutX(layoutX);
    autoCompleteTextField.setLayoutY(layoutY);
    autoCompleteTextField.prefHeight(33.0);
    autoCompleteTextField.setPrefWidth(300);
    autoCompleteTextField.setStyle("-fx-font-size: 15px;");
    filtersPane.getChildren().add(autoCompleteTextField);
    return autoCompleteTextField;
  }
}
