package seng202.team6.gui;

import javafx.fxml.FXML;
import seng202.team6.managers.ManagerContext;

public class SettingsController extends Controller {

  public SettingsController(ManagerContext managerContext) {
    super(managerContext);
  }

  @FXML
  private void onUpdatePassword() {
    managerContext.GUIManager.mainController.openUpdatePasswordScreen();
  }

}
