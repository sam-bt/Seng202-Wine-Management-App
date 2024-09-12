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

  private static final String LOGIN_PATH = "login_info.csv";
  private ArrayList<String[]> loginInfo;

  private String validateLogin(String username,String password,ArrayList<String[]> loginInfo) {
    String result = Login.validateLogin(username,password,loginInfo);
    return result;
  }

  @FXML
  private void onConfirm() {
    // should validate that login file exists first, also to update message to prompt admin first time login
    System.out.println();
    Path filePath = Paths.get(LOGIN_PATH).toAbsolutePath();
    try {
      ArrayList<String[]> rows = ProcessCSV.getCSVRows(new File(String.valueOf(filePath)));

      String[] columnNames = rows.getFirst();
      loginInfo = rows;
      for (String[] row: loginInfo) {
        System.out.println(Arrays.toString(row));
      }
    } catch(Exception exception) {
      LogManager.getLogger(getClass())
          .error("Failed to read CSV file: {}", LOGIN_PATH, exception);
    }
    String username = usernameField.getText();
    String password = passwordField.getText();
    String validateResponse = validateLogin(username,password,loginInfo);
    if (validateResponse == "Success") {
      
    } else if (validateResponse == "AdminSuccess") {

    } else {
      loginMessageLabel.setStyle("-fx-text-fill: red");
      loginMessageLabel.setText(validateResponse);
    }
  }
}
