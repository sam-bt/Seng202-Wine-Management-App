package seng202.team6.unittests.service;

import javafx.beans.property.IntegerProperty;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import seng202.team6.service.PageService;

public class PageServiceTest {

  private PageService pageService;

  @BeforeEach
  public void setUp() {
    pageService = new PageService(100);
    Assertions.assertEquals(100, pageService.getPageSize());
  }

  @Test
  public void nextPageTest() {
    pageService.nextPage();
    Assertions.assertEquals(2, pageService.getPageNumber()); // Starts on page 1

    pageService.setTotalItems(1000);
    Assertions.assertEquals(1000, pageService.getTotalItems());

    pageService.setPageNumber(10);
    pageService.nextPage(); // 11 (overflow page)
    pageService.nextPage(); // shouldn't go to 12
    Assertions.assertEquals(11, pageService.getPageNumber());
  }

  @Test
  public void previousPageTest() {
    pageService.previousPage();
    Assertions.assertEquals(1, pageService.getPageNumber());

    pageService.nextPage();
    pageService.nextPage();
    pageService.previousPage();
    Assertions.assertEquals(2, pageService.getPageNumber());
  }

  @Test
  public void setLastPageTest() {
    pageService.setMaxPages(10);
    pageService.setPageNumber(10);
    pageService.nextPage();
    Assertions.assertEquals(10, pageService.getPageNumber());
  }

  @Test
  public void minRangeTest() {
    pageService.setPageNumber(10);
    Assertions.assertEquals(900, pageService.getMinRange());
  }

  @Test
  public void maxRangeTest() {
    pageService.setPageNumber(10);
    Assertions.assertEquals(1000, pageService.getMaxRange());
  }

  @Test
  public void getIntegerPropertyTest() {
    pageService.setPageNumber(10);
    IntegerProperty property = pageService.pageNumberProperty();

    Assertions.assertEquals(10, property.get());
  }

  @Test
  public void updateTotalItems() {
    pageService.setTotalItems(100000);
    Assertions.assertEquals(100000, pageService.getTotalItems());
    Assertions.assertEquals(1001, pageService.getMaxPages()); // one extra page in case of overflow
    Assertions.assertNotNull(pageService.totalItemsProperty());
  }

}
