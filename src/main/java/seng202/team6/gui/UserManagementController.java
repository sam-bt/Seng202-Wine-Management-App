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

    @FXML
    private void initialize() {
        System.out.println("RUNNING");
        ObservableList<String> users = dbman.getUsernames();
        userList.setItems(users);
        userList.setOnMouseClicked(this::selectUser);
        deleteUser.setDisable(true);
    }

    private void resetView() {
        ObservableList<String> users = dbman.getUsernames();
        userList.setItems(users);
        workingUsername = "";
        userLabel.setText("No User Selected");
        deleteUser.setDisable(true);
    }

    @FXML
    private void selectUser(MouseEvent event) {
        if (event.getClickCount() == 2) {
            workingUsername = userList.getSelectionModel().getSelectedItem();
            userLabel.setText(workingUsername);
            deleteUser.setDisable(false);
        }
    }

    @FXML
    private void onDeletePressed() {
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
