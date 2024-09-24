package seng202.team6.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;
import seng202.team6.managers.DatabaseManager;

import java.io.IOException;

public class Note {
    private final String note;
    private long wineID;
    private String username;
    private final StringProperty wineTitle;

    public Note(String note, long wineID, String username, DatabaseManager databaseManager) {
        this.note = note;
        this.wineID = wineID;
        this.username = username;
        wineTitle = new SimpleStringProperty(this, "title", databaseManager.getWineTitleByID(wineID));
    }

    public String getNote() {
        return note;
    }

    public StringProperty titleProperty() {
        return wineTitle;
    }

    public String getWineTitle() {
        return wineTitle.get();
    }







}
