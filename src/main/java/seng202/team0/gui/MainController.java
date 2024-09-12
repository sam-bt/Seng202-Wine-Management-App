package seng202.team0.gui;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.util.Builder;
import seng202.team0.managers.ManagerContext;

/**
 * Main controller from where other scenes are embedded
 * @author Angus McDougall
 */
public class MainController extends Controller {

  @FXML
  private BorderPane borderPane;

  @FXML
  private AnchorPane pageContent;

  /**
   * Constructor
   *
   * @param managerContext manager context
   */
  public MainController(ManagerContext managerContext) {
    super(managerContext);
    // This is an ugly circular dependency. It is easier to resolve here
    managerContext.GUIManager.setMainController(this);
  }

  public void switchScene(String fxml, String title, Builder<?> builder) {
    try {
      FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
      // provide a custom Controller with parameters
      loader.setControllerFactory(param -> builder.build());
      Parent parent = loader.load();
      pageContent.getChildren().clear();
      pageContent.getChildren().add(parent);
      if(loader.getController() instanceof Controller controller){
        controller.init();
      }
      managerContext.GUIManager.setWindowTitle(title);
    } catch (IOException e) {
      System.err.println("Failed to load screen: " + fxml);
      e.printStackTrace();
    }
  }


  /**
   * Launches the data set screen.
   */
  @FXML
  public void openDataSetsScreen() {
    switchScene("/fxml/dataset_screen.fxml", "Manage Datasets", () -> new DataTableController(managerContext));
  }

  /**
   * Launches the home screen.
   */
  @FXML
  public void openHomeScreen() {
    switchScene("/fxml/home_screen.fxml", "Home", () -> new HomeScreenController(managerContext));
  }
  /**
   * Launches the wine screen.
   */
  @FXML
  public void openWineScreen() {
    switchScene("/fxml/wine_screen.fxml", "Wine Information", () -> new WineScreenController(managerContext));
  }
  /**
   * Launches the list screen.
   */
  @FXML
  public void openListScreen() {
    switchScene("/fxml/list_screen.fxml", "My Lists", () -> new WishlistController(managerContext));
  }
  /**
   * Launches the vineyard screen.
   */
  @FXML
  public void openVineyardsScreen() {
    switchScene("/fxml/vineyard_screen.fxml", "Vineyards",  () -> new VineyardScreenController(managerContext));
  }
  /**
   * Launches the consumption calculator screen.
   */
  @FXML
  public void openConsumptionCalculatorScreen() {
    switchScene("/fxml/consumption_calculator_screen.fxml", "Consumption Calculator",  () -> new ConsumptionCalculatorController(managerContext));
  }
  /**
   * Launches the login screen.
   */
  @FXML
  public void openLoginScreen() {
    switchScene("/fxml/login_screen.fxml", "Login", () -> new LoginController(managerContext));
  }

  /**
   * Launches the register screen
   */
  @FXML
  public void openRegisterScreen() {
    switchScene("/fxml/register_screen.fxml", "Register", () -> new RegisterController(managerContext));
  }
}
