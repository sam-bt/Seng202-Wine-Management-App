package seng202.team0.managers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import seng202.team0.database.Wine;

/**
 * Mediates access to the database
 *
 */
public class DatabaseManager {

  /**
   * Gets a subset of the wines in the database
   * <p>
   *   The order of elements should remain stable until a write operation occurs.
   * </p>
   * @param begin beginning element
   * @param end end element (begin + size)
   * @return subset list of wines
   */
  public ObservableList<Wine> getWinesInRange(int begin, int end) {
    ObservableList<Wine> wines = FXCollections.observableArrayList(
        new Wine("Plume Pinot Noir", "Pinot Noir", "New Zealand", "Lake Chalice", "", 75, 0.0f,
            10.0f),
        new Wine("Clayvin Single Vineyard Chardonnay", "Chardonnay", "New Zealand", "Giesen Group", "", 50, 0.0f,
            12.0f)
    );
    return wines;
  }

  /**
   * Gets the number of wine records
   * @return total number of wine records
   */
  public int getWinesSize() {
    return 2;
  }

}
