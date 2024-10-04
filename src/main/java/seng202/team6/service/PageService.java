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

  private final IntegerProperty totalItems = new SimpleIntegerProperty();

  private final IntegerProperty maxPages = new SimpleIntegerProperty();

  /**
   * Constructor
   *
   * @param pageSize the number of elements on each page
   */
  public PageService(int pageSize) {
    this.pageSize = pageSize;
    this.pageNumber.set(1);

    // Ensures that pages are "infinite" if not set
    this.maxPages.set(Integer.MAX_VALUE);
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
    if (this.pageNumber.get() + 1 <= this.maxPages.get()) {
      this.pageNumber.set(this.pageNumber.get() + 1);
    }
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
   * Updates the number of max pages based on the number of items and items per page
   */
  private void updateMaxPages() {
    this.maxPages.set(this.totalItems.get() / this.pageSize + 1);
  }

  /**
   * Returns the current page number
   *
   * @return current page number
   */
  public int getPageNumber() {
    return pageNumber.get();
  }

  /**
   * Sets the current page number
   * <p>
   * Does nothing if the page number is greater than max pages or less than 0
   * </p>
   *
   * @param pageNumber page number to go too
   */
  public void setPageNumber(int pageNumber) {
    if (pageNumber <= maxPages.get() && pageNumber > 0) {
      this.pageNumber.set(pageNumber);
    }
  }

  /**
   * Gets the page number property
   * <p>
   * <b>DO NOT USE THIS TO SET VALUES!</b>
   * </p>
   *
   * @return the page number integer property
   */
  public IntegerProperty pageNumberProperty() {
    return pageNumber;
  }

  public int getPageSize() {
    return pageSize;
  }

  public int getTotalItems() {
    return totalItems.get();
  }

  /**
   * Sets the total number of items that the page service will have to go through
   * <p>
   * For example, with 100 total items and page size of 25, there will be 4 pages
   * </p>
   *
   * @param totalItems the total items the page service needs to page through
   */
  public void setTotalItems(int totalItems) {
    this.totalItems.set(totalItems);
    this.updateMaxPages();
  }

  public IntegerProperty totalItemsProperty() {
    return totalItems;
  }

  public int getMaxPages() {
    return maxPages.get();
  }

  public void setMaxPages(int maxPages) {
    this.maxPages.set(maxPages);
  }

  public IntegerProperty maxPagesProperty() {
    return maxPages;
  }
}
