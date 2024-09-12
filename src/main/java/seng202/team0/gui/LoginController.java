package seng202.team0.gui;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.apache.logging.log4j.LogManager;
import seng202.team0.managers.ManagerContext;
import seng202.team0.util.Login;
import seng202.team0.util.ProcessCSV;

/**
 * Login Controller (MORE DETAIL HERE!)
 */
public class LoginController extends Controller{

  @FXML
  private TextField usernameField;

  @FXML
  private TextField passwordField;
  @FXML
  private Label loginMessageLabel;
  /**
   * Constructor
   *
   * @param managerContext manager context
   */
  public LoginController(ManagerContext managerContext) {
    super(managerContext);
  }

  private String validateLogin(String username,String password) {
    String result = Login.validateLogin(username,password);
    return result;
  }

  @FXML
  private void onConfirm() {

    String username = usernameField.getText();
    String password = passwordField.getText();
    String validateResponse = validateLogin(username,password);
    if (validateResponse == "Success") {

    } else if (validateResponse == "AdminSuccess") {

    } else {
      loginMessageLabel.setStyle("-fx-text-fill: red");
      loginMessageLabel.setText(validateResponse);
    }
  }
}
