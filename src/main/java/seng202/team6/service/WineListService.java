package seng202.team6.service;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import seng202.team6.managers.AuthenticationManager;
import seng202.team6.managers.DatabaseManager;
import seng202.team6.model.User;
import seng202.team6.model.Wine;
import seng202.team6.model.WineList;

/**
 * Service to manage wine lists.
 */
public class WineListService {

  private final AuthenticationManager authenticationManager;
  private final DatabaseManager databaseManager;
  private final ObservableList<WineList> wineLists = FXCollections.observableArrayList();

  /**
   * Constructor.
   *
   * @param authenticationManager authentication manager
   * @param databaseManager       database manager
   */
  public WineListService(AuthenticationManager authenticationManager,
      DatabaseManager databaseManager) {
    this.authenticationManager = authenticationManager;
    this.databaseManager = databaseManager;
  }

  /**
   * Initializes the service.
   */
  public void init() {
    User user = authenticationManager.getAuthenticatedUser();
    wineLists.addAll(databaseManager.getWineListDao().getAll(user));
  }

  /**
   * Checks if the wine is in the list.
   *
   * @param wineList wine list
   * @param wine     wine
   * @return if wine is in list
   */
  public boolean isWineInList(WineList wineList, Wine wine) {
    return databaseManager.getWineListDao().isWineInList(wineList, wine);
  }

  /**
   * Creates a wine list.
   *
   * @param user user
   * @param name name
   */
  public void createWineList(User user, String name) {
    WineList wineList = databaseManager.getWineListDao().create(user, name);
    wineLists.add(wineList);
  }

  /**
   * Deletes a wine list.
   *
   * @param wineList list of wines
   */
  public void deleteWineList(WineList wineList) {
    wineLists.remove(wineList);
    databaseManager.getWineListDao().delete(wineList);
  }

  /**
   * Checks if an item can be removed.
   *
   * @param wineList wine list
   * @return if item can be removed
   */
  public boolean canRemove(WineList wineList) {
    return !wineList.name().equals("Favourites") && !wineList.name().equals("History");
  }

  /**
   * Gets the wine lists.
   *
   * @return wine list
   */
  public ObservableList<WineList> getWineLists() {
    return wineLists;
  }
}
