package seng202.team6.gui.popup;

import java.util.List;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import seng202.team6.gui.Controller;
import seng202.team6.managers.ManagerContext;
import seng202.team6.model.User;
import seng202.team6.model.WineList;
import seng202.team6.service.WineListService;

/**
 * Controller for the creating list popup.
 */
public class CreateListPopupController extends Controller {

  private final WineListService wineListService;
  @FXML
  private TextField listNameTextField;
  @FXML
  private Label errorMessageLabel;

  /**
   * Constructor.
   *
   * @param managerContext  manager context.
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
    managerContext.getGuiManager().closePopup();
  }

  /**
   * Creates the lists, adding it to the array and updates relevant information on screen.
   *
   * @param actionEvent triggers this function when on action.
   */
  @FXML
  public void onCreateListConfirmButton(ActionEvent actionEvent) {
    resetFields();
    String name = listNameTextField.getText();
    List<WineList> wineLists = wineListService.getWineLists();
    if (wineLists.stream().anyMatch(wineList -> wineList.name().equals(name))) {
      errorMessageLabel.setText("List Already Exists");
      errorMessageLabel.setVisible(true);
    } else {
      String errorMessage = "";

      if (name.length() < 3 || name.length() > 10 || !name.matches("[a-zA-Z0-9_ ]+")) {
        if ((name.length() < 3 || name.length() > 10) && !name.matches("[a-zA-Z0-9_ ]")) {
          errorMessage +=
              "List name must be between 3 and 10 characters and cannot contain special characters";
        } else if (name.length() < 3 || name.length() > 10) {
          errorMessage += "List name must be between 3 and 10 characters";
        } else if (!name.matches("[a-zA-Z0-9_ ]")) {
          errorMessage += "List name cannot contain special characters";
        }
        listNameTextField.getStyleClass().add("error-text-field");
        errorMessageLabel.setText(errorMessage);

        errorMessageLabel.setVisible(true);
      } else {
        errorMessageLabel.setVisible(false);

        User user = managerContext.getAuthenticationManager().getAuthenticatedUser();
        wineListService.createWineList(user, name);

        listNameTextField.setText("");
        onBackButtonClick();
      }
    }
  }

  private void resetFields() {
    errorMessageLabel.setText("");
    errorMessageLabel.setVisible(false);
    listNameTextField.getStyleClass().add("normal-text-field");
    listNameTextField.getStyleClass().remove("error-text-field");

  }

}