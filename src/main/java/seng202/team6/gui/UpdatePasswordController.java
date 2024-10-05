package seng202.team6.gui;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.input.KeyCode;
import seng202.team6.managers.ManagerContext;
import seng202.team6.model.AuthenticationResponse;

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
   * Constructor
   *
   * @param managerContext manager context
   */
  public UpdatePasswordController(ManagerContext managerContext) {
    super(managerContext);
  }

  public void initialize() {
    disabled = managerContext.getGuiManager().mainController.isDisabled();
    if (disabled) {
      titledPane.setText("First time admin login, please change password");
    }
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
    String username = managerContext.getAuthenticationManager().getAuthenticatedUsername();
    String oldPassword = oldPasswordField.getText();
    String newPassword = newPasswordField.getText();
    String confirmNewPassword = confirmNewPasswordField.getText();
    AuthenticationResponse response = managerContext.getAuthenticationManager().validateUpdate(
        username, oldPassword, newPassword, confirmNewPassword);
    if (response == AuthenticationResponse.PASSWORD_CHANGED_SUCCESS) {
      managerContext.getGuiManager().mainController.openWineScreen();
      if (managerContext.getAuthenticationManager().isAdminFirstLogin()) {
        managerContext.getGuiManager().mainController.onLogin();
        managerContext.getGuiManager().mainController.setDisable(false);
        managerContext.getAuthenticationManager().setAdminFirstLogin(false);
        disabled = true;
      }
    } else {
      updateMessageLabel.setStyle("-fx-text-fill: red");
      updateMessageLabel.setText(response.getMessage());
    }
  }
}
