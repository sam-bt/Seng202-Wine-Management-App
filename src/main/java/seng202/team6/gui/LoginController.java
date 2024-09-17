package seng202.team6.gui;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import seng202.team6.managers.ManagerContext;
import seng202.team6.model.AuthenticationResponse;
import seng202.team6.service.AuthenticationService;

/**
 * Login Controller (MORE DETAIL HERE!)
 */
public class LoginController extends Controller {

  @FXML
  private TextField usernameField;
  @FXML
  private TextField passwordField;
  @FXML
  private Label loginMessageLabel;

  private final AuthenticationService authenticationService;

  /**
   * Constructor
   *
   * @param managerContext manager context
   */
  public LoginController(ManagerContext managerContext, AuthenticationService authenticationService) {
    super(managerContext);
    this.authenticationService = authenticationService;
  }

  @FXML
  private void onConfirm() {

    String username = usernameField.getText();
    String password = passwordField.getText();
    AuthenticationResponse response = authenticationService.validateLogin(username, password);
    if (response == AuthenticationResponse.LOGIN_SUCCESS) {
      if (managerContext.authenticationManager.isAdminFirstLogin()) {
        managerContext.GUIManager.mainController.setDisable(true);
        managerContext.GUIManager.mainController.openUpdatePasswordScreen();
        return;
      }
      managerContext.GUIManager.mainController.openWineScreen();
      managerContext.GUIManager.mainController.onLogin();
    } else {
      loginMessageLabel.setStyle("-fx-text-fill: red");
      loginMessageLabel.setText(response.getMessage());
    }
  }
}
