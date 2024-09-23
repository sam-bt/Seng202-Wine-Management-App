package seng202.team6.gui;

import javafx.fxml.FXML;
import javafx.scene.control.ListView;

import javafx.scene.input.MouseEvent;
import seng202.team6.managers.ManagerContext;
import seng202.team6.service.AuthenticationService;

import java.io.IOException;

public class NotesController extends Controller{
    @FXML
    private ListView<String> notesList;

    private ManagerContext managerContext;
    private AuthenticationService authenticationService;

    public NotesController(ManagerContext managerContext, AuthenticationService authenticationService) {
        super(managerContext);
        this.managerContext = managerContext;
        this.authenticationService = authenticationService;
    }


    public void init() {
        notesList.setItems(managerContext.databaseManager.getNotesByUser(authenticationService.getAuthenticatedUsername()));
    }
}
