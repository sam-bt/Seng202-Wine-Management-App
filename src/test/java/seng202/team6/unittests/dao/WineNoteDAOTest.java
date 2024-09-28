package seng202.team6.unittests.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.SQLException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import seng202.team6.dao.UserDAO;
import seng202.team6.dao.WineDAO;
import seng202.team6.dao.WineNotesDAO;
import seng202.team6.managers.DatabaseManager;
import seng202.team6.model.Note;
import seng202.team6.model.User;
import seng202.team6.model.Wine;

public class WineNoteDAOTest {
  private DatabaseManager databaseManager;
  private WineNotesDAO wineNotesDAO;
  private WineDAO wineDAO;
  private UserDAO userDAO;
  private User user;
  private Wine wine;

  @BeforeEach
  void setup() throws SQLException {
    databaseManager = new DatabaseManager();
    wineNotesDAO = databaseManager.getWineNotesDAO();
    wineDAO = databaseManager.getWineDAO();
    userDAO = databaseManager.getUserDAO();
    wineNotesDAO.setUseCache(false);

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
  void testNoNoteReturnsBlank() {
    Note note = wineNotesDAO.get(user, wine);
    assertTrue(note.getNote().isEmpty());
  }

  @Test
  void testNoNoteDoesNotAddToDatabase() {
    Note note = wineNotesDAO.get(user, wine);
    assertTrue(wineNotesDAO.getAll().isEmpty());
  }

  @Test
  void testNoNoteModifyAddsToDatabase() {
    Note note = wineNotesDAO.get(user, wine);
    note.setNote("MyNote");

    Note newNote = wineNotesDAO.get(user, wine);
    assertEquals(newNote.getNote(), "MyNote");
  }

  @Test
  void testExistingNoteMadeBlankDeletesFromDatabase() {
    Note note = wineNotesDAO.get(user, wine);
    note.setNote("MyNote");

    Note newNote = wineNotesDAO.get(user, wine);
    newNote.setNote("");
    assertTrue(wineNotesDAO.getAll().isEmpty());
  }

  @Test
  void testExistingNoteModifiedUpdatesDatabase() {
    Note note = wineNotesDAO.get(user, wine);
    note.setNote("MyNote");

    Note newNote = wineNotesDAO.get(user, wine);
    newNote.setNote("MyNewNote");

    Note newUpdatedNote = wineNotesDAO.get(user, wine);;
    assertEquals("MyNewNote", newUpdatedNote.getNote());
  }
}
