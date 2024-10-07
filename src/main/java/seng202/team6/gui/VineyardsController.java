package seng202.team6.gui;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.TilePane;
import javafx.scene.web.WebView;
import seng202.team6.gui.controls.AutoCompletionTextField;
import seng202.team6.gui.controls.card.Card;
import seng202.team6.gui.controls.cardcontent.VineyardCardContent;
import seng202.team6.managers.ManagerContext;
import seng202.team6.model.Vineyard;
import seng202.team6.service.VineyardService;

/**
 * Controller for the screen that displays vineyards.
 */
public class VineyardsController extends Controller {

  private final ObservableMap<Vineyard, Card> vineyardCards = FXCollections.observableHashMap();
  private final VineyardService vineyardService;
  @FXML
  AnchorPane filtersPane;
  @FXML
  TilePane vineyardsViewContainer;
  @FXML
  WebView webView;
  private AutoCompletionTextField nameTextField;
  private AutoCompletionTextField addressTextField;
  private AutoCompletionTextField regionTextField;
  private LeafletOsmController mapController;

  /**
   * Constructs the Vineyards Controller.
   *
   * @param context The manager context
   */
  public VineyardsController(ManagerContext context) {
    super(context);
    this.vineyardService = new VineyardService(context.getDatabaseManager());
    bindToVineyardService();
  }

  @Override
  public void init() {
    mapController = new LeafletOsmController(webView.getEngine());
    mapController.initMap();
    vineyardService.init();
    nameTextField = createAutoCompleteTextField(9, 45);
    addressTextField = createAutoCompleteTextField(9, 105);
    regionTextField = createAutoCompleteTextField(9, 165);
    vineyardService.addDistinctValues(nameTextField.getEntries(), addressTextField.getEntries(),
        regionTextField.getEntries());
  }

  private void bindToVineyardService() {
    ObservableList<Vineyard> vineyards = vineyardService.get();
    vineyards.addListener((ListChangeListener<Vineyard>) change -> {
      while (change.next()) {
        if (change.wasAdded()) {
          change.getAddedSubList().forEach(vineyard -> {
            Card card = createVineyardCard(vineyard);
            vineyardCards.put(vineyard, card);
            vineyardsViewContainer.getChildren().add(card);
            mapController.runOrQueueWhenReady(() ->
                mapController.addVineyardMaker(vineyard, false));
          });
        }
        if (change.wasRemoved()) {
          change.getRemoved().forEach(vineyard -> {
            Card card = vineyardCards.get(vineyard);
            vineyardsViewContainer.getChildren().remove(card);
            vineyardCards.remove(vineyard);
          });
        }
      }
    });
  }

  /**
   * Handles when the plan a tour button is clicked.
   */
  @FXML
  public void onPlanTourClick() {
    managerContext.getGuiManager().mainController.openTourPlanningScreen();
  }

  private Card createVineyardCard(Vineyard vineyard) {
    Card card = new Card(vineyardsViewContainer.widthProperty(),
        vineyardsViewContainer.hgapProperty());
    VineyardCardContent cardContent = new VineyardCardContent(vineyard, 200, 150);
    card.setOnMouseClicked(event -> {
      if (event.getClickCount() == 2) {
        openDetailedVineyardView(vineyard);
      }
    });
    card.setAlignment(Pos.CENTER);
    card.getChildren().add(cardContent);
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

  private void openDetailedVineyardView(Vineyard vineyard) {
    Runnable backAction = () -> managerContext.getGuiManager().mainController.openVineyardsScreen();
    managerContext.getGuiManager().mainController.openDetailedVineyardView(vineyard, backAction);
  }
}
