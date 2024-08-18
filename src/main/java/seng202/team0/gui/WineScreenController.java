package seng202.team0.gui;

import javafx.fxml.FXML;
import seng202.team0.managers.ManagerContext;

/**
 * Wine Screen Controller (MORE DETAIL HERE!)
 */

public class WineScreenController extends Controller{

  /**
   * Constructor
   *
   * @param managerContext manager context
   */
  public WineScreenController(ManagerContext managerContext) {
    super(managerContext);
  }


  /**
   * Launches the data set screen.
   */
  @FXML
  public void openDataSetsScreen() {
    managerContext.GUIManager.launchDataSetsScreen(managerContext);
  }
  /**
   * Launches the home screen.
   */
  @FXML
  public void openHomeScreen() {
    managerContext.GUIManager.launchHomeScreen(managerContext);
  }

  /**
   * Launches the wine screen.
   */
  @FXML
  public void openWineScreen() {
    managerContext.GUIManager.launchWineScreen(managerContext);
  }
  /**
   * Launches the list screen.
   */
  @FXML
  public void openListScreen() {
    managerContext.GUIManager.launchListsScreen(managerContext);
  }
  /**
   * Launches the vineyard screen.
   */
  @FXML
  public void openVineyardsScreen() {
    managerContext.GUIManager.launchVineyardsScreen(managerContext);
  }
  /**
   * Launches the consumption calculator screen.
   */
  @FXML
  public void openConsumptionCalculatorScreen() {
    managerContext.GUIManager.launchConsumptionCalculatorScreen(managerContext);
  }


}
