package seng202.team6.gui.popup;

import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import seng202.team6.gui.Controller;
import seng202.team6.managers.ManagerContext;


/**
 * Controller for the user search popup.
 */
public class UserSearchPopupController extends Controller {

  @FXML
  private TextField searchTextField;

  @FXML
  private TableView userTableView;

  @FXML
  private TableColumn userTableColumn;

  /**
   * Constructor for the user search popup.
   */
  public UserSearchPopupController(ManagerContext context) {
    super(context);
  }

  @FXML
  void onBackButtonClick() {
    managerContext.getGuiManager().closePopup();
  }

  @FXML
  void onSearchButtonClick() {
    managerContext.getGuiManager().closePopup();
  }


}
