package seng202.team6.gui;

import javafx.fxml.FXML;
import seng202.team6.managers.ManagerContext;

/**
 * Controller for the user settings menu.
 */
public class SettingsController extends Controller {

  /**
   * Constructor.
   *
   * @param managerContext manager context
   */
  public SettingsController(ManagerContext managerContext) {
    super(managerContext);
  }

  /**
   * Called to open password screen.
   */
  @FXML
  private void onUpdatePassword() {
    managerContext.getGuiManager().mainController.openUpdatePasswordScreen();
  }

}
