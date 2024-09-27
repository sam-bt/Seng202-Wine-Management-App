package seng202.team6.service;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import seng202.team6.managers.AuthenticationManager;
import seng202.team6.managers.OldDatabaseManager;
import seng202.team6.model.Wine;
import seng202.team6.model.WineReview;

public class WineReviewsService {

  private final AuthenticationManager authenticationManager;
  private final OldDatabaseManager databaseManager;
  private final ObservableList<WineReview> wineReviews = FXCollections.observableArrayList();
  private final Wine wine;
  private final Property<WineReview> usersReview = new SimpleObjectProperty<>();
  private final DoubleProperty averageRating = new SimpleDoubleProperty();

  public WineReviewsService(AuthenticationManager authenticationManager,
      OldDatabaseManager databaseManager, Wine wine) {
    this.authenticationManager = authenticationManager;
    this.databaseManager = databaseManager;
    this.wine = wine;
  }

  public void init() {
    String username = authenticationManager.getAuthenticatedUsername();
    wineReviews.addAll(databaseManager.getWineReviews(wine));
    usersReview.setValue(wineReviews.stream()
        .filter(wineReview -> wineReview.getUsername().equals(username))
        .findFirst()
        .orElse(null));
    calculateAverageReview();
  }

  public void addOrUpdateUserReview(double rating, String description) {
    String username = authenticationManager.getAuthenticatedUsername();
    if (hasUserReviewed()) {
      WineReview usersReview = getUsersReview();
      usersReview.setRating(rating);
      usersReview.setDescription(description);
      databaseManager.updateWineReview(username, wine.getKey(), rating, description);
      calculateAverageReview();
      return;
    }
    WineReview wineReview = databaseManager.addWineReview(username, wine.getKey(), rating, description);
    if (wineReview != null) {
      wineReviews.add(wineReview);
      usersReview.setValue(wineReview);
      calculateAverageReview();
    }
  }

//  public WineReview getReviewByUsernameAndWineID(String username, long wineId) {
//    return databaseManager.get
//  }

  public void deleteUsersReview() {
    WineReview wineReview = getUsersReview();
    if (wineReview != null) {
      databaseManager.removeWineReview(wineReview);
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
