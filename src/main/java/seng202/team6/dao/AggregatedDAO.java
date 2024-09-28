package seng202.team6.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import seng202.team6.model.Note;
import seng202.team6.model.User;
import seng202.team6.model.Wine;
import seng202.team6.util.Timer;

// todo - idk what to call this but pre much is just uses methods between different DAOS
public class AggregatedDAO extends DAO {
  private final WineNotesDAO wineNotesDAO;
  private final WineDAO wineDAO;

  /**
   * Constructs a new DAO with the given database connection and initializes logging.
   *
   * @param connection          The database connection to be used by this DAO.
   */
  public AggregatedDAO(Connection connection, WineNotesDAO wineNotesDAO, WineDAO wineDAO) {
    super(connection, AggregatedDAO.class);
    this.wineNotesDAO = wineNotesDAO;
    this.wineDAO = wineDAO;
  }

  public ObservableMap<Wine, Note> getAllNotesMappedWithWinesByUser(User user) {
    Timer timer = new Timer();
    String sql = "SELECT * FROM NOTES " +
        "INNER JOIN WINE ON NOTES.WINE_ID = WINE.ID " +
        "WHERE NOTES.USERNAME = ?";
    ObservableMap<Wine, Note> wineAndNotes = FXCollections.observableHashMap();
    try (PreparedStatement statement = connection.prepareStatement(sql)) {
      statement.setString(1, user.getUsername());

      try (ResultSet resultSet = statement.executeQuery()) {
        while (resultSet.next()) {
          Wine wine = wineDAO.extractWineFromResultSet(resultSet);
          Note note = wineNotesDAO.extractNoteFromResultSet(resultSet);
          wineAndNotes.put(wine, note);
        }
      }
      log.info("Successfully retrieves {} wines with notes by user '{}' in {}ms",
          wineAndNotes.size(), user.getUsername(), timer.stop());
    } catch (SQLException e) {
      log.error("Failed to retrieve wines with notes by user '{}'", user.getUsername());
    }
    return wineAndNotes;
  }
}
