package seng202.team6.gui;

import java.util.Set;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.TilePane;
import javafx.scene.web.WebView;
import javafx.util.StringConverter;
import javafx.util.converter.DefaultStringConverter;
import javafx.util.converter.FloatStringConverter;
import javafx.util.converter.IntegerStringConverter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.controlsfx.control.RangeSlider;
import seng202.team6.gui.controls.AutoCompletionTextField;
import seng202.team6.gui.controls.WineCard;
import seng202.team6.managers.ManagerContext;
import seng202.team6.model.Wine;
import seng202.team6.model.WineFilters;
import seng202.team6.service.PageService;
import seng202.team6.util.YearStringConverter;

/**
 * Controller for the screen that displays wines.
 */

public class WineScreenController extends Controller {

  private final Logger log = LogManager.getLogger(WineScreenController.class);
  private final PageService pageService = new PageService(100);
  public TabPane tabPane;
  public Button prevPageButtonSimpleView;
  public TextField pageNumberTextFieldSimpleView;
  public Label maxPageNumberSimpleView;
  public Button nextPageButtonSimpleView;
  @FXML
  TableView<Wine> tableView;
  @FXML
  AnchorPane filtersPane;
  @FXML
  WebView webView;
  @FXML
  Button applyFiltersButton;
  @FXML
  Button resetFiltersButton;
  AutoCompletionTextField countryTextField;
  AutoCompletionTextField wineryTextField;
  AutoCompletionTextField colorTextField;
  @FXML
  TextField titleTextField;
  @FXML
  private TilePane winesViewContainer;
  private RangeSlider scoreSlider;
  private RangeSlider abvSlider;
  private RangeSlider priceSlider;
  private RangeSlider vintageSlider;
  private LeafletOsmController mapController;

  @FXML
  private Button nextPageButtonRawViewer;

  @FXML
  private Button prevPageButtonRawViewer;

  @FXML
  private TextField pageNumberTextFieldRawViewer;

  @FXML
  private WineFilters currentFilters;

  @FXML
  private Label maxPageNumberRawViewer;


  /**
   * Constructor.
   *
   * @param managerContext manager context
   */
  public WineScreenController(ManagerContext managerContext) {
    super(managerContext);
  }

  /**
   * Opens a page of wines from the database according to filters.
   *
   * @param filters list of filters
   */
  private void openWineRange(WineFilters filters) {
    // Clear existing data
    tableView.getItems().clear();

    // Calculate range of data to get
    int begin = this.pageService.getMinRange();
    int end = this.pageService.getMaxRange();

    // Check if filters exist
    ObservableList<Wine> wines = managerContext.getDatabaseManager().getWineDao()
        .getAllInRange(begin, end, filters);

    // send the wines to the map if they have a geo location
    mapController.runOrQueueWhenReady(() -> {
      mapController.clearWineMarkers();
      mapController.clearHeatmap();
      wines.stream()
          .filter(wine -> wine.getGeoLocation() != null)
          .forEach(mapController::addWineMarker);
    });

    winesViewContainer.getChildren().clear();
    wines.forEach(this::createWineCard);

    // Set fetched data to the table
    tableView.setItems(wines);
  }

  /**
   * Sets up the table columns.
   */
  public void setupTableColumns() {
    // Clear any existing cols
    tableView.getColumns().clear();

    final StringConverter<String> stringConverter = new DefaultStringConverter();
    final StringConverter<Integer> intConverter = new IntegerStringConverter();
    final StringConverter<Float> floatConverter = new FloatStringConverter();

    // Create and config cols
    tableView.setEditable(true);

    final TableColumn<Wine, String> titleColumn = new TableColumn<>("Title");
    final TableColumn<Wine, String> varietyColumn = new TableColumn<>("Variety");
    final TableColumn<Wine, String> wineryColumn = new TableColumn<>("Winery");
    final TableColumn<Wine, String> regionColumn = new TableColumn<>("Region");
    final TableColumn<Wine, String> colorColumn = new TableColumn<>("Color");
    final TableColumn<Wine, Integer> vintageColumn = new TableColumn<>("Vintage");
    final TableColumn<Wine, Integer> scoreColumn = new TableColumn<>("Score");
    final TableColumn<Wine, Float> abvColumn = new TableColumn<>("ABV%");
    final TableColumn<Wine, Float> priceColumn = new TableColumn<>("NZD");

    titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
    varietyColumn.setCellValueFactory(new PropertyValueFactory<>("variety"));
    wineryColumn.setCellValueFactory(new PropertyValueFactory<>("winery"));
    regionColumn.setCellValueFactory(new PropertyValueFactory<>("region"));
    colorColumn.setCellValueFactory(new PropertyValueFactory<>("color"));
    vintageColumn.setCellValueFactory(new PropertyValueFactory<>("vintage"));
    scoreColumn.setCellValueFactory(new PropertyValueFactory<>("scorePercent"));
    abvColumn.setCellValueFactory(new PropertyValueFactory<>("abv"));
    priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));

    // Enable editing if admin
    if (managerContext.getAuthenticationManager().isAdmin()) {
      titleColumn.setCellFactory(
          wineStringTableColumn -> new TextFieldTableCell<>(stringConverter));
      varietyColumn.setCellFactory(
          wineStringTableColumn -> new TextFieldTableCell<>(stringConverter));
      wineryColumn.setCellFactory(
          wineStringTableColumn -> new TextFieldTableCell<>(stringConverter));
      regionColumn.setCellFactory(
          wineStringTableColumn -> new TextFieldTableCell<>(stringConverter));
      colorColumn.setCellFactory(
          wineStringTableColumn -> new TextFieldTableCell<>(stringConverter));
      vintageColumn.setCellFactory(wineStringTableColumn -> new TextFieldTableCell<>(intConverter));
      scoreColumn.setCellFactory(wineStringTableColumn -> new TextFieldTableCell<>(intConverter));
      abvColumn.setCellFactory(wineStringTableColumn -> new TextFieldTableCell<>(floatConverter));
      priceColumn.setCellFactory(wineStringTableColumn -> new TextFieldTableCell<>(floatConverter));
    }

    tableView.getColumns().add(titleColumn);
    tableView.getColumns().add(varietyColumn);
    tableView.getColumns().add(wineryColumn);
    tableView.getColumns().add(regionColumn);
    tableView.getColumns().add(colorColumn);
    tableView.getColumns().add(vintageColumn);
    tableView.getColumns().add(scoreColumn);
    tableView.getColumns().add(abvColumn);
    tableView.getColumns().add(priceColumn);
  }

  /**
   * Creates a card for a wine.
   *
   * @param wine wine
   */
  public void createWineCard(Wine wine) {
    WineCard card = new WineCard(winesViewContainer.widthProperty(),
        winesViewContainer.hgapProperty(), wine, true);
    card.setOnMouseClicked(event -> {
      if (event.getClickCount() == 2) {
        openDetailedWineView(wine);
      }
    });
    winesViewContainer.getChildren().add(card);
  }

  /**
   * Called after the constructor for when fxml is loaded.
   * <p>
   * Gets, loads, and displays a table from a list of wines from the controller layer
   * </p>
   */
  @Override
  public void init() {
    // Create AutoCompleteBoxes
    this.countryTextField = createAutoCompleteTextField(9.0, 105.0);
    this.wineryTextField = createAutoCompleteTextField(9.0, 165.0);
    this.colorTextField = createAutoCompleteTextField(9.0, 225.0);

    // Create sliders
    this.vintageSlider = createSlider(11, 290, 0, 100, 10);
    this.scoreSlider = createSlider(11, 365, 0, 100, 10);
    this.abvSlider = createSlider(11, 445, 0, 100, 10);
    this.priceSlider = createSlider(11, 525, 0, 100, 10);

    colorTextField.setOnKeyPressed(event -> {
      if (event.getCode() == KeyCode.TAB) {
        applyFiltersButton.requestFocus();
      }
    });

    // Ensure uniques are up to date
    managerContext.getDatabaseManager().getWineDao().updateUniques();
    setFilterValues();

    // Set snap to ticks
    vintageSlider.setSnapToTicks(true);
    scoreSlider.setSnapToTicks(true);
    abvSlider.setSnapToTicks(true);
    priceSlider.setSnapToTicks(true);

    // Set button functions
    applyFiltersButton.setOnAction(event -> onApplyFiltersButtonPressed());
    resetFiltersButton.setOnAction(event -> onResetFiltersButtonPressed());
    prevPageButtonRawViewer.setOnAction(actionEvent -> previousPage());
    nextPageButtonRawViewer.setOnAction(actionEvent -> nextPage());
    prevPageButtonSimpleView.setOnAction(actionEvent -> previousPage());
    nextPageButtonSimpleView.setOnAction(actionEvent -> nextPage());

    // Set textfield listener and on action to ensure valid inputs
    pageNumberTextFieldRawViewer.focusedProperty()
        .addListener((observableValue, oldValue, newValue) -> {
          if (!newValue) {
            // This is executed when the text field loses focus
            ensureValidPageNumber(pageNumberTextFieldRawViewer);
          }
        });
    pageNumberTextFieldRawViewer.setOnAction(
        actionEvent -> ensureValidPageNumber(pageNumberTextFieldRawViewer));

    pageNumberTextFieldSimpleView.focusedProperty()
        .addListener((observableValue, oldValue, newValue) -> {
          if (!newValue) {
            ensureValidPageNumber(pageNumberTextFieldSimpleView);
          }
        });
    pageNumberTextFieldSimpleView.setOnAction(
        actionEvent -> ensureValidPageNumber(pageNumberTextFieldSimpleView));

    // Set listener to pageService to change pages
    // Disables the buttons upon hitting a limit
    pageService.pageNumberProperty()
        .addListener((observableValue, oldValue, newValue) -> {
          openWineRange(this.currentFilters);
          this.nextPageButtonRawViewer.setDisable((int) newValue == pageService.getMaxPages());
          this.prevPageButtonRawViewer.setDisable((int) newValue == 1);
          this.nextPageButtonSimpleView.setDisable((int) newValue == pageService.getMaxPages());
          this.prevPageButtonSimpleView.setDisable((int) newValue == 1);
        });

    // Set up max pages
    pageService.setTotalItems(managerContext.getDatabaseManager().getWineDao().getCount());
    maxPageNumberRawViewer.setText("/" + pageService.getMaxPages()); // Set initial value
    maxPageNumberSimpleView.setText("/" + pageService.getMaxPages());
    pageService.maxPagesProperty().addListener((observableValue, oldValue, newValue) -> {

      // Change max pages label when max pages changes
      maxPageNumberRawViewer.setText("/" + newValue);
      maxPageNumberSimpleView.setText("/" + newValue);

      // ensure page number doesn't get "caught" outside range
      if ((int) newValue < this.pageService.getPageNumber()) {
        this.pageService.setPageNumber(this.pageService.getMaxPages());
        this.pageNumberTextFieldRawViewer.setText(this.pageService.getMaxPages() + "");
        this.pageNumberTextFieldSimpleView.setText(this.pageService.getMaxPages() + "");
      }
    });

    mapController = new LeafletOsmController(webView.getEngine());
    mapController.initMap();

    setupTableColumns();
    openWineRange(null);

    tableView.setOnMouseClicked(this::openWineOnClick);
  }

  /**
   * Creates a range slider element and displays it on the filters pane at the given layout
   * coordinates.
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
    rangeSlider.getStylesheets().add("css/range_slider.css");
    filtersPane.getChildren().add(rangeSlider);
    return rangeSlider;
  }

  /**
   * Creates an auto complete field at a location.
   *
   * @param layoutX x location
   * @param layoutY y location
   * @return auto complete field
   */
  public AutoCompletionTextField createAutoCompleteTextField(double layoutX, double layoutY) {
    AutoCompletionTextField autoCompleteTextField = new AutoCompletionTextField();
    autoCompleteTextField.setLayoutX(layoutX);
    autoCompleteTextField.setLayoutY(layoutY);
    autoCompleteTextField.prefHeight(33.0);
    autoCompleteTextField.setPrefWidth(300);
    autoCompleteTextField.setStyle("-fx-font-size: 15px;");
    filtersPane.getChildren().add(autoCompleteTextField);
    return autoCompleteTextField;
  }


  /**
   * Is called when the apply button is pressed<br> Updates table with filtered data.
   */
  public void onApplyFiltersButtonPressed() {
    currentFilters = new WineFilters(
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

    // update max pages
    this.pageService.setTotalItems(
        managerContext.getDatabaseManager().getWineDao().getCount(currentFilters));

    // Update table with filtered wines
    openWineRange(currentFilters);
  }

  /**
   * Handles reset button being pressed.
   */
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

    // Reset current filters
    this.currentFilters = null;

    // Update pages
    pageService.setTotalItems(managerContext.getDatabaseManager().getWineDao().getCount());

    // Update wines
    openWineRange(null);
  }

  /**
   * Opens a wine when mouse is clicked.
   *
   * @param event event
   */
  @FXML
  public void openWineOnClick(MouseEvent event) {
    if (event.getClickCount() != 2) {
      Wine wine = tableView.getSelectionModel().getSelectedItem();
      if (wine != null) {
        openDetailedWineView(wine);
      }
    }
  }

  /**
   * Opens the detailed wine view for a wine.
   *
   * @param wine wine
   */
  private void openDetailedWineView(Wine wine) {
    Runnable backAction = () -> managerContext.getGuiManager().mainController.openWineScreen();
    managerContext.getGuiManager().mainController.openDetailedWineView(wine, backAction);
  }

  /**
   * Ensures the value in the text field is a valid page number.
   *
   * @param textField Text field to test
   */
  public void ensureValidPageNumber(TextField textField) {
    String currentText = textField.getText();

    if (!isInteger(currentText)) {
      textField.setText(
          String.valueOf(this.pageService.getPageNumber())); // Set back to current page

      // ensure valid range
    } else if (Integer.parseInt(currentText) > pageService.getMaxPages()
        || Integer.parseInt(currentText) < 1) {
      textField.setText(
          String.valueOf(this.pageService.getPageNumber())); // Set back to current page
    } else {
      pageService.setPageNumber(Integer.parseInt(currentText)); // Change page if valid
    }
  }

  /**
   * Goes to the next page.
   */
  public void nextPage() {
    this.pageService.nextPage();
    pageNumberTextFieldRawViewer.setText(
        this.pageService.pageNumberProperty().getValue().toString());
    pageNumberTextFieldSimpleView.setText(
        this.pageService.pageNumberProperty().getValue().toString());
  }

  /**
   * Goes to the previous.
   */
  public void previousPage() {
    this.pageService.previousPage();
    pageNumberTextFieldRawViewer.setText(
        this.pageService.pageNumberProperty().getValue().toString());
    pageNumberTextFieldSimpleView.setText(
        this.pageService.pageNumberProperty().getValue().toString());
  }


  /**
   * Sets the value of the current filters based off the inputted filters.
   */
  public void setFilterValues() {
    // Auto Complete boxes and range sliders
    // Update filter checkboxes
    Set<String> winerySet = managerContext.getDatabaseManager().getWineDao()
        .getWineDataStatService()
        .getUniqueWineries();
    Set<String> countrySet = managerContext.getDatabaseManager().getWineDao()
        .getWineDataStatService()
        .getUniqueCountries();
    Set<String> colorSet = managerContext.getDatabaseManager().getWineDao()
        .getWineDataStatService()
        .getUniqueColors();
    final int minVintage = managerContext.getDatabaseManager().getWineDao()
        .getWineDataStatService()
        .getMinVintage();
    final int maxVintage = managerContext.getDatabaseManager().getWineDao()
        .getWineDataStatService()
        .getMaxVintage();
    final double maxScore = managerContext.getDatabaseManager().getWineDao()
        .getWineDataStatService()
        .getMaxScore();
    final double minScore = managerContext.getDatabaseManager().getWineDao()
        .getWineDataStatService()
        .getMinScore();
    final double minPrice = managerContext.getDatabaseManager().getWineDao()
        .getWineDataStatService()
        .getMinPrice();
    final double maxPrice = managerContext.getDatabaseManager().getWineDao()
        .getWineDataStatService()
        .getMaxPrice();

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
    priceSlider.setMin(minPrice);
    priceSlider.setMax(maxPrice);

    // Set slider handles to min and max values
    // Fixes a graphic issue where the slider values don't change with the min and max adjustments
    scoreSlider.setHighValue(scoreSlider.getMax());
    scoreSlider.setLowValue(scoreSlider.getMin());
    vintageSlider.setHighValue(vintageSlider.getMax());
    vintageSlider.setLowValue(vintageSlider.getMin());
    priceSlider.setHighValue(priceSlider.getMax());
    priceSlider.setLowValue(priceSlider.getMin());

    // Ensure the sliders display properly
    scoreSlider.setMajorTickUnit(1);
    vintageSlider.setMajorTickUnit(1);
    vintageSlider.setMinorTickCount(0);

    YearStringConverter yearStringConverter = new YearStringConverter();
    vintageSlider.setLabelFormatter(yearStringConverter);

  }

  /**
   * Ensures a value is an integer.
   *
   * @param str string value to check
   * @return true if it is an integer false if not
   */
  private boolean isInteger(String str) {
    try {
      Integer.parseInt(str);
      return true;
    } catch (NumberFormatException e) {
      return false;
    }
  }
}

