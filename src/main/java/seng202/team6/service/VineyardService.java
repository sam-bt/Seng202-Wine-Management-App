package seng202.team6.service;

import java.util.SortedSet;
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

  public void applyFilters(VineyardFilters filters) {
    vineyards.clear();
    ObservableList<Vineyard> vineyards = databaseManager.getVineyardsDao()
        .getAllInRange(0, 100, filters);
    this.vineyards.addAll(vineyards);
  }

  /**
   * Adds distinct vineyard values (names, addresses, and regions) to the provided sorted sets.
   *
   * @param nameValues    a sorted set to hold distinct vineyard names
   * @param addressValues a sorted set to hold distinct vineyard addresses
   * @param regionValues  a sorted set to hold distinct vineyard regions
   */
  public void addDistinctValues(SortedSet<String> nameValues, SortedSet<String> addressValues,
      SortedSet<String> regionValues) {
    // fixme - angus merge conflict deleted this
    // nameValues.addAll(databaseManager
    //     .getD("NAME", "VINEYARD"));
    // addressValues.addAll(databaseManager
    //     .getDistinctStringValues("ADDRESS", "VINEYARD"));
    // regionValues.addAll(databaseManager
    //     .getDistinctStringValues("REGION", "VINEYARD"));
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
