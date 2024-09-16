package seng202.team0.gui;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import seng202.team0.managers.ManagerContext;
import seng202.team0.service.UserService;

/**
 * Login Controller (MORE DETAIL HERE!)
 */
public class LoginController extends Controller{

  @FXML
  private TextField usernameField;
  @FXML
  private TextField passwordField;
  @FXML
  private Label loginMessageLabel;
  /**
   * Constructor
   *
   * @param managerContext manager context
   */
  public LoginController(ManagerContext managerContext) {
    super(managerContext);
  }

  private String validateLogin(String username,String password) {
    String result = UserService.validateLogin(username,password, managerContext);
    return result;
  }

  @FXML
  private void onConfirm() {

    String username = usernameField.getText();
    String password = passwordField.getText();
    String validateResponse = validateLogin(username,password);
    if (validateResponse.equals("Success")) {
      managerContext.authenticationManager.setAuthenticated(true);
      managerContext.authenticationManager.setAdmin(false);
      managerContext.authenticationManager.setUsername(username);
      managerContext.GUIManager.mainController.openWineScreen();
      managerContext.GUIManager.mainController.onLogin();
    } else if (validateResponse.equals("Admin Success")) {
      managerContext.authenticationManager.setAuthenticated(true);
      managerContext.authenticationManager.setAdmin(true);
      managerContext.authenticationManager.setUsername(username);
      managerContext.GUIManager.mainController.openWineScreen();
      managerContext.GUIManager.mainController.onLogin();
    } else if (validateResponse.equals("Admin First Success")) {
      managerContext.GUIManager.mainController.setDisable(true);
      managerContext.authenticationManager.setUsername(username);
      managerContext.GUIManager.mainController.openUpdatePasswordScreen();

    } else {
      loginMessageLabel.setStyle("-fx-text-fill: red");
      loginMessageLabel.setText(validateResponse);
    }
  }
}
