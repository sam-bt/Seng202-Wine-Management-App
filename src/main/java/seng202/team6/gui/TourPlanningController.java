package seng202.team6.gui;

import java.util.List;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;
import seng202.team6.gui.controls.ButtonsList;
import seng202.team6.gui.controls.card.AddRemoveCard;
import seng202.team6.gui.controls.card.Card;
import seng202.team6.gui.controls.cardcontent.ItineraryItemCardContent;
import seng202.team6.gui.controls.cardcontent.VineyardCardContent;
import seng202.team6.gui.controls.container.CardsContainer;
import seng202.team6.gui.popup.GeneralPopupController;
import seng202.team6.managers.ManagerContext;
import seng202.team6.model.GeoLocation;
import seng202.team6.model.User;
import seng202.team6.model.Vineyard;
import seng202.team6.model.VineyardTour;
import seng202.team6.model.WineList;
import seng202.team6.service.TourPlanningService;
import seng202.team6.service.VineyardService;
import seng202.team6.service.VineyardToursService;
import seng202.team6.util.GeolocationResolver;

/**
 * The controller for manging the vineyard tour planning screen.
 */
public class TourPlanningController extends Controller {

  private final VineyardToursService vineyardToursService;
  private final VineyardService vineyardService;
  private final GeolocationResolver geolocationResolver;
  @FXML
  private VBox planTourTabContainer;
  @FXML
  private HBox noTourSelectedContainer;
  @FXML
  private VBox planTourOptionsContainer;
  @FXML
  private Label viewingTourLabel;
  @FXML
  private ScrollPane vineyardToursContainer;
  @FXML
  private ScrollPane vineyardsContainer;
  @FXML
  private ScrollPane itineraryContainer;
  @FXML
  private WebView webView;
  @FXML
  private TabPane tabPane;
  @FXML
  private Tab viewTourTab;
  @FXML
  private Tab planTourTab;
  private ButtonsList<VineyardTour> vineyardTourButtonsList;
  private CardsContainer<Vineyard> vineyardCardsContainer;
  private CardsContainer<Vineyard> itineraryCardsContainer;
  private LeafletOsmController mapController;
  private TourPlanningService currentTourPlanningService;

  /**
   * Constructs a new TourPlanningController.
   *
   * @param context the manager context
   */
  public TourPlanningController(ManagerContext context) {
    super(context);
    vineyardToursService = new VineyardToursService(managerContext.getAuthenticationManager(),
        managerContext.getDatabaseManager());
    vineyardService = new VineyardService(managerContext.getDatabaseManager());
    geolocationResolver = new GeolocationResolver();
    bindToVineyardToursService();
  }

  /**
   * Binds the vineyard tours service to the UI. The bindings ensure changes to the vineyard tours
   * are reflected in the UI. The listeners will graphically display or remove vineyard tour buttons
   * upon change in the vineyard tours service list.
   */
  private void bindToVineyardToursService() {
    ObservableList<VineyardTour> vineyardTours = vineyardToursService.getVineyardTours();
    vineyardTours.addListener((ListChangeListener<VineyardTour>) change -> {
      while (change.next()) {
        if (change.wasAdded()) {
          change.getAddedSubList().forEach(vineyardTour ->
              vineyardTourButtonsList.add(vineyardTour, vineyardTour.nameProperty(), () -> {
                // if the user already has the tour open, don't open it again
                if (currentTourPlanningService == null
                    || currentTourPlanningService.getVineyardTour() != vineyardTour) {
                  openVineyardTour(vineyardTour);
                  tabPane.getSelectionModel().select(planTourTab);
                }
              }));
        }
        if (change.wasRemoved()) {
          change.getRemoved().forEach(vineyardTour -> vineyardTourButtonsList.remove(vineyardTour));
        }
      }
    });
  }

  /**
   * Initializes the controller and its components.
   */
  @Override
  public void init() {
    vineyardTourButtonsList = new ButtonsList<>(
        vineyardToursContainer.viewportBoundsProperty(),
        vineyardToursContainer.widthProperty());
    vineyardCardsContainer = new CardsContainer<>(
        vineyardsContainer.viewportBoundsProperty(),
        vineyardsContainer.widthProperty());
    itineraryCardsContainer = new CardsContainer<>(
        itineraryContainer.viewportBoundsProperty(),
        itineraryContainer.widthProperty());
    vineyardToursContainer.setContent(vineyardTourButtonsList);
    vineyardsContainer.setContent(vineyardCardsContainer);
    itineraryContainer.setContent(itineraryCardsContainer);
    vineyardToursService.init();
    vineyardService.init();

    mapController = new LeafletOsmController(webView.getEngine());
    mapController.initMap();

    planTourTabContainer.getChildren().remove(planTourOptionsContainer);
  }

  /**
   * Handles the action when the "Create Tour" button is clicked.
   */
  @FXML
  public void onCreateTourButtonClick() {
    GeneralPopupController popup = setupCreateTourPopup("Create Vineyard Tour");
    VBox optionsWrapper = createTourPopupOptionsWrapper(popup);
    TextField nameTextField = createNameField(optionsWrapper);
    popup.addContent(optionsWrapper);

    popup.addButton("Create", () -> {
      createVineyardTour(nameTextField.getText(), popup);
    });
    popup.addCancelButton();
  }

  /**
   * Handles the action when the "Create Tour from List" button is clicked.
   */
  @FXML
  public void onCreateTourFromListButtonClick() {
    GeneralPopupController popup = setupCreateTourPopup("Create Vineyard Tour from List");
    VBox optionsWrapper = createTourPopupOptionsWrapper(popup);
    TextField nameTextField = createNameField(optionsWrapper);

    Label listLabel = new Label("List");
    ComboBox<WineList> wineListsComboBox = createWineListComboBox();
    optionsWrapper.getChildren().addAll(listLabel, wineListsComboBox);
    popup.addContent(optionsWrapper);

    popup.addButton("Create", () -> {
      VineyardTour vineyardTour = createVineyardTour(nameTextField.getText(), popup);
      if (vineyardTour == null) {
        return;
      }
      WineList wineList = wineListsComboBox.getSelectionModel().getSelectedItem();
      managerContext.getDatabaseManager().getVineyardsDao()
          .getAllInList(wineList)
          .forEach(vineyard -> currentTourPlanningService.addVineyard(vineyard));
      openVineyardTour(vineyardTour);
    });
    popup.addCancelButton();
  }

  /**
   * Handles the action when the delete tour button is clicked.
   */
  @FXML
  public void onDeleteTourClick() {
    GeneralPopupController popup = managerContext.getGuiManager().mainController.showPopup();
    VineyardTour currentVineyardTour = currentTourPlanningService.getVineyardTour();
    popup.setTitle("Delete Tour Confirmation");
    popup.setMessage("Are you sure you would like to delete the tour '"
        + currentVineyardTour.getName() + "'?");
    popup.addButton("Confirm", () -> {
      vineyardToursService.removeVineyardTour(currentVineyardTour);
      currentTourPlanningService = null;
      closeVineyardTour();
      popup.close();
    });
    popup.addCancelButton();
  }

  /**
   * Handles the action when the calculate tour button is clicked.
   */
  @FXML
  public void onCalculateTourClick() {
    List<Vineyard> vineyards = currentTourPlanningService.getVineyards();
    if (vineyards.size() < 2) {
      showNotEnoughVineyardsToCalculateError();
      return;
    }

    mapController.clearWineMarkers();
    List<GeoLocation> vineyardLocations = currentTourPlanningService.getVineyards().stream()
        .peek(vineyard -> mapController.addVineyardMaker(vineyard, false))
        .map(Vineyard::getGeoLocation)
        .toList();
    String geometry = geolocationResolver.resolveRoute(vineyardLocations);
    if (geometry == null) {
      showCalculatingRouteError();
      return;
    }
    mapController.addRoute(geometry);
    tabPane.getSelectionModel().select(viewTourTab);
  }

  /**
   * Opens the specified vineyard tour and updates the UI with the associated vineyards.
   * <p>
   * This method populates the vineyard and itinerary containers with the appropriate cards for the
   * selected vineyard tour, allowing the user to view and manage the vineyards included in the
   * tour.
   * </p>
   *
   * @param vineyardTour The vineyard tour to be opened.
   */
  private void openVineyardTour(VineyardTour vineyardTour) {
    vineyardCardsContainer.removeAll();
    itineraryCardsContainer.removeAll();

    ObservableList<Vineyard> vineyards = vineyardService.get();
    vineyards.forEach(vineyard -> {
      VineyardCardContent vineyardCardContent = new VineyardCardContent(vineyard, 150, 100);
      AddRemoveCard addRemoveCard = new AddRemoveCard(vineyardCardsContainer.widthProperty(),
          new SimpleDoubleProperty(), vineyardCardContent, true,
          !vineyardToursService.isVineyardInTour(vineyardTour, vineyard), false,
          () -> currentTourPlanningService.addVineyard(vineyard),
          () -> currentTourPlanningService.removeVineyard(vineyard),
          "Add winery to tour", "Remove winery from tour");
      vineyardCardsContainer.addCard(vineyard, addRemoveCard);
    });
    currentTourPlanningService = new TourPlanningService(managerContext.getDatabaseManager(),
        vineyardTour);
    currentTourPlanningService.getVineyards().addListener((ListChangeListener<Vineyard>) change -> {
      while (change.next()) {
        if (change.wasAdded()) {
          change.getAddedSubList().forEach(vineyard -> {
            ItineraryItemCardContent itineraryItemCardContent = new ItineraryItemCardContent(
                vineyard);
            Card card = new Card(itineraryCardsContainer.widthProperty(),
                new SimpleDoubleProperty());
            card.getChildren().add(itineraryItemCardContent);
            itineraryCardsContainer.addCard(vineyard, card);
          });
        }
        if (change.wasRemoved()) {
          change.getRemoved().forEach(vineyard -> itineraryCardsContainer.remove(vineyard));
        }
      }
    });
    currentTourPlanningService.init();
    viewingTourLabel.setText("Viewing Tour: " + vineyardTour.getName());

    planTourTabContainer.getChildren().remove(noTourSelectedContainer);
    // if a tour was previously open, don't add the tour options container again
    if (!planTourTabContainer.getChildren().contains(planTourOptionsContainer)) {
      planTourTabContainer.getChildren().add(planTourOptionsContainer);
    }
  }

  /**
   * Closes the vineyard tour planning options container.
   */
  private void closeVineyardTour() {
    planTourTabContainer.getChildren().remove(planTourOptionsContainer);
  }

  /**
   * Displays an error popup indicating that there are not enough vineyards in the itinerary to
   * calculate a route.
   */
  private void showNotEnoughVineyardsToCalculateError() {
    GeneralPopupController popup = managerContext.getGuiManager().mainController.showErrorPopup();
    popup.setTitle("Error Calculating Route");
    popup.setMessage("Please add 2 or more vineyards to your itinerary to calculate a route.");
    popup.addOkButton();
  }

  /**
   * Displays an error popup indicating that there was an issue calculating the route.
   */
  private void showCalculatingRouteError() {
    GeneralPopupController popup = managerContext.getGuiManager().mainController.showErrorPopup();
    popup.setTitle("Error Calculating Route");
    popup.setMessage("There was an error calculating a route. Please try again later.");
    popup.addOkButton();
  }

  /**
   * Creates a wrapper VBox to contain options in the "Create Tour" popup. This method sets up
   * spacing, width, and bindings for the wrapper.
   *
   * @param popup The popup where the options wrapper will be added.
   * @return The configured VBox containing options for the popup.
   */
  private VBox createTourPopupOptionsWrapper(GeneralPopupController popup) {
    VBox optionsWrapper = new VBox();
    optionsWrapper.setSpacing(10);
    optionsWrapper.setMaxWidth(Double.MAX_VALUE);
    optionsWrapper.prefWidthProperty().bind(popup.getContentContainer().widthProperty());
    return optionsWrapper;
  }

  /**
   * Sets up and displays a popup for creating a vineyard tour with the specified title.
   *
   * @param title The title of the popup.
   * @return The GeneralPopupController handling the popup.
   */
  private GeneralPopupController setupCreateTourPopup(String title) {
    GeneralPopupController popup = managerContext.getGuiManager().mainController.showPopup();
    popup.setTitle(title);
    return popup;
  }

  /**
   * Creates a text field for entering the name of the vineyard tour and adds it to the options
   * container in the popup.
   *
   * @param optionsWrapper The VBox to which the name field will be added.
   * @return The created TextField for entering the tour name.
   */
  private TextField createNameField(VBox optionsWrapper) {
    final Label nameLabel = new Label("Name");
    TextField nameTextField = new TextField();
    nameTextField.setMaxWidth(Double.MAX_VALUE);
    optionsWrapper.getChildren().addAll(nameLabel, nameTextField);
    return nameTextField;
  }

  /**
   * Creates and returns a ComboBox containing wine lists for the authenticated user.
   *
   * @return A ComboBox populated with the wine lists of the authenticated user.
   */
  private ComboBox<WineList> createWineListComboBox() {
    ComboBox<WineList> wineListsComboBox = new ComboBox<>();
    User user = managerContext.getAuthenticationManager().getAuthenticatedUser();
    ObservableList<WineList> wineLists = managerContext.getDatabaseManager().getWineListDao()
        .getAll(user);
    wineListsComboBox.getItems().addAll(wineLists);
    return wineListsComboBox;
  }

  /**
   * Creates a new vineyard tour with the specified name and closes the popup upon creation.
   * The method also opens the newly created vineyard tour for the user to view and manage.
   *
   * @param name  The name of the new vineyard tour.
   * @param popup The popup used for creating the tour.
   * @return The newly created VineyardTour object.
   */
  private VineyardTour createVineyardTour(String name, GeneralPopupController popup) {
    if (name.length() < VineyardToursService.MIN_NAME_SIZE
        || name.length() > VineyardToursService.MAX_NAME_SIZE) {
      popup.setErrorMessage("The tour name must be between " + VineyardToursService.MIN_NAME_SIZE
          + " and " + VineyardToursService.MAX_NAME_SIZE + " characters.");
      return null;
    }
    if (vineyardToursService.getVineyardTours().stream()
        .anyMatch(vineyardTour -> vineyardTour.getName().equalsIgnoreCase(name))) {
      popup.setErrorMessage("A tour with this name already exists");
      return null;
    }

    VineyardTour vineyardTour = vineyardToursService.createVineyardTour(name);
    openVineyardTour(vineyardTour);
    popup.close();
    return vineyardTour;
  }
}
