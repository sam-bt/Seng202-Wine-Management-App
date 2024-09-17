package seng202.team6.gui;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import seng202.team6.managers.ManagerContext;
import seng202.team6.model.AuthenticationResponse;
import seng202.team6.service.AuthenticationService;

public class UpdatePasswordController extends Controller {

  @FXML
  private TextField oldPasswordField;
  @FXML
  private TextField newPasswordField;
  @FXML
  private Label loginMessageLabel;
  @FXML
  private TitledPane titlePane;

  private final AuthenticationService authenticationService;

  private boolean disabled;

  /**
   * Constructor
   *
   * @param managerContext manager context
   */
  public UpdatePasswordController(ManagerContext managerContext,
      AuthenticationService authenticationService) {
    super(managerContext);
    this.authenticationService = authenticationService;
  }

  public void initialize() {
    disabled = managerContext.GUIManager.mainController.isDisabled();
    if (disabled) {
      titlePane.setText("First time admin login, please change password");
    }
  }

  @FXML
  private void onConfirm() {
    String username = authenticationService.getAuthenticatedUsername();
    String oldPassword = oldPasswordField.getText();
    String newPassword = newPasswordField.getText();
    AuthenticationResponse response = authenticationService.validateUpdate(username, oldPassword, newPassword);
    if (response == AuthenticationResponse.PASSWORD_CHANGED_SUCCESS) {
      managerContext.GUIManager.mainController.openWineScreen();
    } else {
      loginMessageLabel.setStyle("-fx-text-fill: red");
      loginMessageLabel.setText(response.getMessage());
    }
  }
}
