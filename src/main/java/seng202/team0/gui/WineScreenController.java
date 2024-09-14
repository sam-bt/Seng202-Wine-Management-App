package seng202.team0.gui;

import java.util.HashSet;
import java.util.Set;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import org.controlsfx.control.RangeSlider;
import seng202.team0.database.Wine;
import seng202.team0.managers.ManagerContext;
import seng202.team0.util.Filters;

/**
 * Controller for the screen that displays wines
 */

public class WineScreenController extends Controller {

  @FXML
  TableView<Wine> tableView;

  @FXML
  AnchorPane filtersPane;

  @FXML
  Button applyFiltersButton;

  @FXML
  Button resetFiltersButton;

  @FXML
  AutoCompletionTextField countryTextField;

  @FXML
  AutoCompletionTextField wineryTextField;

  @FXML
  AutoCompletionTextField colorTextField;

  @FXML
  TextField titleTextField;

  private RangeSlider scoreSlider;

  private RangeSlider abvSlider;

  private RangeSlider priceSlider;

  private RangeSlider vintageSlider;

  /**
   * Constructor
   *
   * @param managerContext manager context
   */
  public WineScreenController(ManagerContext managerContext) {
    super(managerContext);
  }

  private void openWineRange(int begin, int end, Filters filters) {
    // Clear existing data
    tableView.getItems().clear();

    // Check if filters exist
    ObservableList<Wine> wines;
    if (filters != null) {
      wines = managerContext.databaseManager.getWinesInRange(begin, end, filters);
    } else {
      wines = managerContext.databaseManager.getWinesInRange(begin, end);
    }

    // Set fetched data
    tableView.setItems(wines);

    // Only update autocomplete if NOT filtering
    if (filters == null) {
      // Auto Complete boxes and range sliders
      // Update filter checkboxes
      Set<String> winerySet = new HashSet<>();
      Set<String> countrySet = new HashSet<>();
      Set<String> colorSet = new HashSet<>();
      int minVintage = 10000;
      int maxVintage = 0;
      double maxScore = 0;
      double minScore = 100;
      double minAbv = 100;
      double maxAbv = 0;
      double minPrice = 10000;
      double maxPrice = 0;
      for (Wine w : wines) {
        winerySet.add(w.getWinery());
        countrySet.add(w.getCountry());
        colorSet.add(w.getColor());

        // Min and Max Values
        if (w.getScorePercent() > maxScore) {
          maxScore = w.getScorePercent();
        }

        if (w.getScorePercent() < minScore) {
          minScore = w.getScorePercent();
        }

        if (w.getVintage() > maxVintage) {
          maxVintage = w.getVintage();
        }

        if (w.getVintage() < minVintage) {
          minVintage = w.getVintage();
        }

        if (w.getAbv() > maxAbv) {
          maxAbv = w.getAbv();
        }

        if (w.getAbv() < minAbv) {
          minAbv = w.getAbv();
        }

        if (w.getPrice() > maxPrice) {
          maxPrice = w.getPrice();
        }

        if (w.getPrice() < minPrice) {
          minPrice = w.getPrice();
        }
      }

      // Clear old list data
      wineryTextField.getEntries().clear();
      countryTextField.getEntries().clear();
      colorTextField.getEntries().clear();

      // Set data for auto complete
      wineryTextField.getEntries().addAll(winerySet);
      countryTextField.getEntries().addAll(countrySet);
      colorTextField.getEntries().addAll(colorSet);

      // Following entries are commented out as we currently don't have data for them
      // Set min and max ranges
      scoreSlider.setMin(minScore);
      scoreSlider.setMax(maxScore);
      vintageSlider.setMin(minVintage);
      vintageSlider.setMax(maxVintage);
      //abvSlider.setMin(minAbv);
      //abvSlider.setMax(maxAbv);
      //priceSlider.setMin(minPrice);
      //priceSlider.setMax(maxPrice);

      // Set slider handles to min and max values
      // Fixes a graphic issue where the slider values don't change with the min and max adjustments
      scoreSlider.setHighValue(scoreSlider.getMax());
      scoreSlider.setLowValue(scoreSlider.getMin());
      vintageSlider.setHighValue(vintageSlider.getMax());
      vintageSlider.setLowValue(vintageSlider.getMin());
      //abvSlider.setHighValue(abvSlider.getMax());
      //abvSlider.setLowValue(abvSlider.getMin());
      //priceSlider.setHighValue(priceSlider.getMax());
      //priceSlider.setLowValue(priceSlider.getMin());
    }

  }

  public void setupTableColumns() {
    // Clear any existing cols
    tableView.getColumns().clear();

    // Create and config cols
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

    TableColumn<Wine, String> colorColumn = new TableColumn<>("Color");
    colorColumn.setCellValueFactory(new PropertyValueFactory<>("color"));
    tableView.getColumns().add(colorColumn);

    TableColumn<Wine, String> vintageColumn = new TableColumn<>("Vintage");
    vintageColumn.setCellValueFactory(new PropertyValueFactory<>("vintage"));
    tableView.getColumns().add(vintageColumn);

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
   * Gets, loads, and displays a table from a list of wines from the controller layer
   * </p>
   */
  @Override
  public void init() {
    this.vintageSlider = createSlider(11, 290, 0, 100, 10);
    this.scoreSlider = createSlider(11, 365, 0, 100, 10);
    this.abvSlider = createSlider(11, 445, 0, 100, 10);
    this.priceSlider = createSlider(11, 525, 0, 100, 10);

    // Set button functions
    applyFiltersButton.setOnAction(event -> onApplyFiltersButtonPressed());
    resetFiltersButton.setOnAction(event -> onResetFiltersButtonPressed());

    setupTableColumns();
    openWineRange(0, 100, null);
  }

  /**
   * Creates a range slider element and displays it on the filters pane at the given layout
   * coordinates
   *
   * @param layoutX         The layout X position
   * @param layoutY         The layout Y position
   * @param min             The minimum value displayed on the slider
   * @param max             The maximum value displayed on the slider
   * @param blockIncrements The gap between tick marks
   */
  public RangeSlider createSlider(int layoutX, int layoutY, int min, int max, int blockIncrements) {
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
    return rangeSlider;
  }


  /**
   * Is called when the apply button is pressed<br> Updates table with filtered data
   */
  public void onApplyFiltersButtonPressed() {
    Filters filters = new Filters(
        titleTextField.getText(),
        countryTextField.getText(),
        wineryTextField.getText(),
        colorTextField.getText(),
        (int) vintageSlider.getLowValue(),
        (int) vintageSlider.getHighValue(),
        scoreSlider.getLowValue(),
        scoreSlider.getHighValue(),
        abvSlider.getLowValue(),
        abvSlider.getHighValue(),
        priceSlider.getLowValue(),
        priceSlider.getHighValue()
    );
    openWineRange(0, 100, filters);
  }

  public void onResetFiltersButtonPressed() {
    // Reset all parameters
    priceSlider.setHighValue(priceSlider.getMax());
    priceSlider.setLowValue(priceSlider.getMin());
    scoreSlider.setHighValue(scoreSlider.getMax());
    scoreSlider.setLowValue(scoreSlider.getMin());
    abvSlider.setHighValue(abvSlider.getMax());
    abvSlider.setLowValue(abvSlider.getMin());
    vintageSlider.setHighValue(vintageSlider.getMax());
    vintageSlider.setLowValue(vintageSlider.getMin());
    wineryTextField.setText("");
    countryTextField.setText("");
    titleTextField.setText("");
    colorTextField.setText("");

    // Update wines
    openWineRange(0, 100, null);
  }

}
