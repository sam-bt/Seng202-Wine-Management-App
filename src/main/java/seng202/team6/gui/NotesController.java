package seng202.team6.gui;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import seng202.team6.dao.AggregatedDao;
import seng202.team6.managers.ManagerContext;
import seng202.team6.model.Note;
import seng202.team6.model.User;
import seng202.team6.model.Wine;


public class NotesController extends Controller {

  @FXML
  private TextArea noteArea;

  @FXML
  private TableView<Map.Entry<Wine, Note>> notesTable;

  @FXML
  private Label wineTitle;

  @FXML
  private Button deleteButton;

  @FXML
  private Button saveButton;

  private Note openedNote;
  private Wine noteWine;

  public NotesController(ManagerContext managerContext) {
    super(managerContext);
  }

  public void init() {
    populateTable();
    deleteButton.setDisable(true);
    saveButton.setDisable(true);
  }

  /**
   * Populates table columns using the wineTitle string property in the Note object. Called to
   * refresh the notes in the table.
   */
  private void populateTable() {
    User user = managerContext.authenticationManager.getAuthenticatedUser();
    AggregatedDao aggregatedDao = managerContext.databaseManager.getAggregatedDao();
    ObservableMap<Wine, Note> allNotesMappedWithWinesByUser = aggregatedDao
        .getAllNotesMappedWithWinesByUser(user);
    ObservableList<Map.Entry<Wine, Note>> noteList = FXCollections.observableArrayList(
        allNotesMappedWithWinesByUser.entrySet());
    setupColumns();
    notesTable.getItems().clear();
    notesTable.setItems(noteList);
    notesTable.setOnMouseClicked(this::openNoteOnClick);
  }

  /**
   * Sets up the table's only column.
   */
  private void setupColumns() {
    notesTable.getColumns().clear();

    TableColumn<Map.Entry<Wine, Note>, String> wineTitleColumn = new TableColumn<>("Wine");
    wineTitleColumn.setCellValueFactory(cellData ->
        cellData.getValue().getKey().titleProperty());
    wineTitleColumn.setMinWidth(500);
    wineTitleColumn.setResizable(false);
    notesTable.getColumns().add(wineTitleColumn);
  }

  /**
   * Clears the right hand side of the page: Removes text from the notetaking field Resets the title
   * to 'No note selected' Used when a note is deleted so that it doesn't stick around.
   */
  private void clearNotesPanel() {
    noteArea.clear();
    wineTitle.setText("No note selected");
    deleteButton.setDisable(true);
    saveButton.setDisable(true);
  }

  @FXML
  public void openNoteOnClick(MouseEvent event) {
    Entry<Wine, Note> selectedItem = notesTable.getSelectionModel().getSelectedItem();
      if (selectedItem == null) {
          return;
      }

    openedNote = selectedItem.getValue();
    noteWine = selectedItem.getKey();
    wineTitle.setText(noteWine.getTitle());
    noteArea.setText(openedNote.getNote());
    saveButton.setDisable(false);
    deleteButton.setDisable(false);
  }

  @FXML
  public void onSaveClicked() {
    openedNote.setNote(noteArea.getText());
  }

  @FXML
  public void onDeleteClicked() {
    Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
    confirmation.setTitle("Confirm Deletion");
    confirmation.setHeaderText("Deleting: " + noteWine.getTitle());
    confirmation.setContentText("Are you sure you want to delete this note?");

    Optional<ButtonType> result = confirmation.showAndWait();
    if (result.isPresent() && result.get() == ButtonType.OK) {
      // setting the note to empty will trigger the database to delete the note
      openedNote.setNote("");
      populateTable();
      clearNotesPanel();
    }
  }
}
