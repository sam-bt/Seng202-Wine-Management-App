package seng202.team6.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;
import seng202.team6.model.Wine;
import seng202.team6.model.WineList;
import seng202.team6.util.Timer;

// todo - idk what to call this but pre much is just uses methods between different DAOS
public class MultiDAO extends DAO {
  private final WineListDAO wineListDAO;
  private final WineDAO wineDAO;

  /**
   * Constructs a new DAO with the given database connection and initializes logging.
   *
   * @param connection          The database connection to be used by this DAO.
   * @param implementationClass The class that implements this DAO, used to configure the logger.
   */
  public MultiDAO(Connection connection, Class<?> implementationClass, WineListDAO wineListDAO,
      WineDAO wineDAO) {
    super(connection, implementationClass);
    this.wineListDAO = wineListDAO;
    this.wineDAO = wineDAO;
  }

  public ObservableList<Wine> getWinesInList(WineList wineList) {
    Timer timer = new Timer();
    String sql = "SELECT * FROM WINE " +
        "INNER JOIN LIST_ITEMS ON WINE.ID = LIST_ITEMS.WINE_ID " +
        "INNER JOIN LIST_NAME ON LIST_ITEMS.LIST_ID = LIST_NAME.ID " +
        "LEFT JOIN GEOLOCATION on lower(WINE.REGION) like lower(GEOLOCATION.NAME) " +
        "WHERE LIST_NAME.ID = ?";
    try (PreparedStatement statement = connection.prepareStatement(sql)) {
      statement.setLong(1, wineList.id());

      try (ResultSet resultSet = statement.executeQuery()) {
        ObservableList<Wine> wines = wineDAO.extractAllWinesFromResultSet(resultSet);
//        wines.addListener((ListChangeListener<Wine>) change ->
//            onWineListChange(wineList, change));
        log.info("Successfully retrieved {} wines in list with ID {} in {}ms",
            wines.size(), wineList.id(), timer.stop());
        return wines;
      }
    } catch (SQLException error) {
      log.error("Failed to retrieve wines in list with ID {} in {}ms", wineList.id(),
          timer.stop());
    }
    return FXCollections.emptyObservableList();
  }

//  private void onWineListChange(WineList wineList, Change<? extends Wine> change) {
//    while (change.next()) {
//      if (change.wasAdded()) {
//        change.getAddedSubList().forEach(addedWine -> addWineToList(wineList, addedWine));
//      }
//      if (change.wasRemoved()) {
//        change.getRemoved().forEach(removedWine -> removeWineFromList(wineList, removedWine));
//      }
//    }
//  }
}
