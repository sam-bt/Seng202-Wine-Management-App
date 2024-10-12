package seng202.team6.util;

import seng202.team6.model.WineFilters;
import seng202.team6.service.PageService;

/**
 * Saves the state of the filters and pages service for winescreencontroller.
 */
public class WineState {

  private final WineFilters filters;
  private final PageService pageService;

  /**
   * Constructor.
   *
   * @param filters     filters
   * @param pageService page service
   */
  public WineState(WineFilters filters, PageService pageService) {
    this.filters = filters;
    this.pageService = pageService;
  }

  public WineFilters getFilters() {
    return filters;
  }

  public PageService getPageService() {
    return pageService;
  }

}
