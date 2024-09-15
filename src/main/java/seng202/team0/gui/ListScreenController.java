package seng202.team0.gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import seng202.team0.managers.ManagerContext;

/**
 * List Screen Controller (MORE DETAIL HERE!)
 */
public class ListScreenController extends Controller {

  @FXML
  public Button pressButton;

  @FXML
  public Label changeThisPlease;

  /**
   * Constructor
   *
   * @param managerContext manager context
   */
  public ListScreenController(ManagerContext managerContext) {
    super(managerContext);
  }


  public void onButtonPressed(ActionEvent actionEvent) {
    changeThisPlease.setText("Wooo it worked!");
  }
}
