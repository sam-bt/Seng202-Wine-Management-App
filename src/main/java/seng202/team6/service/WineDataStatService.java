package seng202.team6.service;

import java.util.HashSet;
import java.util.Set;

/**
 * Stores a range of unique values and stats.<br> Stores:
 * <ul>
 *   <li>uniqueCountries</li>
 *   <li>uniqueWineries</li>
 *   <li>uniqueColors</li>
 *   <li>minVintage</li>
 *   <li>maxVintage</li>
 *   <li>minScore</li>
 *   <li>maxScore</li>
 *   <li>minAbv</li>
 *   <li>maxAbv</li>
 *   <li>minPrice</li>
 *   <li>maxPrice</li>
 * </ul>
 */
public class WineDataStatService {

  // Uniques
  private final Set<String> uniqueCountries = new HashSet<>();
  private final Set<String> uniqueWineries = new HashSet<>();
  private final Set<String> uniqueColors = new HashSet<>();

  // minimum and maximum values
  private int minVintage;
  private int maxVintage;
  private int minScore;
  private int maxScore;
  private float minAbv;
  private float maxAbv;
  private float minPrice;
  private float maxPrice;

  /**
   * Constructor.
   */
  public WineDataStatService() {
  }

  /**
   * Resets all values.
   */
  public void reset() {
    this.uniqueCountries.clear();
    this.uniqueWineries.clear();
    this.uniqueColors.clear();
    this.minVintage = Integer.MAX_VALUE;
    this.maxVintage = 0;
    this.minScore = 100;
    this.maxScore = 0;
    this.minAbv = 100;
    this.maxAbv = 0;
    this.minPrice = Float.MAX_VALUE;
    this.maxPrice = 0;
  }

  public Set<String> getUniqueCountries() {
    return uniqueCountries;
  }

  public Set<String> getUniqueWineries() {
    return uniqueWineries;
  }

  public Set<String> getUniqueColors() {
    return uniqueColors;
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

  public int getMinScore() {
    return minScore;
  }

  public void setMinScore(int minScore) {
    this.minScore = minScore;
  }

  public int getMaxScore() {
    return maxScore;
  }

  public void setMaxScore(int maxScore) {
    this.maxScore = maxScore;
  }

  public float getMinAbv() {
    return minAbv;
  }

  public void setMinAbv(float minAbv) {
    this.minAbv = minAbv;
  }

  public float getMaxAbv() {
    return maxAbv;
  }

  public void setMaxAbv(float maxAbv) {
    this.maxAbv = maxAbv;
  }

  public float getMinPrice() {
    return minPrice;
  }

  public void setMinPrice(float minPrice) {
    this.minPrice = minPrice;
  }

  public float getMaxPrice() {
    return maxPrice;
  }

  public void setMaxPrice(float maxPrice) {
    this.maxPrice = maxPrice;
  }
}
