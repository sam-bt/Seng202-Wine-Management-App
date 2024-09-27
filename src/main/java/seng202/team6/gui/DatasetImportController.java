package seng202.team6.gui;

import java.io.File;
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
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import org.apache.logging.log4j.LogManager;
import seng202.team6.managers.ManagerContext;
import seng202.team6.model.Wine;
import seng202.team6.util.Exceptions.ValidationException;
import seng202.team6.util.ProcessCSV;
import seng202.team6.util.WineValidator;

/**
 * This class handles importing existing data into the managers
 *
 * @author Samuel Beattie
 * @author Angus McDougall
 */
public class DatasetImportController extends Controller {

  /**
   * Names of columns
   */
  private final PrettyName[] prettyNames = new PrettyName[]{
      PrettyName.NIL,
      PrettyName.TITLE,
      PrettyName.VARIETY,
      PrettyName.COUNTRY,
      PrettyName.WINERY,
      PrettyName.REGION,
      PrettyName.COLOR,
      PrettyName.VINTAGE,
      PrettyName.DESCRIPTION,
      PrettyName.SCORE,
      PrettyName.ABV,
      PrettyName.NZD,
  };
  /**
   * List of column name buttons
   */
  ArrayList<ChoiceBox<PrettyName>> columnNames = new ArrayList<>();
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
   * Array of rows of current csv
   * <p>
   * Might be null
   * </p>
   */
  private List<String[]> selectedTable;

  /**
   * Constructor
   *
   * @param managerContext manager context
   */
  public DatasetImportController(ManagerContext managerContext) {
    super(managerContext);
  }

  /**
   * Checks that all remap columns are in a valid state
   * <p>
   * A state is valid if: - Only one column of each type is selected - The title is selected
   * </p>
   *
   * @return if state is valid
   */
  boolean isValidRemapping() {
    // Check for each box if there are any others with same value
    for (int i = 0; i < columnNames.size(); i++) {
      PrettyName name = columnNames.get(i).getValue();
      if (name == null || name == PrettyName.NIL) {
        continue;
      }

      for (int j = 0; j < columnNames.size(); j++) {
        if (i != j) {
          if (columnNames.get(j).getValue() == name) {
            return false;
          }
        }
      }
    }
    // Check there is a title box
    boolean containsTitle = columnNames.stream()
        .anyMatch(stringChoiceBox -> stringChoiceBox.getValue() == PrettyName.TITLE);
    return containsTitle;

  }

  /**
   * Gets column idx from name
   * <p>
   * requires each column name to be unique
   * </p>
   *
   * @param name name
   * @return index
   */
  int getRenamedColumn(PrettyName name) {
    for (int i = 0; i < columnNames.size(); i++) {
      if (columnNames.get(i).getValue() == name) {
        return i;
      }
    }
    return -1;
  }

  /**
   * Gets a list of wines from the currently selected table
   *
   * @return list of wines
   * @throws ValidationException if validation error
   */
  ArrayList<Wine> getWinesFromTable() throws ValidationException {

    // It is possible value might be null. Validator should catch if attribute is needed
    int title = getRenamedColumn(PrettyName.TITLE);
    int variety = getRenamedColumn(PrettyName.VARIETY);
    int country = getRenamedColumn(PrettyName.COUNTRY);
    int region = getRenamedColumn(PrettyName.REGION);
    int winery = getRenamedColumn(PrettyName.WINERY);
    int color = getRenamedColumn(PrettyName.COLOR);
    int vintage = getRenamedColumn(PrettyName.VINTAGE);
    int description = getRenamedColumn(PrettyName.DESCRIPTION);
    int score = getRenamedColumn(PrettyName.SCORE);
    int abv = getRenamedColumn(PrettyName.ABV);
    int nzd = getRenamedColumn(PrettyName.NZD);

    ArrayList<Wine> wines = new ArrayList<>();
    for (int row = 1; row < selectedTable.size(); row++) {

      String[] tuple = selectedTable.get(row);

      wines.add(WineValidator.parseWine(
          managerContext.databaseManager,
          title != -1 ? tuple[title] : "",
          variety != -1 ? tuple[variety] : "",
          country != -1 ? tuple[country] : "",
          region != -1 ? tuple[region] : "",
          winery != -1 ? tuple[winery] : "",
          color != -1 ? tuple[color] : "",
          vintage != -1 ? tuple[vintage] : "",
          description != -1 ? tuple[description] : "",
          score != -1 ? tuple[score] : "",
          abv != -1 ? tuple[abv] : "",
          nzd != -1 ? tuple[nzd] : "",
          null
      ));
    }
    return wines;

  }

  /**
   * Checks if each row is suitable to construct a wine with
   *
   * @return if all rows are valid
   */
  boolean validateColumnValues() {
    if (selectedTable == null) {
      return false;
    }

    try {
      getWinesFromTable();
    } catch (Exception ignored) {
      return false;
    }

    return true;
  }

  /**
   * Updates the state of if this selection of rows to remap is valid
   */
  void updateValidation() {
    // Check all option boxes only correspond to one
    if (!isValidRemapping() || !validateColumnValues()) {
      appendButton.setDisable(true);
      replaceButton.setDisable(true);
      return;
    }
    appendButton.setDisable(false);
    replaceButton.setDisable(false);
  }

  /**
   * Makes the option box
   *
   * @param name name to maybe preselect
   * @return option box
   */
  private Node makeOptionBox(String name) {

    ChoiceBox<PrettyName> choiceBox = new ChoiceBox<>();
    choiceBox.getItems().addAll(prettyNames);
    choiceBox.setMaxWidth(Double.MAX_VALUE);
    choiceBox.setValue(PrettyName.NIL);
    choiceBox.setConverter(new StringConverter<>() {
      @Override
      public String toString(PrettyName prettyName) {
        if (prettyName == null) {
          return "";
        }
        return prettyName.getName();
      }

      @Override
      public PrettyName fromString(String s) {
        return null;
      }
    });

    for (PrettyName prettyName : prettyNames) {
      if (prettyName.getName().compareToIgnoreCase(name) == 0) {
        choiceBox.setValue(prettyName);
        break;
      }
    }

    choiceBox.setOnAction(actionEvent -> updateValidation());

    columnNames.add(choiceBox);
    return choiceBox;
  }

  /**
   * Makes a column for previewing and remapping
   *
   * @param name   column name
   * @param values preview values
   * @return column
   */
  public Node makeRemapColumn(String name, String[] values) {
    VBox vbox = new VBox();

    vbox.setAlignment(Pos.CENTER_LEFT);
    vbox.getChildren().add(new Label(name));
    vbox.getChildren().add(makeOptionBox(name));
    for (String value : values) {
      vbox.getChildren().add(new Label(value));
    }
    vbox.getChildren().add(new Label("..."));

    return vbox;
  }

  /**
   * Makes a list of columns for remapping
   *
   * @param columnNames names of columns
   * @param rows        list of rows
   */
  private void makeColumnRemapList(String[] columnNames, List<String[]> rows) {

    columnRemapList.getChildren().clear();
    this.columnNames.clear();
    for (int i = 0; i < columnNames.length; i++) {
      // First row
      String[] column = new String[rows.size()];
      for (int j = 0; j < rows.size(); j++) {
        column[j] = rows.get(j)[i];
      }

      columnRemapList.getChildren().add(makeRemapColumn(columnNames[i], column));
    }
  }

  void clearMainScreen() {
    selectedTable = null;
    columnRemapList.getChildren().clear();
    this.columnNames.clear();

    updateValidation();
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
    if (selectedFile == null) {
      return;
    }
    try {
      // Should be first row on pretty much all files
      List<String[]> rows = ProcessCSV.getCSVRows(selectedFile);
      String[] columnNames = rows.getFirst();
      makeColumnRemapList(columnNames, rows.subList(1, Math.min(10, rows.size())));
      selectedTable = rows;
    } catch (Exception exception) {
      LogManager.getLogger(getClass())
          .error("Failed to read CSV file: {}", selectedFile.getAbsolutePath(), exception);
    }

    updateValidation();
  }

  /**
   * Called to append the current file to the database
   */
  public void appendCSVFile() {
    try {
      List<Wine> wines = getWinesFromTable();
      managerContext.databaseManager.getWineDAO().addAll(wines);
    } catch (Exception e) {
      LogManager.getLogger(getClass()).error("Expected wines to be valid", e);
    }
    clearMainScreen();
  }

  /**
   * Called to replace the current database with this file
   */
  public void replaceCSVFile() {
    try {
      List<Wine> wines = getWinesFromTable();
      managerContext.databaseManager.getWineDAO().replaceAll(wines);
    } catch (Exception e) {
      LogManager.getLogger(getClass()).error("Expected wines to be valid", e);
    }
    clearMainScreen();
  }

  /**
   * Enum for column renaming names
   */
  enum PrettyName {
    NIL("NOT AVAILABLE"),
    TITLE("Title"),
    VARIETY("Variety"),
    COUNTRY("Country"),
    REGION("Region"),
    WINERY("Winery"),
    COLOR("Color"),
    VINTAGE("Vintage"),
    DESCRIPTION("Description"),
    SCORE("Score"),
    ABV("ABV"),
    NZD("NZD");

    private final String name;

    PrettyName(String prettyName) {
      this.name = prettyName;
    }

    public String getName() {
      return name;
    }
  }
}
