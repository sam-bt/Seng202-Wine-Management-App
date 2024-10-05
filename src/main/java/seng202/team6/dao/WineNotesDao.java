package seng202.team6.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import seng202.team6.managers.DatabaseManager;
import seng202.team6.model.Note;
import seng202.team6.model.User;
import seng202.team6.model.Wine;
import seng202.team6.util.DatabaseObjectUniquer;
import seng202.team6.util.Timer;

/**
 * Data Access Object (DAO) for handling wine notes related database operations.
 */
public class WineNotesDao extends Dao {

  /**
   * Cache to store and reuse Note objects to avoid duplication
   */
  private final DatabaseObjectUniquer<Note> notesCache = new DatabaseObjectUniquer<>();


  /**
   * Constructs a new WineNotesDAO with the given database connection.
   *
   * @param connection The database connection to be used for wine note operations.
   */
  public WineNotesDao(Connection connection) {
    super(connection, WineNotesDao.class);
  }

  @Override
  public String[] getInitialiseStatements() {
    return new String[]{
        "CREATE TABLE IF NOT EXISTS NOTES ("
            + "ID             INTEGER       PRIMARY KEY,"
            + "USERNAME       VARCHAR(64)   NOT NULL,"
            + "WINE_ID        INTEGER       NOT NULL, "
            + "NOTE           TEXT,"
            + "FOREIGN KEY (USERNAME) REFERENCES USER(USERNAME) ON DELETE CASCADE,"
            + "FOREIGN KEY (WINE_ID) REFERENCES WINE(ID) ON DELETE CASCADE"
            + ")"
    };
  }

  /**
   * Retrieves all notes from the NOTES table.
   *
   * @return An ObservableList of all Note objects in the database
   */
  public ObservableList<Note> getAll() {
    Timer timer = new Timer();
    String sql = "SELECT * FROM NOTES";
    try (Statement statement = connection.createStatement()) {
      try (ResultSet resultSet = statement.executeQuery(sql)) {
        ObservableList<Note> notes = extractAllNotesFromResultSet(resultSet);
        log.info("Successfully retrieved all {} notes in {}ms",
            notes.size(), timer.stop());
        return notes;
      }
    } catch (SQLException error) {
      log.info("Failed to retrieve all notes", error);
    }
    return FXCollections.emptyObservableList();
  }

  /**
   * Retrieves all notes owned by the provided user from the NOTES table.
   *
   * @param user The user whose notes are being retrieved
   * @return ObservableList of Note objects owned by the user
   */
  public ObservableList<Note> getAll(User user) {
    Timer timer = new Timer();
    String sql = "SELECT * FROM NOTES WHERE USERNAME = ?";
    try (PreparedStatement statement = connection.prepareStatement(sql)) {
      statement.setString(1, user.getUsername());

      try (ResultSet resultSet = statement.executeQuery()) {
        ObservableList<Note> notes = extractAllNotesFromResultSet(resultSet);
        log.info("Successfully retrieved all {} notes for user '{}' in {}ms",
            notes.size(), user.getUsername(), timer.stop());
        return notes;
      }
    } catch (SQLException error) {
      log.info("Failed to retrieve notes for user '{}'", user.getUsername(), error);
    }
    return FXCollections.emptyObservableList();
  }

  /**
   * Retrieves a note belonging to the specified wine and user. If the note does not exist for the
   * given parameters, a new Note object is created. Listeners are binded to the object to ensure
   * any changes or updates are reflected in the database.
   *
   * @param user The user the note belongs to
   * @param wine The wine the note belongs to
   * @return The note object belonging to the specified User and Wine
   */
  public Note get(User user, Wine wine) {
    Timer timer = new Timer();
    String sql = "SELECT * FROM NOTES " +
        "WHERE USERNAME = ? AND WINE_ID = ?";
    try (PreparedStatement statement = connection.prepareStatement(sql)) {
      statement.setString(1, user.getUsername());
      statement.setLong(2, wine.getKey());

      try (ResultSet resultSet = statement.executeQuery()) {
        if (resultSet.next()) {
          long id = resultSet.getLong("ID");
          log.info("Successfully retrieved note with ID {} for user '{}' and wine with ID {} "
              + "in {}ms", id, user.getUsername(), wine.getKey(), timer.stop());
          return extractNoteFromResultSet(resultSet);
        } else {
          log.warn(
              "Could not find note for user '{}' and wine with ID {} so returning blank note in {}ms",
              user.getUsername(), wine.getKey(), timer.stop());
          // do not add to cache as it has common -1 key which indicates it's not in the database
          Note note = new Note(-1, user.getUsername(), wine.getKey(), "");
          bindUpdater(note);
          return note;
        }
      }
    } catch (SQLException error) {
      log.error("Failed to find note for user '{}' and wine with ID {}", user.getUsername(),
          wine.getKey(), error);
    }
    return null;
  }

  /**
   * Adds a note to the database.
   * <p>
   * Upon successful insertion, the note's ID is set to the generated ID. This is to ensure any
   * classes holding a reference to this note can still be used and will update the database as
   * attributes are changed.
   * </p>
   *
   * @param note The note to be added to the database
   */
  private void add(Note note) {
    if (note.getId() != -1) {
      log.error(
          "Failed to add note for user '{}' as the note has a valid ID indicating it is in the database already");
      return;
    }
    Timer timer = new Timer();
    String sql = "INSERT INTO NOTES VALUES (null, ?, ?, ?)";
    try (PreparedStatement statement = connection.prepareStatement(sql,
        Statement.RETURN_GENERATED_KEYS)) {
      statement.setString(1, note.getUsername());
      statement.setLong(2, note.getWineId());
      statement.setString(3, note.getNote());
      statement.executeUpdate();

      try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
        if (generatedKeys.next()) {
          long id = generatedKeys.getLong(1);
          note.setId(id);
          if (useCache()) {
            notesCache.addObject(id, note);
          }
          log.info("Successfully added note with ID '{}' for user {} and wine with ID {} in {}ms",
              id, note.getUsername(), note.getWineId(), timer.stop());
        } else {
          log.warn("Could not add note for user {} and wine with ID {} in {}ms", note.getUsername(),
              note.getWineId(), timer.stop());
        }
      }
    } catch (SQLException error) {
      log.warn("Failed to add note for user {} and wine with ID {}", note.getUsername(),
          note.getWineId(), error);
    }
  }

  /**
   * Deletes a note from the database.
   * <p>
   * Upon successful removal, the note's ID is set to -1 to indicate that the note is not in the
   * database.
   * </p>
   *
   * @param note The note to be deleted.
   */
  public void delete(Note note) {
    if (note.getId() == -1) {
      log.error(
          "Failed to add note for user '{}' as the note has an invalid ID indicating it is not in the database already");
      return;
    }
    Timer timer = new Timer();
    String sql = "DELETE FROM NOTES WHERE ID = ?";
    try (PreparedStatement statement = connection.prepareStatement(sql)) {
      statement.setLong(1, note.getId());

      int rowsAffected = statement.executeUpdate();
      if (rowsAffected == 1) {
        log.info("Successfully deleted note with ID {} in {}ms", note.getId(), timer.stop());
      } else {
        log.warn("Could not delete note with ID {} in {}ms", note.getId(), timer.stop());
      }
      // set the note id to -1 as it is no longer in the database but the object will still
      // be used in the service, remove from cache as well
      notesCache.removeObject(note.getId());
      note.setId(-1);
    } catch (SQLException error) {
      log.error("Failed to delete note with ID {}", note.getId(), error);
    }
  }

  /**
   * Extracts all notes from the provided ResultSet and stores them in an ObservableList.
   *
   * @param resultSet The ResultSet containing note data
   * @return ObservableList of Note objects extracted from the ResultSet
   * @throws SQLException if a database access error occurs
   */
  private ObservableList<Note> extractAllNotesFromResultSet(ResultSet resultSet)
      throws SQLException {
    ObservableList<Note> notes = FXCollections.observableArrayList();
    while (resultSet.next()) {
      notes.add(extractNoteFromResultSet(resultSet));
    }
    return notes;
  }

  /**
   * Extracts a Note object from the provided ResultSet. The note cache is check before creating a
   * new note instance.
   *
   * @param resultSet The ResultSet containing note data
   * @return The Note object extracted from the ResultSet
   * @throws SQLException if a database access error occurs
   */
  Note extractNoteFromResultSet(ResultSet resultSet) throws SQLException {
    long id = resultSet.getLong("ID");
    Note cachedNote = notesCache.tryGetObject(id);
    if (cachedNote != null) {
      return cachedNote;
    }

    Note note = new Note(
        id,
        resultSet.getString("USERNAME"),
        resultSet.getLong("WINE_ID"),
        resultSet.getString("NOTE")
    );
    if (useCache()) {
      notesCache.addObject(id, note);
    }
    bindUpdater(note);
    return note;
  }

  /**
   * Binds listeners to the Note object to ensure that any changes to the users properties are
   * automatically reflected in the database.
   * <ul>
   *   <li>If the provided note is empty and the ID is -1, the note is not in the database and is
   *   empty so no action is required</li>
   *   <li>If the provided note is empty and the ID is not -1, the note is not in the database and
   *   must be deleted.</li>
   *   <li>If the provided note is not empty and the ID is -1, the note is not in the database and
   *   must be added.</li>
   *   <li>If the provided note is not empty and the ID is not -1, the note is in the database and
   *   must be updated.</li>
   * </ul>
   *
   * @param note The Note object to bind listeners to
   */
  private void bindUpdater(Note note) {
    note.noteProperty().addListener((observableValue, before, after) -> {
      if (after.isEmpty()) { // if the new note is empty
        // if id is -1, it is not in the database
        // otherwise the note is in the database but can be removed as it is now blank
        if (note.getId() != -1) {
          delete(note);
        }
        return;
      }
      // the note is not in the database, so we add it to database
      if (note.getId() == -1) {
        add(note);
      } else { // otherwise the note is in the database so update it
        updateAttribute(note.getId(), "NOTE", update -> {
          update.setString(1, note.getNote());
        });
      }
    });
  }

  /**
   * Updates a specific attribute of the note in the NOTES table
   *
   * @param attributeName   name of attribute
   * @param attributeSetter callback to set attribute
   */
  private void updateAttribute(long id, String attributeName,
      DatabaseManager.AttributeSetter attributeSetter) {
    Timer timer = new Timer();
    String sql = "UPDATE NOTES set " + attributeName + " = ? where ID = ?";
    try (PreparedStatement update = connection.prepareStatement(sql)) {
      attributeSetter.setAttribute(update);
      update.setLong(2, id);

      int rowsAffected = update.executeUpdate();
      if (rowsAffected == 1) {
        log.info("Successfully updated attribute '{}' for note with ID {} in {}ms",
            attributeName, id, timer.stop());
      } else {
        log.info("Could not update attribute '{}' for note with ID {} in {}ms",
            attributeName, id, timer.stop());
      }
    } catch (SQLException error) {
      log.error("Failed to update attribute '{}' for note with ID {} in {}ms",
          attributeName, id, timer.stop(), error);
    }
  }
}
