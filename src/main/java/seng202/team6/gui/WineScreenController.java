package seng202.team6.gui;

import java.util.Set;
import javafx.application.Platform;
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
import seng202.team6.dao.WineDao;
import seng202.team6.gui.controls.AutoCompletionTextField;
import seng202.team6.gui.controls.CustomRangeSlider;
import seng202.team6.gui.controls.WineCard;
import seng202.team6.managers.ManagerContext;
import seng202.team6.model.Wine;
import seng202.team6.model.WineFilters;
import seng202.team6.service.PageService;
import seng202.team6.service.WineDataStatService;
import seng202.team6.util.NoDecimalCurrencyStringConverter;
import seng202.team6.util.YearStringConverter;

/**
 * Controller for the screen that displays wines.
 */

public class WineScreenController extends Controller {

  // Utilities and services
  private final Logger log = LogManager.getLogger(WineScreenController.class);
  private final PageService pageService;
  @FXML
  public TabPane tabPane;
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
  private CustomRangeSlider scoreSlider;
  private CustomRangeSlider abvSlider;
  private CustomRangeSlider priceSlider;
  private CustomRangeSlider vintageSlider;
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
    this.pageService = new PageService(100);
  }

  /**
   * Constructor with save state.
   *
   * @param managerContext manager context
   */
  public WineScreenController(ManagerContext managerContext, PageService pageService,
      WineFilters filters) {
    super(managerContext);
    this.currentFilters = filters;
    this.pageService = pageService;
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
    mapController.runOrQueueWhenReady(() -> mapController.enableToggleButtons());

    // Setup table with wine data
    setupTableColumns();
    // Check for saved state and if so, load it
    if (this.currentFilters != null) {
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
    winesViewContainer.getChildren().clear();

    // Calculate range of data to get
    int begin = this.pageService.getMinRange();
    int end = this.pageService.getMaxRange();

    getManagerContext().getGuiManager().showLoadingIndicator(() -> {
      ObservableList<Wine> wines = getManagerContext().getDatabaseManager().getWineDao()
              .getAllInRange(begin, end, filters);
      mapController.runOrQueueWhenReady(() -> {
        mapController.clearWineMarkers();
        mapController.clearHeatmap();
        wines.stream()
                .filter(wine -> wine.getGeoLocation() != null)
                .forEach(mapController::addWineMarker);
      });
      wines.forEach(this::createWineCard);
      tableView.setItems(wines);
    });
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
    final TableColumn<Wine, Float> priceColumn = new TableColumn<>("Price");

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
    if (getManagerContext().getAuthenticationManager().isAdmin()) {
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
        getManagerContext().getDatabaseManager().getWineDao().getCount(currentFilters));

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
    pageService.setTotalItems(getManagerContext().getDatabaseManager().getWineDao().getCount());

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
   * Sets the value of the current filters based off the inputted filters.
   */
  public void setFilterValues() {

    WineDao wineDao = getManagerContext().getDatabaseManager().getWineDao();
    WineDataStatService wineDataStatService = wineDao.getWineDataStatService();

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
    NoDecimalCurrencyStringConverter currencyConverter = new NoDecimalCurrencyStringConverter();
    YearStringConverter yearConverter = new YearStringConverter();
    // Calculate vintage tick unit
    int tickUnit = ((maxVintage - minVintage) >= 4 ? (maxVintage - minVintage) / 4 : 1);
    scoreSlider.configure(0, 100, 10, 5, null);
    priceSlider.configure(minPrice, maxPrice, 100, 5, currencyConverter);
    vintageSlider.configure(minVintage, maxVintage, tickUnit, 0, yearConverter);
    abvSlider.configure(minAbv, maxAbv, 1, 0, null);

    // Handle a special case for score slider as we hard set its min and max
    if (minScore >= maxScore) {
      scoreSlider.setMin(0);
      scoreSlider.setMax(1);
      scoreSlider.setShowTickLabels(false);
      scoreSlider.setDisable(true);
    }

    // Set slider handles to min and max values
    // Fixes a graphic issue where the slider values don't change with the min and max adjustments
    resetSliderThumbs();
  }

  // Private helper functions

  /**
   * Loads a saved state.
   */
  private void loadState() {
    // Set textfield values
    titleTextField.setText(currentFilters.getTitle());
    countryTextField.setText(currentFilters.getCountry());
    wineryTextField.setText(currentFilters.getWinery());
    colorTextField.setText(currentFilters.getColor());

    // Set slider values (if not disabled)
    if (!vintageSlider.isDisabled()) {
      vintageSlider.setLowHigh(currentFilters.getMinVintage(), currentFilters.getMaxVintage());
    }
    if (!scoreSlider.isDisabled()) {
      scoreSlider.setLowHigh(currentFilters.getMinScore(), currentFilters.getMaxScore());
    }
    if (!abvSlider.isDisabled()) {
      abvSlider.setLowHigh(currentFilters.getMinAbv(), currentFilters.getMaxAbv());
    }
    if (!priceSlider.isDisabled()) {
      priceSlider.setLowHigh(currentFilters.getMinPrice(), currentFilters.getMaxPrice());
    }

    // Hide all context menus → fixes a bug caused by setting the text
    countryTextField.getEntriesPopup().hide();
    wineryTextField.getEntriesPopup().hide();
    colorTextField.getEntriesPopup().hide();

    onApplyFiltersButtonPressed();
    pageService.setPageNumber(pageService.getPageNumber());
    // Update the buttons as the listener isn't call if the value isnt updated
    validatePageButtons(pageService.getPageNumber());
    LogManager.getLogger(this.getClass().getName()).info("Successfully loaded a previous state!");
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
   * Resets all sliders back to their minimums and maxes.
   */
  private void resetSliderThumbs() {
    scoreSlider.resetThumbs();
    vintageSlider.resetThumbs();
    priceSlider.resetThumbs();
    abvSlider.resetThumbs();
  }

  /**
   * Opens the detailed wine view for a wine.
   *
   * @param wine wine
   */
  private void openDetailedWineView(Wine wine) {
    Runnable backAction;
    if (currentFilters == null && pageService.getPageNumber() == 1) { // Don't need to save state
      backAction = () -> getManagerContext().getGuiManager()
          .openWineScreen();
    } else {
      backAction = () -> getManagerContext().getGuiManager()
          .openWineScreen(pageService, currentFilters);
    }
    getManagerContext().getGuiManager().openDetailedWineView(wine, backAction);
  }

  /**
   * Initialises filter sliders.
   */
  private void sliderInit() {
    this.vintageSlider = new CustomRangeSlider(11, 290);
    this.scoreSlider = new CustomRangeSlider(11, 365);
    this.abvSlider = new CustomRangeSlider(11, 445);
    this.priceSlider = new CustomRangeSlider(11, 525);
    filtersPane.getChildren().addAll(vintageSlider, scoreSlider, abvSlider, priceSlider);
  }

  /**
   * Sets up page navigation buttons.
   */
  private void setupNavigation() {
    // Set button functions
    applyFiltersButton.setOnAction(event -> onApplyFiltersButtonPressed());
    resetFiltersButton.setOnAction(event -> onResetFiltersButtonPressed());
    prevPageButtonRawViewer.setOnAction(actionEvent -> pageService.previousPage());
    nextPageButtonRawViewer.setOnAction(actionEvent -> pageService.nextPage());
    prevPageButtonSimpleView.setOnAction(actionEvent -> pageService.previousPage());
    nextPageButtonSimpleView.setOnAction(actionEvent -> pageService.nextPage());

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
          this.nextPageButtonRawViewer.setDisable((int) newValue == pageService.getMaxPages());
          this.prevPageButtonRawViewer.setDisable((int) newValue == 1);
          this.nextPageButtonSimpleView.setDisable((int) newValue == pageService.getMaxPages());
          this.prevPageButtonSimpleView.setDisable((int) newValue == 1);
          this.pageNumberTextFieldSimpleView.setText(newValue + "");
          this.pageNumberTextFieldRawViewer.setText(newValue + "");
        });

    // Set up max pages
    pageService.setTotalItems(getManagerContext().getDatabaseManager().getWineDao().getCount());
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

