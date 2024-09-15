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

  public ArrayList<String> wineLists = new ArrayList<String>();

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
    if (wineLists.size() == 5) {
      createListRequestButton.setDisable(true);
    }
    listName.setText("");
  }

  @FXML
  public void onCreateListConfirmButton(ActionEvent actionEvent) {
    String name = listName.getText();
    if (wineLists.contains(name)) {
      errorText.setVisible(true);
    } else {
      errorText.setVisible(false);
      wineLists.add(name);

      listName.setText("");

      if (!wineLists.isEmpty()) {
        listOneButton.setText(wineLists.getFirst());
        if (wineLists.size()>= 2) {
          listTwoButton.setText(wineLists.get(1));
          if (wineLists.size()>= 3) {
            listThreeButton.setText(wineLists.get(2));
            if (wineLists.size()>= 4) {
              listFourButton.setText(wineLists.get(3));
              if (wineLists.size() == 5) {
                listFiveButton.setText(wineLists.get(4));
              }
            }
          }
        }
      }
    onBackButton(actionEvent);

    }
  }


}
