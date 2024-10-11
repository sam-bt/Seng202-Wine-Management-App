package seng202.team6.gui;

import java.util.Optional;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import seng202.team6.enums.AuthenticationResponse;
import seng202.team6.managers.DatabaseManager;
import seng202.team6.managers.ManagerContext;
import seng202.team6.model.User;


/**
 * Controller for the admin screen.
 */
public class AdminController extends Controller {
  @FXML
  Label passwordBoxLabel;

  @FXML
  AnchorPane selectedActionsPane;

  @FXML
  Label passwordErrorLabel;

  @FXML
  Label adminMessageLabel;

  @FXML
  private Button resetPasswordButton;

  @FXML
  private Button acceptPassword;


  @FXML
  private Button cancelPasswordButton;

  @FXML
  private PasswordField confirmField;

  @FXML
  Button yesButton;

  @FXML
  Button noButton;

  @FXML
  Button deleteButton;
  @FXML
  Button deleteReviews;

  @FXML
  private ListView<User> userList;

  @FXML
  private Label userLabel;

  @FXML
  private Button deleteUser;

  @FXML
  private VBox importWinesScreenContainer;

  @FXML
  private AnchorPane passwordBox;

  @FXML
  private PasswordField passwordField;

  private final DatabaseManager databaseManager;

  private User workingUser = null;

  private static final Logger log = LogManager.getLogger(AdminController.class);

  /**
   * Constructor.
   *
   * @param managerContext manager context
   */
  public AdminController(ManagerContext managerContext) {
    super(managerContext);
    this.databaseManager = managerContext.getDatabaseManager();
  }

  /**
   * Initializes the controller.
   */
  public void initialize() {
    noButton.setVisible(false);
    yesButton.setVisible(false);
    togglePasswordBox(false);

    VBox parent = (VBox) managerContext.getGuiManager().loadImportWineScreen(
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
    managerContext.getGuiManager().openWineScreen();
  }

  @FXML
  private void onNo() {
    deleteButton.setDisable(false);
    noButton.setVisible(false);
    yesButton.setVisible(false);
    deleteButton.setText("Delete all Users");
  }

  /**
   * Delete a user and their data.
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
      databaseManager.getUserDao().delete(workingUser);
      resetView();
    }
  }

  @FXML
  private void onDeleteReviews() {
    Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
    confirmation.setTitle("Confirm Deletion");
    confirmation.setHeaderText("Deleting reviws for: " + workingUser.getUsername());
    confirmation.setContentText(
        "Are you sure you want to delete all reviews left by this user?\n"
            + "This action cannot be undone");
    Optional<ButtonType> result = confirmation.showAndWait();
    if (result.get() == ButtonType.OK) {
      databaseManager.getWineReviewDao().deleteAllFromUser(workingUser);
    }
  }

  /**
   * Reset FXML component content. Used on account deletion.
   */
  private void resetView() {
    ObservableList<User> users = databaseManager.getUserDao().getAll();
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
    deleteReviews.setDisable(true);
    resetPasswordButton.setDisable(true);
  }

  /**
   * Select a user from the list by double clicking on them.
   *
   * @param event is the MouseEvent.
   */
  @FXML
  private void selectUser(MouseEvent event) {
    //doubleclick
    if (event.getClickCount() == 2) {
      workingUser = userList.getSelectionModel().getSelectedItem();
      userLabel.setText(workingUser.getUsername());
      deleteUser.setDisable(false);
      deleteReviews.setDisable(false);
      resetPasswordButton.setDisable(false);
    }
  }

  private void togglePasswordBox(boolean state) {
    passwordField.setVisible(state);
    confirmField.setVisible(state);
    acceptPassword.setVisible(state);
    cancelPasswordButton.setVisible(state);
    passwordBoxLabel.setVisible(state);
    if (!state) {
      passwordField.setText("");
      confirmField.setText("");
      passwordErrorLabel.setText("");
    }


  }

  @FXML
  void onResetPassword() {
    togglePasswordBox(true);
  }

  @FXML
  void onNewPasswordAccept() {
    String password = passwordField.getText();
    String confirm = confirmField.getText();
    AuthenticationResponse response =
            managerContext.getAuthenticationManager().validatePasswordReset(
            workingUser.getUsername(), password, confirm
    );

    if (response == AuthenticationResponse.PASSWORD_CHANGED_SUCCESS) {
      log.info("Password updated");
      togglePasswordBox(false);
      selectedActionsPane.setPrefHeight(200);
    } else {
      selectedActionsPane.setPrefHeight(300);
      passwordErrorLabel.setStyle("-fx-text-fill: red");
      passwordErrorLabel.setText(response.getMessage());
    }

  }

  @FXML
  void onNewPasswordCancel() {
    togglePasswordBox(false);
    selectedActionsPane.setPrefHeight(200);
  }

}
