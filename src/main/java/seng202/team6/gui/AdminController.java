package seng202.team6.gui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import seng202.team6.managers.ManagerContext;
import seng202.team6.model.Wine;

public class AdminController extends Controller {

  @FXML
  Label adminMessageLabel;

  @FXML
  Button yesButton;

  @FXML
  Button noButton;

  @FXML
  Button deleteButton;

  @FXML
  private VBox importWinesScreenContainer;

  public AdminController(ManagerContext managerContext) {
    super(managerContext);
  }

  public void initialize() {
    noButton.setVisible(false);
    yesButton.setVisible(false);

    VBox parent = (VBox) managerContext.GUIManager.mainController.loadImportWineScreen(
        importWinesScreenContainer);
    VBox.setVgrow(parent, Priority.ALWAYS);
    parent.minHeightProperty().bind(importWinesScreenContainer.minHeightProperty());
    parent.maxHeightProperty().bind(importWinesScreenContainer.maxHeightProperty());
    parent.prefHeightProperty().bind(importWinesScreenContainer.prefHeightProperty());
    parent.minWidthProperty().bind(importWinesScreenContainer.minWidthProperty());
    parent.maxWidthProperty().bind(importWinesScreenContainer.maxWidthProperty());
    parent.prefWidthProperty().bind(importWinesScreenContainer.prefWidthProperty());
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
    managerContext.databaseManager.getUserDAO().deleteAll();
    managerContext.GUIManager.mainController.openWineScreen();
  }

  @FXML
  private void onNo() {
    deleteButton.setDisable(false);
    noButton.setVisible(false);
    yesButton.setVisible(false);
    deleteButton.setText("Delete all Users");
  }

  @FXML
  public void onManageUsers() {
    Runnable backAction = () -> managerContext.GUIManager.mainController.openAdminScreen();
    managerContext.GUIManager.mainController.openUserManagementView(backAction);
  }

}
