package seng202.team6.gui;

import java.util.Comparator;
import java.util.Objects;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.Background;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javax.swing.GroupLayout.Alignment;
import seng202.team6.managers.ManagerContext;
import seng202.team6.model.WineDatePair;
import seng202.team6.model.WineList;

/**
 * Controller for the scene that shows wine consumption
 */
public class ConsumptionController extends Controller {
  @FXML
  VBox wineHistoryList;


  public ConsumptionController(ManagerContext managerContext) {
    super(managerContext);
  }


  /**
   * Gets the history list for the current user
   * @return history list
   */
  private WineList getHistoryList() {
    return managerContext.databaseManager.getUserLists(managerContext.authenticationManager.getAuthenticatedUsername()).stream().filter(wineList -> Objects.equals(
        wineList.name(), "History")).findFirst().orElse(null);
  }


  private Node createWineView(WineDatePair pair) {
    HBox separator = new HBox();
    Label date = new Label(pair.date().toString());

    separator.getChildren().add(new Button());
    // anything above 20 is full strength
    double abvProgress = Math.min(1f, pair.wine().getAbv() / 20f);

    VBox abvSegment = new VBox();
    ProgressBar progressBar = new ProgressBar(abvProgress);
    abvSegment.getChildren().add(new Label(pair.wine().getAbv() + "%"));
    abvSegment.getChildren().add(progressBar);
    abvSegment.setAlignment(Pos.CENTER);

    separator.getChildren().add(date);
    separator.getChildren().add(abvSegment);

    separator.setMinWidth(250);
    separator.setMaxHeight(50);

    separator.setStyle("@../css/global.css");
    return separator;
  }

  private void updateHistoryList() {
    wineHistoryList.getChildren().clear();
    ObservableList<WineDatePair> wineHistory = managerContext.databaseManager.getWineDatesInList(getHistoryList());
    wineHistory.sort(Comparator.comparing(WineDatePair::date));
    for(WineDatePair pair : wineHistory) {
      wineHistoryList.getChildren().add(createWineView(pair));
    }
  }



  /**
   * Initialize lists
   */
  @Override
  public void init() {
    updateHistoryList();
  }
}