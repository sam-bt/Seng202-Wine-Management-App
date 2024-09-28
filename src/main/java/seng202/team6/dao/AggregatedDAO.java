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

/**
 * Aggregated Data Access Object (DAO) is responsible for combining functionality from multiple
 * DAOs and handling operations that involve joining data from different tables. It avoids code
 * repetition by using existing DAO methods to extract data
 */
public class AggregatedDAO extends DAO {
  private final WineNotesDAO wineNotesDAO;
  private final WineDAO wineDAO;

  /**
   * Constructs a new DAO with the given database connection and initializes references to DAOs.
   *
   * @param connection The database connection to be used by this DAO.
   * @param wineNotesDAO The DAO responsible for handling operations related to wine notes.
   * @param wineDAO The DAO responsible for handling operations related to wines.
   */
  public AggregatedDAO(Connection connection, WineNotesDAO wineNotesDAO, WineDAO wineDAO) {
    super(connection, AggregatedDAO.class);
    this.wineNotesDAO = wineNotesDAO;
    this.wineDAO = wineDAO;
  }

  /**
   * Retrieves all notes mapped to wines for a specific user. This method joins the NOTES
   * table with the WINE table based on the wine ID and returns a map of Wine objects to
   * their associated Note objects for the specified user.
   *
   * @param user The user for whom to retrieve the notes and wines.
   * @return An ObservableMap where the key is a Wine object and the value is the associated Note object.
   */
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
