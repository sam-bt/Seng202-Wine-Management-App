package seng202.team6.unittests.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import seng202.team6.dao.VineyardDao;
import seng202.team6.managers.DatabaseManager;
import seng202.team6.model.GeoLocation;
import seng202.team6.model.Vineyard;
import seng202.team6.model.VineyardFilters;
import seng202.team6.service.VineyardService;

public class VineyardServiceTest {

  private VineyardService vineyardService;
  private VineyardDao vineyardDao;
  private DatabaseManager databaseManager;

  @BeforeEach
  void setUp() {
    // Mock database
    databaseManager = mock(DatabaseManager.class);
    vineyardService = new VineyardService(databaseManager);
    vineyardDao = mock(VineyardDao.class);
  }

  @Test
  void testInit() {
    // Set up test data
    GeoLocation geoLocation = new GeoLocation(10.5, 10.5);
    ObservableList<Vineyard> testVineyards = FXCollections.observableArrayList(
        new Vineyard(1, "vineyard1", "10 address road",
            "region name", "awesome website", "sick description",
            "logo url", geoLocation),
        new Vineyard(1, "vineyard2", "11 address road",
            "region name2", "awesome website2", "sick description2",
            "logo url2", geoLocation)
    );

    // Mock get all in range method
    when(databaseManager.getVineyardsDao()).thenReturn(vineyardDao);
    when(vineyardDao.getAllInRange(0, 100, null))
        .thenReturn(testVineyards);

    // Call init
    vineyardService.init();

    // Check list populated correctly
    assertEquals(2, vineyardService.get().size());
    assertEquals("vineyard1", vineyardService.get().getFirst().getName());
    assertEquals("vineyard2", vineyardService.get().getLast().getName());

    // verify only one call was made
    verify(databaseManager.getVineyardsDao(), times(1)).updateUniques();
  }

  @Test
  void testApplyFilters() {
    // Set up test data
    GeoLocation geoLocation = new GeoLocation(10.5, 10.5);
    ObservableList<Vineyard> testVineyards = FXCollections.observableArrayList(
        new Vineyard(1, "vineyard1", "10 address road",
            "region name", "awesome website", "sick description",
            "logo url", geoLocation)
    );
    VineyardFilters filters = new VineyardFilters("vineyard1", "10 address road",
        "region name");

    // Setup mock functionality
    when(databaseManager.getVineyardsDao()).thenReturn(vineyardDao);
    when(vineyardDao.getAllInRange(0, 100, filters)).thenReturn(testVineyards);

    // Call apply filters
    vineyardService.applyFilters(filters);

    // Check
    assertEquals(1, vineyardService.get().size());
    assertEquals("vineyard1", vineyardService.get().getFirst().getName());

    // Verify only one call made
    verify(databaseManager.getVineyardsDao(), times(1))
        .getAllInRange(0, 100, filters);
  }
}
