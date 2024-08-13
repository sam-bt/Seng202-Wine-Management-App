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

      @FXML public void openWineScreen() {TabChangerService.onWine(winoManager);}
      @FXML public void openListScreen() {TabChangerService.onLists(winoManager);}
      @FXML public void openVineyardsScreen() {TabChangerService.onVineyards(winoManager);}
      @FXML public void openDataSetsScreen() {TabChangerService.onDatasets(winoManager);}
      @FXML public void openConsumptionCalculatorScreen() {TabChangerService.onConsumption(winoManager);}

}
