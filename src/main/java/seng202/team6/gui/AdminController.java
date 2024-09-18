package seng202.team6.gui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import seng202.team6.managers.ManagerContext;

public class AdminController extends Controller {

  @FXML
  Label adminMessageLabel;

  @FXML
  Button yesButton;

  @FXML
  Button noButton;

  @FXML
  Button deleteButton;

  public AdminController(ManagerContext managerContext) {
    super(managerContext);
  }

  public void initialize() {
    noButton.setVisible(false);
    yesButton.setVisible(false);
  }

  @FXML
  private void onDeleteMembers() {
    deleteButton.setDisable(true);
    noButton.setVisible(true);
    yesButton.setVisible(true);
    deleteButton.setText("Are you sure?");
  }

  @FXML
  private void onYes() {
    managerContext.databaseManager.deleteAllUsers();
    managerContext.GUIManager.mainController.openWineScreen();
  }

  @FXML
  private void onNo() {
    deleteButton.setDisable(false);
    noButton.setVisible(false);
    yesButton.setVisible(false);
    deleteButton.setText("Delete all Users");
  }

}
