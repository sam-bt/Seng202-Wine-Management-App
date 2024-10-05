package seng202.team6.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import seng202.team6.enums.Island;
import seng202.team6.model.User;
import seng202.team6.model.Vineyard;
import seng202.team6.model.VineyardTour;
import seng202.team6.util.DatabaseObjectUniquer;
import seng202.team6.util.Timer;

public class VineyardTourDAO extends DAO {

  /**
   * Cache to store and reuse WineList objects to avoid duplication
   */
  private final DatabaseObjectUniquer<VineyardTour> wineTourCache = new DatabaseObjectUniquer<>();

  /**
   * Constructs a new WineTourDAO with the given database connection.
   *
   * @param connection The database connection to be used for wine tour operations.
   */
  public VineyardTourDAO(Connection connection) {
    super(connection, VineyardTourDAO.class);
  }

  @Override
  public String[] getInitialiseStatements() {
    return new String[]{
        "CREATE TABLE IF NOT EXISTS VINEYARD_TOUR (" +
            "ID             INTEGER       PRIMARY KEY," +
            "USERNAME       VARCHAR(64)   NOT NULL," +
            "NAME           VARCHAR(32)   NOT NULL," +
            "ISLAND         CHAR(1)       NOT NULL," +
            "FOREIGN KEY (USERNAME) REFERENCES USER(USERNAME) ON DELETE CASCADE" +
            ")",
        "CREATE TABLE IF NOT EXISTS VINEYARD_TOUR_ITEM (" +
            "TOUR_ID        INTEGER       NOT NULL," +
            "VINEYARD_ID    INTEGER       NOT NULL," +
            "PRIMARY KEY (TOUR_ID, VINEYARD_ID)," +
            "FOREIGN KEY (TOUR_ID) REFERENCES VINEYARD_TOUR(ID) ON DELETE CASCADE," +
            "FOREIGN KEY (VINEYARD_ID) REFERENCES VINEYARD(ID) ON DELETE CASCADE" +
            ")"
    };
  }

  public ObservableList<VineyardTour> getAll(User user) {
    Timer timer = new Timer();
    String sql = "SELECT * FROM VINEYARD_TOUR WHERE USERNAME = ?";
    try (PreparedStatement statement = connection.prepareStatement(sql)) {
      statement.setString(1, user.getUsername());

      try (ResultSet resultSet = statement.executeQuery()) {
        ObservableList<VineyardTour> vineyardTours = extractVineyardToursFromResultSet(resultSet);
        log.info("Successfully retrieved all {} vineyard tours for user '{}' in {}ms",
            vineyardTours.size(), user.getUsername(), timer.stop());
        return vineyardTours;
      }
    } catch (SQLException error) {
      log.info("Failed to retrieve vineyard tours for user '{}'", user.getUsername(), error);
    }
    return FXCollections.emptyObservableList();
  }

  public VineyardTour create(User user, String tourName, Island island) {
    Timer timer = new Timer();
    String sql = "INSERT INTO VINEYARD_TOUR VALUES (NULL, ?, ?, ?)";
    try (PreparedStatement statement = connection.prepareStatement(sql,
        Statement.RETURN_GENERATED_KEYS)) {
      statement.setString(1, user.getUsername());
      statement.setString(2, tourName);
      statement.setString(3, String.valueOf(island.getCode()));
      statement.executeUpdate();

      try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
        if (generatedKeys.next()) {
          long id = generatedKeys.getLong(1);
          log.info("Successfully created wine tour '{}' with ID {} for user '{}' in {}ms", tourName,
              id, user.getUsername(), timer.stop());
          VineyardTour vineyardTour = new VineyardTour(id, user.getUsername(), tourName, island);
          if (useCache()) {
            wineTourCache.addObject(id, vineyardTour);
          }
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

  public boolean isVineyardInTour(VineyardTour vineyardTour, Vineyard vineyard) {
    Timer timer = new Timer();
    String sql = "SELECT * FROM VINEYARD_TOUR_ITEM WHERE TOUR_ID = ? AND VINEYARD_ID = ?";
    try (PreparedStatement statement = connection.prepareStatement(sql)) {
      statement.setLong(1, vineyardTour.getId());
      statement.setLong(2, vineyard.getId());

      try (ResultSet resultSet = statement.executeQuery()) {
        boolean found = resultSet.next();
        log.info("Successfully found vineyard with ID {} is {} list with ID {} in {}ms",
            vineyard.getId(), found ? "in" : "not in", vineyardTour.getId(), timer.stop());
        return found;
      }
    } catch (SQLException error) {
      log.error("Failed to check if vineyard with ID {} is in list '{}'", vineyard.getId(),
          vineyardTour.getId(), error);
    }
    return false;
  }

  public void addVineyard(VineyardTour vineyardTour, Vineyard vineyard) {
    Timer timer = new Timer();
    String sql = "INSERT INTO VINEYARD_TOUR_ITEM VALUES (?, ?)";
    try (PreparedStatement statement = connection.prepareStatement(sql)) {
      statement.setLong(1, vineyardTour.getId());
      statement.setLong(2, vineyard.getId());

      int rowsAffected = statement.executeUpdate();
      if (rowsAffected == 1) {
        log.info("Successfully added vineyard with ID {} to tour with ID {} in {}ms",
            vineyard.getId(), vineyardTour.getId(), timer.stop());
      } else {
        log.warn("Could not add vineyard with ID {} to tour with ID {} in {}ms",
            vineyard.getId(), vineyardTour.getId(), timer.stop());
      }
    } catch (SQLException error) {
      log.error("Failed to add vineyard with ID {} to tour with ID {}",
          vineyard.getId(),vineyardTour.getId(), error);
    }
  }

  public void removeVineyard(VineyardTour vineyardTour, Vineyard vineyard) {
    Timer timer = new Timer();
    String sql = "DELETE FROM VINEYARD_TOUR_ITEM WHERE TOUR_ID = ? AND VINEYARD_ID = ?";
    try (PreparedStatement statement = connection.prepareStatement(sql)) {
      statement.setLong(1, vineyardTour.getId());
      statement.setLong(2, vineyard.getId());

      int rowsAffected = statement.executeUpdate();
      if (rowsAffected == 1) {
        log.info("Successfully removed vineyard with ID {} from tour with ID {} in {}ms",
            vineyard.getId(),vineyardTour.getId(), timer.stop());
      } else {
        log.warn("Could not remove vineyard with ID {} from tour with ID {} in {}ms",
            vineyard.getId(),vineyardTour.getId(), timer.stop());
      }
    } catch (SQLException error) {
      log.error("Failed to remove vineyard with ID {} from tour with ID {}",
          vineyard.getId(),vineyardTour.getId(), error);
    }
  }

  private ObservableList<VineyardTour> extractVineyardToursFromResultSet(ResultSet resultSet)
      throws SQLException {
    ObservableList<VineyardTour> vineyardTours = FXCollections.observableArrayList();
    while (resultSet.next()) {
      vineyardTours.add(extractVineyardTourFromResultSet(resultSet));
    }
    return vineyardTours;
  }

  private VineyardTour extractVineyardTourFromResultSet(ResultSet resultSet) throws SQLException {
    long id = resultSet.getLong("ID");
    VineyardTour cachedVineyardTour = wineTourCache.tryGetObject(id);
    if (cachedVineyardTour != null) {
      return cachedVineyardTour;
    }

    VineyardTour vineyardTour = new VineyardTour(
        id,
        resultSet.getString("USERNAME"),
        resultSet.getString("NAME"),
        Island.byCode(resultSet.getString("USERNAME").charAt(0))
    );
    if (useCache()) {
      wineTourCache.addObject(id, vineyardTour);
    }
    return vineyardTour;
  }
}
