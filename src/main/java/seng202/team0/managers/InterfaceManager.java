package seng202.team0.managers;

import javafx.util.Builder;
import seng202.team0.gui.ConsumptionCalculatorController;
import seng202.team0.gui.DataTableController;
import seng202.team0.gui.FXWrapper;
import seng202.team0.gui.HomeScreenController;
import seng202.team0.gui.VineyardScreenController;
import seng202.team0.gui.WineScreenController;
import seng202.team0.gui.WishlistController;

/**
 * Manager for interacting with the GUI
 * @author Angus McDougall
 */
public class InterfaceManager {

  private final FXWrapper wrapper;

  public InterfaceManager(FXWrapper wrapper) {
    this.wrapper = wrapper;
  }

  /**
   * Switches the current scene
   * @param fxmlPath path to the fxml to load
   * @param title title of the scene to display
   * @param builder builder to instantiate the controller
   */
  public void switchScene(String fxmlPath, String title, Builder<?> builder) {
    wrapper.loadScreen(fxmlPath, title, builder);
  }

  /**
   * Loads the home screen.
   *
   * @param managerContext manager context
   */
  public void launchHomeScreen(ManagerContext managerContext) {
    switchScene("/fxml/home_screen.fxml", "Home", () -> new HomeScreenController(managerContext));
  }

  /**
   * Loads the wine screen.
   *
   * @param managerContext manager context
   */
  public void launchWineScreen(ManagerContext managerContext) {
    switchScene("/fxml/wine_screen.fxml", "Wine Information",
        () -> new WineScreenController(managerContext));
  }

  /**
   * Loads the lists screen.
   *
   * @param managerContext manager context
   */
  public void launchListsScreen(ManagerContext managerContext) {
    switchScene("/fxml/list_screen.fxml", "My Lists", () -> new WishlistController(managerContext));
  }

  /**
   * Loads the vineyard screen.
   *
   * @param managerContext manager context
   */
  public void launchVineyardsScreen(ManagerContext managerContext) {
    switchScene("/fxml/vineyard_screen.fxml", "Vineyards",
        () -> new VineyardScreenController(managerContext));
  }

  /**
   * Loads the datasets screen.
   *
   * @param managerContext manager context
   */
  public void launchDataSetsScreen(ManagerContext managerContext) {
    switchScene("/fxml/dataset_screen.fxml", "Manage Data Sets",
        () -> new DataTableController(managerContext));
  }

  /**
   * Loads the consumption calculator screen.
   *
   * @param managerContext manager context
   */
  public void launchConsumptionCalculatorScreen(ManagerContext managerContext) {
    switchScene("/fxml/consumption_calculator_screen.fxml", "Consumption Calculator",
        () -> new ConsumptionCalculatorController(managerContext));
  }
}
