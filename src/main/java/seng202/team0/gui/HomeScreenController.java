package seng202.team0.gui;


import javafx.fxml.FXML;
import seng202.team0.WinoManager;

public class HomeScreenController {

    /**
     * The WINO manager instance.
     */
    private final WinoManager winoManager;

    /**
     * Constructs the HomeScreenController.
     * @param manager The WINO manager instance.
     */
    HomeScreenController(WinoManager manager) {
        winoManager = manager;


    }
  /**
   * Launches the wine screen.
   */
      @FXML public void openWineScreen() {TabChangerService.onWine(winoManager);}
  /**
   * Launches the list screen.
   */
      @FXML public void openListScreen() {TabChangerService.onLists(winoManager);}
  /**
   * Launches the vineyard screen.
   */
      @FXML public void openVineyardsScreen() {TabChangerService.onVineyards(winoManager);}
  /**
   * Launches the data set screen.
   */
      @FXML public void openDataSetsScreen() {TabChangerService.onDatasets(winoManager);}
  /**
   * Launches the consumption calculator screen.
   */
      @FXML public void openConsumptionCalculatorScreen() {TabChangerService.onConsumption(winoManager);}

}
