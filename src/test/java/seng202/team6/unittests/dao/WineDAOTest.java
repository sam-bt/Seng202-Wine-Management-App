package seng202.team6.unittests.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import seng202.team6.dao.WineDAO;
import seng202.team6.managers.DatabaseManager;
import seng202.team6.model.Wine;

public class WineDAOTest {
  private DatabaseManager databaseManager;
  private WineDAO wineDAO;

  @BeforeEach
  void setup() throws SQLException {
    databaseManager = new DatabaseManager();
    databaseManager.init();
    wineDAO = databaseManager.getWineDAO();
  }

  @AfterEach
  void teardown() throws SQLException {
    databaseManager.teardown();
  }

  @Test
  void testAddSingleWine() {
    addWines(1);
    assertEquals(1, wineDAO.getAll().size());
  }

  @Test
  void testAddingMultipleWines() {
    addWines(20);
    assertEquals(20, wineDAO.getAll().size());
  }

  @Test
  void testRemoveAllWines() {
    addWines(10);
    wineDAO.removeAll();
    assertEquals(0, wineDAO.getAll().size());
  }

  @Test
  void testReplaceAllWines() {
    addWines(10);

    List<Wine> wines = new ArrayList<>();
    for (int i = 0; i < 3; i++) {
      wines.add(
          new Wine(-1, "wine", "blue", "nz", "christchurch", "", "", 1024, "na", 99, 25.0f,
              50f, null));
    }

    wineDAO.replaceAll(wines);
    assertEquals(wines.size(), wineDAO.getAll().size());
  }

  private void addWines(int num) {
    List<Wine> wines = new ArrayList<>();
    for (int i = 0; i < num; i++) {
      wines.add(
          new Wine(-1, "wine", "blue", "nz", "christchurch", "bob's wine", "red", 2011,
              "na", 99, 25f,
              (float) i, null));
    }
    wineDAO.addAll(wines);
  }
}
