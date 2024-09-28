package seng202.team6.model;

import java.sql.Date;

import javafx.beans.property.*;

public class WineReview {
  private final ReadOnlyLongProperty id;
  private final ReadOnlyLongProperty wineId;
  private final StringProperty username;
  private final DoubleProperty rating;
  private final StringProperty description;
  private final Property<Date> date;
  private StringProperty wineName;
  private BooleanProperty isFlagged;

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
    this.isFlagged = new SimpleBooleanProperty(false, "flagged");
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
    this.isFlagged = new SimpleBooleanProperty(false);
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

  public StringProperty descriptionProperty() {
    return description;
  }

  public void setDescription(String description) {
    this.description.setValue(description);
  }

  public Date getDate() {
    return date.getValue();
  }

  public Property<Date> dateProperty() {
    return date;
  }

  public StringProperty getWineName() { return wineName; }

  public void setIsFlagged(boolean isFlagged) {
    this.isFlagged.set(isFlagged);
  }
  public BooleanProperty isFlaggedProperty() {return isFlagged;}
  public Boolean isFlagged() {return isFlagged.get();}
}
