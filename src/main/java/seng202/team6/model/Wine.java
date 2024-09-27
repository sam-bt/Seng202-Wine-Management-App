package seng202.team6.model;

import java.sql.SQLException;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.FloatProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.apache.logging.log4j.LogManager;
import seng202.team6.managers.OldDatabaseManager;

/**
 * Wine represents the wine record in the database
 * <p>
 *   There must only be one wine per id. This is assured by the database
 * </p>
 */
public class Wine {

  /**
   * Title
   */
  private final StringProperty title;

  /**
   * Variety
   */
  private final StringProperty variety;

  /**
   * Country
   */
  private final StringProperty country;
  /**
   * Region
   */
  private final StringProperty region;
  /**
   * Winery
   * <p>
   * Represented as a name to ease binding
   * </p>
   */
  private final StringProperty winery;
  /**
   * Color of the wine
   */
  private final StringProperty color;
  /**
   * Age of the wine as a year
   */
  private final IntegerProperty vintage;
  /**
   * Description of the wine
   */
  private final StringProperty description;
  /**
   * Normalized score from 0-100
   */
  private final IntegerProperty scorePercent;
  /**
   * Alcohol by volume as a percentage if known, else 0
   */
  private final FloatProperty abv;
  /**
   * Price of the wine in NZD if known, else 0
   */
  private final FloatProperty price;
  /**
   * ID of wine record
   * <p>
   * -1 represents no database record attached. Setters will fail in this case.
   * </p>
   */
  private long key;
  /**
   * Reference to database
   * <p>
   * We need to maintain this for JavaBean compliance with the setters and getters
   * </p>
   */

  /**
   * number of reviews the wine has
   */
  private IntegerProperty reviewCount;

  /**
   * Average rating of the wine
   */
  private DoubleProperty rating;


  private OldDatabaseManager databaseManager;
  /**
   * GeoLocation which holds the coordinates of the region name
   * <p>
   * If the region is invalid, not present, or not found from the geolocation dataset, this will be
   * null
   * </p>
   */
  private GeoLocation geoLocation;

  /**
   * Constructs a new Wine with the given attributes
   *
   * @param key          database key, -1 if no record attached
   * @param title        title
   * @param variety      variety
   * @param country      country
   * @param region       region
   * @param winery       winery
   * @param description  description of wine
   * @param scorePercent score from 0-100
   * @param abv          alcohol by volume
   * @param price        NZD price
   * @param geoLocation  geographical location
   */
  public Wine(
      long key,
      OldDatabaseManager databaseManager,
      String title,
      String variety,
      String country,
      String region,
      String winery,
      String color,
      Integer vintage,
      String description,
      Integer scorePercent,
      Float abv,
      Float price,
      GeoLocation geoLocation
  ) {
    this.key = key;
    this.databaseManager = databaseManager;
    this.title = new SimpleStringProperty(this, "title", title);
    this.variety = new SimpleStringProperty(this, "variety", variety);
    this.country = new SimpleStringProperty(this, "country", country);
    this.region = new SimpleStringProperty(this, "region", region);
    this.winery = new SimpleStringProperty(this, "winery", winery);
    this.color = new SimpleStringProperty(this, "color", color);
    this.vintage = new SimpleIntegerProperty(this, "vintage", vintage);
    this.description = new SimpleStringProperty(this, "description", description);
    this.scorePercent = new SimpleIntegerProperty(this, "scorePercent", scorePercent);
    this.abv = new SimpleFloatProperty(this, "abv", abv);
    this.price = new SimpleFloatProperty(this, "price", price);
    this.geoLocation = geoLocation;
    setupSetters();
  }

  /**
   * Constructs a new Wine with the given attributes
   *
   * @param key          database key, -1 if no record attached
   * @param title        title
   * @param variety      variety
   * @param country      country
   * @param region       region
   * @param winery       winery
   * @param description  description of wine
   * @param scorePercent score from 0-100
   * @param abv          alcohol by volume
   * @param price        NZD price
   * @param geoLocation  geographical location
   * @param reviewCount  number of reviews that wine has
   * @param rating  average rating of the wine
   */
  public Wine(
      long key,
      OldDatabaseManager databaseManager,
      String title,
      String variety,
      String country,
      String region,
      String winery,
      String color,
      Integer vintage,
      String description,
      Integer scorePercent,
      Float abv,
      Float price,
      GeoLocation geoLocation,
      Integer reviewCount,
      Double rating
  ) {
    this.key = key;
    this.databaseManager = databaseManager;
    this.title = new SimpleStringProperty(this, "title", title);
    this.variety = new SimpleStringProperty(this, "variety", variety);
    this.country = new SimpleStringProperty(this, "country", country);
    this.region = new SimpleStringProperty(this, "region", region);
    this.winery = new SimpleStringProperty(this, "winery", winery);
    this.color = new SimpleStringProperty(this, "color", color);
    this.vintage = new SimpleIntegerProperty(this, "vintage", vintage);
    this.description = new SimpleStringProperty(this, "description", description);
    this.scorePercent = new SimpleIntegerProperty(this, "scorePercent", scorePercent);
    this.abv = new SimpleFloatProperty(this, "abv", abv);
    this.price = new SimpleFloatProperty(this, "price", price);
    this.geoLocation = geoLocation;
    this.reviewCount = new SimpleIntegerProperty(this, "reviewCount", reviewCount);
    this.rating = new SimpleDoubleProperty(this, "rating", rating);

  }

  /**
   * Default constructor for a Wine with default attributes
   */
  public Wine() {
    this.key = -1;
    this.title = new SimpleStringProperty(this, "title");
    this.variety = new SimpleStringProperty(this, "variety");
    this.country = new SimpleStringProperty(this, "country");
    this.region = new SimpleStringProperty(this, "region");
    this.winery = new SimpleStringProperty(this, "winery");
    this.color = new SimpleStringProperty(this, "color");
    this.vintage = new SimpleIntegerProperty(this, "vintage");
    this.description = new SimpleStringProperty(this, "description");
    this.scorePercent = new SimpleIntegerProperty(this, "scorePercent");
    this.abv = new SimpleFloatProperty(this, "abv");
    this.price = new SimpleFloatProperty(this, "price");
    setupSetters();
  }

  /**
   * Setup listeners
   * <p>
   * JavaFX uses properties to change stuff. This means we need to listen to changes rather than
   * just intercept through setters.
   * </p>
   */
  public void setupSetters() {
    // This might be mem leaky but that is too hard to think about
    titleProperty().addListener((observableValue, before, after) -> {
      setAttribute("TITLE", update -> {
        update.setString(1, after);
      });
    });

    varietyProperty().addListener((observableValue, before, after) -> {
      setAttribute("VARIETY", update -> {
        update.setString(1, after);
      });
    });

    countryProperty().addListener((observableValue, before, after) -> {
      setAttribute("COUNTRY", update -> {
        update.setString(1, after);
      });
    });

    regionProperty().addListener((observableValue, before, after) -> {
      setAttribute("REGION", update -> {
        update.setString(1, after);
      });
    });

    wineryProperty().addListener((observableValue, before, after) -> {
      setAttribute("WINERY", update -> {
        update.setString(1, after);
      });
    });

    colorProperty().addListener((observableValue, before, after) -> {
      setAttribute("COLOR", update -> {
        update.setString(1, after);
      });
    });

    vintageProperty().addListener((observableValue, before, after) -> {
      setAttribute("VINTAGE", update -> {
        update.setInt(1, (Integer) after);
      });
    });

    descriptionProperty().addListener((observableValue, before, after) -> {
      setAttribute("DESCRIPTION", update -> {
        update.setString(1, after);
      });
    });

    scorePercentProperty().addListener((observableValue, before, after) -> {
      setAttribute("SCORE_PERCENT", update -> {
        update.setInt(1, (Integer) after);
      });
    });

    abvProperty().addListener((observableValue, before, after) -> {
      setAttribute("ABV", update -> {
        update.setFloat(1, (Float) after);
      });
    });

    priceProperty().addListener((observableValue, before, after) -> {
      setAttribute("PRICE", update -> {
        update.setFloat(1, (Float) after);
      });
    });
  }


  /**
   * Helper to set an attribute
   *
   * @param attributeName name of attribute
   * @param callback      callback to set attribute
   */
  private void setAttribute(String attributeName,
      OldDatabaseManager.AttributeSetterCallBack callback) {
    if (key == -1) {
      return;
    }
    try {
      databaseManager.setWineAttribute(key, attributeName, callback);
    } catch (SQLException exception) {
      LogManager.getLogger(getClass())
          .error("Failed to set attribute when updating database: {}", attributeName, exception);
    }

  }

  /**
   * Gets the key
   *
   * @return key
   */
  public long getKey() {
    return key;
  }

  /**
   * Sets the key
   *
   * @param key key
   */
  public void setKey(long key) {
    this.key = key;
  }

  /**
   * Gets the database
   * <p>
   * Please don't use this. This is only here for the sake of being a bean.
   * </p>
   *
   * @return database
   */
  public OldDatabaseManager getDatabaseManager() {
    return databaseManager;
  }

  /**
   * Sets the database
   *
   * @param databaseManager database
   */
  public void setDatabaseManager(OldDatabaseManager databaseManager) {
    this.databaseManager = databaseManager;
  }

  /**
   * Gets the title
   *
   * @return title
   */
  public String getTitle() {
    return title.get();
  }

  /**
   * Sets the title
   *
   * @param title title
   */
  public void setTitle(String title) {
    this.title.set(title);
  }

  /**
   * Gets the title property
   *
   * @return title property
   */
  public StringProperty titleProperty() {
    return title;
  }

  /**
   * Gets the variety
   *
   * @return variety
   */
  public String getVariety() {
    return variety.get();
  }

  /**
   * Sets the variety
   *
   * @param variety variety
   */
  public void setVariety(String variety) {
    this.variety.set(variety);
  }

  /**
   * Gets the variety property
   *
   * @return variety property
   */
  public StringProperty varietyProperty() {
    return variety;
  }

  /**
   * Gets the country
   *
   * @return country
   */
  public String getCountry() {
    return country.get();
  }

  /**
   * Sets the country
   *
   * @param country country
   */
  public void setCountry(String country) {
    this.country.set(country);
  }

  /**
   * Gets the country property
   *
   * @return country property
   */
  public StringProperty countryProperty() {
    return country;
  }

  /**
   * Gets the Region
   *
   * @return region
   */
  public String getRegion() {
    return region.get();
  }

  /**
   * Sets the region
   *
   * @param region region
   */
  public void setRegion(String region) {
    this.region.set(region);
  }

  /**
   * Gets the region property
   *
   * @return region property
   */
  public StringProperty regionProperty() {
    return region;
  }


  /**
   * Gets the winery
   *
   * @return winery
   */
  public String getWinery() {
    return winery.get();
  }

  /**
   * Sets the winery
   *
   * @param winery winery
   */
  public void setWinery(String winery) {
    this.winery.set(winery);
  }

  /**
   * Gets the winery property
   *
   * @return winery property
   */
  public StringProperty wineryProperty() {
    return winery;
  }

  /**
   * Gets the color of the wine
   *
   * @return color
   */
  public String getColor() {
    return color.get();
  }

  /**
   * Sets the color of the wine
   *
   * @param color color
   */
  public void setColor(String color) {
    this.color.set(color);
  }

  /**
   * Returns the color property
   *
   * @return color property
   */
  public StringProperty colorProperty() {
    return color;
  }

  /**
   * Returns the vintage
   *
   * @return vintage
   */
  public int getVintage() {
    return vintage.get();
  }

  /**
   * Sets the vintage
   *
   * @param vintage vintage
   */
  public void setVintage(int vintage) {
    this.vintage.set(vintage);
  }

  /**
   * Gets the vintage property
   *
   * @return vintage property
   */
  public IntegerProperty vintageProperty() {
    return vintage;
  }

  /**
   * Gets the description
   *
   * @return description
   */
  public String getDescription() {
    return description.get();
  }

  /**
   * Sets the description
   *
   * @param description description
   */
  public void setDescription(String description) {
    this.description.set(description);
  }

  /**
   * Gets the description property
   *
   * @return description property
   */
  public StringProperty descriptionProperty() {
    return description;
  }

  /**
   * Gets the score percentage
   *
   * @return score percentage
   */
  public int getScorePercent() {
    return scorePercent.get();
  }

  /**
   * Sets the score percentage
   *
   * @param scorePercent score percentage
   */
  public void setScorePercent(int scorePercent) {
    this.scorePercent.set(scorePercent);
  }

  /**
   * Gets the score percentage
   *
   * @return score percentage
   */
  public IntegerProperty scorePercentProperty() {
    return scorePercent;
  }

  /**
   * Gets the alcohol by volume
   *
   * @return abv
   */
  public float getAbv() {
    return abv.get();
  }

  /**
   * Gets the alcohol by volume
   *
   * @param abv alcohol by volume
   */
  public void setAbv(float abv) {
    this.abv.set(abv);
  }

  /**
   * Gets the alcohol by volume property
   *
   * @return alcohol by volume
   */
  public FloatProperty abvProperty() {
    return abv;
  }

  /**
   * Gets the price
   *
   * @return price
   */
  public float getPrice() {
    return price.get();
  }

  /**
   * Sets the price
   *
   * @param price price
   */
  public void setPrice(float price) {
    this.price.set(price);
  }

  /**
   * Gets the price property
   *
   * @return price property
   */
  public FloatProperty priceProperty() {
    return price;
  }

  /**
   * Gets the geolocation
   *
   * @return geolocation the geolocation
   */
  public GeoLocation getGeoLocation() {
    return geoLocation;
  }

  /**
   * Sets the geolocation
   *
   * @param geoLocation the geolocation
   */
  public void setGeoLocation(GeoLocation geoLocation) {
    this.geoLocation = geoLocation;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Wine wine = (Wine) o;
    return key == wine.key;
  }

  public IntegerProperty getReviewCount() {
    return reviewCount;
  }


  public DoubleProperty getRating() {
    return rating;
  }
}
