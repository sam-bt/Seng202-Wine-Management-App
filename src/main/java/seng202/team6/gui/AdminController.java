package seng202.team6.gui;

import java.util.Optional;
import javafx.beans.property.BooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import seng202.team6.enums.AuthenticationResponse;
import seng202.team6.managers.DatabaseManager;
import seng202.team6.managers.ManagerContext;
import seng202.team6.model.User;
import seng202.team6.model.WineReview;

/**
 * Controller for the admin screen.
 */
public class AdminController extends Controller {

  private static final Logger log = LogManager.getLogger(AdminController.class);
  private final DatabaseManager databaseManager;
  private final ObservableList<WineReview> selectedReviews = FXCollections.observableArrayList();
  @FXML
  TableView<WineReview> reviewsTable;
  @FXML
  Label passwordBoxLabel;
  @FXML
  AnchorPane selectedActionsPane;
  @FXML
  Label passwordErrorLabel;
  @FXML
  Label adminMessageLabel;
  @FXML
  Button yesButton;
  @FXML
  Button noButton;
  @FXML
  Button deleteButton;
  @FXML
  Button deleteReviews;
  @FXML
  private Button resetPasswordButton;
  @FXML
  private Button acceptPassword;
  @FXML
  private Button cancelPasswordButton;
  @FXML
  private PasswordField confirmField;
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
  @FXML
  private Label bulkLabel;
  @FXML
  private Button deleteSelected;
  @FXML
  private Button keepSelected;
  @FXML
  private Button deleteAll;
  @FXML
  private Button keepAll;
  private User workingUser = null;
  private ObservableList<WineReview> allFlaggedReviews = FXCollections.observableArrayList();

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
    //==========| Users Tab |==========<
    noButton.setVisible(false);
    yesButton.setVisible(false);
    togglePasswordBox(false);
    resetView();
    userList.setOnMouseClicked(this::selectUser);

    //==========| Reviews Tab |==========<
    allFlaggedReviews = databaseManager.getWineReviewDao().getAllFlaggedReviews();
    setupReviewTable();
    refreshReviewActions();

    //==========| Data Tab |==========<
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

  //=================================| USER MANAGEMENT |=================================<

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

  //=================================| REVIEW MODERATION |=================================<

  /**
   * Sets up the review table columns.
   */
  private void setupReviewTable() {
    reviewsTable.getItems().clear();
    reviewsTable.getColumns().clear();

    reviewsTable.setEditable(true);

    final TableColumn<WineReview, Integer> reviewColumn = new TableColumn<>("ID");
    final TableColumn<WineReview, Integer> ratingColumn = new TableColumn<>("Rating");
    final TableColumn<WineReview, String> descriptionColumn = new TableColumn<>("Description");
    final TableColumn<WineReview, Boolean> reviewCheckboxColumn = new TableColumn<>("Selection");

    reviewColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
    ratingColumn.setCellValueFactory(new PropertyValueFactory<>("rating"));
    descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));

    descriptionColumn.setCellFactory(tableCell -> {
      TableCell<WineReview, String> cell = new TableCell<WineReview, String>();
      Text text = new Text();
      cell.setGraphic(text);
      cell.setPrefHeight(Control.USE_COMPUTED_SIZE);
      text.wrappingWidthProperty().bind(descriptionColumn.widthProperty());
      text.textProperty().bind(cell.itemProperty());
      return cell;
    });

    reviewCheckboxColumn.setCellFactory(column -> new CheckBoxTableCell<>());
    reviewCheckboxColumn.setCellValueFactory(cellData -> {
          WineReview cellValue = cellData.getValue();
          BooleanProperty property = cellValue.selectedProperty();
          property.addListener((observable, oldValue, newValue) -> {
            cellValue.setSelected(newValue);
            if (newValue) {
              if (!selectedReviews.contains(cellValue)) {
                selectedReviews.add(cellValue);
                log.info(selectedReviews.size() + " reviews selected");
              }
            } else {
              selectedReviews.remove(cellValue);
            }
          });
          return property;
        }
    );

    descriptionColumn.setPrefWidth(800);

    reviewsTable.getColumns()
        .addAll(reviewColumn, ratingColumn, descriptionColumn, reviewCheckboxColumn);
    refreshReviewTable();

  }

  /**
   * Reloads the reviews table data.
   */
  private void refreshReviewTable() {
    reviewsTable.getItems().clear();
    allFlaggedReviews = databaseManager.getWineReviewDao().getAllFlaggedReviews();
    reviewsTable.setItems(allFlaggedReviews);

  }

  /**
   * Disables the action buttons if no reviews are present.
   */
  private void refreshReviewActions() {
    if (allFlaggedReviews.size() == 0) {
      keepAll.setDisable(true);
      keepSelected.setDisable(true);
      deleteAll.setDisable(true);
      deleteSelected.setDisable(true);
    } else {
      keepAll.setDisable(false);
      keepSelected.setDisable(false);
      deleteAll.setDisable(false);
      deleteSelected.setDisable(false);
    }
  }

  @FXML
  void onDeleteSelected() {
    for (int i = 0; i < selectedReviews.size(); i++) {
      databaseManager.getWineReviewDao().delete(selectedReviews.get(i));
      long wineId = selectedReviews.get(i).getWineId();
      ObservableList<WineReview> theseReviews = databaseManager.getWineReviewDao()
          .getAll(databaseManager.getWineDao().get(wineId));
      double sum = theseReviews.stream()
          .mapToDouble(WineReview::getRating)
          .sum();
      databaseManager.getWineDao().get(wineId).setAverageRating(sum / theseReviews.size());

    }
    selectedReviews.clear();
    allFlaggedReviews.clear();
    refreshReviewTable();
    refreshReviewActions();
  }

  @FXML
  void onDeleteAll() {
    databaseManager.getWineReviewDao().deleteAllFlaggedReviews();
    for (int i = 0; i < allFlaggedReviews.size(); i++) {
      long wineId = allFlaggedReviews.get(i).getWineId();
      ObservableList<WineReview> theseReviews = databaseManager.getWineReviewDao()
          .getAll(databaseManager.getWineDao().get(wineId));
      double sum = theseReviews.stream()
          .mapToDouble(WineReview::getRating)
          .sum();
      databaseManager.getWineDao().get(wineId).setAverageRating(sum / theseReviews.size());
    }

    selectedReviews.clear();
    allFlaggedReviews.clear();
    refreshReviewTable();
  }

  @FXML
  void onKeepAll() {
    for (int i = 0; i < allFlaggedReviews.size(); i++) {
      allFlaggedReviews.get(i).setFlag(0);
      databaseManager.getWineReviewDao().updateWineReviewFlag(allFlaggedReviews.get(i));
      allFlaggedReviews.get(i).setSelected(false);
    }
    selectedReviews.clear();
    allFlaggedReviews.clear();
    refreshReviewTable();

  }

  @FXML
  void onKeepSelected() {
    log.info(selectedReviews.size());
    for (int i = 0; i < selectedReviews.size(); i++) {
      selectedReviews.get(i).setFlag(0);
      databaseManager.getWineReviewDao().updateWineReviewFlag(selectedReviews.get(i));
      //selectedReviews.get(i).setSelected(false);
    }
    refreshReviewTable();
  }


}
