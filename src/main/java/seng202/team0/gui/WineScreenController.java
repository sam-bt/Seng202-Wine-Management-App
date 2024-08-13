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

    @FXML public void openHomeScreen() {winoManager.launchHomeScreen();}
    @FXML public void openListScreen() {winoManager.launchHomeScreen();}
    @FXML public void openVineyardsScreen() {winoManager.launchHomeScreen();}
    @FXML public void openDataSetsScreen() {winoManager.launchHomeScreen();}
    @FXML public void openConsumptionCalculatorScreen() {winoManager.launchHomeScreen();}

//      @FXML public void openHomeScreen() {TabChangerService.onHome(winoManager);}
//      @FXML public void openListScreen() {TabChangerService.onLists(winoManager);}
//      @FXML public void openVineyardsScreen() {TabChangerService.onVineyards(winoManager);}
//      @FXML public void openDataSetsScreen() {TabChangerService.onDatasets(winoManager);}
//      @FXML public void openConsumptionCalculatorScreen() {TabChangerService.onConsumption(winoManager);


}
