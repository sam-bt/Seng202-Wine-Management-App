package seng202.team6.model;

/**
 * Represents a set of filters for querying or filtering wine data. This class encapsulates various
 * criteria that can be used to filter wines
 */
public class Filters {

  /**
   * The title of the wine to filter by.
   */
  private String title;

  /**
   * The country of origin to filter by.
   */
  private String country;

  /**
   * The winery to filter by.
   */
  private String winery;

  /**
   * The color of the wine to filter by.
   */
  private String color;

  /**
   * The minimum vintage year to filter by.
   */
  private int minVintage;

  /**
   * The maximum vintage year to filter by.
   */
  private int maxVintage;

  /**
   * The minimum score to filter by.
   */
  private double minScore;

  /**
   * The maximum score to filter by.
   */
  private double maxScore;

  /**
   * The minimum alcohol by volume (ABV) percentage to filter by.
   */
  private double minAbv;

  /**
   * The maximum alcohol by volume (ABV) percentage to filter by.
   */
  private double maxAbv;

  /**
   * The minimum price to filter by.
   */
  private double minPrice;

  /**
   * The maximum price to filter by.
   */
  private double maxPrice;

  /**
   * Constructs a Filters object with specified criteria.
   *
   * @param title      The title of the wine.
   * @param country    The country of origin.
   * @param winery     The winery.
   * @param color      The color of the wine.
   * @param minVintage The minimum vintage year.
   * @param maxVintage The maximum vintage year.
   * @param minScore   The minimum score.
   * @param maxScore   The maximum score.
   * @param minAbv     The minimum ABV.
   * @param maxAbv     The maximum ABV.
   * @param minPrice   The minimum price.
   * @param maxPrice   The maximum price.
   */
  public Filters(
      String title,
      String country,
      String winery,
      String color,
      int minVintage,
      int maxVintage,
      double minScore,
      double maxScore,
      double minAbv,
      double maxAbv,
      double minPrice,
      double maxPrice
  ) {
    this.title = title;
    this.country = country;
    this.winery = winery;
    this.color = color;
    this.minVintage = minVintage;
    this.maxVintage = maxVintage;
    this.minScore = minScore;
    this.maxScore = maxScore;
    this.minAbv = minAbv;
    this.maxAbv = maxAbv;
    this.minPrice = minPrice;
    this.maxPrice = maxPrice;
  }

  /**
   * Constructs a Filters object with default values. String fields are set to empty strings, and
   * numeric fields are set to their respective MIN_VALUE and MAX_VALUE to represent no filtering.
   */
  public Filters() {
    this(
        "",
        "",
        "",
        "",
        0,
        Integer.MAX_VALUE,
        0,
        Double.MAX_VALUE,
        0,
        Double.MAX_VALUE,
        0,
        Double.MAX_VALUE);
  }

  /**
   * Gets the title filter.
   *
   * @return The title of the wine.
   */
  public String getTitle() {
    return title;
  }

  /**
   * Sets the title filter.
   *
   * @param title The title of the wine to set.
   */
  public void setTitle(String title) {
    this.title = title;
  }

  /**
   * Gets the country filter.
   *
   * @return The country of origin.
   */
  public String getCountry() {
    return country;
  }

  /**
   * Sets the country filter.
   *
   * @param country The country of origin to set.
   */
  public void setCountry(String country) {
    this.country = country;
  }

  /**
   * Gets the winery filter.
   *
   * @return The winery.
   */
  public String getWinery() {
    return winery;
  }

  /**
   * Sets the winery filter.
   *
   * @param winery The winery to set.
   */
  public void setWinery(String winery) {
    this.winery = winery;
  }

  /**
   * Gets the color filter.
   *
   * @return The color of the wine.
   */
  public String getColor() {
    return color;
  }

  /**
   * Sets the color filter.
   *
   * @param color The color of the wine to set.
   */
  public void setColor(String color) {
    this.color = color;
  }

  /**
   * Gets the minimum vintage filter.
   *
   * @return The minimum vintage year.
   */
  public int getMinVintage() {
    return minVintage;
  }

  /**
   * Sets the minimum vintage filter.
   *
   * @param minVintage The minimum vintage year to set.
   */
  public void setMinVintage(int minVintage) {
    this.minVintage = minVintage;
  }

  /**
   * Gets the maximum vintage filter.
   *
   * @return The maximum vintage year.
   */
  public int getMaxVintage() {
    return maxVintage;
  }

  /**
   * Sets the maximum vintage filter.
   *
   * @param maxVintage The maximum vintage year to set.
   */
  public void setMaxVintage(int maxVintage) {
    this.maxVintage = maxVintage;
  }

  /**
   * Gets the minimum score filter.
   *
   * @return The minimum score.
   */
  public double getMinScore() {
    return minScore;
  }

  /**
   * Sets the minimum score filter.
   *
   * @param minScore The minimum score to set.
   */
  public void setMinScore(double minScore) {
    this.minScore = minScore;
  }

  /**
   * Gets the maximum score filter.
   *
   * @return The maximum score.
   */
  public double getMaxScore() {
    return maxScore;
  }

  /**
   * Sets the maximum score filter.
   *
   * @param maxScore The maximum score to set.
   */
  public void setMaxScore(double maxScore) {
    this.maxScore = maxScore;
  }

  /**
   * Gets the minimum ABV filter.
   *
   * @return The minimum alcohol by volume percentage.
   */
  public double getMinAbv() {
    return minAbv;
  }

  /**
   * Sets the minimum ABV filter.
   *
   * @param minAbv The minimum alcohol by volume percentage to set.
   */
  public void setMinAbv(double minAbv) {
    this.minAbv = minAbv;
  }

  /**
   * Gets the maximum ABV filter.
   *
   * @return The maximum alcohol by volume percentage.
   */
  public double getMaxAbv() {
    return maxAbv;
  }

  /**
   * Sets the maximum ABV filter.
   *
   * @param maxAbv The maximum alcohol by volume percentage to set.
   */
  public void setMaxAbv(double maxAbv) {
    this.maxAbv = maxAbv;
  }

  /**
   * Gets the minimum price filter.
   *
   * @return The minimum price.
   */
  public double getMinPrice() {
    return minPrice;
  }

  /**
   * Sets the minimum price filter.
   *
   * @param minPrice The minimum price to set.
   */
  public void setMinPrice(double minPrice) {
    this.minPrice = minPrice;
  }

  /**
   * Gets the maximum price filter.
   *
   * @return The maximum price.
   */
  public double getMaxPrice() {
    return maxPrice;
  }

  /**
   * Sets the maximum price filter.
   *
   * @param maxPrice The maximum price to set.
   */
  public void setMaxPrice(double maxPrice) {
    this.maxPrice = maxPrice;
  }
}