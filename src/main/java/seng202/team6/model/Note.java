package seng202.team6.model;

import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;
import seng202.team6.managers.DatabaseManager;

import java.io.IOException;

public class Note {
  private final LongProperty id;
  private final LongProperty wineId;
  private final StringProperty note;

  public Note(long id, long wineId, String note) {
    this.id = new SimpleLongProperty(id);
    this.wineId = new SimpleLongProperty(wineId);
    this.note = new SimpleStringProperty(note);
  }

  public LongProperty idProperty() {
    return id;
  }

  public long getID() {
    return id.get();
  }

  public LongProperty wineIdProperty() {
    return wineId;
  }

  public long getWineID() {
    return wineId.get();
  }

  public StringProperty noteProperty() {
    return note;
  }

  public String getNote() {
    return note.get();
  }

  public void setNote(String note) {
    noteProperty().set(note);
  }
}
