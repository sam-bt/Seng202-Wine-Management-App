package seng202.team6.unittests.service;

import static org.junit.Assert.assertEquals;

import java.sql.SQLException;
import org.junit.Before;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import seng202.team6.dao.UserDao;
import seng202.team6.dao.WineDao;
import seng202.team6.dao.WineNotesDao;
import seng202.team6.managers.AuthenticationManager;
import seng202.team6.managers.DatabaseManager;
import seng202.team6.managers.ManagerContext;
import seng202.team6.model.Note;
import seng202.team6.model.User;
import seng202.team6.model.Wine;
import seng202.team6.service.WineNoteService;

public class WineNoteServiceTest {
  private DatabaseManager databaseManager;
  private WineNotesDao wineNotesDao;
  private WineNoteService wineNoteService;
  private WineDao wineDao;
  private UserDao userDao;
  private User user;
  private Wine wine;
  private Note testNote;

  @BeforeEach
  void setup() throws SQLException {
    databaseManager = new DatabaseManager();
    wineNotesDao = databaseManager.getWineNotesDao();
    wineDao = databaseManager.getWineDao();
    userDao = databaseManager.getUserDao();
    wineNotesDao.setUseCache(false);


    user = new User("username", "password", "role", "salt");
    userDao.add(user);

    wine = new Wine(-1, "wine", "blue", "nz", "christchurch", "", "", 1024, "na", 99, 25.0f,
        50f, null, 0.0);
    wineDao.add(wine);

    testNote = wineNotesDao.get(user, wine);
    testNote.setNote("test");

    wineNoteService = new WineNoteService(new AuthenticationManager(databaseManager), databaseManager, wine);
  }

  @AfterEach
  void teardown() {
    databaseManager.teardown();
  }

  /**
   * Tests that the note is retrieved correctly from the service.
   */
  @Test
  void testLoadUsersNote() {
    Note note = wineNoteService.loadUsersNote(user);
    assertEquals(note.getNote(), "test");
  }

  @Test
  void testLoadSelectedNote() {
    wineNoteService.loadUsersNote(user);
    Note note = wineNoteService.getNote();
    assertEquals(note.getNote(), "test");
  }





}
