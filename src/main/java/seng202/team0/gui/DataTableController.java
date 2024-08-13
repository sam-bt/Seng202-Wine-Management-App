package seng202.team0.gui;

import javafx.fxml.FXML;
import seng202.team0.WinoManager;
import seng202.team0.managers.ManagerContext;

/**
 * Data Table Controller (MORE DETAIL HERE!)
 */
public class DataTableController extends Controller{

  private final WinoManager winoManager;

  /**
   * Constructor
   *
   * @param winoManager Manager context (MORE DETAIL REQUIRED HERE!)
   */
  public DataTableController(WinoManager winoManager) {
    super(winoManager);
    this.winoManager = winoManager;
    //TODO Implement me!
  }

  /**
   * Should be called whenever a controller is popped from the stack
   *
   * @return
   */
  @Override
  public Object onPopped() {
    return null;
    //TODO Implement me!
  }

  /**
   * Should be called when the child of the controller is popped
   *
   * @param obj
   */
  @Override
  public void onChildContextPopped(Object obj) {
    //TODO Implement me!
  }

  @FXML
  public void openHomeScreen() {TabChangerService.onHome(winoManager);}
  @FXML
  public void openWineScreen() {TabChangerService.onWine(winoManager);}
  @FXML public void openListScreen() {TabChangerService.onLists(winoManager);}
  @FXML public void openVineyardsScreen() {TabChangerService.onVineyards(winoManager);}
  @FXML public void openConsumptionCalculatorScreen() {TabChangerService.onConsumption(winoManager);}


}
