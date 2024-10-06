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

public class VineyardTourPopupController extends Controller {
  @FXML
  private HBox buttonsContainer;
  @FXML
  private TitledPane createModifyReviewTitlePane;
  @FXML
  private Button deleteButton;
  @FXML
  private ComboBox<Island> islandComboBox;
  @FXML
  private Button submitButton;
  @FXML
  private TextField tourNameTextfield;

  private final VineyardToursService vineyardToursService;
  private final VineyardTour modifyingVineyard;

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

  @Override
  public void init() {
    boolean modifying = modifyingVineyard != null;
    createModifyReviewTitlePane.setText((modifying ? "Modify" : "Create") + " a Vineyard Tour");
    submitButton.setText(modifying ? "Modify" : "Create");
    if (!modifying) {
      buttonsContainer.getChildren().remove(deleteButton);
    }
    islandComboBox.getItems().addAll(Island.values());
    islandComboBox.getSelectionModel().selectFirst();
  }

  @FXML
  void onBackButtonClick() {
    managerContext.getGuiManager().mainController.closePopup();
  }

  @FXML
  void onDeleteButtonClick() {

  }

  @FXML
  void onSubmitButtonClick() {
    String name = tourNameTextfield.getText();
    Island island = islandComboBox.getSelectionModel().getSelectedItem();
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
