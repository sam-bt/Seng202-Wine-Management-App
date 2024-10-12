package seng202.team6.model;

import java.sql.Date;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyLongProperty;
import javafx.beans.property.ReadOnlyLongWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
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
  private IntegerProperty flag;
  private BooleanProperty selected;

  /**
   * Constructor.
   *
   * @param id          id
   * @param wineId      id
   * @param username    username of user
   * @param rating      rating
   * @param description description
   * @param date        date
   */
  public WineReview(
      long id,
      long wineId,
      String username,
      double rating,
      String description,
      Date date,
      int flag
  ) {
    this.id = new ReadOnlyLongWrapper(this, "id", id);
    this.wineId = new ReadOnlyLongWrapper(wineId);
    this.username = new ReadOnlyStringWrapper(username);
    this.rating = new SimpleDoubleProperty(this, "rating", rating);
    this.description = new SimpleStringProperty(this, "description", description);
    this.date = new SimpleObjectProperty<>(date);
    this.flag = new SimpleIntegerProperty(flag);
    this.selected = new SimpleBooleanProperty(this, "selected", false);
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
   * @return rating.
   */
  public double getRating() {
    return rating.get();
  }

  /**
   * Sets the rating.
   *
   * @param rating rating.
   */
  public void setRating(double rating) {
    this.rating.setValue(rating);
  }

  /**
   * Gets the rating property.
   *
   * @return rating.
   */
  public DoubleProperty ratingProperty() {
    return rating;
  }

  /**
   * Gets the description.
   *
   * @return description.
   */
  public String getDescription() {
    return description.get();
  }

  /**
   * Sets the description.
   *
   * @param description description.
   */
  public void setDescription(String description) {
    this.description.setValue(description);
  }

  /**
   * Gets the description property.
   *
   * @return description.
   */
  public StringProperty descriptionProperty() {
    return description;
  }

  /**
   * Gets the date.
   *
   * @return date.
   */
  public Date getDate() {
    return date.getValue();
  }

  /**
   * Sets the date.
   */
  public void setDate(Date date) {
    this.date.setValue(date);
  }

  /**
   * Gets the date property.
   *
   * @return date.
   */
  public Property<Date> dateProperty() {
    return date;
  }

  /**
   * Get the flag. 0 == not flagged, 1 == flagged.
   *
   * @return the flag.
   */
  public int getFlag() {
    return flag.get();
  }

  /**
   * Get the flag property.
   *
   * @return the flag property.
   */
  public IntegerProperty flagProperty() {
    return flag;
  }

  /** Set the value of the flag property.
   *
   * @param flag The value to set.
   */
  public void setFlag(int flag) {
    this.flag.set(flag);
  }


  /**
   * Get the selected value. This is a boolean used in review moderation
   * table to select multiple reviews at once for mass deletion or reinstation.
   *
   * @return the selected value
   */
  public boolean getSelected() {
    return selected.get();
  }

  /**
   * Get the selected property.
   *
   * @return the selected property.
   */
  public BooleanProperty selectedProperty() {
    return selected;
  }

  /** Set the value of the selected property.
   *
   * @param selected The value to set.
   */
  public void setSelected(boolean selected) {
    this.selected.set(selected);
  }

}
