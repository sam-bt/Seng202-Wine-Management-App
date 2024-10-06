package seng202.team6.model;

import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * This class represents a note object in the database.
 */
public class Note {

  private final LongProperty id;
  private final SimpleStringProperty username;
  private final LongProperty wineId;
  private final StringProperty note;

  /**
   * Constructor.
   *
   * @param id id
   * @param username username
   * @param wineId wine id
   * @param note note
   */
  public Note(long id, String username, long wineId, String note) {
    this.id = new SimpleLongProperty(id);
    this.username = new SimpleStringProperty(username);
    this.wineId = new SimpleLongProperty(wineId);
    this.note = new SimpleStringProperty(note);
  }

  /**
   * Gets the id property.
   *
   * @return id property
   */
  public LongProperty idProperty() {
    return id;
  }

  /**
   * Gets the id.
   *
   * @return id
   */
  public long getId() {
    return id.get();
  }

  /**
   * Sets the id.
   *
   * @param id id
   */
  public void setId(long id) {
    idProperty().set(id);
  }

  /**
   * Gets username.
   *
   * @return username
   */
  public String getUsername() {
    return username.get();
  }

  /**
   * Gets the username property.
   *
   * @return username property
   */
  public StringProperty usernameProperty() {
    return username;
  }

  /**
   * Gets the wine id property.
   *
   * @return wine id property
   */
  public LongProperty wineIdProperty() {
    return wineId;
  }

  /**
   * Gets the wine id.
   *
   * @return wine id
   */
  public long getWineId() {
    return wineId.get();
  }

  /**
   * Gets the note property.
   *
   * @return note property
   */
  public StringProperty noteProperty() {
    return note;
  }

  /**
   * Gets the note.
   *
   * @return note
   */
  public String getNote() {
    return note.get();
  }

  /**
   * Sets the note.
   *
   * @param note note
   */
  public void setNote(String note) {
    noteProperty().set(note);
  }
}
