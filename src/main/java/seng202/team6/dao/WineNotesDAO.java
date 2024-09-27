package seng202.team6.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import seng202.team6.model.Note;
import seng202.team6.model.User;
import seng202.team6.model.Wine;
import seng202.team6.model.WineReview;
import seng202.team6.util.DatabaseObjectUniquer;
import seng202.team6.util.Timer;

/**
 * Data Access Object (DAO) for handling wine notes related database operations.
 */
public class WineNotesDAO extends DAO {

  /**
   * Cache to store and reuse Note objects to avoid duplication
   */
  private final DatabaseObjectUniquer<Note> wineReviewCache = new DatabaseObjectUniquer<>();


  /**
   * Constructs a new WineNotesDAO with the given database connection.
   *
   * @param connection The database connection to be used for wine note operations.
   */
  public WineNotesDAO(Connection connection) {
    super(connection, WineNotesDAO.class);
  }

  @Override
  public String[] getInitialiseStatements() {
    return new String[]{
        "CREATE TABLE IF NOT EXISTS NOTES (" +
            "ID             INTEGER       PRIMARY KEY," +
            "USERNAME       VARCHAR(64)   NOT NULL," +
            "WINE_ID        INTEGER       NOT NULL, " +
            "NOTE           TEXT," +
            "FOREIGN KEY (USERNAME) REFERENCES USER(USERNAME) ON DELETE CASCADE," +
            "FOREIGN KEY (WINE_ID) REFERENCES WINE(ID) ON DELETE CASCADE" +
            ")"
    };
  }

  public ObservableList<Note> getAll(User user) {
    Timer timer = new Timer();
    String sql = "SELECT ID, NOTE FROM NOTES " +
        "WHERE USERNAME = ? AND WINE_ID = ?";
  }

  public Note get(User user, Wine wine) {
    Timer timer = new Timer();
    String sql = "SELECT ID, NOTE FROM NOTES " +
        "WHERE USERNAME = ? AND WINE_ID = ?";
    try (PreparedStatement statement = connection.prepareStatement(sql)) {
      statement.setString(1, user.getUsername());
      statement.setLong(2, wine.getKey());

      try (ResultSet resultSet = statement.executeQuery()) {
        if (resultSet.next()) {
          long id = resultSet.getLong("ID");
          log.info("Successfully retrieved note with ID {} for user '{}' and wine with ID {} "
              + "in {}ms", id, user.getUsername(), wine.getKey(), timer.stop());
          return new Note(
              id,
              wine.getKey(),
              resultSet.getString("NOTE")
          );
        } else {
          log.warn("Could not find note for user '{}' and wine with ID {} in {}ms"
              + "in {}ms", user.getUsername(), wine.getKey(), timer.stop());
        }
      }
    } catch (SQLException error) {
      log.error("Failed to find note for user '{}' and wine with ID {}", user.getUsername(),
          wine.getKey(), error);
    }
    return null;
  }

  public void delete(Note note) {
    Timer timer = new Timer();
    String sql = "DELETE FROM NOTES WHERE ID = ?";
    try (PreparedStatement statement = connection.prepareStatement(sql)) {
      statement.setLong(1, note.getID());

      int rowsAffected = statement.executeUpdate();
      if (rowsAffected == 1) {
        log.info("Successfully deleted note with ID {} in {}ms", note.getID(), timer.stop());
      } else {
        log.warn("Could not delete note with ID {} in {}ms", note.getID(), timer.stop());
      }
    } catch (SQLException error) {
      log.error("Failed to delete note with ID {}", note.getID(), error);
    }
  }

  private ObservableList<Note> extractAllNotesFromResultSet(ResultSet resultSet)
      throws SQLException {
    ObservableList<Note> notes = FXCollections.observableArrayList();
    while (resultSet.next()) {
      notes.add(extractNoteFromResultSet(resultSet));
    }
    return notes;
  }

  private Note extractNoteFromResultSet(ResultSet resultSet) throws SQLException {
    long id = resultSet.getLong("ID");
    Note cachedNote = wineReviewCache.tryGetObject(id);
    if (cachedNote != null) {
      return cachedNote;
    }

    return new Note(
        id,
        resultSet.getLong("WINE_ID"),
        resultSet.getString("NOTE")
    );
  }
}
