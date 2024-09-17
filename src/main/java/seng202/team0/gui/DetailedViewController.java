package seng202.team0.gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import seng202.team0.database.Wine;
import seng202.team0.database.WineList;
import seng202.team0.managers.ManagerContext;
import seng202.team0.util.RegexProcessor;

import java.awt.event.ActionEvent;
import java.sql.SQLException;
import java.util.stream.Collectors;

public class DetailedViewController extends Controller {


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
    private ChoiceBox choiceBoxListSelector;

    @FXML
    private Button addToListButton;

    @FXML
    private Label errorText;

    private Wine wine;

    private RegexProcessor extractor = new RegexProcessor();


    public DetailedViewController(ManagerContext context) {
        super(context);
    }

    @FXML
    public void init() {
        if(managerContext.authenticationManager.isAuthenticated()) {
            addToListButton.setVisible(true);
            choiceBoxListSelector.setVisible(true);
            String user = managerContext.authenticationManager.getUsername();
            ObservableList<WineList> list = FXCollections.observableList(managerContext.databaseManager.getUserLists(user));
            choiceBoxListSelector.setItems(list);
            choiceBoxListSelector.setValue(list.getFirst());
        } else {
            addToListButton.setVisible(false);
            choiceBoxListSelector.setVisible(false);
        }
        errorText.setVisible(false);
    }


    @FXML
    public void onAddToListButton() {
        WineList selectedWineList = (WineList) choiceBoxListSelector.getValue();
        if (managerContext.databaseManager.isWineInList(selectedWineList, wine)) {
            errorText.setVisible(true);
            errorText.setText("Wine Already in list " + selectedWineList);
            System.out.println("Error AAdding to list");
        } else {
            errorText.setVisible(false);
            managerContext.databaseManager.addWineToList(selectedWineList, wine);
            System.out.println("AAAAdding to list");
        }

    }

    @FXML
    void onSaveNoteClicked(){
        try {
            managerContext.databaseManager.writeNoteToTable(notesArea.getText(), wine.getKey(), managerContext.authenticationManager.getUsername());
            System.out.println("Note Saved");
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error saving note");
        }
    }

    private String getNote(long wineID) {
        String uname = managerContext.authenticationManager.getUsername();
        try {
            String note = managerContext.databaseManager.getNoteByUserAndWine(uname, wineID);
            return note;
        } catch (SQLException e) {
            System.out.println(e);
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
