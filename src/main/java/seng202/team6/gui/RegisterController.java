package seng202.team6.gui;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import seng202.team6.managers.ManagerContext;
import seng202.team6.model.AuthenticationResponse;
import seng202.team6.service.AuthenticationService;

/**
 * Register Controller
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

  private final AuthenticationService authenticationService;

  /**
   * Constructor
   *
   * @param managerContext manager context
   */
  public RegisterController(ManagerContext managerContext,
      AuthenticationService authenticationService) {
    super(managerContext);
    this.authenticationService = authenticationService;
  }

  @FXML
  private void onConfirm() {

    String username = usernameField.getText();
    String password = passwordField.getText();
    String confirmPassword = confirmPasswordField.getText();
    AuthenticationResponse response = authenticationService.validateRegistration(username, password, confirmPassword);
    if (response == AuthenticationResponse.REGISTER_SUCCESS) {
      managerContext.GUIManager.mainController.openLoginScreen();
      managerContext.databaseManager.createList(username, "Favourites");
      managerContext.databaseManager.createList(username, "History");
    } else {
      registerMessageLabel.setStyle("-fx-text-fill: red");
      registerMessageLabel.setText(response.getMessage());
    }
  }

}
