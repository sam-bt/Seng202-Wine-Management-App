package seng202.team6.gui;

import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import seng202.team6.model.Wine;
import seng202.team6.model.WineList;
import seng202.team6.managers.ManagerContext;
import seng202.team6.service.AuthenticationService;
import seng202.team6.util.RegexProcessor;

import java.awt.event.ActionEvent;
import java.sql.SQLException;
import java.util.stream.Collectors;

public class DetailedViewController extends Controller {

    private final Logger log = LogManager.getLogger(getClass());
    private final AuthenticationService authenticationService;

    @FXML
    private Label countryLabel;

    @FXML
    private TextArea descriptionArea;

    @FXML
    private TextArea notesArea;

    @FXML
    private ProgressIndicator scoreWheel;

    @FXML
    private Label wineNameLabel;

    @FXML
    private Label wineryLabel;

    @FXML
    private Label yearLabel;

    @FXML
    private ChoiceBox<WineList> choiceBoxListSelector;

    @FXML
    private Button addToListButton;

    @FXML
    private Button deleteFromListButton;

    @FXML
    private Label errorText;

    @FXML
    private Button saveNoteButton;


    private Wine wine;

    private RegexProcessor extractor = new RegexProcessor();


    public DetailedViewController(ManagerContext context, AuthenticationService authenticationService) {
        super(context);
        this.authenticationService = authenticationService;
    }

    @FXML
    public void init() {
        if(authenticationService.isAuthenticated()) {
            addToListButton.setVisible(true);
            choiceBoxListSelector.setVisible(true);
            notesArea.setVisible(true);
            saveNoteButton.setVisible(true);
            String user = authenticationService.getAuthenticatedUsername();
            ObservableList<WineList> list = FXCollections.observableList(managerContext.databaseManager.getUserLists(user));
            choiceBoxListSelector.setItems(list);
            choiceBoxListSelector.setValue(list.getFirst());
            choiceBoxListSelector.getSelectionModel().selectedIndexProperty().addListener((property, oldIndex, newIndex) -> {
                WineList selectedList = list.get(newIndex.intValue());
                updateListButton(selectedList);
            });
            updateListButton(list.getFirst());
        } else {
            addToListButton.setVisible(false);
            choiceBoxListSelector.setVisible(false);
            notesArea.setVisible(false);
            saveNoteButton.setVisible(false);
        }
        errorText.setVisible(false);
    }

    private void updateListButton(WineList selectedList) {
        if (managerContext.databaseManager.isWineInList(selectedList, wine)) {
            deleteFromListButton.setDisable(false);
            addToListButton.setDisable(true);

            deleteFromListButton.setVisible(true);
            addToListButton.setVisible(false);
        } else {
            addToListButton.setDisable(false);
            deleteFromListButton.setDisable(true);

            addToListButton.setVisible(true);
            deleteFromListButton.setVisible(false);
        }
    }

    @FXML
    public void onAddToListButton() {
        // button is only shown if the wine is not in the list
        WineList selectedWineList = choiceBoxListSelector.getValue();
        managerContext.databaseManager.addWineToList(selectedWineList, wine);
        updateListButton(selectedWineList);
    }

    @FXML
    public void onDeleteFromListButton() {
        WineList selectedWineList = choiceBoxListSelector.getValue();
        managerContext.databaseManager.deleteWineFromList(selectedWineList, wine);
        updateListButton(selectedWineList);
    }

    @FXML
    void onSaveNoteClicked(){
        try {
            managerContext.databaseManager.writeNoteToTable(notesArea.getText(), wine.getKey(), authenticationService.getAuthenticatedUsername());
        } catch (SQLException e) {
            log.error("Could not save note", e);
        }
    }

    private String getNote(long wineID) {
        String uname = authenticationService.getAuthenticatedUsername();
        try {
          return managerContext.databaseManager.getNoteByUserAndWine(uname, wineID);
        } catch (SQLException e) {
            return "NO NOTE FOUND";
        }
    }

    public void setWine(Wine wine) {
        this.wine = new Wine(wine.getKey(), managerContext.databaseManager, wine.getTitle(), wine.getVariety(), "", "", wine.getWinery(), wine.getColor(), 1000, wine.getDescription(), 0, 0.0f, 0.0f, null);
        wineNameLabel.setText(wine.getTitle());
        if (wine.getVintage() == 0) {
            yearLabel.setText(extractor.extractYearFromString(wine.getTitle()));
        } else {
            yearLabel.setText(String.valueOf(wine.getVintage()));
        }
        countryLabel.setText(wine.getCountry());
        descriptionArea.setText(wine.getDescription());
        wineryLabel.setText(wine.getWinery());
        double score = wine.getScorePercent();
        score /= 100.0;
        scoreWheel.setProgress(score);
        notesArea.setText(getNote(wine.getKey()));
    }
}
