package seng202.team0.gui;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
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

  /**
   * Constructor
   *
   * @param managerContext manager context
   */
  public MainController(ManagerContext managerContext) {
    super(managerContext);
  }

  public void switchScene(String fxml, Builder<?> builder) {
    try {
      FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
      // provide a custom Controller with parameters
      loader.setControllerFactory(param -> builder.build());
      Parent parent = loader.load();
      borderPane.setCenter(parent);
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
    switchScene("/fxml/dataset_screen.fxml", () -> new DataTableController(managerContext));
  }

  /**
   * Launches the home screen.
   */
  @FXML
  public void openHomeScreen() {
    switchScene("/fxml/home_screen.fxml", () -> new HomeScreenController(managerContext));
  }
  /**
   * Launches the wine screen.
   */
  @FXML
  public void openWineScreen() {
    switchScene("/fxml/wine_screen.fxml", () -> new WineScreenController(managerContext));
  }
  /**
   * Launches the list screen.
   */
  @FXML
  public void openListScreen() {
    switchScene("/fxml/list_screen.fxml", () -> new WishlistController(managerContext));
  }
  /**
   * Launches the vineyard screen.
   */
  @FXML
  public void openVineyardsScreen() {
    switchScene("/fxml/vineyard_screen.fxml", () -> new VineyardScreenController(managerContext));
  }
  /**
   * Launches the consumption calculator screen.
   */
  @FXML
  public void openConsumptionCalculatorScreen() {
    switchScene("/fxml/consumption_calculator_screen.fxml", () -> new ConsumptionCalculatorController(managerContext));
  }

}
