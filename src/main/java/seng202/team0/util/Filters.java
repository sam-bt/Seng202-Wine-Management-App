package seng202.team0.util;

public class Filters {
  private String title;
  private String country;
  private String winery;
  private double minScore;
  private double maxScore;
  private double minAbv;
  private double maxAbv;
  private double minPrice;
  private double maxPrice;

  public Filters(String title, String country, String winery, double minScore, double maxScore,
      double minAbv,
      double maxAbv, double minPrice, double maxPrice) {
    this.title = title;
    this.country = country;
    this.winery = winery;
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
