package seng202.team6.gui;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import seng202.team6.enums.AuthenticationResponse;
import seng202.team6.managers.AuthenticationManager;
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
    String usernameError = "";
    String passwordError = "";
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
      switch (response) {
        case AuthenticationResponse.USERNAME_ALREADY_REGISTERED:;
          usernameField.getStyleClass().add("error-text-field");
          passwordField.getStyleClass().add("error-text-field");
          confirmPasswordField.getStyleClass().add("error-text-field");
          usernameErrorLabel.setText(
              AuthenticationResponse.USERNAME_ALREADY_REGISTERED.getMessage()
          );
          usernameErrorLabel.setVisible(true);
          passwordErrorLabel.setVisible(true);
          confirmPasswordErrorLabel.setVisible(true);
          break;
        case AuthenticationResponse.INVALID_USERNAME:
          usernameField.getStyleClass().add("error-text-field");
          passwordField.getStyleClass().add("error-text-field");
          confirmPasswordField.getStyleClass().add("error-text-field");
          int userLength = usernameField.getText().length();
          if (userLength < 3 || userLength > 15) {
            usernameError += AuthenticationResponse.INVALID_USERNAME_LENGTH.getMessage() + "\n";
          }
          if (!username.matches("[a-zA-Z0-9_]+")) {
            usernameError += AuthenticationResponse.INVALID_USERNAME_SYMBOL.getMessage();
          }
          usernameErrorLabel.setText(usernameError);
          usernameErrorLabel.setVisible(true);
          passwordErrorLabel.setVisible(true);
          break;
        case AuthenticationResponse.SAME_AS_USERNAME:
          passwordField.getStyleClass().add("error-text-field");
          confirmPasswordField.getStyleClass().add("error-text-field");
          passwordErrorLabel.setText(AuthenticationResponse.SAME_AS_USERNAME.getMessage());
          passwordErrorLabel.setVisible(true);
          break;
        case AuthenticationResponse.INVALID_PASSWORD:
          passwordField.getStyleClass().add("error-text-field");
          confirmPasswordField.getStyleClass().add("error-text-field");
          int passwordLength = password.length();
          if (passwordLength < 8 || passwordLength > 30) {
            passwordError += AuthenticationResponse.INVALID_PASSWORD_LENGTH.getMessage() + "\n";
          }
          if (password.matches(".*\\s.*")) {
            passwordError += AuthenticationResponse.INVALID_PASSWORD_CONTAINS_SPACES.getMessage();
          } else {
            if (!password.equals(
                AuthenticationResponse.PASSWORD_CONSTRAINTS.getMessage())) {
              passwordError +=
                  AuthenticationResponse.INVALID_PASSWORD_HEADER.getMessage();
              if (!password.matches(".*[a-z].*")) {
                passwordError +=
                    AuthenticationResponse.INVALID_PASSWORD_MISSING_LOWERCASE.getMessage();
              }
              if (!password.matches(".*[A-Z].*")) {
                passwordError +=
                    AuthenticationResponse.INVALID_PASSWORD_MISSING_UPPERCASE.getMessage();
              }
              if (!password.matches("(.*[0-9].*)")) {
                passwordError +=
                    AuthenticationResponse.INVALID_PASSWORD_MISSING_NUMBER.getMessage();
              }
              if (!password.matches("(.*[!@#$%^&*.()\\-+={\\[\\]}].*)")) {
                passwordError +=
                    AuthenticationResponse.INVALID_PASSWORD_MISSING_SPECIAL_CHAR.getMessage();
              }
            }
          }
          passwordErrorLabel.setText(passwordError);
          passwordErrorLabel.setVisible(true);
          break;
        case AuthenticationResponse.MISMATCHING_CONFIRMED_PASSWORD:
          confirmPasswordField.getStyleClass().add("error-text-field");
          confirmPasswordErrorLabel.setText(
              AuthenticationResponse.MISMATCHING_CONFIRMED_PASSWORD.getMessage());
          confirmPasswordErrorLabel.setVisible(true);
          break;
        default:
          break;
      }


      if (response == AuthenticationResponse.REGISTER_SUCCESS) {
        managerContext.getAuthenticationManager().validateLoginPassword(username, password);
        managerContext.getGuiManager().mainController.updateNavigation();
        managerContext.getGuiManager().mainController.openWineScreen();
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
    usernameErrorLabel.setText("");
    passwordErrorLabel.setText("");
    confirmPasswordErrorLabel.setText("");
    usernameErrorLabel.setVisible(false);
    passwordErrorLabel.setVisible(false);
    confirmPasswordErrorLabel.setVisible(false);
  }
}
