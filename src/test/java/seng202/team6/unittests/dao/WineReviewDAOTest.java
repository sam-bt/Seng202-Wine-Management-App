package seng202.team6.unittests.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.sql.Date;
import java.sql.SQLException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import seng202.team6.dao.UserDAO;
import seng202.team6.dao.WineDAO;
import seng202.team6.dao.WineReviewDAO;
import seng202.team6.managers.DatabaseManager;
import seng202.team6.model.User;
import seng202.team6.model.Wine;
import seng202.team6.model.WineReview;

public class WineReviewDAOTest {
  private DatabaseManager databaseManager;
  private WineReviewDAO wineReviewDAO;
  private WineDAO wineDAO;
  private UserDAO userDAO;
  private User user;
  private Wine wine;

  @BeforeEach
  void setup() throws SQLException {
    databaseManager = new DatabaseManager();
    wineReviewDAO = databaseManager.getWineReviewDAO();
    wineDAO = databaseManager.getWineDAO();
    userDAO = databaseManager.getUserDAO();
    wineReviewDAO.setUseCache(false);

    user = new User("username", "password", "role", "salt");
    userDAO.add(user);

    wine = new Wine(-1, "wine", "blue", "nz", "christchurch", "", "", 1024, "na", 99, 25.0f,
        50f, null);
    wineDAO.add(wine);
  }

  @AfterEach
  void teardown() {
    databaseManager.teardown();
  }

  @Test
  void testRatingUpdatesInDatabase() {
    double initial = 3.5;
    double changed = 4.0;
    WineReview initialReview = createWineReview(initial, "Initial description");
    initialReview.setRating(changed);

    WineReview updatedReview = wineReviewDAO.getAll(user).getFirst();
    assertEquals(changed, updatedReview.getRating(), 0.001);
  }

  @Test
  void testDescriptionUpdatesInDatabase() {
    String initial = "Initial description";
    String changed = "Changed description";
    WineReview initialReview = createWineReview(4.0, initial);
    initialReview.setDescription(changed);

    WineReview updatedReview = wineReviewDAO.getAll(user).getFirst();
    assertEquals(changed, updatedReview.getDescription());
  }

  private WineReview createWineReview(double rating, String description) {
    return wineReviewDAO.add(user, wine, rating, description, new Date(System.currentTimeMillis()));
  }
}
