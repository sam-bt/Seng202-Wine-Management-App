package seng202.team6.gui;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import seng202.team6.enums.AuthenticationResponse;
import seng202.team6.managers.ManagerContext;

/**
 * Register Controller.
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
   * Constructor.
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
    AuthenticationResponse response
        = getManagerContext().getAuthenticationManager().validateRegistration(
        username, password, confirmPassword);
    if (response == AuthenticationResponse.REGISTER_SUCCESS) {
      getManagerContext().getAuthenticationManager().validateLogin(username, password);
      getManagerContext().getGuiManager().updateNavigation();
      getManagerContext().getGuiManager().openWineScreen();
    } else {
      registerMessageLabel.setStyle("-fx-text-fill: red");
      registerMessageLabel.setText(response.getMessage());
    }
  }

}
