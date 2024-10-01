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

public class WineReview {

  private final ReadOnlyLongProperty id;
  private final ReadOnlyLongProperty wineId;
  private final StringProperty username;
  private final DoubleProperty rating;
  private final StringProperty description;
  private final Property<Date> date;
  private StringProperty wineName;

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

  public WineReview(
      long id,
      long wineId,
      String username,
      double rating,
      String description,
      Date date,
      String wineName
  ) {
    this.id = new ReadOnlyLongWrapper(wineId);
    this.wineId = new ReadOnlyLongWrapper(wineId);
    this.username = new ReadOnlyStringWrapper(username);
    this.rating = new SimpleDoubleProperty(rating);
    this.description = new SimpleStringProperty(description);
    this.date = new SimpleObjectProperty<>(date);
    this.wineName = new SimpleStringProperty(wineName);
  }

  public long getID() {
    return id.get();
  }

  public ReadOnlyLongProperty idProperty() {
    return id;
  }

  public long getWineID() {
    return wineId.get();
  }

  public ReadOnlyLongProperty wineIDProperty() {
    return wineId;
  }

  public String getUsername() {
    return username.get();
  }

  public StringProperty usernameProperty() {
    return username;
  }

  public double getRating() {
    return rating.get();
  }

  public void setRating(double rating) {
    this.rating.setValue(rating);
  }

  public DoubleProperty ratingProperty() {
    return rating;
  }

  public String getDescription() {
    return description.get();
  }

  public void setDescription(String description) {
    this.description.setValue(description);
  }

  public StringProperty descriptionProperty() {
    return description;
  }

  public Date getDate() {
    return date.getValue();
  }

  public Property<Date> dateProperty() {
    return date;
  }

  public StringProperty getWineName() {
    return wineName;
  }
}
