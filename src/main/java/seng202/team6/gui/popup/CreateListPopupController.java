package seng202.team6.gui.popup;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import seng202.team6.gui.Controller;
import seng202.team6.managers.ManagerContext;
import seng202.team6.model.User;
import seng202.team6.model.WineList;
import seng202.team6.service.WineListService;
import java.util.List;

public class CreateListPopupController extends Controller {
    @FXML
    private TextField listNameTextField;
    @FXML
    private Label errorText;
    private final WineListService wineListService;
    /**
     *
     * @param managerContext manager context.
     * @param wineListService the service class which handles creating the list.
     */
    public CreateListPopupController(ManagerContext managerContext, WineListService wineListService) {
        super(managerContext);
        this.wineListService = wineListService;
    }

    /**
     * Closes the popup without creating a list.
     */
    public void onBackButtonClick() {
        managerContext.GUIManager.mainController.closePopup();
    }

    /**
     * creates the lists, adding it to the array and updates relevant information on screen
     *
     * @param actionEvent triggers this function when on action.
     */
    @FXML
    public void onCreateListConfirmButton(ActionEvent actionEvent) {
        String name = listNameTextField.getText();
        List<WineList> wineLists = wineListService.getWineLists();
        if (wineLists.stream().anyMatch(wineList -> wineList.name().equals(name))) {
            errorText.setText("List Already Exists");
            errorText.setVisible(true);
        } else {

            if (name.length() < 3 || name.length() > 10 || !name.matches("[a-zA-Z0-9_]+")) {
                errorText.setText("Invalid List Name");
                errorText.setVisible(true);
            } else {
                errorText.setVisible(false);

                User user = managerContext.authenticationManager.getAuthenticatedUser();
                wineListService.createWineList(user, name);

                listNameTextField.setText("");
                onBackButtonClick();
            }
        }
    }
}