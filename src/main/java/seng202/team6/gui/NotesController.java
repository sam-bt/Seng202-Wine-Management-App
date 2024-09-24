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

public class NotesController extends Controller{
    @FXML
    private TextArea noteArea;

    @FXML
    private TableView<Note> notesTable;

    @FXML
    private Label wineTitle;

    private ManagerContext managerContext;
    private AuthenticationService authenticationService;

    public NotesController(ManagerContext managerContext, AuthenticationService authenticationService) {
        super(managerContext);
        this.managerContext = managerContext;
        this.authenticationService = authenticationService;
    }


    public void init() {
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

        notesTable.getColumns().add(titleColumn);
    }

    @FXML
    public void openNoteOnClick(MouseEvent event) {
        if (event.getClickCount() == 2) {
            Note thisNote = notesTable.getSelectionModel().getSelectedItem();
            wineTitle.setText(thisNote.getWineTitle());
            noteArea.setText(thisNote.getNote());
        }
    }
}
