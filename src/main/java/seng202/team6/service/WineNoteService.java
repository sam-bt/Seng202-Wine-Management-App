package seng202.team6.service;

import javafx.beans.property.SimpleStringProperty;
import seng202.team6.managers.AuthenticationManager;
import seng202.team6.managers.DatabaseManager;
import seng202.team6.model.Note;
import seng202.team6.model.User;
import seng202.team6.model.Wine;

public class WineNoteService {
  private final AuthenticationManager authenticationManager;
  private final DatabaseManager databaseManager;
  private final Wine wine;
  private Note note;

  public WineNoteService(AuthenticationManager authenticationManager,
      DatabaseManager databaseManager, Wine wine) {
    this.authenticationManager = authenticationManager;
    this.databaseManager = databaseManager;
    this.wine = wine;
  }

  public Note loadUsersNote(User user) {
    note = databaseManager.getWineNotesDAO().get(user, wine);
    return note;
  }

  public Note getNote() {
    return note;
  }
}
