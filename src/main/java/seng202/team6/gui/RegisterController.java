package seng202.team6.gui;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import seng202.team6.managers.ManagerContext;
import seng202.team6.model.AuthenticationResponse;

/**
 * Register Controller
 */
public class RegisterController extends Controller {

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

  @Override
  public void init() {
    usernameField.requestFocus();
    //set key handlers for ENTER to attempt login on keypress
    usernameField.setOnKeyPressed(event -> {
      if (event.getCode() == KeyCode.ENTER) {
        onConfirm();
      }
    });
    passwordField.setOnKeyPressed(event -> {
      if (event.getCode() == KeyCode.ENTER) {
        onConfirm();
      }
    });
    confirmPasswordField.setOnKeyPressed(event -> {
      if (event.getCode() == KeyCode.ENTER) {
        onConfirm();
      }
    });

  }

  @FXML
  private void onConfirm() {

    String username = usernameField.getText();
    String password = passwordField.getText();
    String confirmPassword = confirmPasswordField.getText();
    AuthenticationResponse response = managerContext.authenticationManager.validateRegistration(
        username, password, confirmPassword);
    if (response == AuthenticationResponse.REGISTER_SUCCESS) {
      managerContext.authenticationManager.validateLogin(username, password);
      managerContext.GUIManager.mainController.onLogin();
      managerContext.GUIManager.mainController.openWineScreen();
    } else {
      registerMessageLabel.setStyle("-fx-text-fill: red");
      registerMessageLabel.setText(response.getMessage());
    }
  }

}
