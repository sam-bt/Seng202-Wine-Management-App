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
  }

  @Test
  public void nextPageTest() {
    pageService.nextPage();
    Assertions.assertEquals(2, pageService.getPageNumber()); // Starts on page 1
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
    pageService.setPageNumber(10);
    Assertions.assertEquals(10, pageService.getPageNumber());
  }

  @Test
  public void minRangeTest(){
    pageService.setPageNumber(10);
    Assertions.assertEquals(900, pageService.getMinRange());
  }

  @Test
  public void maxRangeTest(){
    pageService.setPageNumber(10);
    Assertions.assertEquals(1000, pageService.getMaxRange());
  }

  @Test
  public void getIntegerPropertyTest() {
    pageService.setPageNumber(10);
    IntegerProperty property = pageService.pageNumberProperty();

    Assertions.assertEquals(10, property.get());
  }

}
