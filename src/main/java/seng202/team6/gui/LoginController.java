package seng202.team6.gui;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import seng202.team6.enums.AuthenticationResponse;
import seng202.team6.managers.ManagerContext;

/**
 * Login Controller.
 */
public class LoginController extends Controller {

  @FXML
  private TextField usernameField;
  @FXML
  private TextField passwordField;
  @FXML
  private Label usernameErrorMessageLabel;
  @FXML
  private Label passwordErrorMessageLabel;

  /**
   * Constructor.
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
   * Logs a user into the system.
   */
  private void login() {

    resetFields();

    String username = usernameField.getText();
    String password = passwordField.getText();

    boolean usernameNull = (username.isEmpty());
    boolean passwordNull = (password.isEmpty());

    if (!usernameNull && !passwordNull) {
      AuthenticationResponse usernameResponse = managerContext.getAuthenticationManager()
          .validateLoginUsername(username);
      if (usernameResponse == AuthenticationResponse.INVALID_LOGIN_USERNAME) {
        usernameErrorMessageLabel.setText(AuthenticationResponse
            .INVALID_LOGIN_USERNAME.getMessage());
        usernameField.getStyleClass().add("error-text-field");
        passwordField.getStyleClass().add("error-text-field");
        usernameErrorMessageLabel.setVisible(true);
      } else {
        AuthenticationResponse response = managerContext.getAuthenticationManager()
            .validateLoginPassword(username, password);

        if (response == AuthenticationResponse.LOGIN_SUCCESS) {
          if (managerContext.getAuthenticationManager().isAdminFirstLogin()) {
            managerContext.getGuiManager().mainController.setDisable(true);
            managerContext.getGuiManager().mainController.openUpdatePasswordScreen();
            return;
          }
          managerContext.getGuiManager().mainController.openWineScreen();
          managerContext.getGuiManager().mainController.onLogin();
        } else {
          passwordField.getStyleClass().add("error-text-field");
          passwordErrorMessageLabel.setVisible(true);
          passwordErrorMessageLabel.setText(AuthenticationResponse
              .INVALID_LOGIN_PASSWORD.getMessage());
        }
      }

    } else {
      usernameErrorMessageLabel.setText("Please enter a username");
      passwordErrorMessageLabel.setText("Please enter a password");
      usernameErrorMessageLabel.setVisible(usernameNull);
      passwordErrorMessageLabel.setVisible(passwordNull);

      if (usernameNull) {
        usernameField.getStyleClass().add("error-text-field");
      }
      if (passwordNull) {
        passwordField.getStyleClass().add("error-text-field");
      }
    }

  }

  private void resetFields() {
    usernameField.getStyleClass().add("normal-text-field");
    usernameField.getStyleClass().remove("error-text-field");
    passwordField.getStyleClass().add("normal-text-field");
    passwordField.getStyleClass().remove("error-text-field");
    usernameErrorMessageLabel.setVisible(false);
    passwordErrorMessageLabel.setVisible(false);
  }

}


