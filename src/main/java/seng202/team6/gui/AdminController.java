package seng202.team6.gui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import seng202.team6.managers.ManagerContext;
import seng202.team6.model.WineReview;

/**
 * Controller for the admin screen.
 */
public class AdminController extends Controller {
  @FXML
  TableView<WineReview> reviewsTable;

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

  /**
   * Constructor.
   *
   * @param managerContext manager context
   */
  public AdminController(ManagerContext managerContext) {
    super(managerContext);
  }

  /**
   * Initializes the controller.
   */
  public void initialize() {
    noButton.setVisible(false);
    yesButton.setVisible(false);

    VBox parent = (VBox) managerContext.getGuiManager().mainController.loadImportWineScreen(
        importWinesScreenContainer);
    VBox.setVgrow(parent, Priority.ALWAYS);
    parent.minHeightProperty().bind(importWinesScreenContainer.minHeightProperty());
    parent.maxHeightProperty().bind(importWinesScreenContainer.maxHeightProperty());
    parent.prefHeightProperty().bind(importWinesScreenContainer.prefHeightProperty());
    parent.minWidthProperty().bind(importWinesScreenContainer.minWidthProperty());
    parent.maxWidthProperty().bind(importWinesScreenContainer.maxWidthProperty());
    parent.prefWidthProperty().bind(importWinesScreenContainer.prefWidthProperty());
  }

  private void setupReviewTable() {
    reviewsTable.getItems().clear();
  }

  private void refreshReviewTable() {

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
    managerContext.getDatabaseManager().getUserDao().deleteAll();
    managerContext.getGuiManager().mainController.openWineScreen();
  }

  @FXML
  private void onNo() {
    deleteButton.setDisable(false);
    noButton.setVisible(false);
    yesButton.setVisible(false);
    deleteButton.setText("Delete all Users");
  }
}
