package seng202.team6.gui.popup;

import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import seng202.team6.gui.Controller;
import seng202.team6.gui.controls.container.AddRemoveCardsContainer;
import seng202.team6.managers.ManagerContext;
import seng202.team6.model.Vineyard;
import seng202.team6.model.VineyardTour;
import seng202.team6.service.VineyardToursService;

/**
 * Controller for the add to tour popup.
 */
public class AddToTourPopupController extends Controller {

  private final VineyardToursService vineyardToursService;
  private final Vineyard vineyard;
  @FXML
  ScrollPane vineyardToursContainer;
  private AddRemoveCardsContainer<VineyardTour> addRemoveCardsContainer;

  /**
   * Constructs a new AddToTourPopupController.
   *
   * @param context the manager context
   */
  public AddToTourPopupController(ManagerContext context, Vineyard vineyard) {
    super(context);
    vineyardToursService = new VineyardToursService(context.getAuthenticationManager(),
        context.getDatabaseManager());
    this.vineyard = vineyard;
    bindToVineyardToursService();
  }

  /**
   * Initializes the controller and its components.
   */
  @Override
  public void init() {
    addRemoveCardsContainer = new AddRemoveCardsContainer<>(
        vineyardToursContainer.viewportBoundsProperty(),
        vineyardToursContainer.widthProperty());
    vineyardToursContainer.setContent(addRemoveCardsContainer);
    vineyardToursService.init();
  }

  /**
   * Handles the action when the back button is clicked.
   */
  @FXML
  void onBackButtonClick() {
    managerContext.getGuiManager().mainController.closePopup();
  }

  /**
   * Binds the vineyards tours service to the UI.
   */
  private void bindToVineyardToursService() {
    vineyardToursService.getVineyardTours()
        .addListener((ListChangeListener<VineyardTour>) change -> {
          while (change.next()) {
            if (change.wasAdded()) {
              change.getAddedSubList().forEach(vineyardTour -> {
                addRemoveCardsContainer.add(vineyardTour, vineyardTour.nameProperty(),
                    !vineyardToursService.isVineyardInTour(vineyardTour, vineyard),
                    () -> managerContext.getDatabaseManager().getVineyardTourDao()
                        .addVineyard(vineyardTour, vineyard),
                    () -> managerContext.getDatabaseManager().getVineyardTourDao()
                        .removeVineyard(vineyardTour, vineyard));
              });
            }
          }
        });
  }
}
