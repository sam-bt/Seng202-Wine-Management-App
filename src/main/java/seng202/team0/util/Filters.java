package seng202.team0.util;

public class Filters {

  private final String title;
  private final String country;
  private final String winery;
  private final String color;
  private final int minVintage;
  private final int maxVintage;
  private final double minScore;
  private final double maxScore;
  private final double minAbv;
  private final double maxAbv;
  private final double minPrice;
  private final double maxPrice;

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

  public String getTitle() {
    return title;
  }

  public String getCountry() {
    return country;
  }

  public String getWinery() {
    return winery;
  }

  public String getColor() {
    return color;
  }

  public int getMinVintage() {
    return minVintage;
  }

  public int getMaxVintage() {
    return maxVintage;
  }

  public double getMinScore() {
    return minScore;
  }

  public double getMaxScore() {
    return maxScore;
  }

  public double getMinAbv() {
    return minAbv;
  }

  public double getMaxAbv() {
    return maxAbv;
  }

  public double getMinPrice() {
    return minPrice;
  }

  public double getMaxPrice() {
    return maxPrice;
  }
}
