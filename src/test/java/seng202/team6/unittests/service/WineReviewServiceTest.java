package seng202.team6.unittests.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.text.SimpleDateFormat;
import java.util.Date;

import javafx.beans.binding.StringBinding;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import seng202.team6.model.WineReview;
import seng202.team6.service.WineReviewsService;
import seng202.team6.util.DateFormatter;

public class WineReviewServiceTest {

  private WineReviewsService wineReviewService;
  private WineReview wineReview;
  private static final SimpleDateFormat DATE_FORMAT = (SimpleDateFormat) DateFormatter.DATE_FORMAT;

  @BeforeEach
  void setUp() {
    wineReviewService = new WineReviewsService();

    long id = 1L;
    long wineId = 101L;
    String username = "test_user";
    double rating = 4.5;
    String description = "A good wine.";
    long millis=System.currentTimeMillis();
    java.sql.Date date =new java.sql.Date(millis);

    wineReview = new WineReview(id, wineId, username, rating, description, date, 0);
  }

  @Test
  void testGetCaptionWithDateFormatted() {
    String expectedFormattedDate = DATE_FORMAT.format(wineReview.getDate());
    String expectedCaption = "From " + wineReview.getUsername() + " on " + expectedFormattedDate;

    String actualCaption = wineReviewService.getCaptionWithDateFormatted(wineReview);

    assertEquals(expectedCaption, actualCaption);
  }

  @Test
  void testGetCaptionBinding() {
    StringBinding captionBinding = wineReviewService.getCaptionBinding(wineReview);

    assertNotNull(captionBinding);

    String expectedFormattedDate = DATE_FORMAT.format(wineReview.getDate());
    String expectedCaption = "From " + wineReview.getUsername() + " on " + expectedFormattedDate;

    assertEquals(expectedCaption, captionBinding.get());
  }

  @Test
  void testCaptionBindingUpdatesWithDateChange() {
    StringBinding captionBinding = wineReviewService.getCaptionBinding(wineReview);

    String expectedFormattedDate = DATE_FORMAT.format(wineReview.getDate());
    String expectedCaption = "From " + wineReview.getUsername() + " on " + expectedFormattedDate;
    assertEquals(expectedCaption, captionBinding.get());

    long millis=System.currentTimeMillis();
    java.sql.Date newDate = new java.sql.Date(millis);

    expectedFormattedDate = DATE_FORMAT.format(newDate);
    expectedCaption = "From " + wineReview.getUsername() + " on " + expectedFormattedDate;
    assertEquals(expectedCaption, captionBinding.get());
  }
}
