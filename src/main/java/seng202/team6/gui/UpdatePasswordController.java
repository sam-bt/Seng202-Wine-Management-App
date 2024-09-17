package seng202.team6.gui;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import seng202.team6.managers.ManagerContext;
import seng202.team6.service.UserService;

public class UpdatePasswordController extends Controller {

  @FXML
  private TextField oldPasswordField;
  @FXML
  private TextField newPasswordField;
  @FXML
  private Label loginMessageLabel;
  @FXML
  private TitledPane titlePane;

  private boolean disabled;

  /**
   * Constructor
   *
   * @param managerContext manager context
   */
  public UpdatePasswordController(ManagerContext managerContext) {
    super(managerContext);
  }

  private String validateUpdate(String username, String oldPassword, String newPassword) {
    String result = UserService.validateUpdate(username, oldPassword, newPassword, managerContext);
    return result;
  }

  public void initialize() {
    disabled = managerContext.GUIManager.mainController.isDisabled();
    System.out.println(disabled);
    if (disabled) {
      titlePane.setText("First time admin login, please change password");
    }
  }

  @FXML
  private void onConfirm() {
    String username = managerContext.authenticationManager.getUsername();
    String oldPassword = oldPasswordField.getText();
    String newPassword = newPasswordField.getText();
    String validateResponse = validateUpdate(username, oldPassword, newPassword);
    if (validateResponse.equals("Success")) {
      // todo signify success somehow
      if (disabled) {
        managerContext.authenticationManager.setAuthenticated(true);
        managerContext.authenticationManager.setAdmin(true);
        managerContext.GUIManager.mainController.setDisable(false);
        managerContext.GUIManager.mainController.onLogin();
      }
      managerContext.GUIManager.mainController.openWineScreen();
    } else {
      loginMessageLabel.setStyle("-fx-text-fill: red");
      loginMessageLabel.setText(validateResponse);
    }
  }
}
