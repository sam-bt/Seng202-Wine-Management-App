package seng202.team0.gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import seng202.team0.managers.ManagerContext;

import javax.swing.*;
import java.lang.reflect.Array;
import java.util.ArrayList;

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

  public ArrayList<String> wineLists = new ArrayList<String>();

  private boolean deleting;
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
    wineLists.add("Favourites");
    updateListOptions();
    tabViewing.setText("VIEWING: " + wineLists.getFirst());
    selected = 1;
    deleteListRequestButton.setDisable(true);
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
      errorText.setVisible(true);
    } else {
      errorText.setVisible(false);
      wineLists.add(name);

      listName.setText("");
      updateListOptions();
      deleteListRequestButton.setDisable(false);
      onBackButton(actionEvent);
      selected = wineLists.size();
      changeSelected();

    }
  }

  /**
   * deletes the selected list. Cannot delete the favourites list.
   * @param actionEvent triggers this function when on action.
   */
  public void onDeleteListRequestButton(ActionEvent actionEvent) {
    if (selected != 1) {
      wineLists.remove(selected - 1);
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
    for (int i = 0; i < buttons.length; i++) {
      if (i < wineLists.size()) {
        buttons[i].setText(wineLists.get(i));
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
   * @param actionEvent
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
    tabViewing.setText("VIEWING: " + wineLists.get(selected - 1));
  }
}
