package seng202.team6.gui;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import seng202.team6.managers.DatabaseManager;
import seng202.team6.managers.ManagerContext;
import seng202.team6.model.User;

import java.util.Optional;

public class UserManagementController extends Controller {
    @FXML
    private ListView<User> userList;

    @FXML
    private Label userLabel;

    @FXML
    private Button deleteUser;

    private ManagerContext managerContext;

    private DatabaseManager dbman;

    private User workingUser = null;

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
        ObservableList<User> users = dbman.getUserDAO().getAll();
        resetView();
        userList.setOnMouseClicked(this::selectUser);
    }

    /**
     * Reset FXML component content. Used on account deletion.
     */
    private void resetView() {
        ObservableList<User> users = dbman.getUserDAO().getAll();
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
            dbman.getUserDAO().delete(workingUser);
            resetView();
        }
    }
}
