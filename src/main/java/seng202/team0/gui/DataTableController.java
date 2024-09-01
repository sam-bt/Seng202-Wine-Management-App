package seng202.team0.gui;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import seng202.team0.managers.ManagerContext;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;
import seng202.team0.util.ProcessCSV;

/**
 * Data Table Controller (MORE DETAIL HERE!)
 *
 * @author Samuel Beattie
 * @author Angus McDougall
 */
public class DataTableController extends Controller {

  @FXML
  private HBox columnRemapList;

  /**
   * Constructor
   *
   * @param managerContext manager context
   */
  public DataTableController(ManagerContext managerContext) {
    super(managerContext);
  }

  @FXML
  private Button uploadCSVButton;

  private Node getOptionLabel() {
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
    return choiceBox;
  }


  public Node makeRemapColumn(String string){
    VBox vbox = new VBox();

    vbox.setAlignment(Pos.CENTER);
    Label text = new Label(string);
    vbox.getChildren().add(text);
    vbox.getChildren().add(getOptionLabel());

    return vbox;
  }
  private void makeColumnRemapList(String[] columns){
    columnRemapList.getChildren().clear();
    for(String columnName : columns) {
      columnRemapList.getChildren().add(makeRemapColumn(columnName));
    }
  }

  /**
   * Triggers the extension to upload a file when the upload csv button is pressed
   */
  public void uploadCSVFile() {
    FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle("Open CSV File");
    fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));

    Stage stage = (Stage) uploadCSVButton.getScene().getWindow();
    File selectedFile = fileChooser.showOpenDialog(stage);
    try{
      String[] columnNames = ProcessCSV.getColumnNames(selectedFile);
      makeColumnRemapList(columnNames);

    } catch(Exception ignored) {

    }
    if (selectedFile != null) {
      ProcessCSV.processFile(selectedFile);
    }
  }

}
