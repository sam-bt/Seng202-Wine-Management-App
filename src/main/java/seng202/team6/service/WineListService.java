package seng202.team6.service;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import seng202.team6.managers.AuthenticationManager;
import seng202.team6.managers.OldDatabaseManager;
import seng202.team6.model.Wine;
import seng202.team6.model.WineList;

public class WineListService {
  private final AuthenticationManager authenticationManager;
  private final OldDatabaseManager databaseManager;
  private final ObservableList<WineList> wineLists = FXCollections.observableArrayList();

  public WineListService(AuthenticationManager authenticationManager,
      OldDatabaseManager databaseManager) {
    this.authenticationManager = authenticationManager;
    this.databaseManager = databaseManager;
  }

  public void init() {
    String username = authenticationManager.getAuthenticatedUsername();
    wineLists.addAll(databaseManager.getUserLists(username));
  }

  public boolean isWineInList(WineList wineList, Wine wine) {
    return databaseManager.isWineInList(wineList, wine);
  }

  public ObservableList<WineList> getWineLists() {
    return wineLists;
  }
}
