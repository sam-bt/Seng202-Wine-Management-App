package seng202.team0.gui;

import javafx.fxml.FXML;
import seng202.team0.WinoManager;
import seng202.team0.managers.ManagerContext;

/**
 * Wine Screen Controller (MORE DETAIL HERE!)
 */

public class WineScreenController extends Controller{
  private final WinoManager winoManager;

  /**
   * Constructor
   *
   * @param manager Manager context (MORE DETAIL REQUIRED HERE!)
   */
  public WineScreenController(WinoManager manager) {
      super(manager);
      winoManager = manager;

  }

  @Override
  public Object onPopped() {
    return null;
  }

  @Override
  public void onChildContextPopped(Object obj) {
  }
  /**
   * Launches the home screen.
   */
      @FXML public void openHomeScreen() {TabChangerService.onHome(winoManager);}
  /**
   * Launches the list screen.
   */
      @FXML public void openListScreen() {TabChangerService.onLists(winoManager);}
  /**
   * Launches the vineyard screen.
   */
      @FXML public void openVineyardsScreen() {TabChangerService.onVineyards(winoManager);}
  /**
   * Launches the data sets screen.
   */
  @FXML public void openDataSetsScreen() {TabChangerService.onDatasets(winoManager);}
  /**
   * Launches the consumption calculator screen.
   */
      @FXML public void openConsumptionCalculatorScreen() {TabChangerService.onConsumption(winoManager);}


}
