package seng202.team6.unittests.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import seng202.team6.dao.VineyardTourDao;
import seng202.team6.managers.AuthenticationManager;
import seng202.team6.managers.DatabaseManager;
import seng202.team6.model.GeoLocation;
import seng202.team6.model.User;
import seng202.team6.model.Vineyard;
import seng202.team6.model.VineyardTour;
import seng202.team6.service.VineyardToursService;

public class VineyardToursServiceTest {

  private VineyardToursService vineyardToursService;
  private AuthenticationManager authenticationManager;
  private DatabaseManager databaseManager;
  private VineyardTourDao vineyardTourDao;

  @BeforeEach
  void setUp() {
    // Mock dependencies
    authenticationManager = mock(AuthenticationManager.class);
    databaseManager = mock(DatabaseManager.class);
    vineyardTourDao = mock(VineyardTourDao.class);
    vineyardToursService = new VineyardToursService(authenticationManager, databaseManager);

    // Mock functionality
    when(databaseManager.getVineyardTourDao()).thenReturn(vineyardTourDao);
  }

  @Test
  void testInit() {
    // Set up
    User testUser = new User();
    ObservableList<VineyardTour> testTours = FXCollections.observableArrayList(
        new VineyardTour(1, "username", "Tour1"),
        new VineyardTour(2, "username", "Tour2")
    );
    when(authenticationManager.getAuthenticatedUser()).thenReturn(testUser);
    when(vineyardTourDao.getAll(testUser)).thenReturn(testTours);

    // Call init method
    vineyardToursService.init();

    // Check everything worked correctly
    assertEquals(2, vineyardToursService.getVineyardTours().size());
    assertEquals("Tour1", vineyardToursService.getVineyardTours().getFirst().getName());
    assertEquals("Tour2", vineyardToursService.getVineyardTours().getLast().getName());

    // Verify only one call was made to databaseManager
    verify(databaseManager.getVineyardTourDao(), times(1)).getAll(testUser);
  }

  @Test
  void testIsVineyardInTour() {
    // Setup
    GeoLocation geoLocation = new GeoLocation(10.5, 10.5);
    VineyardTour testTour = new VineyardTour(1, "username", "Tour1");
    Vineyard testVineyard = new Vineyard(1, "vineyard1", "10 address road",
        "region name", "awesome website", "sick description",
        "logo url", geoLocation);

    // Mock functionality
    when(vineyardTourDao.isVineyardInTour(testTour, testVineyard)).thenReturn(true);

    // Run isVineyardInTour
    boolean result = vineyardToursService.isVineyardInTour(testTour, testVineyard);

    // Assert
    assertTrue(result);

    // Verify
    verify(databaseManager.getVineyardTourDao(), times(1))
        .isVineyardInTour(testTour, testVineyard);
  }

  @Test
  void testCreateVineyardTour() {
    // Set up
    User testUser = new User();
    when(authenticationManager.getAuthenticatedUser()).thenReturn(testUser);

    // Mock create new vineyard tour
    String tourName = "Tour1";
    VineyardTour testTour = new VineyardTour(1, "username", tourName);
    when(vineyardTourDao.create(testUser, tourName)).thenReturn(testTour);

    // Call create vineyard tour
    VineyardTour createdTour = vineyardToursService.createVineyardTour(tourName);

    // Check correct behaviour
    assertNotNull(createdTour);
    assertEquals(1, vineyardToursService.getVineyardTours().size());
    assertEquals(tourName, vineyardToursService.getVineyardTours().getFirst().getName());

    // Verify one call made to the database
    verify(databaseManager.getVineyardTourDao(), times(1))
        .create(testUser, tourName);
  }

  @Test
  void testRemoveVineyardTour() {
    // Set up
    VineyardTour testTour = new VineyardTour(1, "username", "Tour1");
    vineyardToursService.getVineyardTours().add(testTour);

    // Call remove tour
    vineyardToursService.removeVineyardTour(testTour);

    // Assert tour gone
    assertEquals(0, vineyardToursService.getVineyardTours().size());

    // Ensure one call made
    verify(databaseManager.getVineyardTourDao(), times(1)).remove(testTour);
  }
}
