package seng202.team6.service;

import java.util.SortedSet;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import seng202.team6.managers.DatabaseManager;
import seng202.team6.model.Vineyard;

public class VineyardService {

  private final DatabaseManager databaseManager;
  private final ObservableList<Vineyard> vineyards = FXCollections.observableArrayList();

  public VineyardService(DatabaseManager databaseManager) {
    this.databaseManager = databaseManager;
  }

  public void init() {
    ObservableList<Vineyard> vineyards = databaseManager.getVineyardsDao()
        .getAllInRange(0, 100, null);
    this.vineyards.addAll(vineyards);
  }

  public void addDistinctValues(SortedSet<String> nameValues, SortedSet<String> addressValues,
      SortedSet<String> regionValues) {
    // fixme - angus merge conflict deleted this
//    nameValues.addAll(databaseManager
//        .getD("NAME", "VINEYARD"));
//    addressValues.addAll(databaseManager
//        .getDistinctStringValues("ADDRESS", "VINEYARD"));
//    regionValues.addAll(databaseManager
//        .getDistinctStringValues("REGION", "VINEYARD"));
  }

  public ObservableList<Vineyard> get() {
    return vineyards;
  }
}
