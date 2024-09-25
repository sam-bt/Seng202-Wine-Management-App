package seng202.team6.service;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

/**
 * Simple class used to manage pagination
 */
public class PageService {

  /**
   * This is the number of table elements on said page
   */
  private final int pageSize;
  /**
   * Current page number
   * <p>
   * Attach a listener to this property via pageNumberProperty to check for page changes
   * </p>
   */
  private final IntegerProperty pageNumber = new SimpleIntegerProperty();

  /**
   * Constructer
   *
   * @param pageSize the number of elements on each page
   */
  public PageService(int pageSize) {
    this.pageSize = pageSize;
    this.pageNumber.set(1);
  }

  /**
   * Gets the index of the end element on the page
   * <p>
   * This corresponds to the end of the range of values to get from a<br> table in the database.
   * </p>
   *
   * @return pageNumber * pageSize
   */
  public int getMaxRange() {
    return pageNumber.get() * pageSize;
  }

  /**
   * Gets the index of the first element on the page
   * <p>
   * This corresponds to the start of the range of values to get from a<br> table in the database
   * </p>
   *
   * @return pageNumber * pageSize - pageSize
   */
  public int getMinRange() {
    return pageNumber.get() * pageSize - pageSize;
  }

  /**
   * Increments the current page
   */
  public void nextPage() {
    this.pageNumber.set(this.pageNumber.get() + 1);
  }

  /**
   * Decrements the current page, ensures you cannot have negative pages
   */
  public void previousPage() {
    if (this.pageNumber.get() > 1) { // Ensure you can't go into negative pages
      this.pageNumber.set(this.pageNumber.get() - 1);
    }
  }

  /**
   * Returns the current page number
   *
   * @return current page number
   */
  public int getPageNumber() {
    return pageNumber.get();
  }

  public void setPageNumber(int pageNumber) {
    this.pageNumber.set(pageNumber);
  }

  public IntegerProperty pageNumberProperty() {
    return pageNumber;
  }
}
