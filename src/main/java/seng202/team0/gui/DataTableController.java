package seng202.team0.gui;

import java.util.ArrayList;
import java.util.List;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.apache.logging.log4j.LogManager;
import seng202.team0.managers.ManagerContext;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;
import seng202.team0.util.ProcessCSV;

/**
 * This class handles importing existing data into the managers
 *
 * @author Samuel Beattie
 * @author Angus McDougall
 */
public class DataTableController extends Controller {

  /**
   * HBox that lists columns for mapping
   */
  @FXML
  private HBox columnRemapList;

  /**
   * Import button
   */
  @FXML
  private Button importCSVButton;
  /**
   * Append button
   */
  @FXML
  private Button appendButton;
  /**
   * Replace button
   */
  @FXML
  private Button replaceButton;

  /**
   * Constructor
   *
   * @param managerContext manager context
   */
  public DataTableController(ManagerContext managerContext) {
    super(managerContext);
  }

  /**
   * Makes the option box
   * @param name name to maybe preselect
   * @return option box
   */
  private Node makeOptionBox(String name) {
    String[] prettyNames = new String[]{
        "Title",
        "Variety",
        "Country",
        "Winery",
        "Description",
        "Score",
        "ABV",
        "NZD",
        ""
    };
    ChoiceBox<String> choiceBox = new ChoiceBox<>();
    choiceBox.getItems().addAll(prettyNames);
    choiceBox.setMaxWidth(Double.MAX_VALUE);
    for(String prettyName : prettyNames){
      if(prettyName.compareToIgnoreCase(name) == 0) {
        choiceBox.setValue(prettyName);
        break;
      }
    }
    return choiceBox;
  }


  /**
   * Makes a column for previewing and remapping
   * @param name column name
   * @param values preview values
   * @return column
   */
  public Node makeRemapColumn(String name, String[] values){
    VBox vbox = new VBox();

    vbox.setAlignment(Pos.CENTER_LEFT);
    vbox.getChildren().add(new Label(name));
    vbox.getChildren().add(makeOptionBox(name));
    for(String value : values) {
      vbox.getChildren().add(new Label(value));
    }
    vbox.getChildren().add(new Label("..."));

    return vbox;
  }


  /**
   * Makes a list of columns for remapping
   * @param columnNames names of columns
   * @param rows list of rows
   */
  private void makeColumnRemapList(String[] columnNames, List<String[]> rows){

    columnRemapList.getChildren().clear();
    for(int i=0; i < columnNames.length; i++) {
      // First row
      String[] column = new String[rows.size()];
      for(int j=0; j < rows.size(); j++){
        column[j] = rows.get(j)[i];
      }

      columnRemapList.getChildren().add(makeRemapColumn(columnNames[i], column));
    }
  }

  /**
   * Triggers the extension to import a file when the upload csv button is pressed
   */
  public void importCSVFile() {
    FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle("Open CSV File");
    fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));

    Stage stage = (Stage) importCSVButton.getScene().getWindow();
    File selectedFile = fileChooser.showOpenDialog(stage);
    if (selectedFile == null)
      return;
    try{
      // Should be first row on pretty much all files
      ArrayList<String[]> rows = ProcessCSV.getCSVRows(selectedFile);

      String[] columnNames = rows.getFirst();
      makeColumnRemapList(columnNames, rows.subList(1, Math.min(10, rows.size())));

    } catch(Exception exception) {
      LogManager.getLogger(getClass())
          .error("Failed to read CSV file: {}", selectedFile.getAbsolutePath(), exception);
    }
  }

  /**
   * Called to append the current file to the database
   */
  public void appendCSVFile() {
    // TODO validate + stubs
  }

  /**
   * Called to replace the current database with this file
   */
  public void replaceCSVFile() {

  }
}
