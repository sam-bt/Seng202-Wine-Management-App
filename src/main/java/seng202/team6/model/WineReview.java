package seng202.team6.model;

import java.util.Date;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyLongProperty;
import javafx.beans.property.ReadOnlyLongWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class WineReview {
  private final ReadOnlyLongProperty wineIdProperty;
  private final ReadOnlyLongProperty userIdProperty;
  private final StringProperty usernameProperty;
  private final DoubleProperty rating;
  private final StringProperty descriptionProperty;
  private final Property<Date> dateProperty;

  private WineReview(
      long wineId,
      long userId,
      String username,
      double rating,
      String description,
      Date date
  ) {
    this.wineIdProperty = new ReadOnlyLongWrapper(wineId);
    this.userIdProperty = new ReadOnlyLongWrapper(userId);
    this.usernameProperty = new ReadOnlyStringWrapper(username);
    this.rating = new SimpleDoubleProperty(rating);
    this.descriptionProperty = new SimpleStringProperty(description);
    this.dateProperty = new SimpleObjectProperty<>(date);
  }

  public long getWineId() {
    return wineIdProperty.get();
  }

  public ReadOnlyLongProperty wineIdPropertyProperty() {
    return wineIdProperty;
  }

  public long getUserId() {
    return userIdProperty.get();
  }

  public ReadOnlyLongProperty userIdPropertyProperty() {
    return userIdProperty;
  }

  public String getUsername() {
    return usernameProperty.get();
  }

  public StringProperty usernamePropertyProperty() {
    return usernameProperty;
  }

  public double getRating() {
    return rating.get();
  }

  public DoubleProperty ratingProperty() {
    return rating;
  }

  public String getDescription() {
    return descriptionProperty.get();
  }

  public StringProperty descriptionPropertyProperty() {
    return descriptionProperty;
  }

  public Date getDate() {
    return dateProperty.getValue();
  }

  public Property<Date> datePropertyProperty() {
    return dateProperty;
  }
}
