package seng202.team0.unittests.managers;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.sql.SQLException;
import java.util.ArrayList;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import seng202.team0.database.Wine;
import seng202.team0.managers.DatabaseManager;

class DatabaseManagerTest {
  DatabaseManager manager;

  /**
   * Initializes the database
   */
  @BeforeEach
  void setup(){
    assertDoesNotThrow(() -> {
      manager = new DatabaseManager();
    });
  }

  /**
   * Closes the database
   */
  @AfterEach
  void close(){
    manager.close();
  }

  /**
   * Tests getting wines in a certain range
   * @throws SQLException if error
   */
  @Test
  void getWinesInRange() throws SQLException {
    addWines(10);
    assertEquals(3, manager.getWinesInRange(1, 4).size());
  }

  /**
   * Tests getting wine size
   * @throws SQLException if error
   */
  @Test
  void getWinesSize() throws SQLException {
    addWines(10);
    assertEquals(10, manager.getWinesSize());
  }

  /**
   * Tests replacing wine
   * @throws SQLException if error
   */
  @Test
  void replaceAllWines() throws SQLException {
    addWines(10);

    ArrayList<Wine> wines = new ArrayList<>();
    for(int i=0; i < 3; i++) {
      wines.add(new Wine("wine", "blue", "nz", "bob's wine", "na", 99, 25.0f, 50f));
    }
    manager.replaceAllWines(wines);
    assertEquals(3, manager.getWinesSize());
  }

  /**
   * Adds wine
   * @throws SQLException if error
   */
  void addWines(int num) throws SQLException {
    ArrayList<Wine> wines = new ArrayList<>();
    for(int i=0; i < num; i++) {
      wines.add(new Wine("wine", "blue", "nz", "bob's wine", "na", 99, 25f, (float)i));
    }
    manager.addWines(wines);


  }
}