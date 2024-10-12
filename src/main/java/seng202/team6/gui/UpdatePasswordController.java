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
  private Label updateMessageLabel;
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


  }

  @FXML
  private void onConfirm() {
    String username = getManagerContext().getAuthenticationManager().getAuthenticatedUsername();
    String oldPassword = oldPasswordField.getText();
    String newPassword = newPasswordField.getText();
    String confirmNewPassword = confirmNewPasswordField.getText();
    AuthenticationResponse response = getManagerContext().getAuthenticationManager().validateUpdate(
        username, oldPassword, newPassword, confirmNewPassword);
    if (response == AuthenticationResponse.PASSWORD_CHANGED_SUCCESS) {
      getManagerContext().getGuiManager().openWineScreen();
      disabled = PasswordUtil.checkAdminLogin(getManagerContext(), disabled);
    } else {
      updateMessageLabel.setStyle("-fx-text-fill: red");
      updateMessageLabel.setText(response.getMessage());
    }
  }
}
