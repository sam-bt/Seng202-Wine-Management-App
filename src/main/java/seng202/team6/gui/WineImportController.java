package seng202.team6.gui;

import java.io.File;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
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
import seng202.team6.enums.WinePropertyName;
import seng202.team6.managers.ManagerContext;
import seng202.team6.util.ProcessCSV;

public class WineImportController extends Controller {
  @FXML
  private GridPane dataColumnsContainer;

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

    List<String[]> rows = ProcessCSV.getCSVRows(selectedFile);
    String[] columnNames = rows.removeFirst();
    makeColumnRemapList(columnNames, rows);
  }

  private void makeColumnRemapList(String[] columnNames, List<String[]> rows) {
    int columns = 4;
    int row = 0;
    for (int i = 0; i < columnNames.length; i++) {
      String columnName = columnNames[i];
      int column = i % columns;
      if (i % columns == 0) {
        addRow();
        row++;
      }

      ObservableList<String> sampleValues = FXCollections.observableArrayList();
      for (int j = 0; j < 5 && j != rows.size(); j++) {
        sampleValues.add(rows.get(j)[i]);
      }

      WinePropertyName possiblePropertyName = WinePropertyName.tryMatch(columnName);
      GridPane wrapper = createSomeElement(columnName, possiblePropertyName, sampleValues);
      dataColumnsContainer.add(wrapper, column, row);
    }
  }

  private void addRow() {
    RowConstraints rowConstraint = new RowConstraints();
    rowConstraint.setVgrow(Priority.ALWAYS);
    dataColumnsContainer.getRowConstraints().add(rowConstraint);
  }

  // idk what to call this
  private GridPane createSomeElement(String columnName, WinePropertyName possiblePropertyName,
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
