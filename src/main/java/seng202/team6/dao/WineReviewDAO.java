package seng202.team6.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import seng202.team6.model.User;
import seng202.team6.model.Wine;
import seng202.team6.model.WineReview;
import seng202.team6.util.DatabaseObjectUniquer;
import seng202.team6.util.Timer;

/**
 * Data Access Object (DAO) for handling wine review related database operations.
 */
public class WineReviewDAO extends DAO {

  /**
   * Cache to store and reuse WineReview objects to avoid duplication
   */
  private final DatabaseObjectUniquer<WineReview> wineReviewCache = new DatabaseObjectUniquer<>();

  /**
   * Constructs a new WineReviewDAO with the given database connection.
   *
   * @param connection The database connection to be used for wine review operations.
   */
  public WineReviewDAO(Connection connection) {
    super(connection, WineReviewDAO.class);
  }

  @Override
  public String[] getInitialiseStatements() {
    return new String[] {
        "CREATE TABLE IF NOT EXISTS WINE_REVIEW (" +
            "ID             INTEGER       PRIMARY KEY," +
            "USERNAME       varchar(64)   NOT NULL," +
            "WINE_ID        INTEGER       NOT NULL," +
            "RATING         DOUBLE        NOT NULL," +
            "DESCRIPTION    VARCHAR(256)  NOT NULL," +
            "DATE           DATE          NOT NULL," +
            "FOREIGN KEY (USERNAME) REFERENCES USER(USERNAME) ON DELETE CASCADE," +
            "FOREIGN KEY (WINE_ID) REFERENCES WINE(ID) ON DELETE CASCADE" +
            ")"
    };
  }

  public ObservableList<WineReview> getAll(Wine wine) {
    Timer timer = new Timer();
    String sql = "SELECT * FROM WINE_REVIEW WHERE WIND_ID = ?";
    try (PreparedStatement statement = connection.prepareStatement(sql)) {
      statement.setLong(1, wine.getKey());

      try (ResultSet resultSet = statement.executeQuery()) {
        ObservableList<WineReview> wineReviews = extractAllWineReviewsFromResultSet(resultSet);
        log.info("Successfully retrieved all {} reviews for wine with ID {} in {}ms",
            wineReviews.size(), wine.getKey(), timer.stop());
        return wineReviews;
      }
    } catch (SQLException error) {
      log.info("Failed to retrieve reviews for wine with ID {}", wine.getKey(), error);
    }
    return FXCollections.emptyObservableList();
  }

  public ObservableList<WineReview> getAll(User user) {
    Timer timer = new Timer();
    String sql = "SELECT * FROM WINE_REVIEW WHERE USERNAME = ?";
    try (PreparedStatement statement = connection.prepareStatement(sql)) {
      statement.setString(1, user.getUsername());

      try (ResultSet resultSet = statement.executeQuery()) {
        ObservableList<WineReview> wineReviews = extractAllWineReviewsFromResultSet(resultSet);
        log.info("Successfully retrieved all {} reviews for user '{}' in {}ms",
            wineReviews.size(), user.getUsername(), timer.stop());
        return wineReviews;
      }
    } catch (SQLException error) {
      log.info("Failed to retrieve reviews for user '{}'", user.getUsername(), error);
    }
    return FXCollections.emptyObservableList();
  }

  public ObservableList<WineReview> getAllInRange(int begin, int end) {
    Timer timer = new Timer();
    String sql = "SELECT * FROM WINE_REVIEW " +
        "LIMIT ? " +
        "OFFSET ?";
    try (PreparedStatement statement = connection.prepareStatement(sql)) {
      statement.setInt(1, end - begin);
      statement.setInt(2, begin);

      try (ResultSet resultSet = statement.executeQuery()) {
        ObservableList<WineReview> wineReviews = extractAllWineReviewsFromResultSet(resultSet);
        log.info("Successfully retrieved {} reviews in range {}-{} for user '{}' in {}ms",
            wineReviews.size(), begin, end, timer.stop());
        return wineReviews;
      }
    } catch (SQLException error) {
      log.info("Failed to retrieve reviews in range {}-{}", begin, end, error);
    }
    return FXCollections.emptyObservableList();
  }

  public WineReview add(User user, Wine wine, double rating, String description, Date date) {
    Timer timer = new Timer();
    String insert = "INSERT INTO WINE_REVIEW VALUES (null, ?, ?, ?, ?, ?)";
    try (PreparedStatement statement = connection.prepareStatement(insert,
        Statement.RETURN_GENERATED_KEYS)) {
      statement.setString(1, user.getUsername());
      statement.setLong(2, wine.getKey());
      statement.setDouble(3, rating);
      statement.setString(4, description);
      statement.setDate(5, date);
      statement.executeUpdate();

      try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
        if (generatedKeys.next()) {
          long id = generatedKeys.getLong(1);
          log.info(
              "Successfully created wine review with ID {} for user '{}' and wine with ID {} in {}ms",
              id, user.getUsername(), wine.getKey(), timer.stop());
          return new WineReview(
              id,
              wine.getKey(),
              user.getUsername(),
              rating,
              description,
              date
          );
        }
        log.warn("Could not create wine review for user '{}' and wine with ID {} in {}ms",
            user.getUsername(), wine.getKey(), timer.stop());
      }
    } catch (SQLException error) {
      log.info("Failed to create review for user '{}' and wine with ID {}",
          user.getUsername(), wine.getKey(), error);
    }
    return null;
  }

  private ObservableList<WineReview> extractAllWineReviewsFromResultSet(ResultSet resultSet) throws SQLException {
    ObservableList<WineReview> wineReviews = FXCollections.observableArrayList();
    while (resultSet.next()) {
      wineReviews.add(extractWineReviewFromResultSet(resultSet));
    }
    return wineReviews;
  }

  private WineReview extractWineReviewFromResultSet(ResultSet resultSet) throws SQLException {
    long id = resultSet.getLong("ID");
    WineReview cachedWineReview = wineReviewCache.tryGetObject(id);
    if (cachedWineReview != null) {
      return cachedWineReview;
    }

    return new WineReview(
        id,
        resultSet.getLong("WIND_ID"),
        resultSet.getString("USERNAME"),
        resultSet.getDouble("RATING"),
        resultSet.getString("DESCRIPTION"),
        resultSet.getDate("DATE")
    );
  }
}
