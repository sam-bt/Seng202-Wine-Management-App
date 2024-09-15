package seng202.team0.gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import seng202.team0.managers.ManagerContext;

// TODO change the name to listScreenController rather than wishlist to fit other conventions?

/**
 * Wishlist Controller (MORE DETAIL HERE!)
 */
public class WishlistController extends Controller {

  @FXML
  public Button pressButton;

  @FXML
  public Label changeThisPlease;

  /**
   * Constructor
   *
   * @param managerContext manager context
   */
  public WishlistController(ManagerContext managerContext) {
    super(managerContext);
  }


  public void onButtonPressed(ActionEvent actionEvent) {
    changeThisPlease.setText("Wooo it worked!");
  }
}
