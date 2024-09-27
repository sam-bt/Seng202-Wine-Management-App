package seng202.team6.service;

import java.sql.Date;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import seng202.team6.managers.AuthenticationManager;
import seng202.team6.managers.DatabaseManager;
import seng202.team6.model.User;
import seng202.team6.model.Wine;
import seng202.team6.model.WineReview;

public class WineReviewsService {

  private final AuthenticationManager authenticationManager;
  private final DatabaseManager databaseManager;
  private final ObservableList<WineReview> wineReviews = FXCollections.observableArrayList();
  private final Wine wine;
  private final Property<WineReview> usersReview = new SimpleObjectProperty<>();
  private final DoubleProperty averageRating = new SimpleDoubleProperty();

  public WineReviewsService(AuthenticationManager authenticationManager,
      DatabaseManager databaseManager, Wine wine) {
    this.authenticationManager = authenticationManager;
    this.databaseManager = databaseManager;
    this.wine = wine;
  }

  public void init() {
    String username = authenticationManager.getAuthenticatedUsername();
    wineReviews.addAll(databaseManager.getWineReviewDAO().getAll(wine));
    usersReview.setValue(wineReviews.stream()
        .filter(wineReview -> wineReview.getUsername().equals(username))
        .findFirst()
        .orElse(null));
    calculateAverageReview();
  }

  public void addOrUpdateUserReview(double rating, String description) {
    User user = authenticationManager.getAuthenticatedUser();
    if (hasUserReviewed()) {
      WineReview usersReview = getUsersReview();
      usersReview.setRating(rating);
      usersReview.setDescription(description);
      calculateAverageReview();
      return;
    }
    Date currentDate = new Date(System.currentTimeMillis());
    WineReview wineReview = databaseManager.getWineReviewDAO().add(user, wine, rating, description, currentDate);
    if (wineReview != null) {
      wineReviews.add(wineReview);
      usersReview.setValue(wineReview);
      calculateAverageReview();
    }
  }

  public void deleteUsersReview() {
    WineReview wineReview = getUsersReview();
    if (wineReview != null) {
      databaseManager.getWineReviewDAO().delete(wineReview);
      usersReview.setValue(null);
      wineReviews.remove(wineReview);
    }
  }

  public boolean hasReviews() {
    return !wineReviews.isEmpty();
  }

  public ObservableList<WineReview> getWineReviews() {
    return wineReviews;
  }

  public Wine getWine() {
    return wine;
  }

  public Property<WineReview> usersReviewProperty() {
    return usersReview;
  }

  public WineReview getUsersReview() {
    return usersReview.getValue();
  }

  public boolean hasUserReviewed() {
    return getUsersReview() != null;
  }

  public DoubleProperty averageRatingProperty() {
    return averageRating;
  }

  public double getAverageRating() {
    return averageRating.get();
  }

  private void calculateAverageReview() {
    if (!hasReviews())
      averageRating.set(0);

    double sum = wineReviews.stream()
        .mapToDouble(WineReview::getRating)
        .sum();
    averageRating.set(sum / getWineReviews().size());
  }
}
