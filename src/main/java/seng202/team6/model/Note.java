package seng202.team6.model;

import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Note {
  private final LongProperty id;
  private final SimpleStringProperty username;
  private final LongProperty wineId;
  private final StringProperty note;

  public Note(long id, String username, long wineId, String note) {
    this.id = new SimpleLongProperty(id);
    this.username = new SimpleStringProperty(username);
    this.wineId = new SimpleLongProperty(wineId);
    this.note = new SimpleStringProperty(note);
  }

  public LongProperty idProperty() {
    return id;
  }

  public long getID() {
    return id.get();
  }

  public void setID(long id) {
    idProperty().set(id);
  }

  public String getUsername() {
    return username.get();
  }

  public StringProperty usernameProperty() {
    return username;
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
