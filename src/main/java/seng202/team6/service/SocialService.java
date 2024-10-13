package seng202.team6.service;

import java.sql.SQLException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import seng202.team6.managers.DatabaseManager;
import seng202.team6.model.User;
import seng202.team6.model.WineReview;


/**
 * Service class for the social features.
 */
public class SocialService {

  private final DatabaseManager databaseManager;
  private final User user;
  private final ObservableList<WineReview> userReviews = FXCollections.observableArrayList();

  /**
   * Constructor for the social service class.
   */
  public SocialService(DatabaseManager databaseManager, User user) {
    this.databaseManager = databaseManager;
    this.user = user;
  }

  /**
   * Initializer for the social service class.
   */
  public void init() throws SQLException {
    userReviews.addAll(databaseManager.getWineReviewDao().getAll(user));
  }

  public ObservableList<WineReview> getUserReviews() {
    return userReviews;
  }

}
