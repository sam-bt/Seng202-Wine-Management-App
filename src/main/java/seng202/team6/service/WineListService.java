package seng202.team6.service;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import seng202.team6.managers.DatabaseManager;
import seng202.team6.model.Wine;
import seng202.team6.model.WineList;

public class WineListService {
  private final AuthenticationService authenticationService;
  private final DatabaseManager databaseManager;
  private final ObservableList<WineList> wineLists = FXCollections.observableArrayList();

  public WineListService(AuthenticationService authenticationService,
      DatabaseManager databaseManager) {
    this.authenticationService = authenticationService;
    this.databaseManager = databaseManager;
  }

  public void init() {
    String username = authenticationService.getAuthenticatedUsername();
    wineLists.addAll(databaseManager.getUserLists(username));
  }

  public boolean isWineInList(WineList wineList, Wine wine) {
    return databaseManager.isWineInList(wineList, wine);
  }

  public ObservableList<WineList> getWineLists() {
    return wineLists;
  }
}
