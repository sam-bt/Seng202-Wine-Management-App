package seng202.team6.gui;

import java.util.Optional;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import seng202.team6.managers.ManagerContext;
import seng202.team6.model.Note;
import seng202.team6.model.User;


public class NotesController extends Controller{
    @FXML
    private TextArea noteArea;

    @FXML
    private TableView<Note> notesTable;

    @FXML
    private Label wineTitle;

    @FXML
    private Button deleteButton;

    @FXML
    private Button saveButton;

    private Note note;

    public NotesController(ManagerContext managerContext) {
        super(managerContext);
    }


    public void init() {
        populateTable();
        deleteButton.setDisable(true);
        saveButton.setDisable(true);
    }

    /**
     * Populates table columns using the wineTitle string property in the Note object. Called to refresh the notes in the table.
     */
    private void populateTable() {
        User user = managerContext.authenticationManager.getAuthenticatedUser();
        ObservableList<Note> noteList = managerContext.databaseManager.getWineNotesDAO().getAll(user);
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

        TableColumn<Note, String> titleColumn = new TableColumn<>("Wine");

        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        titleColumn.setMinWidth(500);
        titleColumn.setResizable(false);

        notesTable.getColumns().add(titleColumn);
    }

    /**
     * Clears the right hand side of the page:
     *      Removes text from the notetaking field
     *      Resets the title to 'No note selected'
     *      Used when a note is deleted so that it doesn't stick around.
     */
    private void clearNotesPanel() {
        noteArea.clear();
        wineTitle.setText("No note selected");
        deleteButton.setDisable(true);
        saveButton.setDisable(true);
    }

    @FXML
    public void openNoteOnClick(MouseEvent event) {
        if (event.getClickCount() == 2) {
            note = notesTable.getSelectionModel().getSelectedItem();
            if (note == null)
                return;
            // todo - come up with a solution to this
//            wineTitle.setText(note.getWineTitle());
            noteArea.setText(note.getNote());
            saveButton.setDisable(false);
            deleteButton.setDisable(false);
        }
    }

    @FXML
    public void onSaveClicked() {
        // todo - save using beans
//        managerContext.databaseManager.saveNote(note.getWineID(),
//            managerContext.authenticationManager.getAuthenticatedUsername(), noteArea.getText());
        populateTable();

    }

    @FXML
    public void onDeleteClicked() {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirm Deletion");
        // todo - come up with a solution to this
//        confirmation.setHeaderText("Deleting: " + note.getWineTitle());
        confirmation.setContentText("Are you sure you want to delete this note?");

        Optional<ButtonType> result = confirmation.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            managerContext.databaseManager.getWineNotesDAO().delete(note);
            populateTable();
            clearNotesPanel();
        }
    }
}
