package seng202.team6.gui;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import seng202.team6.enums.WinePropertyName;
import seng202.team6.managers.ManagerContext;
import seng202.team6.model.Wine;
import seng202.team6.util.Exceptions.ValidationException;
import seng202.team6.util.ProcessCSV;
import seng202.team6.util.WineValidator;

public class WineImportController extends Controller {
  @FXML
  private GridPane dataColumnsContainer;

  @FXML
  private CheckBox extractVintageFromTitleCheckbox;

  private final Logger log = LogManager.getLogger(getClass());

  private final Map<Integer, WinePropertyName> selectedWineProperties = new HashMap<>();

  private List<String[]> currentFileRows;

  public WineImportController(ManagerContext context) {
    super(context);
  }

  @Override
  public void init() {
    dataColumnsContainer.getRowConstraints().clear();
    dataColumnsContainer.setHgap(10);
    dataColumnsContainer.setVgap(20);
  }

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

    currentFileRows = ProcessCSV.getCSVRows(selectedFile);
    String[] columnNames = currentFileRows.removeFirst();
    selectedWineProperties.clear();
    makeColumnRemapList(columnNames, currentFileRows);
  }


  @FXML
  void onAppendDataButtonClick() {
    if (!validate())
      return;
    parseWines(false);
  }

  @FXML
  void onReplaceDataButtonClick() {
    if (!validate())
      return;
    parseWines(true);
  }

  private void reset() {
    dataColumnsContainer.getChildren().clear();
    dataColumnsContainer.getRowConstraints().clear();
    dataColumnsContainer.getColumnConstraints().clear();
    extractVintageFromTitleCheckbox.setSelected(false);
    selectedWineProperties.clear();
    currentFileRows = null;
  }

  private void parseWines(boolean replace) {
    List<Wine> parsedWines = new ArrayList<>();
    Map<WinePropertyName, Integer> valid = new HashMap<>() {{
      selectedWineProperties.forEach(((integer, winePropertyName) ->
          put(winePropertyName, integer)));
    }};
    currentFileRows.forEach(row -> {
      try {
        parsedWines.add(WineValidator.parseWine(
            extractPropertyFromRowOrDefault(valid, row, WinePropertyName.TITLE),
            extractPropertyFromRowOrDefault(valid, row, WinePropertyName.VARIETY),
            extractPropertyFromRowOrDefault(valid, row, WinePropertyName.COUNTRY),
            extractPropertyFromRowOrDefault(valid, row, WinePropertyName.REGION),
            extractPropertyFromRowOrDefault(valid, row, WinePropertyName.WINERY),
            extractPropertyFromRowOrDefault(valid, row, WinePropertyName.COLOUR),
            extractPropertyFromRowOrDefault(valid, row, WinePropertyName.VINTAGE),
            extractPropertyFromRowOrDefault(valid, row, WinePropertyName.DESCRIPTION),
            extractPropertyFromRowOrDefault(valid, row, WinePropertyName.SCORE),
            extractPropertyFromRowOrDefault(valid, row, WinePropertyName.ABV),
            extractPropertyFromRowOrDefault(valid, row, WinePropertyName.NZD),
            null
        ));
      } catch (ValidationException e) {
        log.error("Could not parse a wine: {}", e.getMessage());
      }
    });
    log.info("Successfully parsed {} out of {} wines", parsedWines.size(),
      currentFileRows.size());

    if (replace) {
      managerContext.databaseManager.getWineDAO().replaceAll(parsedWines);
    } else {
      managerContext.databaseManager.getWineDAO().addAll(parsedWines);
    }
    reset();
  }

  private String extractPropertyFromRowOrDefault(Map<WinePropertyName, Integer> valid, String[] row,
      WinePropertyName winePropertyName) {
    return valid.containsKey(winePropertyName) ? row[valid.get(winePropertyName)] : "";
  }

  private boolean validate() {
    return checkContainsTitleProperty() && checkDuplicateProperties();
  }

  private boolean checkContainsTitleProperty() {
    if (!selectedWineProperties.containsValue(WinePropertyName.TITLE)) {
      Alert alert = new Alert(AlertType.ERROR);
      alert.setTitle("Invalid Selections");
      alert.setHeaderText("Missing Mandatory Property");
      alert.setContentText("The property TITLE is required but has not been selected");
      alert.showAndWait();
      return false;
    }
    return true;
  }

  private boolean checkDuplicateProperties() {
    Set<WinePropertyName> duplicatedProperties = new HashSet<>();
    Set<WinePropertyName> selectedProperties = new HashSet<>();
    selectedWineProperties.forEach((index, winePropertyName) -> {
      if (!selectedProperties.add(winePropertyName)) { // returns false if the set already contained
        duplicatedProperties.add(winePropertyName);
      }
    });

    if (!duplicatedProperties.isEmpty()) {
      Alert alert = new Alert(AlertType.ERROR);
      alert.setTitle("Invalid Selections");
      alert.setHeaderText("Duplicate Properties Selected");
      alert.setContentText("The property field(s) " + duplicatedProperties.stream()
          .map(WinePropertyName::name)
          .collect(Collectors.joining(", ")) +
          " have been selected more than once.");
      alert.showAndWait();
      return false;
    }
    return true;
  }

  private void makeColumnRemapList(String[] columnNames, List<String[]> rows) {
    int columns = 4;
    int row = 0;
    int column = 0;
    for (int i = 0; i < columnNames.length; i++) {
      String columnName = columnNames[i];
      if (columnName.isBlank()) // skip if the column name is empty
        continue;
      if (column >= columns) {
        addRow();
        row++;
        column = 0;
      }

      ObservableList<String> sampleValues = FXCollections.observableArrayList();
      for (int j = 0; j < 5 && j != rows.size(); j++) {
        sampleValues.add(rows.get(j)[i]);
      }

      WinePropertyName possiblePropertyName = WinePropertyName.tryMatch(columnName);
      GridPane wrapper = createSomeElement(i, columnName, possiblePropertyName, sampleValues);
      dataColumnsContainer.add(wrapper, column, row);
      column++;
    }
  }

  private void addRow() {
    RowConstraints rowConstraint = new RowConstraints();
    rowConstraint.setVgrow(Priority.ALWAYS);
    dataColumnsContainer.getRowConstraints().add(rowConstraint);
  }

  // idk what to call this
  private GridPane createSomeElement(int index, String columnName, WinePropertyName possiblePropertyName,
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
