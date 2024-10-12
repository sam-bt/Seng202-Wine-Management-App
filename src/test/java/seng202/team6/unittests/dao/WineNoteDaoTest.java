package seng202.team6.unittests.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.SQLException;
import javafx.collections.ObservableList;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import seng202.team6.dao.UserDao;
import seng202.team6.dao.WineDao;
import seng202.team6.dao.WineNotesDao;
import seng202.team6.managers.DatabaseManager;
import seng202.team6.model.GeoLocation;
import seng202.team6.model.Note;
import seng202.team6.model.User;
import seng202.team6.model.Wine;

/**
 * Unit tests for the WineNotesDao class, which handles operations related to user notes
 * on wines. These tests ensure correct behavior when adding, modifying, and retrieving notes.
 */
public class WineNoteDaoTest {

  private DatabaseManager databaseManager;
  private WineNotesDao wineNotesDao;
  private WineDao wineDao;
  private UserDao userDao;
  private User user;
  private Wine wine;

  /**
   * Sets up the database manager, WineNotesDao, WineDao, and UserDao before each test. Adds a
   * test user and wine to the database.
   *
   * @throws SQLException if an error occurs during database setup.
   */
  @BeforeEach
  void setup() throws SQLException {
    databaseManager = new DatabaseManager();
    wineNotesDao = databaseManager.getWineNotesDao();
    wineDao = databaseManager.getWineDao();
    userDao = databaseManager.getUserDao();

    user = new User("username", "password", "role", "salt");
    userDao.add(user);

    wine = new Wine(-1, "wine", "blue", "nz", "christchurch", "", "", 1024, "na", 99, 25.0f,
        50f, null, 0.0);
    wineDao.add(wine);
  }

  /**
   * Tears down the database after each test, removing any added data and resetting the state.
   */
  @AfterEach
  void teardown() {
    databaseManager.teardown();
  }

  /**
   * Tests that if no note exists for a wine, null is returned
   */
  @Test
  void testNoNoteReturnsEmpty() {
    Note note = wineNotesDao.getOrCreate(user, wine);
    assertTrue(note.getNote().isEmpty());
  }

  /**
   * Tests that querying a note when none exists does not add the note to the database.
   */
  @Test
  void testNoNoteDoesNotAddToDatabase() {
    Note note = wineNotesDao.getOrCreate(user, wine);
    assertTrue(wineNotesDao.getAll().isEmpty());
  }

  /**
   * Tests that modifying a blank note adds it to the database.
   */
  @Test
  void testNoNoteModifyAddsToDatabase() {
    Note note = wineNotesDao.getOrCreate(user, wine);
    note.setNote("MyNote");

    Note newNote = wineNotesDao.getOrCreate(user, wine);
    assertEquals(newNote.getNote(), "MyNote");
  }

  /**
   * Tests that setting an existing note to blank removes it from the database.
   */
  @Test
  void testExistingNoteMadeBlankDeletesFromDatabase() {

    wineNotesDao.getOrCreate(user, wine).setNote("Note");

    Note newNote = wineNotesDao.getOrCreate(user, wine);
    newNote.setNote("");
    assertTrue(wineNotesDao.getAll().isEmpty());
  }

  /**
   * Tests that modifying an existing note updates the note in the database.
   */
  @Test
  void testExistingNoteModifiedUpdatesDatabase() {
    Note note = wineNotesDao.getOrCreate(user, wine);
    note.setNote("MyNote");

    Note newNote = wineNotesDao.getOrCreate(user, wine);
    newNote.setNote("MyNewNote");

    Note newUpdatedNote = wineNotesDao.getOrCreate(user, wine);
    ;
    assertEquals("MyNewNote", newUpdatedNote.getNote());
  }

  /**
   * Tests that all notes for a user can be retrieved correctly.
   */
  @Test
  void testGetAllNotesForUser() throws SQLException {

    Wine testWine = new Wine(-1, "wine", "pinot gris", "nz", "christchurch",
        "bob's wine", "red", 2011, "na", 99, 25f, 10f,
        new GeoLocation(10,10), 5.0);
    wineDao.add(testWine);

    Note note1 = wineNotesDao.getOrCreate(user, wine);
    Note note2 = wineNotesDao.getOrCreate(user, testWine);

    note1.setNote("Testnote1");
    note2.setNote("Testnote2");

    ObservableList<Note> result =  wineNotesDao.getAll(user);

    assertEquals(2, result.size());
    assertEquals(note1.getNote(), result.get(0).getNote());
    assertEquals(note2.getNote(), result.get(1).getNote());

  }

}
