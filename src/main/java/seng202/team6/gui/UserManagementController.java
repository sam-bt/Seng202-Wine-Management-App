package seng202.team6.gui;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import seng202.team6.managers.DatabaseManager;
import seng202.team6.managers.ManagerContext;

import java.util.Optional;

public class UserManagementController extends Controller {
    @FXML
    private ListView<String> userList;

    @FXML
    private Label userLabel;

    @FXML
    private Button deleteUser;

    private ManagerContext managerContext;

    private DatabaseManager dbman;

    private String workingUsername;

    public UserManagementController(ManagerContext managerContext, Runnable backAction) {
        super(managerContext);
        this.managerContext = managerContext;
        this.dbman = managerContext.databaseManager;
    }

    /**
     * Initialize the controller, set up the list and event listeners
     */
    @FXML
    private void initialize() {
        ObservableList<String> users = dbman.getUsernames();
        userList.setItems(users);
        userList.setOnMouseClicked(this::selectUser);
        deleteUser.setDisable(true);
    }

    /**
     * Reset FXML component content. Used on account deletion.
     */
    private void resetView() {
        ObservableList<String> users = dbman.getUsernames();
        userList.setItems(users);
        workingUsername = "";
        userLabel.setText("No User Selected");
        deleteUser.setDisable(true);
    }

    /**
     * Seelct a user from the list by double clicking on them.
     * @param event
     */
    @FXML
    private void selectUser(MouseEvent event) {
        //doubleclick
        if (event.getClickCount() == 2) {
            workingUsername = userList.getSelectionModel().getSelectedItem();
            userLabel.setText(workingUsername);
            deleteUser.setDisable(false);
        }
    }

    /**
     * Delete a user and their data
     */
    @FXML
    private void onDeletePressed() {
        // Confirmation dialog
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirm Deletion");
        confirmation.setHeaderText("Deleting: " + workingUsername);
        confirmation.setContentText("Are you sure you want to delete this user?");

        Optional<ButtonType> result = confirmation.showAndWait();
        if (result.get() == ButtonType.OK) {
            dbman.deleteUserFromTable(workingUsername);
            resetView();
        }
    }
}
