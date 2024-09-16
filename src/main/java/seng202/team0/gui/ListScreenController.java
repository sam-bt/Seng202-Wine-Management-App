package seng202.team0.gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import seng202.team0.database.WineList;
import seng202.team0.managers.ManagerContext;

import java.util.List;

/**
 * List Screen Controller (MORE DETAIL HERE!)
 */
public class ListScreenController extends Controller {

  @FXML
  public Button createListRequestButton;
  @FXML
  public Button backButton;
  @FXML
  public TabPane listScreenTabs;
  @FXML
  public Tab tabViewing;
  @FXML
  public Tab tabCreating;
  @FXML
  public TextField listName;
  @FXML
  public Label errorText;
  @FXML
  public Button listOneButton, listTwoButton, listThreeButton, listFourButton, listFiveButton;
  @FXML
  public Button deleteListRequestButton;

  public List<WineList> wineLists;
  private int selected = 1;

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
    updateListOptions();
    tabViewing.setText("VIEWING: " + wineLists.getFirst().name());
    selected = 1;
    if (wineLists.size() == 1) {
      deleteListRequestButton.setDisable(true);
    }
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
    deleteListRequestButton.setDisable(true);
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
    deleteListRequestButton.setDisable(false);
    if (wineLists.size() == 5) {
      createListRequestButton.setDisable(true);
    } else if (wineLists.size() == 1) {
      deleteListRequestButton.setDisable(true);
    }
    listName.setText("");
    errorText.setVisible(false);

  }

  /**
   * creates the lists, adding it to the array and updates relevant information on screen
   * @param actionEvent triggers this function when on action.
   */
  @FXML
  public void onCreateListConfirmButton(ActionEvent actionEvent) {
    String name = listName.getText();
    if (wineLists.contains(name)) {
      errorText.setText("User Already Exists");
      errorText.setVisible(true);
    } else {

      if (name.length() < 3 || name.length() > 10 || !name.matches("[a-zA-Z0-9_]+")) {
        errorText.setText("Invalid List Name");
        errorText.setVisible(true);
      } else {
        errorText.setVisible(false);

        String user = managerContext.authenticationManager.getUsername();

        managerContext.databaseManager.createList(user, name);

        listName.setText("");
        updateListOptions();
        deleteListRequestButton.setDisable(false);
        onBackButton(actionEvent);
        selected = wineLists.size();
        changeSelected();
      }


    }
  }

  /**
   * deletes the selected list. Cannot delete the favourites list.
   * @param actionEvent triggers this function when on action.
   */
  public void onDeleteListRequestButton(ActionEvent actionEvent) {
    if (selected != 1) {

      String user = managerContext.authenticationManager.getUsername();
      WineList wineList = wineLists.get(selected - 1);
      managerContext.databaseManager.deleteList(wineList);
      updateListOptions();
      selected -= 1;
      changeSelected();
      createListRequestButton.setDisable(false);
      if (wineLists.size() == 1) {
        deleteListRequestButton.setDisable(true);
      }
    }
  }

  /**
   * updates the information displayed on the screen
   **/

  @FXML
  public void updateListOptions() {
    Button[] buttons = {listOneButton, listTwoButton, listThreeButton, listFourButton, listFiveButton};
    String user = managerContext.authenticationManager.getUsername();
    wineLists = managerContext.databaseManager.getUserLists(user);
    for (int i = 0; i < buttons.length; i++) {
      if (i < wineLists.size()) {
        buttons[i].setText(wineLists.get(i).name());
        buttons[i].setDisable(false);
      } else {
        buttons[i].setText("Empty List");
        buttons[i].setDisable(true);
      }
    }
  }

  /**
   * Selects List One.
   * @param actionEvent triggers this function when on action.
   */
  public void onListOneButton(ActionEvent actionEvent) {
    selected = 1;
    changeSelected();
  }

  /**
   * Selects List Two.
   * @param actionEvent triggers this function when on action.
   */
  public void onListTwoButton(ActionEvent actionEvent) {
    selected = 2;
    changeSelected();

  }

  /**
   * Selects List Three.
   * @param actionEvent triggers this function when on action.
   */
  public void onListThreeButton(ActionEvent actionEvent) {
    selected = 3;
    changeSelected();

  }

  /**
   * Selects List Four.
   * @param actionEvent triggers this function when on action.
   */
  public void onListFourButton(ActionEvent actionEvent) {
    selected = 4;
    changeSelected();

  }

  /**
   * Selects List Five;
   * @param actionEvent triggers this function when on action.
   */
  public void onListFiveButton(ActionEvent actionEvent) {
    selected = 5;
    changeSelected();

  }

  /**
   * Changes the selected list.
   */
  public void changeSelected() {
    tabViewing.setText("VIEWING: " + wineLists.get(selected - 1).name());
  }
}
