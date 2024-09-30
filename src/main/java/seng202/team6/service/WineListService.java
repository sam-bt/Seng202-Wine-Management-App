package seng202.team6.service;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import seng202.team6.managers.AuthenticationManager;
import seng202.team6.managers.DatabaseManager;
import seng202.team6.model.User;
import seng202.team6.model.Wine;
import seng202.team6.model.WineList;

public class WineListService {

  private final AuthenticationManager authenticationManager;
  private final DatabaseManager databaseManager;
  private final ObservableList<WineList> wineLists = FXCollections.observableArrayList();

  public WineListService(AuthenticationManager authenticationManager,
      DatabaseManager databaseManager) {
    this.authenticationManager = authenticationManager;
    this.databaseManager = databaseManager;
  }

  public void init() {
    User user = authenticationManager.getAuthenticatedUser();
    wineLists.addAll(databaseManager.getWineListDAO().getAll(user));
  }

  public boolean isWineInList(WineList wineList, Wine wine) {
    return databaseManager.getWineListDAO().isWineInList(wineList, wine);
  }

  public void createWineList(User user, String name) {
    WineList wineList = databaseManager.getWineListDAO().create(user, name);
    wineLists.add(wineList);
  }

  public void deleteWineList(WineList wineList) {
    wineLists.remove(wineList);
    databaseManager.getWineListDAO().delete(wineList);
  }

  public boolean canRemove(WineList wineList) {
    return !wineList.name().equals("Favourites") && !wineList.name().equals("History");
  }

  public ObservableList<WineList> getWineLists() {
    return wineLists;
  }
}
