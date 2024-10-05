package seng202.team6.gui;

import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import seng202.team6.gui.controls.ButtonsList;
import seng202.team6.managers.ManagerContext;
import seng202.team6.model.VineyardTour;
import seng202.team6.service.VineyardToursService;

public class TourPlanningController extends Controller {

  @FXML
  private ScrollPane vineyardToursContainer;
  private ButtonsList<VineyardTour> vineyardTourButtonsList;

  private final VineyardToursService vineyardToursService;

  /**
   * Constructs a new TourPlanningController.
   *
   * @param context The manager context
   */
  public TourPlanningController(ManagerContext context) {
    super(context);
    vineyardToursService = new VineyardToursService(managerContext.authenticationManager,
        managerContext.databaseManager);
    bindToVineyardToursService();
  }

  private void bindToVineyardToursService() {
    ObservableList<VineyardTour> vineyardTours = vineyardToursService.getVineyardTours();
    vineyardTours.addListener((ListChangeListener<VineyardTour>) change -> {
      while (change.next()) {
        if (change.wasAdded()) {
          change.getAddedSubList().forEach(vineyardTour ->
              vineyardTourButtonsList.add(vineyardTour, vineyardTour.nameProperty(), () -> {}));
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
        vineyardToursContainer.widthProperty()
    );
    vineyardToursContainer.setContent(vineyardTourButtonsList);
    vineyardToursService.init();
  }

  @FXML
  public void onCreateTourButtonClick() {
    managerContext.GUIManager.mainController.openVineyardTourPopup(vineyardToursService, null);
  }
}
