package seng202.team6.gui;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import seng202.team6.managers.ManagerContext;
import seng202.team6.enums.AuthenticationResponse;

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

  /**
   * Constructor
   *
   * @param managerContext manager context
   */
  public LoginController(ManagerContext managerContext) {

    super(managerContext);

  }

  @FXML
  private void onConfirm() {
    login();
  }

  @Override
  public void init() {
    usernameField.requestFocus();
    //set key handlers for ENTER to attempt login on keypress
    passwordField.setOnKeyPressed(event -> {
      if (event.getCode() == KeyCode.ENTER) {
        login();
      }
    });
    usernameField.setOnKeyPressed(event -> {
      if (event.getCode() == KeyCode.ENTER) {
        login();
      }
    });
  }

  /**
   * Logs a user into the system
   */
  private void login() {
    String username = usernameField.getText();
    String password = passwordField.getText();
    AuthenticationResponse response = managerContext.authenticationManager.validateLogin(username,
        password);
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


