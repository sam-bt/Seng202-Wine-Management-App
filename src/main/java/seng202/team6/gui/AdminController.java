package seng202.team6.gui;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.input.MouseEvent;
import seng202.team6.managers.DatabaseManager;
import seng202.team6.managers.ManagerContext;
import seng202.team6.model.User;
import seng202.team6.model.Wine;

import java.util.Optional;

/**
 * Controller for the admin screen.
 */
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
  private ListView<User> userList;

  @FXML
  private Label userLabel;

  @FXML
  private Button deleteUser;

  @FXML
  private VBox importWinesScreenContainer;

  private DatabaseManager databaseManager;

  private User workingUser = null;

  /**
   * Constructor.
   *
   * @param managerContext manager context
   */
  public AdminController(ManagerContext managerContext) {
    super(managerContext);
    this.databaseManager = managerContext.databaseManager;
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

    resetView();
    userList.setOnMouseClicked(this::selectUser);
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

  /**
   * Delete a user and their data
   */
  @FXML
  private void onDeletePressed() {
    // Confirmation dialog
    Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
    confirmation.setTitle("Confirm Deletion");
    confirmation.setHeaderText("Deleting: " + workingUser.getUsername());
    confirmation.setContentText("Are you sure you want to delete this user?");

    Optional<ButtonType> result = confirmation.showAndWait();
    if (result.get() == ButtonType.OK) {
      databaseManager.getUserDAO().delete(workingUser);
      resetView();
    }
  }

  /**
   * Reset FXML component content. Used on account deletion.
   */
  private void resetView() {
    ObservableList<User> users = databaseManager.getUserDAO().getAll();
    userList.setCellFactory(param -> new ListCell<User>() {
      @Override
      protected void updateItem(User item, boolean empty) {
        super.updateItem(item, empty);

        if (empty || item == null || item.getUsername() == null) {
          setText(null);
        } else {
          setText(item.getUsername());
        }
      }
    });
    userList.setItems(users);
    userLabel.setText("No User Selected");
    deleteUser.setDisable(true);
  }

  /**
   * Select a user from the list by double clicking on them.
   * @param event
   */
  @FXML
  private void selectUser(MouseEvent event) {
    //doubleclick
    if (event.getClickCount() == 2) {
      workingUser = userList.getSelectionModel().getSelectedItem();
      userLabel.setText(workingUser.getUsername());
      deleteUser.setDisable(false);
    }
  }

}
