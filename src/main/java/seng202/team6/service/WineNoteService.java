package seng202.team6.service;

import seng202.team6.managers.AuthenticationManager;
import seng202.team6.managers.DatabaseManager;
import seng202.team6.model.Note;
import seng202.team6.model.User;
import seng202.team6.model.Wine;

/**
 * Service to manage wine notes.
 */
public class WineNoteService {

  private final DatabaseManager databaseManager;
  private final Wine wine;
  private Note note;

  /**
   * Constructor.
   *
   * @param authenticationManager authentication manager
   * @param databaseManager       database manager
   * @param wine                  wine
   */
  public WineNoteService(AuthenticationManager authenticationManager,
      DatabaseManager databaseManager, Wine wine) {
    this.databaseManager = databaseManager;
    this.wine = wine;
  }

  /**
   * Gets the users loaded note for this wine.
   *
   * @param user user
   * @return node
   */
  public Note loadUsersNote(User user) {
    note = databaseManager.getWineNotesDao().getOrCreate(user, wine);
    return note;
  }

  /**
   * Gets the currently selected note.
   *
   * @return note
   */
  public Note getNote() {
    return note;
  }
}
