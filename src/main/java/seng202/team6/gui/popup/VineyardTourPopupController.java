package seng202.team6.gui.popup;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.HBox;
import seng202.team6.enums.Island;
import seng202.team6.gui.Controller;
import seng202.team6.managers.ManagerContext;
import seng202.team6.model.VineyardTour;
import seng202.team6.service.VineyardToursService;

/**
 * Controller for the vineyard tours popup controller which allows modification or creation of a
 * vineyard tour.
 */
public class VineyardTourPopupController extends Controller {

  private final VineyardToursService vineyardToursService;
  private final VineyardTour modifyingVineyard;
  @FXML
  private HBox buttonsContainer;
  @FXML
  private TitledPane createModifyReviewTitlePane;
  @FXML
  private Button deleteButton;
  @FXML
  private Button submitButton;
  @FXML
  private TextField tourNameTextfield;

  /**
   * Constructs a new VineyardTourPopupController.
   *
   * @param context The manager context
   */
  public VineyardTourPopupController(ManagerContext context,
      VineyardToursService vineyardToursService, VineyardTour modifyingVineyard) {
    super(context);
    this.vineyardToursService = vineyardToursService;
    this.modifyingVineyard = modifyingVineyard;
  }

  /**
   * Initializes the controller and its components.
   */
  @Override
  public void init() {
    boolean modifying = modifyingVineyard != null;
    createModifyReviewTitlePane.setText((modifying ? "Modify" : "Create") + " a Vineyard Tour");
    submitButton.setText(modifying ? "Modify" : "Create");
    if (!modifying) {
      buttonsContainer.getChildren().remove(deleteButton);
    }
  }

  /**
   * Handles the action when the back button is clicked.
   */
  @FXML
  void onBackButtonClick() {
    managerContext.getGuiManager().mainController.closePopup();
  }

  /**
   * Handles the action when the delete button is clicked.
   */
  @FXML
  void onDeleteButtonClick() {

  }

  /**
   * Handles the action when the submit button is clicked.
   */
  @FXML
  void onSubmitButtonClick() {
    String name = tourNameTextfield.getText();
    Island island = Island.NORTH;
    ObservableList<VineyardTour> vineyardTours = vineyardToursService.getVineyardTours();
    boolean existingName = vineyardTours.stream().anyMatch(
        vineyardTour -> vineyardTour.getName().equals(name) && modifyingVineyard != vineyardTour);
    if (existingName) {
      // tour already exists
      return;
    }

    // todo - when the user is modifying, update the name via attribute setter
    vineyardToursService.createVineyardTour(name, island);
    managerContext.getGuiManager().mainController.closePopup();
  }
}
