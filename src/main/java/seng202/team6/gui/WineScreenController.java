package seng202.team6.gui;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;
import javafx.util.Duration;
import javafx.util.StringConverter;
import javafx.util.converter.DefaultStringConverter;
import javafx.util.converter.FloatStringConverter;
import javafx.util.converter.IntegerStringConverter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.controlsfx.control.RangeSlider;
import seng202.team6.gui.controls.AutoCompletionTextField;
import seng202.team6.managers.ManagerContext;
import seng202.team6.model.Filters;
import seng202.team6.model.Wine;
import seng202.team6.service.PageService;
import seng202.team6.util.ImageReader;
import seng202.team6.util.WineWidgets;
import seng202.team6.util.YearStringConverter;

/**
 * Controller for the screen that displays wines.
 */

public class WineScreenController extends Controller {

  private static final Image RED_WINE_IMAGE = ImageReader.loadImage("/img/red_wine_cropped.png");
  private static final Image WHITE_WINE_IMAGE = ImageReader.loadImage(
      "/img/white_wine_cropped.png");
  private static final Image ROSE_WINE_IMAGE = ImageReader.loadImage("/img/rose_wine_cropped.png");
  private static final Image DEFAULT_WINE_IMAGE = ImageReader.loadImage(
      "/img/default_wine_cropped.png");
  private static final Map<String, Image> wineImages = new HashMap<>() {
    {
      put("red", RED_WINE_IMAGE);
      put("white", WHITE_WINE_IMAGE);
      put("rose", ROSE_WINE_IMAGE);
      put("rosé", ROSE_WINE_IMAGE);
    }
  };
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
  private Filters currentFilters;

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
  private void openWineRange(Filters filters) {
    // Clear existing data
    tableView.getItems().clear();

    // Calculate range of data to get
    int begin = this.pageService.getMinRange();
    int end = this.pageService.getMaxRange();

    // Check if filters exist
    ObservableList<Wine> wines = managerContext.getDatabaseManager().getWineDao()
        .getAllInRange(begin, end, filters);

    // send the wines to the map if they have a geo location
    mapController.setOnReadyAction(() -> {
      mapController.clearWineMarkers();
      mapController.clearHeatmap();
      wines.stream()
          .filter(wine -> wine.getGeoLocation() != null)
          .forEach(mapController::addWineMarker);
    });

    winesViewContainer.getChildren().clear();

    for (Wine wine : wines) {

      Node node = WineWidgets.createWineCard(wine);

      node.setOnMouseClicked(event -> {
        if (event.getClickCount() == 2) {
          openDetailedWineView(wine);
        }
      });

      winesViewContainer.getChildren().add(node);
    }

    // Set fetched data to the table
    tableView.setItems(wines);

    // Only update autocomplete if NOT filtering
    /*
    Don't think we need to do this but commenting in case

    if (filters == null) {
      setFilterValues();
    }
    */

  }

  /**
   * Sets up the table columns.
   */
  public void setupTableColumns() {
    // Clear any existing cols
    tableView.getColumns().clear();

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

      final StringConverter<String> stringConverter = new DefaultStringConverter();
      final StringConverter<Integer> intConverter = new IntegerStringConverter();
      final StringConverter<Float> floatConverter = new FloatStringConverter();

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
    VBox wrapper = new VBox();
    wrapper.setPadding(new Insets(10));
    wrapper.setStyle("-fx-background-color: #f3f4f6; -fx-background-radius: 10px;");

    Image wineImage = wineImages.getOrDefault(wine.getColor().toLowerCase(), DEFAULT_WINE_IMAGE);
    ImageView imageView = new ImageView(wineImage);
    imageView.setFitHeight(100);
    imageView.setPreserveRatio(true);
    HBox.setHgrow(imageView, Priority.NEVER);

    Label wineTitle = new Label();
    wineTitle.textProperty().bind(wine.titleProperty());
    wineTitle.setStyle("-fx-font-size: 16px;");
    wineTitle.setWrapText(true);

    HBox header = new HBox(imageView, wineTitle);
    header.setAlignment(Pos.CENTER_LEFT);
    header.setSpacing(20);
    wrapper.getChildren().add(header);
    winesViewContainer.getChildren().add(wrapper);
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

    // Ensures the sliders are rendered before installing tooltips (Needed for css lookups)
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

    colorTextField.setOnKeyPressed(event -> {
      if (event.getCode() == KeyCode.TAB) {
        applyFiltersButton.requestFocus();
      }
    });

    // we need to listen to the width property
    // because in the init() method, the winesViewContainer does not yet have a width
    winesViewContainer.widthProperty().addListener((obs, oldVal, newVal) -> {
      double totalWidth = newVal.doubleValue();
      // minus 10 for insets
      double tileWidth = (totalWidth - winesViewContainer.getHgap() * 2) / 3 - 10;
      for (Node child : winesViewContainer.getChildren()) {
        if (child instanceof VBox) {
          ((VBox) child).setPrefWidth(tileWidth);
        }
      }
    });

    // Ensure uniques are up to date
    managerContext.getDatabaseManager().getWineDao().updateUniques();
    setFilterValues();

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
    // Save current filters for pagination
    this.currentFilters = filters;

    // update max pages
    this.pageService.setTotalItems(
        managerContext.getDatabaseManager().getWineDao().getCount(filters));

    // Update table with filtered wines
    openWineRange(filters);
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
    priceSlider.setMajorTickUnit(100);

    YearStringConverter yearStringConverter = new YearStringConverter();
    vintageSlider.setLabelFormatter(yearStringConverter);

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
      LogManager.getLogger().error(
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

          // Using getWindow().getX() to adjust for window postion so tool tip is located correctly
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
