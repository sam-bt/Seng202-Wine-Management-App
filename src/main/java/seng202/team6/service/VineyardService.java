package seng202.team6.service;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import seng202.team6.managers.DatabaseManager;
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

  public void create(Vineyard vineyard, String name, String address, String region, String logoUrl,
      String description) {

  }

  public void delete(Vineyard vineyard) {
    // todo - call vineyard dao and remove it from db
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
