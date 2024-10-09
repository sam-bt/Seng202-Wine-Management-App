package seng202.team6.model;

import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyLongProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * The Vineyard class represents a vineyard.
 */
public class Vineyard {

  private final ReadOnlyLongProperty id;
  private final StringProperty name;
  private final StringProperty address;
  private final StringProperty region;
  private final StringProperty website;
  private final StringProperty description;
  private final StringProperty logoUrl;
  private final Property<GeoLocation> geoLocation;

  /**
   * Constructs a new Vineyard object with the specified properties.
   *
   * @param name        the name of the vineyard
   * @param address     the address of the vineyard
   * @param region      the region where the vineyard is located
   * @param website     the website of the vineyard
   * @param description a brief description of the vineyard
   * @param geoLocation the geographic location of the vineyard
   */
  public Vineyard(long id, String name, String address, String region, String website,
      String description, String logoUrl, GeoLocation geoLocation) {
    this.id = new SimpleLongProperty(id);
    this.name = new SimpleStringProperty(name);
    this.address = new SimpleStringProperty(address);
    this.region = new SimpleStringProperty(region);
    this.website = new SimpleStringProperty(website);
    this.description = new SimpleStringProperty(description);
    this.logoUrl = new SimpleStringProperty(logoUrl);
    this.geoLocation = new SimpleObjectProperty<>(geoLocation);
  }

  /**
   * Returns the LongProperty representing the vineyard's id.
   *
   * @return the LongProperty of the vineyard's id
   */
  public ReadOnlyLongProperty idProperty() {
    return id;
  }

  /**
   * Returns the vineyard's id.
   *
   * @return the id of the vineyard
   */
  public long getId() {
    return id.get();
  }

  /**
   * Returns the StringProperty representing the vineyard's name.
   *
   * @return the StringProperty of the vineyard's name
   */
  public StringProperty nameProperty() {
    return name;
  }

  /**
   * Returns the vineyard's name.
   *
   * @return the name of the vineyard
   */
  public String getName() {
    return name.get();
  }

  /**
   * Sets the vineyard's name.
   *
   * @param name the new name of the vineyard
   */
  public void setName(String name) {
    nameProperty().set(name);
  }

  /**
   * Returns the StringProperty representing the vineyard's address.
   *
   * @return the StringProperty of the vineyard's address
   */
  public StringProperty addressProperty() {
    return address;
  }

  /**
   * Returns the vineyard's address.
   *
   * @return the address of the vineyard
   */
  public String getAddress() {
    return address.get();
  }

  /**
   * Sets the vineyard's address.
   *
   * @param address the new address of the vineyard
   */
  public void setAddress(String address) {
    addressProperty().set(address);
  }

  /**
   * Returns the StringProperty representing the vineyard's region.
   *
   * @return the StringProperty of the vineyard's region
   */
  public StringProperty regionProperty() {
    return region;
  }

  /**
   * Returns the vineyard's region.
   *
   * @return the region of the vineyard
   */
  public String getRegion() {
    return region.get();
  }

  /**
   * Sets the vineyard's region.
   *
   * @param region the new region of the vineyard
   */
  public void setRegion(String region) {
    regionProperty().set(region);
  }

  /**
   * Returns the StringProperty representing the vineyard's website.
   *
   * @return the StringProperty of the vineyard's website
   */
  public StringProperty websiteProperty() {
    return website;
  }

  /**
   * Returns the vineyard's website URL.
   *
   * @return the website URL of the vineyard
   */
  public String getWebsite() {
    return website.get();
  }

  /**
   * Sets the vineyard's website.
   *
   * @param website the new website URL of the vineyard
   */
  public void setWebsite(String website) {
    websiteProperty().set(website);
  }

  /**
   * Returns the StringProperty representing the vineyard's description.
   *
   * @return the StringProperty of the vineyard's description
   */
  public StringProperty descriptionProperty() {
    return description;
  }

  /**
   * Returns the vineyard's description.
   *
   * @return the description of the vineyard
   */
  public String getDescription() {
    return description.get();
  }

  /**
   * Sets the vineyard's description.
   *
   * @param description the new description of the vineyard
   */
  public void setDescription(String description) {
    descriptionProperty().set(description);
  }

  /**
   * Returns the StringProperty representing the vineyard's logo url.
   *
   * @return the StringProperty of the vineyard's logo url.
   */
  public StringProperty logoUrlProperty() {
    return logoUrl;
  }

  /**
   * Retrieves the vineyard's logo url.
   *
   * @return the logo url of the vineyard.
   */
  public String getLogoUrl() {
    return logoUrl.get();
  }

  /**
   * Sets the vineyard's logo url.
   *
   * @param logoUrl the new logo url of the vineyard.
   */
  public void setLogoUrl(String logoUrl) {
    logoUrlProperty().set(logoUrl);
  }

  /**
   * Returns the Property representing the vineyard's geographic location.
   *
   * @return the Property of the vineyard's geographic location
   */
  public Property<GeoLocation> geoLocationProperty() {
    return geoLocation;
  }

  /**
   * Returns the vineyard's geographic location.
   *
   * @return the geographic location of the vineyard
   */
  public GeoLocation getGeoLocation() {
    return geoLocation.getValue();
  }

  /**
   * Sets the vineyard's geographic location.
   *
   * @param geoLocation the new geographic location of the vineyard
   */
  public void setGeoLocation(GeoLocation geoLocation) {
    geoLocationProperty().setValue(geoLocation);
  }

  @Override
  public String toString() {
    return "Vineyard{" + "id=" + id + ", name=" + name + ", address=" + address + ", region="
        + region + ", website=" + website + ", description=" + description + ", logoUrl=" + logoUrl
        + ", geoLocation=" + geoLocation + '}';
  }
}
