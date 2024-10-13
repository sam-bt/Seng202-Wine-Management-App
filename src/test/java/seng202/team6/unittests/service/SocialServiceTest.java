package seng202.team6.unittests.service;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.Date;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import seng202.team6.dao.WineReviewDao;
import seng202.team6.managers.AuthenticationManager;
import seng202.team6.managers.DatabaseManager;
import seng202.team6.model.User;
import seng202.team6.model.WineReview;
import seng202.team6.service.SocialService;

public class SocialServiceTest {

  private SocialService socialService;
  private User user;
  private WineReviewDao wineReviewDao;

  @BeforeEach
  public void setUp() {
    // mock dependencies
    AuthenticationManager authenticationManager = mock(AuthenticationManager.class);
    DatabaseManager databaseManager = mock(DatabaseManager.class);
    user = mock(User.class);
    wineReviewDao = mock(WineReviewDao.class);

    // Ensure correct return from mocked database
    when(databaseManager.getWineReviewDao()).thenReturn(wineReviewDao);

    socialService = new SocialService(authenticationManager,
        databaseManager, user);
  }

  @Test
  void testInitPopulatesUserReviews() {

    // Set up test data
    ObservableList<WineReview> wineReviews = FXCollections.observableArrayList();
    addReviewsToArray(wineReviews);

    // Mock behaviour of WineReviewDao
    when(wineReviewDao.getAll(user)).thenReturn(wineReviews);

    // Call init
    socialService.init();

    // Check if correct returns
    ObservableList<WineReview> returnedReviews = socialService.getUserReviews();
    Assertions.assertEquals(4, returnedReviews.size());
    Assertions.assertEquals(wineReviews.getFirst(), returnedReviews.getFirst());
    Assertions.assertEquals(wineReviews.getLast(), returnedReviews.getLast());

    // Verify getAll was only called once
    verify(wineReviewDao, times(1)).getAll(user);
  }

  @Test
  void testGetUserReviewsIsEmptyBeforeInit() {
    ObservableList<WineReview> wineReviews = FXCollections.observableArrayList();

    // Ensure is empty
    Assertions.assertTrue(wineReviews.isEmpty());
  }

  private void addReviewsToArray(ObservableList<WineReview> reviews) {
    Date date = new Date(System.currentTimeMillis());
    reviews.add(new WineReview(1, 1, user.getUsername(), 3., "Not bad", date));
    reviews.add(new WineReview(2, 2, user.getUsername(), 4., "Bad", date));
    reviews.add(new WineReview(3, 3, user.getUsername(), 5., "Good", date));
    reviews.add(new WineReview(4, 4, user.getUsername(), 6., "Good", date));
  }

}
