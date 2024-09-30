package seng202.team6.gui;

import java.util.Comparator;
import java.util.Date;
import java.util.Objects;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import seng202.team6.managers.ManagerContext;
import seng202.team6.model.User;
import seng202.team6.model.WineDatePair;
import seng202.team6.model.WineList;

/**
 * Controller for the scene that shows wine consumption
 */
public class ConsumptionController extends Controller {

  @FXML
  VBox wineHistoryList;

  @FXML
  ProgressBar winesInPastWeekBar;

  @FXML
  Label winesInPastWeekLabel;

  @FXML
  VBox rootBox;

  static final float STD_DRINK_ETHANOL_VOLUME = 12.7f;

  /**
   * Chart for consumption.
   * <p>
   * The generic parameters for LineChart are checked at runtime and don't seem to be documented...
   * - X seems to be either Number or string - Y seems to be only Number
   * </p>
   */
  @FXML
  LineChart<String, Number> consumptionChart;

  /**
   * Constructor
   * @param managerContext manager context
   */
  public ConsumptionController(ManagerContext managerContext) {
    super(managerContext);
  }


  /**
   * Gets the history list for the current user
   *
   * @return history list
   */
  private WineList getHistoryList() {
    User user = managerContext.authenticationManager.getAuthenticatedUser();
    return managerContext.databaseManager.getWineListDAO().getAll(user)
        .stream()
        .filter(wineList -> Objects.equals(
            wineList.name(), "History")).findFirst().orElse(null);
  }

  /**
   * Gets the std drinks for a wine assuming 750 bottles and 12.7 ml for
   *
   * @param wineDatePair wine
   * @return std drinks for wine
   */
  private float getStdDrinks(WineDatePair wineDatePair) {
    return wineDatePair.wine().getAbv() / 100f * 750f / STD_DRINK_ETHANOL_VOLUME;
  }

  /**
   * Creates a wine widget
   *
   * @param pair wine-date pair to display
   * @return widget
   */
  private Node createWineWidget(WineDatePair pair) {
    HBox separator = new HBox();
    Label date = new Label(pair.date().toString());

    separator.getChildren().add(new Button());
    // anything above 20 is full strength
    double abvProgress = Math.min(1f, pair.wine().getAbv() / 20f);
    VBox abvSegment = new VBox();
    ProgressBar progressBar = new ProgressBar(abvProgress);
    abvSegment.getChildren()
        .add(new Label(pair.wine().getAbv() + "% ABV | " + (int) getStdDrinks(pair) + " Drinks"));
    abvSegment.getChildren().add(progressBar);
    abvSegment.setAlignment(Pos.CENTER);

    separator.getChildren().add(date);
    separator.getChildren().add(abvSegment);

    separator.setMinWidth(250);
    separator.setMaxHeight(50);

    return separator;
  }

  /**
   * Gets all the wines consumed in the past week
   *
   * @return all wines consumed in the past week
   */
  private ObservableList<WineDatePair> getPastWeekConsumption() {
    ObservableList<WineDatePair> wineHistory = managerContext.databaseManager.getAggregatedDAO()
        .getWinesMappedWithDatesFromList(getHistoryList());
    wineHistory.sort(Comparator.comparing(WineDatePair::date));
    long oneWeek = 1000 * 60 * 60 * 24 * 7;
    Date weekAgo = new Date(System.currentTimeMillis() - oneWeek);
    wineHistory.removeIf(wineDatePair -> wineDatePair.date().before(weekAgo));
    return wineHistory;
  }

  /**
   * Update the total consumption bar with std drinks for the past week
   */
  private void updateTotalConsumptionBar(ObservableList<WineDatePair> pastWeekConsumption) {
    float drinks = 0;
    for (WineDatePair pair : pastWeekConsumption) {
      drinks += getStdDrinks(pair);
    }
    // Recommended alcohol intakes are made up for the most part
    // WHO sets 0
    // We make up 10 std drinks a week
    float normalizedProgress = Math.min(1f, drinks / 10f);
    winesInPastWeekBar.setProgress(normalizedProgress);
    winesInPastWeekLabel.setText((drinks > 10f ? "10+" : (int) drinks) + " Drinks");
  }

  /**
   * Updates the history list with a given list
   *
   * @param pastWeekConsumption list of wine-date pairs to update
   */
  private void updateHistoryList(ObservableList<WineDatePair> pastWeekConsumption) {
    wineHistoryList.getChildren().clear();
    for (WineDatePair pair : pastWeekConsumption) {
      wineHistoryList.getChildren().add(createWineWidget(pair));
    }
  }

  /**
   * Updates the graph with a list of wines
   *
   * @param pastConsumption list of wines
   */
  private void updateConsumptionGraph(ObservableList<WineDatePair> pastConsumption) {
    consumptionChart.getData().clear();
    XYChart.Series<String, Number> series = new XYChart.Series<>();
    consumptionChart.getXAxis().setLabel("Date");
    consumptionChart.getYAxis().setLabel("Standard Drinks");
    series.setName("Drinks per day");
    for (WineDatePair pair : pastConsumption) {
      series.getData().add(new Data<>(pair.date() + "", getStdDrinks(pair)));
    }

    consumptionChart.getData().add(series);
  }


  /**
   * Initialize lists
   */
  @Override
  public void init() {
    ObservableList<WineDatePair> observableList = getPastWeekConsumption();
    updateHistoryList(observableList);
    updateTotalConsumptionBar(observableList);
    updateConsumptionGraph(managerContext.databaseManager.getAggregatedDAO()
        .getWinesMappedWithDatesFromList(getHistoryList()));
  }
}