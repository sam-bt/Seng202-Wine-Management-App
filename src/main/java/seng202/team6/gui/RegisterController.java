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
      switch (response){
        case AuthenticationResponse.USERNAME_ALREADY_REGISTERED:;
          usernameField.getStyleClass().add("error-text-field");
          passwordField.getStyleClass().add("error-text-field");
          confirmPasswordField.getStyleClass().add("error-text-field");
          usernameErrorLabel.setText("Username already in use");
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
            usernameError += "Username must be between 3 and 15 characters long\n";
          }
          if (!username.matches("[a-zA-Z0-9_]+")) {
            usernameError += "Username cannot contain special characters or spaces";
          }
          usernameErrorLabel.setText(usernameError);
          usernameErrorLabel.setVisible(true);
          passwordErrorLabel.setVisible(true);
          break;
        case AuthenticationResponse.SAME_AS_USERNAME:
          passwordField.getStyleClass().add("error-text-field");
          confirmPasswordField.getStyleClass().add("error-text-field");
          passwordErrorLabel.setText("Password cannot be same as username");
          passwordErrorLabel.setVisible(true);
          break;
        case AuthenticationResponse.INVALID_PASSWORD:
          passwordField.getStyleClass().add("error-text-field");
          confirmPasswordField.getStyleClass().add("error-text-field");
          int passwordLength = password.length();
          System.out.println(password);
          if (passwordLength < 8 || passwordLength > 30) {
            passwordError += "Pasword must be between 8 and 30 characters long\n";
          }
          if (!password.equals("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&*.()\\-+={\\[\\]}])"
              + "[A-Za-z0-9!@#$%^&*.()\\-+={\\[\\]}]{8,30}$")) {
            passwordError += "Missing a ";
            if (!password.matches(".*[a-z].*")) {
              passwordError += "lowercase, ";
            }
            if (!password.matches(".*[A-Z].*")) {
              passwordError += "uppercase, ";
            }
            if (!password.matches("(.*[0-9].*)")) {
              passwordError += "number, ";
            }
            if (!password.matches("(.*[!@#$%^&*.()\\-+={\\[\\]}].*)")) {
              passwordError += "special character";
            }
          }
          passwordErrorLabel.setText(passwordError);
          passwordErrorLabel.setVisible(true);
          break;
        case AuthenticationResponse.MISMATCHING_CONFIRMED_PASSWORD:
          confirmPasswordField.getStyleClass().add("error-text-field");
          confirmPasswordErrorLabel.setText("Passwords do not match");
          confirmPasswordErrorLabel.setVisible(true);
          break;
      }


      if (response == AuthenticationResponse.REGISTER_SUCCESS) {
        managerContext.getAuthenticationManager().validateLoginPassword(username, password);
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
    usernameErrorLabel.setText("");
    passwordErrorLabel.setText("");
    confirmPasswordErrorLabel.setText("");
    usernameErrorLabel.setVisible(false);
    passwordErrorLabel.setVisible(false);
    confirmPasswordErrorLabel.setVisible(false);
  }


}
