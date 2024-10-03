package seng202.team6.service;

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
}
