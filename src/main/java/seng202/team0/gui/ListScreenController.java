package seng202.team0.gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import seng202.team0.managers.ManagerContext;

/**
 * List Screen Controller (MORE DETAIL HERE!)
 */
public class ListScreenController extends Controller {

  @FXML
  public Button createListRequestButton;
  @FXML
  public Button backButton;
  @FXML
  public Label changeThisPlease;
  @FXML
  public TabPane listScreenTabs;
  @FXML
  public Tab tabViewing;
  @FXML
  public Tab tabCreating;

  /**
   * Constructor
   *
   * @param managerContext manager context
   */
  public ListScreenController(ManagerContext managerContext) {
    super(managerContext);
  }

  /**
   * Initializes the page making sure the tab for creating lists is hidden.
   */
  public void initialize() {
    listScreenTabs.getTabs().remove(tabCreating);
  }

  /**
   * opens the tab for creating lists and hides the tab for viewing lists.
   * @param actionEvent triggers this function when on action.
   */
  @FXML
  public void onCreateListRequestButton(ActionEvent actionEvent) {
    listScreenTabs.getTabs().add(tabCreating);
    listScreenTabs.getTabs().remove(tabViewing);
    createListRequestButton.setDisable(true);
  }

  /**
   * opens the tab for viewing lists and hides the tab for creating lists.
   * @param actionEvent triggers this function when on action.
   */
  @FXML
  public void onBackButton(ActionEvent actionEvent) {
    listScreenTabs.getTabs().add(tabViewing);
    listScreenTabs.getTabs().remove(tabCreating);
    createListRequestButton.setDisable(false);
  }
}
