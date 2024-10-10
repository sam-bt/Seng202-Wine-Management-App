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
  private Label usernameErrorLabel;
  @FXML
  private Label passwordErrorLabel;
  @FXML
  private Label confirmPasswordErrorLabel;

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

    resetFields();

  }

  @FXML
  private void onConfirm() {
    resetFields();

    String username = usernameField.getText();
    String password = passwordField.getText();
    String confirmPassword = confirmPasswordField.getText();

    boolean usernameNull = (username.isEmpty());
    boolean passwordNull = (password.isEmpty());
    boolean confirmPasswordNull = (confirmPassword.isEmpty());

    if (!usernameNull && !passwordNull && !confirmPasswordNull) {
      AuthenticationResponse response
          = managerContext.getAuthenticationManager().validateRegistration(
          username, password, confirmPassword);

      if (response == AuthenticationResponse.REGISTER_SUCCESS) {
        // managerContext.getAuthenticationManager().validateLogin(username, password);
        managerContext.getGuiManager().mainController.updateNavigation();
        managerContext.getGuiManager().mainController.openWineScreen();
      } else {
//      registerMessageLabel.setText(response.getMessage());
      }
    } else {
      usernameErrorLabel.setText("Please enter a username");
      passwordErrorLabel.setText("Please enter a password");
      confirmPasswordErrorLabel.setText("Please re-enter your password");
      usernameErrorLabel.setVisible(usernameNull);
      passwordErrorLabel.setVisible(passwordNull);
      confirmPasswordErrorLabel.setVisible(confirmPasswordNull);


      if (usernameNull) {
        usernameField.getStyleClass().add("error-text-field");
      }
      if (passwordNull) {
        passwordField.getStyleClass().add("error-text-field");
      }
      if (confirmPasswordNull) {
        confirmPasswordField.getStyleClass().add("error-text-field");
    }

  }

  }

  private void resetFields() {
    usernameField.getStyleClass().add("normal-text-field");
    usernameField.getStyleClass().remove("error-text-field");
    passwordField.getStyleClass().add("normal-text-field");
    passwordField.getStyleClass().remove("error-text-field");
    confirmPasswordField.getStyleClass().add("normal-text-field");
    confirmPasswordField.getStyleClass().remove("error-text-field");
    usernameErrorLabel.setVisible(false);
    passwordErrorLabel.setVisible(false);
    confirmPasswordErrorLabel.setVisible(false);
  }


}
