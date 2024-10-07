package seng202.team6.service;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import seng202.team6.managers.AuthenticationManager;
import seng202.team6.managers.DatabaseManager;
import seng202.team6.model.User;
import seng202.team6.model.WineReview;

public class SocialService {

  private final AuthenticationManager authenticationManager;
  private final DatabaseManager databaseManager;
  private final User user;
  private final ObservableList<WineReview> userReviews = FXCollections.observableArrayList();

  public SocialService(AuthenticationManager authenticationManager,
      DatabaseManager databaseManager, User user) {
    this.authenticationManager = authenticationManager;
    this.databaseManager = databaseManager;
    this.user = user;
  }

  public void init() {
    userReviews.addAll(databaseManager.getWineReviewDao().getAll(user));
  }

  public ObservableList<WineReview> getUserReviews() {
    return userReviews;
  }

}
