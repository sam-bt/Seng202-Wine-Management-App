package seng202.team6.gui.popup;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import seng202.team6.gui.Controller;
import seng202.team6.managers.ManagerContext;
import seng202.team6.model.WineList;
import seng202.team6.service.WineListService;

/**
 * Class for deleting a list popup.
 */
public class DeleteListPopupController extends Controller {

  private final WineList wineListToDelete;
  private final WineListService wineListService;
  private final String name;
  @FXML
  private Label deleteLabel;

  /**
   * Constructor.
   *
   * @param managerContext  manager Context.
   * @param wineList        The selected list to delete.
   * @param wineListService The service class which holds the functionality to delete the list.
   */
  public DeleteListPopupController(ManagerContext managerContext, WineList wineList,
      WineListService wineListService) {
    super(managerContext);
    this.wineListToDelete = wineList;
    this.wineListService = wineListService;
    this.name = wineList.name();
  }

  /**
   * Adds text asking the user if they are sure they want to delete the selected list.
   */
  @FXML
  public void initialize() {
    deleteLabel.setText(
        "Are you sure you want to delete the list:\n" + name + "?\nThis action cannot be undone!");
  }

  /**
   * Closes the popup without deleting the list.
   */
  @FXML
  public void onBackButtonClick() {
    getManagerContext().getGuiManager().closePopup();
  }

  /**
   * Deletes the list and closes the popup.
   */
  @FXML
  public void onDeleteListConfirmClick() {
    wineListService.deleteWineList(wineListToDelete);
    getManagerContext().getGuiManager().closePopup();
  }
}