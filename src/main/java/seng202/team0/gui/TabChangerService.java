package seng202.team0.gui;

import javafx.fxml.FXML;
import seng202.team0.WinoManager;

public class TabChangerService {
  /**
   * Launches the home screen.
   *
   * @param winoManager the WinoManager instance
   */
  @FXML
  public static void onHome(WinoManager winoManager) {winoManager.launchHomeScreen();}
  /**
   * Launches the wine screen.
   *
   * @param winoManager the WinoManager instance
   */
  @FXML
  public static void onWine(WinoManager winoManager) {
    winoManager.launchWineScreen();
  }
  /**
   * Launches the list screen.
   *
   * @param winoManager the WinoManager instance
   */
  @FXML
  public static void onLists(WinoManager winoManager) {
    winoManager.launchListsScreen();
  }
  /**
   * Launches the vineyard screen.
   *
   * @param winoManager the WinoManager instance
   */
  @FXML
  public static void onVineyards(WinoManager winoManager) {
    winoManager.launchVineyardsScreen();
  }
  /**
   * Launches the data sets screen.
   *
   * @param winoManager the WinoManager instance
   */
  @FXML
  public static void onDatasets(WinoManager winoManager) {
    winoManager.launchDataSetsScreen();
  }
  /**
   * Launches the consumption calculator screen.
   *
   * @param winoManager the WinoManager instance
   */
  @FXML
  public static void onConsumption(WinoManager winoManager) {
    winoManager.launchConsumptionCalculatorScreen();
  }
}
