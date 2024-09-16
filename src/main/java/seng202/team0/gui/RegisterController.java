package seng202.team0.gui;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import seng202.team0.managers.ManagerContext;
import seng202.team0.util.UserService;

/**
 * Register Controller
 */
public class RegisterController extends Controller{

  @FXML
  private TextField usernameField;
  @FXML
  private TextField passwordField;
  @FXML
  private TextField confirmPasswordField;
  @FXML
  private Label registerMessageLabel;

  /**
   * Constructor
   *
   * @param managerContext manager context
   */
  public RegisterController(ManagerContext managerContext) {
    super(managerContext);
  }

  private String validateRegistration(String username,String password, String confirmPassword) {
    String result = UserService.validateRegistration(username,password,confirmPassword, managerContext);
    return result;
  }

  @FXML
  private void onConfirm() {

    String username = usernameField.getText();
    String password = passwordField.getText();
    String confirmPassword = confirmPasswordField.getText();
    String validateResponse = validateRegistration(username,password,confirmPassword);
    if (validateResponse.equals("Success")) {
      managerContext.GUIManager.mainController.openLoginScreen();

      // todo - move this somewhere better
      managerContext.databaseManager.createList(username, "Favourites");
    } else {
      registerMessageLabel.setStyle("-fx-text-fill: red");
      registerMessageLabel.setText(validateResponse);
    }
  }

}
