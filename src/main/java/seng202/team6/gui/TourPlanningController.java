package seng202.team6.gui;

import java.util.List;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.web.WebView;
import seng202.team6.gui.controls.*;
import seng202.team6.managers.ManagerContext;
import seng202.team6.model.GeoLocation;
import seng202.team6.model.Vineyard;
import seng202.team6.model.VineyardTour;
import seng202.team6.service.TourPlanningService;
import seng202.team6.service.VineyardService;
import seng202.team6.service.VineyardToursService;
import seng202.team6.util.GeolocationResolver;

public class TourPlanningController extends Controller {

  @FXML
  private ScrollPane vineyardToursContainer;

  @FXML
  private ScrollPane vineyardsContainer;
  @FXML
  private WebView webView;
  @FXML
  private TabPane tabPane;
  @FXML
  private Tab viewTourTab;
  private ButtonsList<VineyardTour> vineyardTourButtonsList;
  private CardsContainer<Vineyard> vineyardCardsContainer;

  private final VineyardToursService vineyardToursService;
  private final VineyardService vineyardService;
  private LeafletOSMController mapController;
  private TourPlanningService currentTourPlanningService;

  private final GeolocationResolver geolocation;

  /**
   * Constructs a new TourPlanningController.
   *
   * @param context The manager context
   */
  public TourPlanningController(ManagerContext context) {
    super(context);
    vineyardToursService = new VineyardToursService(managerContext.authenticationManager,
        managerContext.databaseManager);
    vineyardService = new VineyardService(managerContext.databaseManager);
    bindToVineyardToursService();
    geolocation = new GeolocationResolver();
  }

  private void bindToVineyardToursService() {
    ObservableList<VineyardTour> vineyardTours = vineyardToursService.getVineyardTours();
    vineyardTours.addListener((ListChangeListener<VineyardTour>) change -> {
      while (change.next()) {
        if (change.wasAdded()) {
          change.getAddedSubList().forEach(vineyardTour ->
              vineyardTourButtonsList.add(vineyardTour, vineyardTour.nameProperty(),
                      () -> openVineyardTour(vineyardTour)));
        }
        if (change.wasRemoved()) {
          change.getRemoved().forEach(vineyardTour -> vineyardTourButtonsList.remove(vineyardTour));
        }
      }
    });
  }

  @Override
  public void init() {
    vineyardTourButtonsList = new ButtonsList<>(
            vineyardToursContainer.viewportBoundsProperty(),
            vineyardToursContainer.widthProperty());
    vineyardCardsContainer = new CardsContainer<>(
            vineyardsContainer.viewportBoundsProperty(),
            vineyardsContainer.widthProperty());
    vineyardToursContainer.setContent(vineyardTourButtonsList);
    vineyardsContainer.setContent(vineyardCardsContainer);
    vineyardToursService.init();
    vineyardService.init();

    mapController = new LeafletOSMController(webView.getEngine());
    mapController.initMap();
  }

  @FXML
  public void onCreateTourButtonClick() {
    managerContext.GUIManager.mainController.openVineyardTourPopup(vineyardToursService, null);
  }

  @FXML
  public void onCalculateTourClick() {
    tabPane.getSelectionModel().select(viewTourTab);
    mapController.clearWineMarkers();
    List<GeoLocation> list = currentTourPlanningService.getVineyards().stream()
            .peek(vineyard -> mapController.addVineyardMaker(vineyard, false))
            .map(Vineyard::getGeoLocation)
            .toList();
    String geometry = geolocation.getRoute(list);
    mapController.addRoute(geometry);
  }

  public void openVineyardTour(VineyardTour vineyardTour) {
    ObservableList<Vineyard> vineyards = vineyardService.get();
    vineyards.forEach(vineyard -> {
      VineyardCard card = new VineyardCard(vineyardCardsContainer.widthProperty(), new SimpleDoubleProperty(), vineyard, 150, 100);
      card.setOpaqueInsets(new Insets(0, 10, 0, 10));
      HBox hbox = card.getHbox();
      AddRemoveButton addRemoveButton = new AddRemoveButton(
              !vineyardToursService.isVineyardInTour(vineyardTour, vineyard),
              () -> currentTourPlanningService.addVineyard(vineyard),
              () -> currentTourPlanningService.removeVineyard(vineyard),
              false
      );
      HBox addRemoveButtonWrapper = new HBox(addRemoveButton);
      addRemoveButtonWrapper.setAlignment(Pos.CENTER_RIGHT);
      hbox.setAlignment(Pos.CENTER_LEFT);
      HBox.setHgrow(addRemoveButtonWrapper, Priority.ALWAYS);

      hbox.getChildren().add(addRemoveButtonWrapper);
      vineyardCardsContainer.addCard(vineyard, card);
    });
    currentTourPlanningService = new TourPlanningService(managerContext.databaseManager, vineyardTour);
    currentTourPlanningService.init();
  }
}
