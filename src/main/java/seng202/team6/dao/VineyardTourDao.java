package seng202.team6.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import seng202.team6.model.User;
import seng202.team6.model.Vineyard;
import seng202.team6.model.VineyardTour;
import seng202.team6.util.DatabaseObjectUniquer;
import seng202.team6.util.Timer;

/**
 * Data Access Object (DAO) for handling vineyard tour related database operations.
 */
public class VineyardTourDao extends Dao {

  /**
   * Cache to store and reuse WineList objects to avoid duplication.
   */
  private final DatabaseObjectUniquer<VineyardTour> wineTourCache = new DatabaseObjectUniquer<>();

  /**
   * Constructs a new VineyardTourDao with the given database connection.
   *
   * @param connection The database connection to be used for wine tour operations.
   */
  public VineyardTourDao(Connection connection) {
    super(connection, VineyardTourDao.class);
  }

  @Override
  public String[] getInitialiseStatements() {
    return new String[]{
        "CREATE TABLE IF NOT EXISTS VINEYARD_TOUR ("
            + "ID             INTEGER       PRIMARY KEY,"
            + "USERNAME       VARCHAR(64)   NOT NULL,"
            + "NAME           VARCHAR(32)   NOT NULL,"
            + "FOREIGN KEY (USERNAME) REFERENCES USER(USERNAME) ON DELETE CASCADE"
            + ")",
        "CREATE TABLE IF NOT EXISTS VINEYARD_TOUR_ITEM ("
            + "TOUR_ID        INTEGER       NOT NULL,"
            + "VINEYARD_ID    INTEGER       NOT NULL,"
            + "PRIMARY KEY (TOUR_ID, VINEYARD_ID),"
            + "FOREIGN KEY (TOUR_ID) REFERENCES VINEYARD_TOUR(ID) ON DELETE CASCADE,"
            + "FOREIGN KEY (VINEYARD_ID) REFERENCES VINEYARD(ID) ON DELETE CASCADE"
            + ")"
    };
  }

  /**
   * Retrieves all vineyard tours created by the specified user from the database.
   *
   * @param user The User object representing the user whose vineyard tours are being queried.
   * @return An ObservableList of VineyardTour objects created by the specified user.
   */
  public ObservableList<VineyardTour> getAll(User user) {
    Timer timer = new Timer();
    String sql = "SELECT VINEYARD_TOUR.ID as vineyard_tour_id, VINEYARD_TOUR.* "
        + "FROM VINEYARD_TOUR "
        + "WHERE USERNAME = ?";
    try (PreparedStatement statement = connection.prepareStatement(sql)) {
      statement.setString(1, user.getUsername());

      try (ResultSet resultSet = statement.executeQuery()) {
        ObservableList<VineyardTour> vineyardTours = extractVineyardToursFromResultSet(resultSet,
            "vineyard_tour_id");
        log.info("Successfully retrieved all {} vineyard tours for user '{}' in {}ms",
            vineyardTours.size(), user.getUsername(), timer.currentOffsetMilliseconds());
        return vineyardTours;
      }
    } catch (SQLException error) {
      log.info("Failed to retrieve vineyard tours for user '{}'", user.getUsername(), error);
    }
    return FXCollections.emptyObservableList();
  }

  /**
   * Creates a new vineyard tour for a specified user.
   *
   * @param user     The User object representing the user for whom the vineyard tour is being
   *                 created.
   * @param tourName The name of the vineyard tour to be created.
   * @return A VineyardTour object representing the newly created vineyard tour, or null if the
   *        operation fails.
   */
  public VineyardTour create(User user, String tourName) {
    Timer timer = new Timer();
    String sql = "INSERT INTO VINEYARD_TOUR VALUES (NULL, ?, ?)";
    try (PreparedStatement statement = connection.prepareStatement(sql,
        Statement.RETURN_GENERATED_KEYS)) {
      statement.setString(1, user.getUsername());
      statement.setString(2, tourName);
      statement.executeUpdate();

      try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
        if (generatedKeys.next()) {
          long id = generatedKeys.getLong(1);
          log.info("Successfully created wine tour '{}' with ID {} for user '{}' in {}ms", tourName,
              id, user.getUsername(), timer.currentOffsetMilliseconds());
          VineyardTour vineyardTour = new VineyardTour(id, user.getUsername(), tourName);
          wineTourCache.addObject(id, vineyardTour);

          return vineyardTour;
        }
        log.warn("Could not create wine tour '{}' for user '{}'", tourName,
            user.getUsername());
      }
    } catch (SQLException error) {
      log.error("Failed to create wine tour '{}' for user '{}'", tourName,
          user.getUsername(), error);
    }
    return null;
  }

  /**
   * Deletes a vineyard from the database.
   *
   * @param vineyardTour The vineyard to be deleted
   */
  public void remove(VineyardTour vineyardTour) {
    Timer timer = new Timer();
    String sql = "DELETE FROM VINEYARD_TOUR WHERE ID = ?";
    try (PreparedStatement statement = connection.prepareStatement(sql)) {
      statement.setLong(1, vineyardTour.getId());

      int rowsAffected = statement.executeUpdate();
      if (rowsAffected == 1) {
        log.info("Successfully removed vineyard tour with ID {} in {}ms",
            vineyardTour.getId(), timer.currentOffsetMilliseconds());
      } else {
        log.warn("Could not remove vineyard tour ID {} in {}ms", vineyardTour.getId(),
            vineyardTour.getId(), timer.currentOffsetMilliseconds());
      }
    } catch (SQLException error) {
      log.error("Failed to remove vineyard tour with ID {}",
          vineyardTour.getId(), vineyardTour.getId(), error);
    }
  }

  /**
   * Checks if a specific vineyard is part of a given vineyard tour.
   *
   * @param vineyardTour The VineyardTour object representing the tour.
   * @param vineyard     The Vineyard object representing the vineyard being checked.
   * @return True if the vineyard is in the tour, false otherwise.
   */
  public boolean isVineyardInTour(VineyardTour vineyardTour, Vineyard vineyard) {
    Timer timer = new Timer();
    String sql = "SELECT * FROM VINEYARD_TOUR_ITEM WHERE TOUR_ID = ? AND VINEYARD_ID = ?";
    try (PreparedStatement statement = connection.prepareStatement(sql)) {
      statement.setLong(1, vineyardTour.getId());
      statement.setLong(2, vineyard.getId());

      try (ResultSet resultSet = statement.executeQuery()) {
        boolean found = resultSet.next();
        log.info("Successfully found vineyard with ID {} is {} list with ID {} in {}ms",
            vineyard.getId(), found ? "in" : "not in", vineyardTour.getId(),
            timer.currentOffsetMilliseconds());
        return found;
      }
    } catch (SQLException error) {
      log.error("Failed to check if vineyard with ID {} is in list '{}'", vineyard.getId(),
          vineyardTour.getId(), error);
    }
    return false;
  }

  /**
   * Adds a vineyard to an existing vineyard tour.
   *
   * @param vineyardTour The VineyardTour object representing the tour.
   * @param vineyard     The Vineyard object representing the vineyard to be added.
   */
  public void addVineyard(VineyardTour vineyardTour, Vineyard vineyard) {
    Timer timer = new Timer();
    String sql = "INSERT INTO VINEYARD_TOUR_ITEM VALUES (?, ?)";
    try (PreparedStatement statement = connection.prepareStatement(sql)) {
      statement.setLong(1, vineyardTour.getId());
      statement.setLong(2, vineyard.getId());

      int rowsAffected = statement.executeUpdate();
      if (rowsAffected == 1) {
        log.info("Successfully added vineyard with ID {} to tour with ID {} in {}ms",
            vineyard.getId(), vineyardTour.getId(), timer.currentOffsetMilliseconds());
      } else {
        log.warn("Could not add vineyard with ID {} to tour with ID {} in {}ms",
            vineyard.getId(), vineyardTour.getId(), timer.currentOffsetMilliseconds());
      }
    } catch (SQLException error) {
      log.error("Failed to add vineyard with ID {} to tour with ID {}",
          vineyard.getId(), vineyardTour.getId(), error);
    }
  }

  /**
   * Removes a vineyard from an existing vineyard tour.
   *
   * @param vineyardTour The VineyardTour object representing the tour.
   * @param vineyard     The Vineyard object representing the vineyard to be removed.
   */
  public void removeVineyard(VineyardTour vineyardTour, Vineyard vineyard) {
    Timer timer = new Timer();
    String sql = "DELETE FROM VINEYARD_TOUR_ITEM WHERE TOUR_ID = ? AND VINEYARD_ID = ?";
    try (PreparedStatement statement = connection.prepareStatement(sql)) {
      statement.setLong(1, vineyardTour.getId());
      statement.setLong(2, vineyard.getId());

      int rowsAffected = statement.executeUpdate();
      if (rowsAffected == 1) {
        log.info("Successfully removed vineyard with ID {} from tour with ID {} in {}ms",
            vineyard.getId(), vineyardTour.getId(), timer.currentOffsetMilliseconds());
      } else {
        log.warn("Could not remove vineyard with ID {} from tour with ID {} in {}ms",
            vineyard.getId(), vineyardTour.getId(), timer.currentOffsetMilliseconds());
      }
    } catch (SQLException error) {
      log.error("Failed to remove vineyard with ID {} from tour with ID {}",
          vineyard.getId(), vineyardTour.getId(), error);
    }
  }

  /**
   * Extracts all vineyard tours from the provided ResultSet and stores them in an ObservableList.
   *
   * @param resultSet The ResultSet from which vineyard tours are to be extracted
   * @return An ObservableList of VineyardTour objects
   * @throws SQLException If an error occurs while processing the ResultSet
   */
  private ObservableList<VineyardTour> extractVineyardToursFromResultSet(ResultSet resultSet,
      String idColumnName) throws SQLException {
    ObservableList<VineyardTour> vineyardTours = FXCollections.observableArrayList();
    while (resultSet.next()) {
      vineyardTours.add(extractVineyardTourFromResultSet(resultSet, idColumnName));
    }
    return vineyardTours;
  }

  /**
   * Extracts a VineyardTour object from the provided ResultSet. The wine cache is checked before
   * creating a new Wine instance.
   *
   * @param resultSet The ResultSet from which vineyards tours are to be extracted.
   * @return The extracted vineyard tour.
   * @throws SQLException If an error occurs while processing the ResultSet
   */
  private VineyardTour extractVineyardTourFromResultSet(ResultSet resultSet, String idColumnName)
      throws SQLException {
    long id = resultSet.getLong(idColumnName);
    VineyardTour cachedVineyardTour = wineTourCache.tryGetObject(id);
    if (cachedVineyardTour != null) {
      return cachedVineyardTour;
    }

    VineyardTour vineyardTour = new VineyardTour(
        id,
        resultSet.getString("USERNAME"),
        resultSet.getString("NAME")
    );
    wineTourCache.addObject(id, vineyardTour);

    return vineyardTour;
  }
}