package seng202.team6.model;

/**
 * Represents a geographical location with latitude and longitude coordinates
 */
public class GeoLocation {

  /**
   * The latitude coordinate of this location
   */
  private final double latitude;
  /**
   * The longitude coordinate of this location
   */
  private final double longitude;

  /**
   * Constructs a new GeoLocation object with the specified latitude and longitude
   *
   * @param latitude
   * @param longitude
   */
  public GeoLocation(double latitude, double longitude) {
    this.latitude = latitude;
    this.longitude = longitude;
  }

  /**
   * Returns the latitude coordinate of this location
   *
   * @return the latitude
   */
  public double getLatitude() {
    return latitude;
  }

  /**
   * Returns the longitude of this location
   *
   * @return the longitude
   */
  public double getLongitude() {
    return longitude;
  }
}
