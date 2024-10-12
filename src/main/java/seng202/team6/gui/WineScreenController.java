package seng202.team6.gui;

import java.util.Set;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.TilePane;
import javafx.scene.web.WebView;
import javafx.util.Duration;
import javafx.util.StringConverter;
import javafx.util.converter.DefaultStringConverter;
import javafx.util.converter.FloatStringConverter;
import javafx.util.converter.IntegerStringConverter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.controlsfx.control.RangeSlider;
import seng202.team6.dao.WineDao;
import seng202.team6.gui.controls.AutoCompletionTextField;
import seng202.team6.gui.controls.WineCard;
import seng202.team6.managers.ManagerContext;
import seng202.team6.model.Wine;
import seng202.team6.model.WineFilters;
import seng202.team6.service.PageService;
import seng202.team6.service.WineDataStatService;
import seng202.team6.util.WineState;
import seng202.team6.util.YearStringConverter;

/**
 * Controller for the screen that displays wines.
 */

public class WineScreenController extends Controller {

  // Utilities and services
  private final Logger log = LogManager.getLogger(WineScreenController.class);
  private final PageService pageService = new PageService(100);
  @FXML
  public TabPane tabPane;
  private WineState savedState = null;
  private WineFilters currentFilters;
  // FXML elements
  @FXML
  private Button prevPageButtonSimpleView;
  @FXML
  private TextField pageNumberTextFieldSimpleView;
  @FXML
  private Label maxPageNumberSimpleView;
  @FXML
  private Button nextPageButtonSimpleView;
  private LeafletOsmController mapController;
  // Custom element (added in code)
  private RangeSlider scoreSlider;
  private RangeSlider abvSlider;
  private RangeSlider priceSlider;
  private RangeSlider vintageSlider;
  private AutoCompletionTextField countryTextField;
  private AutoCompletionTextField wineryTextField;
  private AutoCompletionTextField colorTextField;
  @FXML
  private TableView<Wine> tableView;
  @FXML
  private AnchorPane filtersPane;
  @FXML
  private WebView webView;
  @FXML
  private Button applyFiltersButton;
  @FXML
  private Button resetFiltersButton;
  @FXML
  private TextField titleTextField;
  @FXML
  private TilePane winesViewContainer;
  @FXML
  private Button nextPageButtonRawViewer;
  @FXML
  private Button prevPageButtonRawViewer;
  @FXML
  private TextField pageNumberTextFieldRawViewer;
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
   * Constructor with save state.
   *
   * @param managerContext manager context
   * @param savedState     the saved state
   */
  public WineScreenController(ManagerContext managerContext, WineState savedState) {
    super(managerContext);
    this.savedState = savedState;
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

    // Setup Sliders and filters
    sliderInit();
    setFilterValues();

    // ensure tabbing doesn't select sliders
    colorTextField.setOnKeyPressed(event -> {
      if (event.getCode() == KeyCode.TAB) {
        applyFiltersButton.requestFocus();
      }
    });

    // Setup page navigation functionality
    setupNavigation();

    // Setup map
    mapController = new LeafletOsmController(webView.getEngine());
    mapController.initMap();

    // Setup table with wine data
    setupTableColumns();
    // Check for saved state and if so, load it
    if (this.savedState != null) {
      Platform.runLater(this::loadState); // this will call open wine range
    } else {
      openWineRange(null);
    }

    // Setup detailed wine
    tableView.setOnMouseClicked(this::openWineOnClick);
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

    // send the wines to the map if they have a geolocation
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
    // by default, the font size matches the parent font size which is the filter title
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
   * Is called when the "apply" button is pressed<br> Updates table with filtered data.
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
    resetSliderThumbs();
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
   * Ensures the value in the text field is a valid page number.
   *
   * @param textField Text field to test
   */
  public void ensureValidPageNumber(TextField textField) {
    String currentText = textField.getText();

    if (!isInteger(currentText)) {
      textField.setText(
          String.valueOf(this.pageService.getPageNumber())); // Set back to the current page

      // ensure valid range
    } else if (Integer.parseInt(currentText) > pageService.getMaxPages()
        || Integer.parseInt(currentText) < 1) {
      textField.setText(
          String.valueOf(this.pageService.getPageNumber())); // Set back to the current page
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
   * Goes to the previous page.
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

    WineDao wineDao = managerContext.getDatabaseManager().getWineDao();
    WineDataStatService wineDataStatService = wineDao.getWineDataStatService();

    // Ensure unique value are up to date
    wineDao.updateUniques();

    // Auto Complete boxes and range sliders
    // Update filter checkboxes
    final Set<String> winerySet = wineDataStatService.getUniqueWineries();
    final Set<String> countrySet = wineDataStatService.getUniqueCountries();
    final Set<String> colorSet = wineDataStatService.getUniqueColors();
    final int minVintage = wineDataStatService.getMinVintage();
    final int maxVintage = wineDataStatService.getMaxVintage();
    final double maxScore = wineDataStatService.getMaxScore();
    final double minScore = wineDataStatService.getMinScore();
    final double minPrice = wineDataStatService.getMinPrice();
    final double maxPrice = wineDataStatService.getMaxPrice();
    final double maxAbv = wineDataStatService.getMaxAbv();
    final double minAbv = wineDataStatService.getMinAbv();

    // Configure auto-complete data
    configureAutoComplete(wineryTextField, winerySet);
    configureAutoComplete(countryTextField, countrySet);
    configureAutoComplete(colorTextField, colorSet);

    // Configure Sliders
    configureSlider(scoreSlider, minScore, maxScore, 1, 0);
    configureSlider(priceSlider, minPrice, maxPrice, 100, 5);

    YearStringConverter yearStringConverter = new YearStringConverter(); // For vintage
    configureSlider(vintageSlider, minVintage, maxVintage, 1, 0,
        yearStringConverter);

    // Set slider handles to min and max values
    // Fixes a graphic issue where the slider values don't change with the min and max adjustments
    resetSliderThumbs();

    // Ensure sliders are valid and disable if not
    validateSlider(vintageSlider, minVintage, maxVintage);
    validateSlider(scoreSlider, minScore, maxScore);
    validateSlider(priceSlider, minPrice, maxPrice);
    validateSlider(abvSlider, minAbv, maxAbv);
  }

  // Private helper functions

  /**
   * Loads a saved state.
   */
  private void loadState() {
    WineFilters filters = savedState.getFilters();
    PageService pageService = savedState.getPageService();

    if (filters != null) {
      // Set textfield values
      titleTextField.setText(filters.getTitle());
      countryTextField.setText(filters.getCountry());
      wineryTextField.setText(filters.getWinery());
      colorTextField.setText(filters.getColor());

      // Set slider values (if not disabled)
      if (!vintageSlider.isDisabled()) {
        vintageSlider.setHighValue(filters.getMaxVintage());
        vintageSlider.setLowValue(filters.getMinVintage());
      }

      if (!scoreSlider.isDisabled()) {
        scoreSlider.setHighValue(filters.getMaxScore());
        scoreSlider.setLowValue(filters.getMinScore());
      }

      if (!abvSlider.isDisabled()) {
        abvSlider.setHighValue(filters.getMaxAbv());
        abvSlider.setLowValue(filters.getMinAbv());
      }

      if (!priceSlider.isDisabled()) {
        priceSlider.setHighValue(filters.getMaxPrice());
        priceSlider.setLowValue(filters.getMinPrice());
      }

      // Hide all context menus → fixes a bug caused by setting the text
      countryTextField.getEntriesPopup().hide();
      wineryTextField.getEntriesPopup().hide();
      colorTextField.getEntriesPopup().hide();
    }

    onApplyFiltersButtonPressed();
    this.pageService.setPageNumber(pageService.getPageNumber());
    // Update the buttons as the listener isn't call if the value isnt updated
    validatePageButtons(pageService.getPageNumber());
    LogManager.getLogger(this.getClass().getName()).info("Successfully loaded a previous state!");
  }

  /**
   * Saves the state of the current filters and page service.
   */
  private WineState saveState() {
    return new WineState(currentFilters, pageService);
  }

  /**
   * Configures Auto-completing text field with given entries.
   *
   * @param textField Target text field
   * @param entries   Auto-complete entries
   */
  private void configureAutoComplete(AutoCompletionTextField textField, Set<String> entries) {
    textField.getEntries().clear(); // Clear old data
    textField.getEntries().addAll(entries); // Add new data for auto-complete
  }

  /**
   * helper method to configure range slider.
   *
   * @param rangeSlider    Range Slider to configure
   * @param min            Minimum range slider value
   * @param max            Maximum range slider value
   * @param majorTickUnit  Units between major tick
   * @param minorTickCount Number of minor ticks
   */
  private void configureSlider(RangeSlider rangeSlider,
      double min,
      double max,
      double majorTickUnit,
      int minorTickCount) {

    configureSlider(rangeSlider, min, max, majorTickUnit, minorTickCount, null);
  }

  /**
   * Overloaded helper method for range slider with label formatter.
   *
   * @param rangeSlider    Range Slider to configure
   * @param min            Minimum range slider value
   * @param max            Maximum range slider value
   * @param majorTickUnit  Units between major tick
   * @param minorTickCount Number of minor ticks
   * @param labelFormatter Label formatter
   */
  private void configureSlider(RangeSlider rangeSlider,
      double min,
      double max,
      double majorTickUnit,
      int minorTickCount,
      StringConverter<Number> labelFormatter) {

    rangeSlider.setMin(min);
    rangeSlider.setMax(max);
    rangeSlider.setMajorTickUnit(majorTickUnit);
    rangeSlider.setMinorTickCount(minorTickCount);

    if (labelFormatter != null) {
      rangeSlider.setLabelFormatter(labelFormatter);
    }
  }

  /**
   * Resets all sliders back to their minimums and maxes.
   */
  private void resetSliderThumbs() {
    scoreSlider.setHighValue(scoreSlider.getMax());
    scoreSlider.setLowValue(scoreSlider.getMin());
    vintageSlider.setHighValue(vintageSlider.getMax());
    vintageSlider.setLowValue(vintageSlider.getMin());
    priceSlider.setHighValue(priceSlider.getMax());
    priceSlider.setLowValue(priceSlider.getMin());
    abvSlider.setHighValue(abvSlider.getMax());
    abvSlider.setLowValue(abvSlider.getMin());
  }

  /**
   * Opens the detailed wine view for a wine.
   *
   * @param wine wine
   */
  private void openDetailedWineView(Wine wine) {
    Runnable backAction;
    if (currentFilters == null && pageService.getPageNumber() == 1) { // Don't need to save state
      backAction = () -> managerContext.getGuiManager()
          .openWineScreen();
    } else {
      backAction = () -> managerContext.getGuiManager()
          .openWineScreen(this.saveState());
    }
    managerContext.getGuiManager().openDetailedWineView(wine, backAction);
  }

  /**
   * Ensures a slider is valid and the user is allowed to interact.
   * <p>
   * Disables the slider if it is unable to be used
   * </p>
   *
   * @param slider the target slider
   * @param min    min value to check
   * @param max    max value to check
   */
  private void validateSlider(RangeSlider slider, double min, double max) {
    // The vintage min is set to the max double for error handling, so min > max check needed
    if (min == 0 & max == 0 || min > max) {
      slider.setMin(0);
      slider.setMax(1);
      slider.setHighValue(1); // ensures the slider is in the right spot
      slider.setShowTickLabels(false);
      slider.setDisable(true);
    } else {
      slider.setDisable(false);
      slider.setShowTickLabels(true);
    }
  }

  /**
   * Adds a tool tips to each thumb of the range slider to indicate values.
   *
   * @param rangeSlider range slider to add tooltips too
   */
  private void installRangeSliderTooltip(RangeSlider rangeSlider) {
    rangeSlider.applyCss();
    rangeSlider.getParent().applyCss();
    final Tooltip lowerToolTip = new Tooltip();
    final Tooltip upperToolTip = new Tooltip();

    // Ensures that tooltips display instantly
    lowerToolTip.setShowDelay(Duration.ZERO);
    lowerToolTip.setHideDelay(Duration.ZERO);
    lowerToolTip.setShowDuration(Duration.INDEFINITE);

    upperToolTip.setShowDelay(Duration.ZERO);
    upperToolTip.setHideDelay(Duration.ZERO);
    upperToolTip.setShowDuration(Duration.INDEFINITE);

    // Get thumbs
    Node lowerThumb = rangeSlider.lookup(".low-thumb");
    Node upperThumb = rangeSlider.lookup(".high-thumb");

    if (lowerThumb != null && upperThumb != null) {
      // add handlers for tooltip logic
      addEventHandlersToThumb(lowerThumb, lowerToolTip);
      addEventHandlersToThumb(upperThumb, upperToolTip);

      // Set initial values
      lowerToolTip.setText(String.format("%.0f", rangeSlider.getLowValue()));
      upperToolTip.setText(String.format("%.0f", rangeSlider.getHighValue()));

      // Add listeners
      rangeSlider.lowValueProperty().addListener((observable, oldValue, newValue) ->
          lowerToolTip.setText(String.format("%.0f", newValue.doubleValue())));
      rangeSlider.highValueProperty().addListener((observable, oldValue, newValue) ->
          upperToolTip.setText(String.format("%.0f", newValue.doubleValue())));

    } else {
      log.error(
          "Thumb nodes not found. Make sure the RangeSlider is added to the scene and rendered.");
    }

  }

  /**
   * Adds required tooltip logic through event handlers.
   *
   * @param thumb   the thumb node to attach the tool tip too
   * @param tooltip tool tip to attach
   */
  private void addEventHandlersToThumb(Node thumb, Tooltip tooltip) {

    // Attach tooltip on click
    thumb.addEventHandler(MouseEvent.MOUSE_PRESSED, e -> {

          Point2D thumbLocation = thumb.localToScene(
              thumb.getBoundsInLocal().getMinX(), thumb.getBoundsInLocal().getMinY());

          // Using getWindow().getX() to adjust for window position so tool tip is located correctly
          tooltip.show(thumb, thumbLocation.getX() + thumb.getScene().getWindow().getX(),
              thumbLocation.getY() + thumb.getScene().getWindow().getY() - 20);
        }
    );

    // Update tooltip as its dragged
    thumb.addEventHandler(MouseEvent.MOUSE_DRAGGED, e -> {

          Point2D thumbLocation = thumb.localToScene(
              thumb.getBoundsInLocal().getMinX(), thumb.getBoundsInLocal().getMinY());

          tooltip.setX(thumbLocation.getX() + thumb.getScene().getWindow().getX());
          tooltip.setY(thumbLocation.getY() + thumb.getScene().getWindow().getY() - 20);
        }
    );

    // Hide on mouse release
    thumb.addEventHandler(MouseEvent.MOUSE_RELEASED, e -> tooltip.hide());
  }

  /**
   * Initialises filter sliders.
   */
  private void sliderInit() {
    // Create sliders
    this.vintageSlider = createSlider(11, 290, 0, 100, 10);
    this.scoreSlider = createSlider(11, 365, 0, 100, 10);
    this.abvSlider = createSlider(11, 445, 0, 100, 10);
    this.priceSlider = createSlider(11, 525, 0, 100, 10);

    // Ensures the sliders are rendered before installing tooltips (Needed for CSS lookups)
    filtersPane.sceneProperty().addListener((observable, oldScene, newScene) -> {
      installRangeSliderTooltip(this.vintageSlider);
      installRangeSliderTooltip(this.scoreSlider);
      installRangeSliderTooltip(this.abvSlider);
      installRangeSliderTooltip(this.priceSlider);
    });

    // Set snap to ticks
    vintageSlider.setSnapToTicks(true);
    scoreSlider.setSnapToTicks(true);
    abvSlider.setSnapToTicks(true);
    priceSlider.setSnapToTicks(true);

  }

  /**
   * Sets up page navigation buttons.
   */
  private void setupNavigation() {
    // Set button functions
    applyFiltersButton.setOnAction(event -> onApplyFiltersButtonPressed());
    resetFiltersButton.setOnAction(event -> onResetFiltersButtonPressed());
    prevPageButtonRawViewer.setOnAction(actionEvent -> previousPage());
    nextPageButtonRawViewer.setOnAction(actionEvent -> nextPage());
    prevPageButtonSimpleView.setOnAction(actionEvent -> previousPage());
    nextPageButtonSimpleView.setOnAction(actionEvent -> nextPage());

    // Set text field listener and on action to ensure valid inputs
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
          validatePageButtons((int) newValue);
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
      } else {
        // Ensure page buttons get correctly disabled
        // There is a listener on pageNumber that also does this,
        // But it won't get called if we are still on page 1
        // That's why this is here
        validatePageButtons(this.pageService.getPageNumber());
      }
    });

    // Validate on first page launch
    validatePageButtons(this.pageService.getPageNumber());
  }

  /**
   * Enables and disables nav buttons based on new page number.
   * <p>
   * Also sets the page text fields.
   * </p>
   *
   * @param newPageNumber the new page number
   */
  private void validatePageButtons(Integer newPageNumber) {
    this.nextPageButtonRawViewer.setDisable(newPageNumber == pageService.getMaxPages());
    this.prevPageButtonRawViewer.setDisable(newPageNumber == 1);
    this.nextPageButtonSimpleView.setDisable(newPageNumber == pageService.getMaxPages());
    this.prevPageButtonSimpleView.setDisable(newPageNumber == 1);
    this.pageNumberTextFieldSimpleView.setText(newPageNumber + "");
    this.pageNumberTextFieldRawViewer.setText(newPageNumber + "");
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

