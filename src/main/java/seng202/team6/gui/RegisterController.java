package seng202.team6.gui;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import seng202.team6.managers.ManagerContext;
import seng202.team6.model.AuthenticationResponse;

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
  @FXML
  private Button createAccountButton;

  /**
   * Constructor
   *
   * @param managerContext manager context
   */
  public RegisterController(ManagerContext managerContext) {
    super(managerContext);
  }

  @FXML
  private void initialize() {
    createAccountButton.setOnKeyPressed(event -> {
      if (event.getCode() == KeyCode.TAB && !event.isShiftDown()) {
        usernameField.requestFocus();
        event.consume();
      }
    });
    Platform.runLater(() -> usernameField.requestFocus());
  }
  @FXML
  private void onConfirm() {

    String username = usernameField.getText();
    String password = passwordField.getText();
    String confirmPassword = confirmPasswordField.getText();
    AuthenticationResponse response = managerContext.authenticationManager.validateRegistration(
        username, password, confirmPassword);
    if (response == AuthenticationResponse.REGISTER_SUCCESS) {
      managerContext.GUIManager.mainController.openLoginScreen();
      managerContext.databaseManager.createList(username, "Favourites");
    } else {
      registerMessageLabel.setStyle("-fx-text-fill: red");
      registerMessageLabel.setText(response.getMessage());
    }
  }

}
