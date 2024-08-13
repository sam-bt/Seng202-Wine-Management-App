package seng202.team0.gui;

import javafx.fxml.FXML;
import seng202.team0.WinoManager;

public class TabChangerService {
  @FXML
  public static void onHome(WinoManager winoManager) {
    winoManager.launchHomeScreen();
  }
  @FXML
  public static void onWine(WinoManager winoManager) {
    winoManager.launchWineScreen();
  }
  @FXML
  public static void onLists(WinoManager winoManager) {
    winoManager.launchListsScreen();
  }
  @FXML
  public static void onVineyards(WinoManager winoManager) {
    winoManager.launchVineyardsScreen();
  }
  @FXML
  public static void onDatasets(WinoManager winoManager) {
    winoManager.launchDataSetsScreen();
  }
  @FXML
  public static void onConsumption(WinoManager winoManager) {
    winoManager.launchConsumptionCalculatorScreen();
  }
}
