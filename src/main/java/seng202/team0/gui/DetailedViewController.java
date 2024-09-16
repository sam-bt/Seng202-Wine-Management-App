package seng202.team0.gui;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextArea;
import seng202.team0.database.Wine;
import seng202.team0.managers.ManagerContext;

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

    private Wine wine;


    public DetailedViewController(ManagerContext context) {
        super(context);
        this.wine = new Wine(-1, managerContext.databaseManager, "{title}", "{variety}", "", "", "{winery}", "{color}", 1000, "{description}", 0, 0.0f, 0.0f);
    }

    public void setWine(Wine wine) {
        wineNameLabel.setText(wine.getTitle());
        yearLabel.setText("0000");
        countryLabel.setText(wine.getCountry());
        descriptionArea.setText(wine.getDescription());
        wineryLabel.setText(wine.getWinery());
        double score = wine.getScorePercent();
        score /= 100.0;
        scoreWheel.setProgress(score);
    }





}
