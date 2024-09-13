package seng202.team0.gui;

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

import java.util.HashSet;
import java.util.Set;
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
    TextField titleTextField;

    private RangeSlider scoreSlider;

    private RangeSlider abvSlider;

    private RangeSlider priceSlider;

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

        // Update filter checkboxes
        Set<String> winerySet = new HashSet<>();
        Set<String> countrySet = new HashSet<>();
        for (Wine w : wines) {
            winerySet.add(w.getWinery());
            countrySet.add(w.getCountry());
        }

        //Clear old list data
        wineryTextField.getEntries().clear();
        countryTextField.getEntries().clear();

        // Convert to observable list and set data
        wineryTextField.getEntries().addAll(winerySet);
        countryTextField.getEntries().addAll(countrySet);
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
        wineryTextField.setText(null);
        countryTextField.setText(null);
        titleTextField.setText("");

        // Update wines
        openWineRange(0, 100, null);
    }

}
