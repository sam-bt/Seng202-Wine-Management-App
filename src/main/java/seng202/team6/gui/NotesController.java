package seng202.team6.gui;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import seng202.team6.managers.ManagerContext;
import seng202.team6.model.Note;
import seng202.team6.service.AuthenticationService;

import java.io.IOException;
import java.util.Optional;

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
    private ManagerContext managerContext;
    private AuthenticationService authenticationService;

    public NotesController(ManagerContext managerContext, AuthenticationService authenticationService) {
        super(managerContext);
        this.managerContext = managerContext;
        this.authenticationService = authenticationService;
    }


    public void init() {
        populateTable();
        deleteButton.setDisable(true);
        saveButton.setDisable(true);
    }

    private void populateTable() {
        ObservableList<Note> noteList = managerContext.databaseManager.getNotesByUser(authenticationService.getAuthenticatedUsername());
        setupColumns();
        notesTable.getItems().clear();
        notesTable.setItems(noteList);
        notesTable.setOnMouseClicked(this::openNoteOnClick);
    }

    private void setupColumns() {
        notesTable.getColumns().clear();

        TableColumn<Note, String> titleColumn = new TableColumn<>("Title");

        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        titleColumn.setMinWidth(500);
        titleColumn.setResizable(false);

        notesTable.getColumns().add(titleColumn);
    }

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
            wineTitle.setText(note.getWineTitle());
            noteArea.setText(note.getNote());
            saveButton.setDisable(false);
            deleteButton.setDisable(false);
        }
    }

    @FXML
    public void onSaveClicked() {
        managerContext.databaseManager.updateExistingNote(note.getWineID(), authenticationService.getAuthenticatedUsername(), noteArea.getText());
        populateTable();

    }

    @FXML
    public void onDeleteClicked() {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirm Deletion");
        confirmation.setHeaderText("Deleting: " + note.getWineTitle());
        confirmation.setContentText("Are you sure you want to delete this note?");

        Optional<ButtonType> result = confirmation.showAndWait();
        if (result.get() == ButtonType.OK) {
            managerContext.databaseManager.deleteNote(note.getWineID(), authenticationService.getAuthenticatedUsername());
            populateTable();
            clearNotesPanel();
        }

    }
}
