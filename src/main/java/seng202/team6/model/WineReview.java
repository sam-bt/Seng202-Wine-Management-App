package seng202.team6.model;

import java.sql.Date;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyLongProperty;
import javafx.beans.property.ReadOnlyLongWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Represents a review of a wine.
 */
public class WineReview {

  private final ReadOnlyLongProperty id;
  private final ReadOnlyLongProperty wineId;
  private final StringProperty username;
  private final DoubleProperty rating;
  private final StringProperty description;
  private final Property<Date> date;

  /**
   * Constructor.
   *
   * @param id id
   * @param wineId id
   * @param username username of user
   * @param rating rating
   * @param description description
   * @param date date
   */
  public WineReview(
      long id,
      long wineId,
      String username,
      double rating,
      String description,
      Date date
  ) {
    this.id = new ReadOnlyLongWrapper(wineId);
    this.wineId = new ReadOnlyLongWrapper(wineId);
    this.username = new ReadOnlyStringWrapper(username);
    this.rating = new SimpleDoubleProperty(rating);
    this.description = new SimpleStringProperty(description);
    this.date = new SimpleObjectProperty<>(date);
  }

  /**
   * Gets the id of the review.
   *
   * @return id
   */
  public long getId() {
    return id.get();
  }

  /**
   * Gets the id property of the review.
   *
   * @return id
   */
  public ReadOnlyLongProperty idProperty() {
    return id;
  }

  /**
   * Gets the wine id.
   *
   * @return wine id
   */
  public long getWineId() {
    return wineId.get();
  }

  /**
   * Gets the wine id property.
   *
   * @return id
   */
  public ReadOnlyLongProperty wineIdProperty() {
    return wineId;
  }

  /**
   * Gets the username.
   *
   * @return username
   */
  public String getUsername() {
    return username.get();
  }

  /**
   * Gets the username property.
   *
   * @return username
   */
  public StringProperty usernameProperty() {
    return username;
  }

  /**
   * Gets the rating.
   *
   * @return rating
   */
  public double getRating() {
    return rating.get();
  }

  /**
   * Sets the rating.
   *
   * @param rating rating
   */
  public void setRating(double rating) {
    this.rating.setValue(rating);
  }

  /**
   * Gets the rating property.
   *
   * @return rating
   */
  public DoubleProperty ratingProperty() {
    return rating;
  }

  /**
   * Gets the description.
   *
   * @return description
   */
  public String getDescription() {
    return description.get();
  }

  /**
   * Sets the description.
   *
   * @param description description
   */
  public void setDescription(String description) {
    this.description.setValue(description);
  }

  /**
   * Gets the description property.
   *
   * @return description
   */
  public StringProperty descriptionProperty() {
    return description;
  }

  /**
   * Gets the date.
   *
   * @return date
   */
  public Date getDate() {
    return date.getValue();
  }

  /**
   * Gets the date property.
   *
   * @return date
   */
  public Property<Date> dateProperty() {
    return date;
  }
}
