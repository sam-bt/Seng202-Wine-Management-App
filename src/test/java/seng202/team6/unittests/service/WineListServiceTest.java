package seng202.team6.unittests.service;

import static java.sql.Types.NULL;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.SQLException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import seng202.team6.dao.WineListDao;
import seng202.team6.managers.AuthenticationManager;
import seng202.team6.managers.DatabaseManager;
import seng202.team6.model.User;
import seng202.team6.model.Wine;
import seng202.team6.model.WineList;
import seng202.team6.service.WineListService;

public class WineListServiceTest {

  private DatabaseManager databaseManager;
  private AuthenticationManager authenticationManager;
  private WineListService wineListService;


  /**
   * Sets up the test environment before each test method. Initializes the DatabaseManager
   * AuthenticationManager and WineListService.
   *
   * @throws SQLException if there's an error setting up the database connection
   */
  @BeforeEach
  public void setup() throws SQLException {
    databaseManager = new DatabaseManager();
    authenticationManager = new AuthenticationManager(databaseManager);
    authenticationManager.setAuthenticatedUser(databaseManager.getUserDao().get("admin"));
    wineListService = new WineListService(authenticationManager, databaseManager);
  }

  /**
   * Closes the database manager connection after each test method.
   */
  @AfterEach
  public void close() { databaseManager.teardown(); }

  /**
   * Tests to see that a wine is in a list.
   *
   * @throws SQLException if there's an error in the database connection
   */
  @Test
  public void isWineInListValid() throws SQLException {
    WineList wineList = databaseManager.getWineListDao()
        .create(databaseManager.getUserDao().get("admin"), "list");
    Wine wine = new Wine(-1, "New Wine", "Variety", "New Zealand",
        "Region", "Winery", "White", 2020, "Description",
        10, 4f, 10f, null, 50);
    databaseManager.getWineDao().add(wine);
    databaseManager.getWineListDao().addWine(wineList, wine);
    boolean inList = wineListService.isWineInList(wineList, wine);
    assertTrue(inList);
  }

  /**
   * Tests to see that a wine is not in a list.
   */
  @Test
  public void isWineInListInvalid() throws SQLException {
    WineList wineList = databaseManager.getWineListDao()
        .create(databaseManager.getUserDao().get("admin"), "list");
    Wine wine = new Wine(-1, "New Wine", "Variety", "New Zealand",
        "Region", "Winery", "White", 2020, "Description",
        10, 4f, 10f, null, 50);
    databaseManager.getWineDao().add(wine);
    boolean inList = wineListService.isWineInList(wineList, wine);
    assertFalse(inList);
  }

  /**
   * Tests that a valid wineList can be created.
   */
  @Test
  public void createWineListValid() throws SQLException {
    User user = databaseManager.getUserDao().get("admin");
    wineListService.createWineList(user, "New List");
    boolean inList = databaseManager.getWineListDao()
        .getAll(user).stream().anyMatch(wineList -> wineList.name().equals("New List"));
    assertTrue(inList);
  }

  /**
   * Tests that an invalid wineList cannot be created.
   */
  @Test
  public void createWineListInvalid() throws SQLException {
    User user = databaseManager.getUserDao().get("admin");
    wineListService.createWineList(user, "New List");
    boolean inList = databaseManager.getWineListDao()
        .getAll(user).stream().anyMatch(wineList -> wineList.name().equals("Not Exists"));
    assertFalse(inList);
  }

  /**
  * Tests a WineList can be removed.
  */
  @Test
  public void testCanRemoveValid() {
    WineList removeableWineList = new WineList(1, "List Name");
    boolean canRemove = wineListService.canRemove(removeableWineList);
    assertTrue(canRemove);
  }

  /**
   * Tests that the favourites list cannot be removed.
   */
  @Test
  public void testCanRemoveInvalid() {
    WineList unremoveableWineList = new WineList(1, "Favourites");
    boolean canRemove = wineListService.canRemove(unremoveableWineList);
    assertFalse(canRemove);
  }

  /**
   * Tests deleting an invalid list.
   */
  @Test
  public void deleteWineListValid() throws SQLException {
    User user = databaseManager.getUserDao().get("admin");
    wineListService.createWineList(user, "New List");
    WineList wineList = databaseManager.getWineListDao().getAll(user).getLast();
    wineListService.deleteWineList(wineList);
    boolean inList = databaseManager.getWineListDao()
        .getAll(user).stream().anyMatch(list -> list.name().equals("New List"));
    assertFalse(inList);
  }

  /**
   * Tests deleting an invalid list.
   */
  @Test
  public void deleteWineListInValid() throws SQLException {
    User user = databaseManager.getUserDao().get("admin");
    wineListService.createWineList(user, "New List");
    WineList wineList = new WineList(-1, "Not Valid");
    wineListService.deleteWineList(wineList);
    boolean inList = databaseManager.getWineListDao()
        .getAll(user).stream().anyMatch(list -> list.name().equals("New List"));
    assertTrue(inList);
  }
}
