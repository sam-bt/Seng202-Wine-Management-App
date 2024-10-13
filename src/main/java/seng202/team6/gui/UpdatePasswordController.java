package seng202.team6.gui;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.input.KeyCode;
import seng202.team6.enums.AuthenticationResponse;
import seng202.team6.managers.ManagerContext;
import seng202.team6.util.PasswordUtil;

/**
 * controller to handle updating passwords.
 */
public class UpdatePasswordController extends Controller {

  @FXML
  private TextField oldPasswordField;
  @FXML
  private TextField newPasswordField;
  @FXML
  private TextField confirmNewPasswordField;
  @FXML
  private Label oldPasswordErrorLabel;
  @FXML
  private Label newPasswordErrorLabel;
  @FXML
  private Label confirmNewPasswordErrorLabel;

  @FXML
  private TitledPane titledPane;

  private boolean disabled;

  /**
   * Constructor.
   *
   * @param managerContext manager context
   */
  public UpdatePasswordController(ManagerContext managerContext) {
    super(managerContext);
  }

  /**
   * Initializes the controller.
   */
  public void initialize() {
    oldPasswordField.setOnKeyPressed(event -> {
      if (event.getCode() == KeyCode.ENTER) {
        onConfirm();
      }
    });
    newPasswordField.setOnKeyPressed(event -> {
      if (event.getCode() == KeyCode.ENTER) {
        onConfirm();
      }
    });
    confirmNewPasswordField.setOnKeyPressed(event -> {
      if (event.getCode() == KeyCode.ENTER) {
        onConfirm();
      }
    });
    resetFields();

  }

  @FXML
  private void onConfirm() {
    resetFields();

    String username = getManagerContext().getAuthenticationManager().getAuthenticatedUsername();
    String oldPassword = oldPasswordField.getText();
    String newPassword = newPasswordField.getText();
    String confirmNewPassword = confirmNewPasswordField.getText();
    String passwordError = "";

    boolean oldPasswordNull = (oldPassword.isEmpty());
    boolean newPasswordNull = (newPassword.isEmpty());
    boolean confirmNewPasswordNull = (confirmNewPassword.isEmpty());
    if (!oldPasswordNull && !newPasswordNull && !confirmNewPasswordNull) {
      AuthenticationResponse response = getManagerContext().getAuthenticationManager()
          .validateUpdate(username, oldPassword, newPassword, confirmNewPassword);

      if (response == AuthenticationResponse.PASSWORD_CHANGED_SUCCESS) {
        getManagerContext().getGuiManager().openWineScreen();
        disabled = PasswordUtil.checkAdminLogin(getManagerContext(), disabled);
      } else {
        switch (response) {
          case AuthenticationResponse.INCORRECT_OLD_PASSWORD:
            oldPasswordErrorLabel.setText(response.getMessage());
            oldPasswordErrorLabel.setVisible(true);
            oldPasswordField.getStyleClass().add("error-text-field");
            newPasswordField.getStyleClass().add("error-text-field");
            confirmNewPasswordField.getStyleClass().add("error-text-field");
            break;
          case AuthenticationResponse.OLD_PASSWORD_SAME_AS_NEW:
            newPasswordErrorLabel.setText(response.getMessage());
            newPasswordErrorLabel.setVisible(true);
            newPasswordField.getStyleClass().add("error-text-field");
            confirmNewPasswordField.getStyleClass().add("error-text-field");
            break;
          case AuthenticationResponse.SAME_AS_USERNAME:
            newPasswordErrorLabel.setText(response.getMessage());
            newPasswordErrorLabel.setVisible(true);
            newPasswordField.getStyleClass().add("error-text-field");
            confirmNewPasswordField.getStyleClass().add("error-text-field");
            break;
          case INVALID_PASSWORD:
            newPasswordField.getStyleClass().add("error-text-field");
            confirmNewPasswordField.getStyleClass().add("error-text-field");
            int passwordLength = newPassword.length();
            if (passwordLength < 8 || passwordLength > 30) {
              passwordError += AuthenticationResponse.INVALID_PASSWORD_LENGTH.getMessage() + "\n";
            }
            if (newPassword.matches(".*\\s.*")) {
              passwordError += AuthenticationResponse.INVALID_PASSWORD_CONTAINS_SPACES.getMessage();
            } else {
              if (!newPassword.matches(
                  "(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[!@#$%^&*.()\\-+={\\[\\]}]).*")) {
                passwordError +=
                    AuthenticationResponse.INVALID_PASSWORD_HEADER.getMessage();
                if (!newPassword.matches(".*[a-z].*")) {
                  passwordError +=
                      AuthenticationResponse.INVALID_PASSWORD_MISSING_LOWERCASE.getMessage();
                }
                if (!newPassword.matches(".*[A-Z].*")) {
                  passwordError +=
                      AuthenticationResponse.INVALID_PASSWORD_MISSING_UPPERCASE.getMessage();
                }
                if (!newPassword.matches("(.*[0-9].*)")) {
                  passwordError +=
                      AuthenticationResponse.INVALID_PASSWORD_MISSING_NUMBER.getMessage();
                }
                if (!newPassword.matches("(.*[!@#$%^&*.()\\-+={\\[\\]}].*)")) {
                  passwordError +=
                      AuthenticationResponse.INVALID_PASSWORD_MISSING_SPECIAL_CHAR.getMessage();
                }
              }
            }
            newPasswordErrorLabel.setText(passwordError);
            newPasswordErrorLabel.setVisible(true);
            break;
          case AuthenticationResponse.MISMATCHING_CONFIRMED_PASSWORD:
            confirmNewPasswordField.getStyleClass().add("error-text-field");
            confirmNewPasswordErrorLabel.setText(
                AuthenticationResponse.MISMATCHING_CONFIRMED_PASSWORD.getMessage());
            confirmNewPasswordErrorLabel.setVisible(true);
            break;
          default:
            break;
        }
      }
    } else {
      oldPasswordErrorLabel.setText("Please enter your old password");
      newPasswordErrorLabel.setText("Please enter a new password");
      confirmNewPasswordErrorLabel.setText("Please re-enter your new password");
      oldPasswordErrorLabel.setVisible(oldPasswordNull);
      newPasswordErrorLabel.setVisible(newPasswordNull);
      confirmNewPasswordErrorLabel.setVisible(confirmNewPasswordNull);
      if (oldPasswordNull) {
        oldPasswordField.getStyleClass().add("error-text-field");
      }
      if (newPasswordNull) {
        newPasswordField.getStyleClass().add("error-text-field");
      }
      if (confirmNewPasswordNull) {
        confirmNewPasswordField.getStyleClass().add("error-text-field");
      }
    }
  }

  private void resetFields() {
    oldPasswordField.getStyleClass().add("normal-text-field");
    oldPasswordField.getStyleClass().remove("error-text-field");
    newPasswordField.getStyleClass().add("normal-text-field");
    newPasswordField.getStyleClass().remove("error-text-field");
    confirmNewPasswordField.getStyleClass().add("normal-text-field");
    confirmNewPasswordField.getStyleClass().remove("error-text-field");
    oldPasswordErrorLabel.setVisible(false);
    newPasswordErrorLabel.setVisible(false);
    confirmNewPasswordErrorLabel.setVisible(false);
  }
}
