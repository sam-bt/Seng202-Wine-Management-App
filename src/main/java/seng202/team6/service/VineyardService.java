package seng202.team6.service;

import java.util.Map;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import seng202.team6.managers.DatabaseManager;
import seng202.team6.model.GeoLocation;
import seng202.team6.model.Vineyard;
import seng202.team6.model.VineyardFilters;

/**
 * The VineyardService class provides methods to manage vineyard data.
 */
public class VineyardService {

  private final DatabaseManager databaseManager;
  private final ObservableList<Vineyard> vineyards = FXCollections.observableArrayList();

  /**
   * Constructs a VineyardService instance.
   *
   * @param databaseManager the DatabaseManager used to interact with the database
   */
  public VineyardService(DatabaseManager databaseManager) {
    this.databaseManager = databaseManager;
  }

  /**
   * Initializes the service by retrieving vineyards from the database.
   */
  public void init() {
    applyFilters(null);
    databaseManager.getVineyardsDao().updateUniques();
  }

  /**
   * Creates a new vineyard. This method also adds the geolocation to the database in case it is an
   * address that does not yet exist.
   *
   * @param name the name of the vineyard
   * @param address the address of the vineyard
   * @param region the region of the vineyard
   * @param website the website of the vineyard
   * @param logoUrl the logoUrl of the vineyard
   * @param description the description of the vineyard
   * @param geoLocation the geoLocation of the vineyard
   */
  public void create(String name, String address, String region, String website, String logoUrl,
      String description, GeoLocation geoLocation) {
    Vineyard vineyard = databaseManager.getVineyardsDao()
        .add(name, address, region, website, description, logoUrl, geoLocation);
    databaseManager.getGeoLocationDao().addAll(Map.of(name, geoLocation));
    vineyards.add(vineyard);
  }

  /**
   * Deletes a vineyard.
   *
   * @param vineyard the vineyard to be deleted
   */
  public void delete(Vineyard vineyard) {
    databaseManager.getVineyardsDao().remove(vineyard);
    vineyards.remove(vineyard);
  }

  /**
   * Clears the list of vineyards currently loaded and adds a range of vineyards which meet the
   * specified filter criteria.
   *
   * @param filters the filters that specify what vineyards to load.
   */
  public void applyFilters(VineyardFilters filters) {
    vineyards.clear();
    ObservableList<Vineyard> vineyards = databaseManager.getVineyardsDao()
        .getAllInRange(0, 100, filters);
    this.vineyards.addAll(vineyards);
  }

  /**
   * Returns an observable list of vineyards.
   *
   * @return an observable list containing all the vineyards managed by this service
   */
  public ObservableList<Vineyard> get() {
    return vineyards;
  }
}
