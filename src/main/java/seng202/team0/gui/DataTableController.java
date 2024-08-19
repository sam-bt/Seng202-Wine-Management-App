package seng202.team0.gui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import seng202.team0.managers.ManagerContext;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;


/**
 * Data Table Controller (MORE DETAIL HERE!)
 *
 * @author Samuel Beattie
 * @author Angus McDougall
 */
public class DataTableController extends Controller {

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

  /**
   * Triggers the extension to upload a file when the upload csv button is pressed
   */
  public void uploadCSVFile() {
    FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle("Open CSV File");
    fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));

    Stage stage = (Stage) uploadCSVButton.getScene().getWindow();
    File selectedFile = fileChooser.showOpenDialog(stage);

    if (selectedFile != null) {
      processCSVFile(selectedFile);
    }
  }

  /**
   * Stand-in function to process csv files (will be replaced when functionality added)
   */
  public void processCSVFile(File file) {
    System.out.println("file processing");
  }

}
