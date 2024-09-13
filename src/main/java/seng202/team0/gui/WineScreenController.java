package seng202.team0.gui;

import com.sun.javafx.webkit.WebConsoleListener;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.controlsfx.control.RangeSlider;
import seng202.team0.database.Wine;
import seng202.team0.managers.ManagerContext;

/**
 * Controller for the screen that displays wines
 */

public class WineScreenController extends Controller{

  private final Logger log = LogManager.getLogger(WineScreenController.class);

  @FXML
  TableView<Wine> tableView;

  @FXML
  AnchorPane filtersPane;

  @FXML
  WebView webView;

  private LeafletOSMController mapController;

  /**
   * Constructor
   *
   * @param managerContext manager context
   */
  public WineScreenController(ManagerContext managerContext) {
    super(managerContext);
  }

  private void openWineRange(int begin, int end) {

    tableView.setItems(managerContext.databaseManager.getWinesInRange(begin, end));
    tableView.setEditable(false);
    TableColumn<Wine, String> titleColumn = new TableColumn<>("Title");
    titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
    tableView.getColumns().add(titleColumn);

    TableColumn<Wine, String> varietyColumn = new TableColumn<>("Variety");
    varietyColumn.setCellValueFactory(new PropertyValueFactory<>("variety"));
    tableView.getColumns().add(varietyColumn);

    TableColumn<Wine, String> wineryColumn = new TableColumn<>("Winery");
    wineryColumn.setCellValueFactory(new PropertyValueFactory<>("winery"));
    tableView.getColumns().add(wineryColumn);

    TableColumn<Wine, String> regionColumn = new TableColumn<>("Region");
    regionColumn.setCellValueFactory(new PropertyValueFactory<>("region"));
    tableView.getColumns().add(regionColumn);

    TableColumn<Wine, String> descriptionColumn = new TableColumn<>("Description");
    descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
    tableView.getColumns().add(descriptionColumn);

    TableColumn<Wine, String> scoreColumn = new TableColumn<>("Score");
    scoreColumn.setCellValueFactory(new PropertyValueFactory<>("scorePercent"));
    tableView.getColumns().add(scoreColumn);

    TableColumn<Wine, Float> abvColumn = new TableColumn<>("ABV%");
    abvColumn.setCellValueFactory(new PropertyValueFactory<>("abv"));
    tableView.getColumns().add(abvColumn);

    TableColumn<Wine, Float> priceColumn = new TableColumn<>("NZD");
    priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
    tableView.getColumns().add(priceColumn);
  }

  /**
   * Called after the constructor for when fxml is loaded
   * <p>
   *   Gets, loads, and displays a table from a list of wines from the controller layer
   * </p>
   */
  @Override
  public void init() {
    // score slider
    createSlider(11, 365, 0, 100, 10);
    // abv slider
    createSlider(11, 445, 0, 100, 10);
    // price slider
    createSlider(11, 525, 0, 100, 10);
    openWineRange(0, 100);

    mapController = new LeafletOSMController(webView.getEngine());
    mapController.initMap();
  }

  /**
   * Creates a range slider element and displays it on the filters pane at the given layout coordinates
   * @param layoutX The layout X position
   * @param layoutY The layout Y position
   * @param min The minimum value displayed on the slider
   * @param max The maximum value displayed on the slider
   * @param blockIncrements The gap between tick marks
   */
  public void createSlider(int layoutX, int layoutY, int min, int max, int blockIncrements) {
    RangeSlider rangeSlider = new RangeSlider(min, max, min, max);
    rangeSlider.setLayoutX(layoutX);
    rangeSlider.setLayoutY(layoutY);
    rangeSlider.setPrefWidth(300);
    rangeSlider.setShowTickMarks(true);
    rangeSlider.setShowTickLabels(true);
    rangeSlider.setBlockIncrement(blockIncrements);
    rangeSlider.setSnapToPixel(true);
    // by default the font size matches the parent font size which is the filters title
    rangeSlider.setStyle("-fx-font-size: 15px;");
    filtersPane.getChildren().add(rangeSlider);
  }

}
