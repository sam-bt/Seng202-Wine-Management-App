package seng202.team6.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import seng202.team6.managers.DatabaseManager;
import seng202.team6.model.User;
import seng202.team6.model.Wine;
import seng202.team6.model.WineReview;
import seng202.team6.util.DatabaseObjectUniquer;
import seng202.team6.util.Timer;

/**
 * Data Access Object (DAO) for handling wine review related database operations.
 */
public class WineReviewDao extends Dao {

  /**
   * Cache to store and reuse WineReview objects to avoid duplication.
   */
  private final DatabaseObjectUniquer<WineReview> wineReviewCache = new DatabaseObjectUniquer<>();

  /**
   * Constructs a new WineReviewDAO with the given database connection.
   *
   * @param connection The database connection to be used for wine review operations.
   */
  public WineReviewDao(Connection connection) {
    super(connection, WineReviewDao.class);
  }

  /**
   * Returns the SQL statements required to initialise the WINE_REVIEW table.
   *
   * @return Array of SQL statements for initialising the WINE_REVIEW table
   */
  @Override
  public String[] getInitialiseStatements() {
    return new String[]{
        "CREATE TABLE IF NOT EXISTS WINE_REVIEW ("
            + "ID             INTEGER       PRIMARY KEY,"
            + "USERNAME       varchar(64)   NOT NULL,"
            + "WINE_ID        INTEGER       NOT NULL,"
            + "RATING         DOUBLE        NOT NULL,"
            + "DESCRIPTION    VARCHAR(256)  NOT NULL,"
            + "DATE           DATE          NOT NULL,"
            + "FLAG           INTEGER       NOT NULL CHECK(FLAG IN (\"0\", \"1\")),"
            + "FOREIGN KEY (USERNAME) REFERENCES USER(USERNAME) ON DELETE CASCADE,"
            + "FOREIGN KEY (WINE_ID) REFERENCES WINE(ID) ON DELETE CASCADE"
            + ")"
    };
  }

  /**
   * Retrieves all wine reviews from the WINE_REVIEW table belonging to the specified wine.
   *
   * @param wine The wine whose wine reviews should be returned
   * @return An ObservableList of all WineReview objects in the database belonging to the specified
   *        wine.
   */
  public ObservableList<WineReview> getAll(Wine wine) throws SQLException {
    Timer timer = new Timer();
    String sql = "SELECT WINE_REVIEW.ID as wine_review_id, WINE_REVIEW.* "
        + "FROM WINE_REVIEW WHERE WINE_ID = ?";
    try (PreparedStatement statement = connection.prepareStatement(sql)) {
      statement.setLong(1, wine.getKey());

      try (ResultSet resultSet = statement.executeQuery()) {
        ObservableList<WineReview> wineReviews = extractAllWineReviewsFromResultSet(resultSet,
            "wine_review_id");
        log.info("Successfully retrieved all {} reviews for wine with ID {} in {}ms",
            wineReviews.size(), wine.getKey(), timer.currentOffsetMilliseconds());
        return wineReviews;
      }
    }
  }

  /**
   * Retrieves all wine reviews from the WINE_REVIEW table belonging to the specified user.
   *
   * @param user The user whose wine reviews should be returned
   * @return An ObservableList of all WineReview objects in the database belonging to the specified
   *        user
   */
  public ObservableList<WineReview> getAll(User user) throws SQLException {
    Timer timer = new Timer();
    String sql = "SELECT WINE_REVIEW.ID as wine_review_id, WINE_REVIEW.* "
        + "FROM WINE_REVIEW "
        + "WHERE USERNAME = ?";
    try (PreparedStatement statement = connection.prepareStatement(sql)) {
      statement.setString(1, user.getUsername());

      try (ResultSet resultSet = statement.executeQuery()) {
        ObservableList<WineReview> wineReviews = extractAllWineReviewsFromResultSet(resultSet,
            "wine_review_id");
        log.info("Successfully retrieved all {} reviews for user '{}' in {}ms",
            wineReviews.size(), user.getUsername(), timer.currentOffsetMilliseconds());
        return wineReviews;
      }
    }
  }

  /**
   * Retrieves a range of reviews from the WINE_REVIEW table.
   *
   * @param begin The start index of the range (inclusive)
   * @param end   The end index of the range (exclusive)
   * @return An ObservableList of WineReview objects within the specified range
   */
  public ObservableList<WineReview> getAllInRange(int begin, int end) throws SQLException {
    Timer timer = new Timer();
    String sql = "SELECT WINE_REVIEW.ID as wine_review_id, WINE_REVIEW.* "
        + "FROM WINE_REVIEW "
        + "LIMIT ? "
        + "OFFSET ?";
    try (PreparedStatement statement = connection.prepareStatement(sql)) {
      statement.setInt(1, end - begin);
      statement.setInt(2, begin);

      try (ResultSet resultSet = statement.executeQuery()) {
        ObservableList<WineReview> wineReviews = extractAllWineReviewsFromResultSet(resultSet,
            "wine_review_id");
        log.info("Successfully retrieved {} reviews in range {}-{} for user '{}' in {}ms",
            wineReviews.size(), begin, end, timer.currentOffsetMilliseconds());
        return wineReviews;
      }
    }
  }

  /**
   * Creates a new wine review and adds it to the WINE_REVIEW table.
   *
   * @param user        The user creating the wine review
   * @param wine        The wine that is being reviewed
   * @param rating      The rating the wine received
   * @param description The description of the rating
   * @param date        The date the wine was reviewed
   * @return The WineReview object with the specified parameters
   */
  public WineReview add(User user, Wine wine, double rating, String description, Date date)
      throws SQLException {
    int flag = 0;
    Timer timer = new Timer();
    String insert = "INSERT INTO WINE_REVIEW VALUES (null, ?, ?, ?, ?, ?, ?)";
    try (PreparedStatement statement = connection.prepareStatement(insert,
        Statement.RETURN_GENERATED_KEYS)) {
      statement.setString(1, user.getUsername());
      statement.setLong(2, wine.getKey());
      statement.setDouble(3, rating);
      statement.setString(4, description);
      statement.setDate(5, date);
      statement.setInt(6, flag);
      statement.executeUpdate();

      try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
        if (generatedKeys.next()) {
          long id = generatedKeys.getLong(1);
          log.info(
              "Successfully created wine review with ID {} for user '{}' "
                  + "and wine with ID {} in {}ms",
              id, user.getUsername(), wine.getKey(), timer.currentOffsetMilliseconds());
          WineReview wineReview = new WineReview(
              id,
              wine.getKey(),
              user.getUsername(),
              rating,
              description,
              date,
              flag
          );
          wineReviewCache.addObject(id, wineReview);

          bindUpdater(wineReview);
          return wineReview;
        }
        log.warn("Could not create wine review for user '{}' and wine with ID {} in {}ms",
            user.getUsername(), wine.getKey(), timer.currentOffsetMilliseconds());
      }
    }
    return null;
  }

  /**
   * Deletes the specified wine review from the database.
   *
   * @param wineReview The wine review to be deleted
   */
  public void delete(WineReview wineReview) throws SQLException {
    Timer timer = new Timer();
    String sql = "DELETE FROM WINE_REVIEW WHERE ID = ?";
    try (PreparedStatement statement = connection.prepareStatement(sql)) {
      statement.setLong(1, wineReview.getId());

      int rowsAffected = statement.executeUpdate();
      if (rowsAffected == 1) {
        log.info("Successfully deleted wine review with ID {} in {}ms", wineReview.getId(),
            timer.currentOffsetMilliseconds());
      } else {
        log.warn("Could not delete wine review with ID {} in {}ms", wineReview.getId(),
            timer.currentOffsetMilliseconds());
      }
    }
    wineReviewCache.removeObject(wineReview.getId());
  }

  /**
   * Delete all reviews from a given user.
   *
   * @param user is the user whose reviews will be removed
   */
  public void deleteAllFromUser(User user) throws SQLException {
    Timer timer = new Timer();
    String sql = "DELETE FROM WINE_REVIEW WHERE USERNAME = ?";
    try (PreparedStatement statement = connection.prepareStatement(sql)) {
      statement.setString(1, user.getUsername());
      int rowsAffected = statement.executeUpdate();
      if (rowsAffected >= 1) {
        log.info("Successfully removed {} reviews in {}ms",
            rowsAffected, timer.currentOffsetMilliseconds());
      }
    }
  }

  /**
   * Extracts all wine reviews from the provided ResultSet and stores them in an ObservableList.
   *
   * @param resultSet The ResultSet containing wine review data
   * @return ObservableList of WineReview objects extracted from the ResultSet
   * @throws SQLException if a database access error occurs
   */
  private ObservableList<WineReview> extractAllWineReviewsFromResultSet(ResultSet resultSet,
      String idColumnName)
      throws SQLException {
    ObservableList<WineReview> wineReviews = FXCollections.observableArrayList();
    while (resultSet.next()) {
      wineReviews.add(extractWineReviewFromResultSet(resultSet, idColumnName));
    }
    return wineReviews;
  }

  /**
   * Get all reviews which have been flagged from the database. Used in review moderation.
   *
   * @return an ObservableList of flagged reviews.
   */
  public ObservableList<WineReview> getAllFlaggedReviews() throws SQLException {
    Timer timer = new Timer();

    String sql = "SELECT * FROM WINE_REVIEW WHERE FLAG = 1";
    try (PreparedStatement statement = connection.prepareStatement(sql)) {
      try (ResultSet resultSet = statement.executeQuery()) {
        ObservableList<WineReview> wineReviews =
            extractAllWineReviewsFromResultSet(resultSet, "ID");
        log.info("Successfully retrieved all flagged reviews in {}ms",
            timer.currentOffsetMilliseconds());
        return wineReviews;
      }
    }
  }

  /**
   * Deletes all flagged reviews (FLAG = '1') from the database.
   */
  public void deleteAllFlaggedReviews() throws SQLException {
    Timer timer = new Timer();
    String sql = "DELETE FROM WINE_REVIEW WHERE FLAG = 1";
    try (PreparedStatement statement = connection.prepareStatement(sql)) {
      int rowsAffected = statement.executeUpdate();
      if (rowsAffected >= 1) {
        log.info("Successfully removed {} reviews in {}ms",
            rowsAffected, timer.currentOffsetMilliseconds());
      }
    }

    wineReviewCache.clear();
  }

  /**
   * UPdate the flag of a review. Used in wine moderation to either flag the review or unflag it
   * from the mod panel.
   *
   * @param review The review to update.
   */
  public void updateWineReviewFlag(WineReview review) throws SQLException {
    log.info("Ran updateWineReviewFlag()");
    Timer timer = new Timer();
    String sql = "UPDATE WINE_REVIEW SET FLAG = ? WHERE ID = ?";
    log.info("Before try()");
    try (PreparedStatement statement = connection.prepareStatement(sql)) {
      statement.setInt(1, review.getFlag());
      statement.setLong(2, review.getId());

      int rowsAffected = statement.executeUpdate();
      if (rowsAffected >= 1) {
        log.info("Successfully updated {} reviews in {}ms",
            rowsAffected, timer.currentOffsetMilliseconds());
      }
    }
  }

  /**
   * Extracts a WineReview object from the provided ResultSet. The wine review cache is checked
   * before creating a new wine list instance.
   *
   * @param resultSet The ResultSet containing wine review data
   * @return The WineReview object extracted from the ResultSet
   * @throws SQLException if a database access error occurs
   */
  public WineReview extractWineReviewFromResultSet(ResultSet resultSet, String idColumnName)
      throws SQLException {
    long id = resultSet.getLong(idColumnName);
    WineReview cachedWineReview = wineReviewCache.tryGetObject(id);
    if (cachedWineReview != null) {
      return cachedWineReview;
    }

    WineReview wineReview = new WineReview(
        id,
        resultSet.getLong("WINE_ID"),
        resultSet.getString("USERNAME"),
        resultSet.getDouble("RATING"),
        resultSet.getString("DESCRIPTION"),
        resultSet.getDate("DATE"),
        resultSet.getInt("FLAG")
    );
    wineReviewCache.addObject(id, wineReview);
    bindUpdater(wineReview);
    return wineReview;
  }

  /**
   * Binds listeners to the WineReview object to ensure that any changes to the wine reviews
   * properties are automatically reflected in the database.
   *
   * @param wineReview The WineReview object to bind listeners to
   */
  private void bindUpdater(WineReview wineReview) {
    wineReview.ratingProperty().addListener(((observableValue, before, after) -> {
      try {
        updateAttribute(wineReview.getId(), "RATING", update -> {
          update.setDouble(1, after.doubleValue());
        });
      } catch (SQLException e) {
        throw new RuntimeException(e);
      }
    }));
    wineReview.descriptionProperty().addListener(((observableValue, before, after) -> {
      try {
        updateAttribute(wineReview.getId(), "DESCRIPTION", update -> {
          update.setString(1, after);
        });
      } catch (SQLException e) {
        throw new RuntimeException(e);
      }
    }));
  }

  /**
   * Updates a specific attribute of the wine review in the WINE_REVIEW table.
   *
   * @param attributeName   name of attribute
   * @param attributeSetter callback to set attribute
   */
  private void updateAttribute(long id, String attributeName,
      DatabaseManager.AttributeSetter attributeSetter) throws SQLException {
    Timer timer = new Timer();
    String sql = "UPDATE WINE_REVIEW set " + attributeName + " = ? where ID = ?";
    try (PreparedStatement update = connection.prepareStatement(sql)) {
      attributeSetter.setAttribute(update);
      update.setLong(2, id);

      int rowsAffected = update.executeUpdate();
      if (rowsAffected == 1) {
        log.info("Successfully updated attribute '{}' for wine review with ID {} in {}ms",
            attributeName, id, timer.currentOffsetMilliseconds());
      } else {
        log.info("Could not update attribute '{}' for wine review with ID {} in {}ms",
            attributeName, id, timer.currentOffsetMilliseconds());
      }
    }
  }
}
