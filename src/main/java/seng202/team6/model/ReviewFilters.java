package seng202.team6.model;


/**
 * Represents a set of filters for querying or filtering review data. This class encapsulates
 * various criteria that can be used to filter reviews
 */
public class ReviewFilters {

  /**
   * The name of the user to filter by.
   */
  private String username;

  /**
   * The name of the wine to filter by.
   */
  private String wineName;

  /**
   * The minimum rating to display of the reviews to filter by.
   */
  private int minRating;

  /**
   * The maximum rating to display of the reviews to filter by.
   */
  private int maxRating;

  /**
   * Constructs a Filters object with specified criteria.
   *
   * @param username  The username of the reviewer.
   * @param wineName  The country of origin.
   * @param minRating The min rating.
   * @param maxRating The max rating.
   */
  public ReviewFilters(String username, String wineName, int minRating, int maxRating) {
    this.username = username;
    this.wineName = wineName;
    this.minRating = minRating;
    this.maxRating = maxRating;
  }


  /**
   * Constructs a Filters object with default values.
   */
  public ReviewFilters() {
    this.username = "";
    this.wineName = "";
    this.minRating = 1;
    this.maxRating = 5;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getWineName() {
    return wineName;
  }

  public void setWineName(String wineName) {
    this.wineName = wineName;
  }

  public int getMinRating() {
    return minRating;
  }

  public void setMinRating(int minRating) {
    this.minRating = minRating;
  }

  public int getMaxRating() {
    return maxRating;
  }

  public void setMaxRating(int maxRating) {
    this.maxRating = maxRating;
  }

}
