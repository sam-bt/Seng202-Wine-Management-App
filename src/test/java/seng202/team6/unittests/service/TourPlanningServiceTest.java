package seng202.team6.unittests.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import javafx.collections.ObservableList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import seng202.team6.dao.VineyardDao;
import seng202.team6.dao.VineyardTourDao;
import seng202.team6.managers.DatabaseManager;
import seng202.team6.model.GeoLocation;
import seng202.team6.model.Vineyard;
import seng202.team6.model.VineyardTour;
import seng202.team6.service.TourPlanningService;

public class TourPlanningServiceTest {

  private VineyardTour vineyardTour;
  private VineyardDao vineyardDao;
  private VineyardTourDao vineyardTourDao;
  private TourPlanningService tourPlanningService;

  @BeforeEach
  public void setUp() {
    // Mock dependencies
    DatabaseManager databaseManager = mock(DatabaseManager.class);
    vineyardTour = mock(VineyardTour.class);
    vineyardDao = mock(VineyardDao.class);
    vineyardTourDao = mock(VineyardTourDao.class);

    // Set up the mocked database
    when(databaseManager.getVineyardsDao()).thenReturn(vineyardDao);
    when(databaseManager.getVineyardTourDao()).thenReturn(vineyardTourDao);

    // Create tour planning service
    tourPlanningService = new TourPlanningService(databaseManager, vineyardTour);
  }

  @Test
  void testInitLoadsVineyards() {
    // Set up testing data
    GeoLocation geoLocation = new GeoLocation(10.5, 10.5);
    Vineyard vineyard1 = new Vineyard(1, "vineyard1", "10 address road",
        "region name", "awesome website", "sick description",
        "logo url", geoLocation);
    Vineyard vineyard2 = new Vineyard(2, "vineyard2", "11 address road",
        "region name2", "awesome website2", "sick description2",
        "logo url2", geoLocation);
    List<Vineyard> vineyards = Arrays.asList(vineyard1, vineyard2);

    // Mock behaviour of VineyardDao
    when(vineyardDao.getAllFromTour(vineyardTour)).thenReturn(vineyards);

    // Call init
    tourPlanningService.init();

    // Ensure everything worked
    ObservableList<Vineyard> loadedVineyards = tourPlanningService.getVineyards();
    assertEquals(2, loadedVineyards.size());
    assertEquals(vineyard1, loadedVineyards.get(0));
    assertEquals(vineyard2, loadedVineyards.get(1));

    // Verify the getAllFromTour was called once
    verify(vineyardDao, times(1)).getAllFromTour(vineyardTour);
  }

  @Test
  void testAddVineyard() {
    // Set up test data
    GeoLocation geoLocation = new GeoLocation(10.5, 10.5);
    Vineyard vineyard = new Vineyard(1, "vineyard1", "10 address road",
        "region name", "awesome website", "sick description",
        "logo url", geoLocation);

    // Add vineyard
    tourPlanningService.addVineyard(vineyard);

    // Ensure everything worked
    ObservableList<Vineyard> loadedVineyards = tourPlanningService.getVineyards();
    assertEquals(1, loadedVineyards.size());
    assertEquals(vineyard, loadedVineyards.getFirst());

    // Verify database manager was called only once
    verify(vineyardTourDao, times(1)).addVineyard(vineyardTour, vineyard);
  }

  @Test
  void removeVineyard() {
    // Set up data
    GeoLocation geoLocation = new GeoLocation(10.5, 10.5);
    Vineyard vineyard = new Vineyard(1, "vineyard1", "10 address road",
        "region name", "awesome website", "sick description",
        "logo url", geoLocation);
    tourPlanningService.addVineyard(vineyard);

    // Remove vineyard
    tourPlanningService.removeVineyard(vineyard);

    // Ensure everything worked correctly
    ObservableList<Vineyard> loadedVineyards = tourPlanningService.getVineyards();
    assertEquals(0, loadedVineyards.size());

    // Verify VineyardTourDao only called once
    verify(vineyardTourDao, times(1)).removeVineyard(vineyardTour, vineyard);
  }

  @Test
  void getVineyardTour() {
    assertEquals(vineyardTour, tourPlanningService.getVineyardTour());
  }
}
