package seng202.team6.model;

public class Filters {

  private String title;
  private String country;
  private String winery;
  private String color;
  private int minVintage;
  private int maxVintage;
  private double minScore;
  private double maxScore;
  private double minAbv;
  private double maxAbv;
  private double minPrice;
  private double maxPrice;

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

  public Filters() {
    this(
        "",
        "",
        "",
        "",
        Integer.MIN_VALUE,
        Integer.MAX_VALUE,
        Double.MIN_VALUE,
        Double.MAX_VALUE,
        Double.MIN_VALUE,
        Double.MAX_VALUE,
        Double.MIN_VALUE,
        Double.MAX_VALUE);
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getCountry() {
    return country;
  }

  public void setCountry(String country) {
    this.country = country;
  }

  public String getWinery() {
    return winery;
  }

  public void setWinery(String winery) {
    this.winery = winery;
  }

  public String getColor() {
    return color;
  }

  public void setColor(String color) {
    this.color = color;
  }

  public int getMinVintage() {
    return minVintage;
  }

  public void setMinVintage(int minVintage) {
    this.minVintage = minVintage;
  }

  public int getMaxVintage() {
    return maxVintage;
  }

  public void setMaxVintage(int maxVintage) {
    this.maxVintage = maxVintage;
  }

  public double getMinScore() {
    return minScore;
  }

  public void setMinScore(double minScore) {
    this.minScore = minScore;
  }

  public double getMaxScore() {
    return maxScore;
  }

  public void setMaxScore(double maxScore) {
    this.maxScore = maxScore;
  }

  public double getMinAbv() {
    return minAbv;
  }

  public void setMinAbv(double minAbv) {
    this.minAbv = minAbv;
  }

  public double getMaxAbv() {
    return maxAbv;
  }

  public void setMaxAbv(double maxAbv) {
    this.maxAbv = maxAbv;
  }

  public double getMinPrice() {
    return minPrice;
  }

  public void setMinPrice(double minPrice) {
    this.minPrice = minPrice;
  }

  public double getMaxPrice() {
    return maxPrice;
  }

  public void setMaxPrice(double maxPrice) {
    this.maxPrice = maxPrice;
  }
}
