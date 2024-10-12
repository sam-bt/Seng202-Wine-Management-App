package seng202.team6.gui;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import seng202.team6.enums.WinePropertyName;
import seng202.team6.gui.popup.GeneralPopupController;
import seng202.team6.managers.ManagerContext;
import seng202.team6.model.Wine;
import seng202.team6.service.WineImportService;
import seng202.team6.util.Exceptions.ValidationException;
import seng202.team6.util.ProcessCsv;
import seng202.team6.util.WineValidator;

/**
 * Controller for wine import.
 */
public class WineImportController extends Controller {

  private final Logger log = LogManager.getLogger(getClass());
  private final Map<Integer, WinePropertyName> selectedWineProperties = new HashMap<>();
  private final WineImportService importService;
  @FXML
  private TilePane dataColumnsContainer;
  private List<String[]> currentFileRows;

  /**
   * Constructor.
   *
   * @param context manager context
   */
  public WineImportController(ManagerContext context) {
    super(context);
    importService = new WineImportService();
  }

  /**
   * Initialize this constructor.
   */
  @Override
  public void init() {
    dataColumnsContainer.setHgap(10);
    dataColumnsContainer.setVgap(20);
  }

  /**
   * Called when the open file button is clicked.
   */
  @FXML
  public void onOpenFileButtonClick() {
    FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle("Open CSV File");
    fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));

    Stage stage = (Stage) dataColumnsContainer.getScene().getWindow();
    File selectedFile = fileChooser.showOpenDialog(stage);
    if (selectedFile == null) {
      return;
    }

    managerContext.getGuiManager().showLoadingIndicator(() -> {
      currentFileRows = ProcessCsv.getCsvRows(selectedFile);
      String[] columnNames = currentFileRows.removeFirst();
      selectedWineProperties.clear();
      makeColumnRemapList(columnNames, currentFileRows);
    });
  }


  /**
   * Called when the append data button is clicked.
   */
  @FXML
  void onAppendDataButtonClick() {
    if (!validate()) {
      return;
    }
    managerContext.getGuiManager().showLoadingIndicator(() -> parseWines(false));
  }

  /**
   * Called when the replace data button is clicked.
   */
  @FXML
  void onReplaceDataButtonClick() {
    if (!validate()) {
      return;
    }
    managerContext.getGuiManager().showLoadingIndicator(() -> parseWines(false));
  }

  /**
   * Resets the wine import screen.
   */
  private void reset() {
    dataColumnsContainer.getChildren().clear();
    selectedWineProperties.clear();
    currentFileRows = null;
  }

  /**
   * Parse the list of currently selected wines.
   *
   * @param replace whether to replace
   */
  private void parseWines(boolean replace) {
    List<Wine> parsedWines = new ArrayList<>();
    Map<WinePropertyName, Integer> valid = importService.validHashMapCreate(
        selectedWineProperties);

    currentFileRows.forEach(row -> {
      try {
        parsedWines.add(WineValidator.parseWine(
            importService.extractPropertyFromRowOrDefault(valid, row, WinePropertyName.TITLE),
            importService.extractPropertyFromRowOrDefault(valid, row, WinePropertyName.VARIETY),
            importService.extractPropertyFromRowOrDefault(valid, row, WinePropertyName.COUNTRY),
            importService.extractPropertyFromRowOrDefault(valid, row, WinePropertyName.REGION),
            importService.extractPropertyFromRowOrDefault(valid, row, WinePropertyName.WINERY),
            importService.extractPropertyFromRowOrDefault(valid, row, WinePropertyName.COLOUR),
            importService.extractPropertyFromRowOrDefault(valid, row, WinePropertyName.VINTAGE),
            importService.extractPropertyFromRowOrDefault(valid, row,
                WinePropertyName.DESCRIPTION),
            importService.extractPropertyFromRowOrDefault(valid, row, WinePropertyName.SCORE),
            importService.extractPropertyFromRowOrDefault(valid, row, WinePropertyName.ABV),
            importService.extractPropertyFromRowOrDefault(valid, row, WinePropertyName.NZD),
            null
        ));
      } catch (ValidationException e) {
        log.error("Could not parse a wine: {}", e.getMessage());
      }
    });
    log.info("Successfully parsed {} out of {} wines", parsedWines.size(),
        currentFileRows.size());

    if (replace) {
      managerContext.getDatabaseManager().getWineDao().replaceAll(parsedWines);
    } else {
      managerContext.getDatabaseManager().getWineDao().addAll(parsedWines);
    }
    reset();
  }


  /**
   * Checks if the importer contains the title property.
   *
   * @return if the selection contains the title property
   */
  private boolean checkContainsTitleProperty() {
    if (!selectedWineProperties.containsValue(WinePropertyName.TITLE)) {
      GeneralPopupController popup = managerContext.getGuiManager().showErrorPopup();
      popup.setTitle("Invalid Selections");
      popup.setMessage("The property TITLE is required but has not been selected");
      popup.addOkButton();
      return false;
    }
    return true;
  }

  /**
   * Validates the current table.
   *
   * @return if current table is valid
   */
  public boolean validate() {
    return checkContainsTitleProperty() && checkDuplicateProperties();
  }

  /**
   * Check for duplicate attribute names.
   *
   * @return true if valid
   */
  private boolean checkDuplicateProperties() {
    Set<WinePropertyName> duplicatedProperties = importService.checkDuplicateProperties(
        selectedWineProperties);

    if (!duplicatedProperties.isEmpty()) {
      GeneralPopupController popup = managerContext.getGuiManager().showErrorPopup();
      popup.setTitle("Invalid Selections");
      popup.setMessage("The property field(s) " + duplicatedProperties.stream()
          .map(WinePropertyName::name)
          .collect(Collectors.joining(", "))
          + " have been selected more than once.");
      popup.addOkButton();
      return false;
    }
    return true;
  }

  /**
   * Makes the column remap list.
   *
   * @param columnNames column names
   * @param rows        rows
   */
  private void makeColumnRemapList(String[] columnNames, List<String[]> rows) {
    dataColumnsContainer.getChildren().clear();
    for (int i = 0; i < columnNames.length; i++) {
      String columnName = columnNames[i];
      // skip if the column name is empty
      if (columnName.isBlank()) {
        continue;
      }

      ObservableList<String> sampleValues = FXCollections.observableArrayList();
      for (int j = 0; j < 5 && j != rows.size(); j++) {
        sampleValues.add(rows.get(j)[i]);
      }

      WinePropertyName possiblePropertyName = WinePropertyName.tryMatch(columnName);
      GridPane wrapper = createImportBox(i, columnName, possiblePropertyName, sampleValues);
      dataColumnsContainer.getChildren().add(wrapper);
    }
  }

  /**
   * Creates an import box.
   *
   * @param index                index
   * @param columnName           column rename
   * @param possiblePropertyName possible property name
   * @param sampleValues         sample values
   * @return created box
   */
  private GridPane createImportBox(int index, String columnName,
      WinePropertyName possiblePropertyName,
      ObservableList<String> sampleValues) {
    GridPane gridPane = new GridPane();
    RowConstraints firstRow = new RowConstraints();
    RowConstraints secondRow = new RowConstraints();
    RowConstraints thirdRow = new RowConstraints();
    ColumnConstraints firstColumn = new ColumnConstraints();
    ColumnConstraints secondColumn = new ColumnConstraints();
    gridPane.getRowConstraints().addAll(firstRow, secondRow, thirdRow);
    gridPane.getColumnConstraints().addAll(firstColumn, secondColumn);
    gridPane.setHgap(10);
    gridPane.setVgap(10);
    gridPane.setPadding(new Insets(10));
    gridPane.setStyle("-fx-background-color: #f3f4f6; -fx-background-radius: 10px;");

    Label columnNameInFile = new Label("Column: " + columnName);
    columnNameInFile.setStyle("-fx-font-weight: bold; -fx-font-size: 16;");
    gridPane.add(columnNameInFile, 0, 0, 2, 1);
    GridPane.setHalignment(columnNameInFile, HPos.CENTER);

    ChoiceBox<WinePropertyName> choiceBox = new ChoiceBox<>(WinePropertyName.VALUES);
    gridPane.add(new Label("Corresponding Property"), 0, 1);
    gridPane.add(choiceBox, 1, 1);
    choiceBox.setOpaqueInsets(new Insets(0, 10, 0, 10));
    choiceBox.setPrefWidth(Double.MAX_VALUE);
    choiceBox.getSelectionModel().selectedItemProperty().addListener(
        (observableValue, before, after) -> selectedWineProperties.put(index, after));
    if (possiblePropertyName != null) {
      choiceBox.getSelectionModel().select(possiblePropertyName);
    }

    Label valuesLabel = new Label("Values from this column");
    valuesLabel.setFont(Font.font(16));

    ListView<String> valuesList = new ListView<>(sampleValues);
    valuesList.setMinHeight(200);
    valuesList.setMaxHeight(200);
    valuesList.setPrefHeight(200);
    valuesList.setCellFactory(param -> new ListCell<>() {
      @Override
      protected void updateItem(String text, boolean empty) {
        super.updateItem(text, empty); // Make sure to call super
        if (empty || text == null || text.isEmpty()) {
          setText(null);
        } else {
          maxWidthProperty().bind(param.widthProperty().subtract(20));
          minWidthProperty().bind(param.widthProperty().subtract(20));
          prefWidthProperty().bind(param.widthProperty().subtract(20));
          setWrapText(true);
          setText(text);
        }
      }
    });

    VBox valuesWrapper = new VBox();
    valuesWrapper.setPadding(new Insets(10, 0, 0, 0));
    valuesWrapper.setAlignment(Pos.CENTER);
    valuesWrapper.getChildren().addAll(valuesLabel, valuesList);
    gridPane.add(valuesWrapper, 0, 2, 2, 1);
    GridPane.setHalignment(valuesWrapper, HPos.CENTER);

    firstColumn.setHgrow(Priority.ALWAYS);
    secondColumn.setHgrow(Priority.ALWAYS);
    firstColumn.setPercentWidth(50);
    secondColumn.setPercentWidth(50);

    GridPane.setFillWidth(gridPane, true);
    return gridPane;
  }
}
