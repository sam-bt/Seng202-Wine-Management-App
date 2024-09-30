package seng202.team6.gui;

import java.io.File;
import java.util.List;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import seng202.team6.enums.WinePropertyName;
import seng202.team6.managers.ManagerContext;
import seng202.team6.util.ProcessCSV;

public class AdminController extends Controller {

  @FXML
  Label adminMessageLabel;

  @FXML
  Button yesButton;

  @FXML
  Button noButton;

  @FXML
  Button deleteButton;

  @FXML
  private GridPane dataColumnsContainer;

  public AdminController(ManagerContext managerContext) {
    super(managerContext);
  }

  public void initialize() {
    noButton.setVisible(false);
    yesButton.setVisible(false);

    dataColumnsContainer.getRowConstraints().clear();
    dataColumnsContainer.setHgap(10);
    dataColumnsContainer.setVgap(10);

    makeColumnRemapList(new String[]{"Name", "Country", "Region", "Colour", "Test"}, List.of());

//    dataColumnsContainer.add(createSomeElement("Title"), 0, 0);
//    dataColumnsContainer.add(createSomeElement("Country"), 1, 0);
//    dataColumnsContainer.add(createSomeElement("Colour"), 2, 0);

  }

  @FXML
  private void onDeleteMembers() {
    deleteButton.setDisable(true);
    noButton.setVisible(true);
    yesButton.setVisible(true);
    deleteButton.setText("Are you sure?");
  }

  @FXML
  private void onYes() {
    managerContext.databaseManager.deleteAllUsers();
    managerContext.GUIManager.mainController.openWineScreen();
  }

  @FXML
  private void onNo() {
    deleteButton.setDisable(false);
    noButton.setVisible(false);
    yesButton.setVisible(false);
    deleteButton.setText("Delete all Users");
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

    List<String[]> rows =ProcessCSV.getCSVRows(selectedFile);
    String[] columnNames = rows.removeFirst();
    makeColumnRemapList(columnNames, rows);
  }

  private void makeColumnRemapList(String[] columnNames, List<String[]> rows) {
    int columns = 3;
    int row = 0;
    for (int i = 0; i < columnNames.length; i++) {
      String columnName = columnNames[i];
      int column = i % columns;
      if (i % columns == 0) {
        addRow();
        row++;
      }

      WinePropertyName possiblePropertyName = WinePropertyName.tryMatch(columnName);
      GridPane wrapper = createSomeElement(columnName, possiblePropertyName);
      dataColumnsContainer.add(wrapper, column, row);
    }
  }

  private void addRow() {
    RowConstraints rowConstraint = new RowConstraints();
    rowConstraint.setVgrow(Priority.ALWAYS);
    dataColumnsContainer.getRowConstraints().add(rowConstraint);
  }

  // idk what to call this
  private GridPane createSomeElement(String columnName, WinePropertyName possiblePropertyName) {
    GridPane gridPane = new GridPane();
    RowConstraints firstRow = new RowConstraints();
    RowConstraints secondRow = new RowConstraints();
    ColumnConstraints firstColumn = new ColumnConstraints();
    ColumnConstraints secondColumn = new ColumnConstraints();
    gridPane.getRowConstraints().addAll(firstRow, secondRow);
    gridPane.getColumnConstraints().addAll(firstColumn, secondColumn);
    gridPane.setHgap(10);
    gridPane.setVgap(10);
    gridPane.setPadding(new Insets(10));
    gridPane.setStyle("-fx-background-color: #f3f4f6; -fx-background-radius: 10px;");

    Label columnNameInFile = new Label("Column Name in File: " + columnName);
    columnNameInFile.setFont(Font.font(16));
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

    firstColumn.setHgrow(Priority.ALWAYS);
    secondColumn.setHgrow(Priority.ALWAYS);
    firstColumn.setPercentWidth(50);
    secondColumn.setPercentWidth(50);

    GridPane.setFillWidth(gridPane, true);
    return gridPane;
  }
}
