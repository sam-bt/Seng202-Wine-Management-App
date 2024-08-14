package seng202.team0.gui;

import javafx.fxml.FXML;
import seng202.team0.WinoManager;
import seng202.team0.managers.ManagerContext;

// TODO change the name to listScreenController rather than wishlist to fit other conventions?

/**
 * Wishlist Controller (MORE DETAIL HERE!)
 */
public class WishlistController extends Controller{

  WinoManager winoManager;

  /**
   * Constructor
   *
   * @param winoManager Manager context (MORE DETAIL REQUIRED HERE!)
   */
  public WishlistController(WinoManager winoManager) {
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
  /**
   * Launches the home screen.
   */
  @FXML public void openHomeScreen() {TabChangerService.onHome(winoManager);}
  /**
   * Launches the wine screen.
   */
  @FXML public void openWineScreen() {TabChangerService.onWine(winoManager);}
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
