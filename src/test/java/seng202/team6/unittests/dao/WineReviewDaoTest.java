package seng202.team6.unittests.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.sql.Date;
import java.sql.SQLException;
import javafx.collections.ObservableList;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import seng202.team6.dao.UserDao;
import seng202.team6.dao.WineDao;
import seng202.team6.dao.WineReviewDao;
import seng202.team6.managers.DatabaseManager;
import seng202.team6.model.GeoLocation;
import seng202.team6.model.User;
import seng202.team6.model.Wine;
import seng202.team6.model.WineReview;

public class WineReviewDaoTest {

  private DatabaseManager databaseManager;
  private WineReviewDao wineReviewDao;
  private WineDao wineDao;
  private UserDao userDao;
  private User user;
  private Wine wine;

  @BeforeEach
  void setup() throws SQLException {
    databaseManager = new DatabaseManager();
    wineReviewDao = databaseManager.getWineReviewDao();
    wineDao = databaseManager.getWineDao();
    userDao = databaseManager.getUserDao();
    wineReviewDao.setUseCache(false);

    user = new User("username", "password", "role", "salt");
    userDao.add(user);

    wine = new Wine(
            -1l,
            "wine",
            "blue",
            "nz",
            "christchurch",
            "",
            "blue",
            1024,
            "na",
            99,
            25.0f,
            50f,
            null,
            2f);
    wineDao.add(wine);
  }

  @AfterEach
  void teardown() {
    databaseManager.teardown();
  }

  @Test
  void testRatingUpdatesInDatabase() {
    double initial = 3.5;
    double changed = 4.0;
    WineReview initialReview = createWineReview(initial, wine, "Initial description");
    initialReview.setRating(changed);

    WineReview updatedReview = wineReviewDao.getAll(user).getFirst();
    assertEquals(changed, updatedReview.getRating(), 0.001);
  }

  @Test
  void testDescriptionUpdatesInDatabase() {
    String initial = "Initial description";
    String changed = "Changed description";
    WineReview initialReview = createWineReview(4.0, wine, initial);
    initialReview.setDescription(changed);

    WineReview updatedReview = wineReviewDao.getAll(user).getFirst();
    assertEquals(changed, updatedReview.getDescription());
  }

  @Test
  void testGetAllInRange() {

    Wine testWine = new Wine(10, "wine", "pinot gris", "nz", "christchurch",
        "bob's wine", "red", 2011, "na", 99, 25f, 10f,
        new GeoLocation(10,10), 5.0);
    wineDao.add(testWine);

    WineReview review1 = createWineReview(2, wine, "test1");
    WineReview review2 = createWineReview(2, testWine, "test2");

    ObservableList<WineReview> result = wineReviewDao.getAllInRange(1, 10);

    assertEquals(result.size(), 1);
    assertEquals(result.getFirst().getDescription(), review2.getDescription());

  }

  @Test
  void testDeleteReview() {

    Wine testWine = new Wine(10, "wine", "pinot gris", "nz", "christchurch",
        "bob's wine", "red", 2011, "na", 99, 25f, 10f,
        new GeoLocation(10,10), 5.0);
    wineDao.add(testWine);

    WineReview review1 = createWineReview(2, wine, "test1");
    WineReview review2 = createWineReview(2, testWine, "test2");

    ObservableList<WineReview> result1 = wineReviewDao.getAll(user);

    assertEquals(result1.size(), 2);

    wineReviewDao.delete(review1);

    ObservableList<WineReview> result2 = wineReviewDao.getAll(user);
    assertEquals(result2.getFirst().getDescription(), review2.getDescription());

  }

  @Test
  void testDeleteAllReviewsFromUser() {

    Wine testWine = new Wine(10, "wine", "pinot gris", "nz", "christchurch",
        "bob's wine", "red", 2011, "na", 99, 25f, 10f,
        new GeoLocation(10,10), 5.0);
    wineDao.add(testWine);

    WineReview review1 = createWineReview(2, wine, "test1");
    WineReview review2 = createWineReview(2, testWine, "test2");

    ObservableList<WineReview> result1 = wineReviewDao.getAll(user);

    assertEquals(result1.size(), 2);

    wineReviewDao.deleteAllFromUser(user);

    ObservableList<WineReview> result2 = wineReviewDao.getAll(user);
    assertEquals(result2.size(), 0);

  }

  @Test
  void testGetAllReviewsForAWine() {

    Wine testWine = new Wine(10, "wine", "pinot gris", "nz", "christchurch",
        "bob's wine", "red", 2011, "na", 99, 25f, 10f,
        new GeoLocation(10,10), 5.0);
    wineDao.add(testWine);

    WineReview review1 = createWineReview(2, wine, "test1");
    WineReview review2 = createWineReview(2, testWine, "test2");
    WineReview review3 = createWineReview(2, wine, "test3");

    ObservableList<WineReview> result = wineReviewDao.getAll(wine);

    assertEquals(result.size(), 2);
    assertEquals(result.get(0).getDescription(), review1.getDescription());
    assertEquals(result.get(1).getDescription(), review3.getDescription());

  }

  @Test
  void testGetAllReviewsForAUser() {

    Wine testWine = new Wine(10, "wine", "pinot gris", "nz", "christchurch",
        "bob's wine", "red", 2011, "na", 99, 25f, 10f,
        new GeoLocation(10,10), 5.0);
    wineDao.add(testWine);

    WineReview review1 = createWineReview(2, wine, "test1");
    WineReview review2 = createWineReview(2, testWine, "test2");
    WineReview review3 = createWineReview(2, wine, "test3");

    ObservableList<WineReview> result = wineReviewDao.getAll(user);

    assertEquals(result.size(), 3);
    assertEquals(result.get(0).getDescription(), review1.getDescription());
    assertEquals(result.get(1).getDescription(), review2.getDescription());
    assertEquals(result.get(2).getDescription(), review3.getDescription());

  }


  private WineReview createWineReview(double rating, Wine wine, String description) {
    return wineReviewDao.add(user, wine, rating, description, new Date(System.currentTimeMillis()));
  }
}
