package seng202.team6.gui;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.util.Builder;
import seng202.team6.managers.ManagerContext;
import seng202.team6.service.AuthenticationService;

/**
 * Main controller from where other scenes are embedded
 */
public class MainController extends Controller {

  @FXML
  private AnchorPane page;

  @FXML
  private AnchorPane pageContent;

  @FXML
  private Button wineScreenButton;

  @FXML
  private Button listScreenButton;

  @FXML
  private Button dataSetsScreenButton;

  @FXML
  private Button adminScreenButton;

  @FXML
  private Button loginButton;

  @FXML
  private Button registerButton;
  @FXML
  private HBox navBarBox;

  private final AuthenticationService authenticationService;

  private boolean disabled = false;

  /**
   * Constructor
   *
   * @param managerContext manager context
   */
  public MainController(ManagerContext managerContext) {
    super(managerContext);
    authenticationService = new AuthenticationService(managerContext.databaseManager);

    // This is an ugly circular dependency. It is easier to resolve here
    managerContext.GUIManager.setMainController(this);
  }

  public void initialize() {
    adminScreenButton.setVisible(false);
    dataSetsScreenButton.setVisible(false);
    navBarBox.getChildren().remove(listScreenButton);
    navBarBox.getChildren().add(3, listScreenButton);
    listScreenButton.setVisible(false);
    dataSetsScreenButton.setVisible(false);
    openWineScreen();
  }

  public void onLogin() {
    if (authenticationService.isAuthenticated()) {
      adminScreenButton.setVisible(authenticationService.isAdmin());
      dataSetsScreenButton.setVisible(authenticationService.isAdmin());
      loginButton.setText("Settings");
      registerButton.setText("Logout");

      navBarBox.getChildren().remove(listScreenButton);
      navBarBox.getChildren().add(1, listScreenButton);
      listScreenButton.setVisible(true);
      dataSetsScreenButton.setVisible(true);

      loginButton.setOnMouseClicked(event -> openSettingsScreen());
      registerButton.setOnMouseClicked(event -> logout());
    } else {
      adminScreenButton.setVisible(false);
      dataSetsScreenButton.setVisible(false);
    }
  }

  public void setDisable(Boolean status) {
    disabled = status;
    wineScreenButton.setDisable(status);
    listScreenButton.setDisable(status);
    dataSetsScreenButton.setDisable(status);
    adminScreenButton.setDisable(status);
  }

  public void switchScene(String fxml, String title, Builder<?> builder) {
    try {
      FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
      // provide a custom Controller with parameters
      loader.setControllerFactory(param -> builder.build());
      Parent parent = loader.load();
      pageContent.getChildren().clear();
      pageContent.getChildren().add(parent);
      if (loader.getController() instanceof Controller controller) {
        controller.init();
      }
      managerContext.GUIManager.setWindowTitle(title);
    } catch (IOException e) {
      System.err.println("Failed to load screen: " + fxml);
    }
  }

  public void logout() {
    authenticationService.logout();
    loginButton.setText("Login");
    registerButton.setText("Register");
    loginButton.setOnMouseClicked(event -> openLoginScreen());
    registerButton.setOnMouseClicked(event -> openRegisterScreen());
    adminScreenButton.setVisible(false);
    dataSetsScreenButton.setVisible(false);

    navBarBox.getChildren().remove(listScreenButton);
    navBarBox.getChildren().add(3, listScreenButton);
    listScreenButton.setVisible(false);

    openWineScreen();
  }

  public boolean isDisabled() {
    return disabled;
  }

  /**
   * Launches the data set screen.
   */
  @FXML
  public void openDataSetsScreen() {
    switchScene("/fxml/dataset_import_screen.fxml", "Manage Datasets",
        () -> new DatasetImportController(managerContext));
  }

  /**
   * Launches the wine screen.
   */
  @FXML
  public void openWineScreen() {
    switchScene("/fxml/wine_screen.fxml", "Wine Information",
        () -> new WineScreenController(managerContext, authenticationService));
  }

  /**
   * Launches the list screen.
   */
  @FXML
  public void openListScreen() {
    switchScene("/fxml/list_screen.fxml", "My Lists",
        () -> new ListScreenController(managerContext, authenticationService));
  }

  /**
   * Launches the login screen.
   */
  @FXML
  public void openLoginScreen() {
    switchScene("/fxml/login_screen.fxml", "Login", () -> new LoginController(managerContext, authenticationService));
  }

  /**
   * Launches the register screen
   */
  @FXML
  public void openRegisterScreen() {
    switchScene("/fxml/register_screen.fxml", "Register",
        () -> new RegisterController(managerContext, authenticationService));
  }

  @FXML
  public void openAdminScreen() {
    switchScene("/fxml/admin_screen.fxml", "Register", () -> new AdminController(managerContext));
  }

  @FXML
  public void openSettingsScreen() {
    switchScene("/fxml/settings_screen.fxml", "Register",
        () -> new SettingsController(managerContext));
  }

  @FXML
  public void openUpdatePasswordScreen() {
    switchScene("/fxml/update_password_screen.fxml", "Register",
        () -> new UpdatePasswordController(managerContext, authenticationService));
  }

  public void setWholePageInteractable(boolean interactable) {
    page.setDisable(!interactable);
  }
}
