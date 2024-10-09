package seng202.team6.unittests.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
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

public class WineNoteDaoTest {

  private DatabaseManager databaseManager;
  private WineNotesDao wineNotesDao;
  private WineDao wineDao;
  private UserDao userDao;
  private User user;
  private Wine wine;

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
  }

  @AfterEach
  void teardown() {
    databaseManager.teardown();
  }

  @Test
  void testNoNoteReturnsBlank() {
    Note note = wineNotesDao.get(user, wine);
    assertTrue(note.getNote().isEmpty());
  }

  @Test
  void testNoNoteDoesNotAddToDatabase() {
    Note note = wineNotesDao.get(user, wine);
    assertTrue(wineNotesDao.getAll().isEmpty());
  }

  @Test
  void testNoNoteModifyAddsToDatabase() {
    Note note = wineNotesDao.get(user, wine);
    note.setNote("MyNote");

    Note newNote = wineNotesDao.get(user, wine);
    assertEquals(newNote.getNote(), "MyNote");
  }

  @Test
  void testExistingNoteMadeBlankDeletesFromDatabase() {
    Note note = wineNotesDao.get(user, wine);
    note.setNote("MyNote");

    Note newNote = wineNotesDao.get(user, wine);
    newNote.setNote("");
    assertTrue(wineNotesDao.getAll().isEmpty());
  }

  @Test
  void testExistingNoteModifiedUpdatesDatabase() {
    Note note = wineNotesDao.get(user, wine);
    note.setNote("MyNote");

    Note newNote = wineNotesDao.get(user, wine);
    newNote.setNote("MyNewNote");

    Note newUpdatedNote = wineNotesDao.get(user, wine);
    ;
    assertEquals("MyNewNote", newUpdatedNote.getNote());
  }

  @Test
  void testGetAllNotesForUser() {

    Wine testWine = new Wine(10, "wine", "pinot gris", "nz", "christchurch",
        "bob's wine", "red", 2011, "na", 99, 25f, 10f,
        new GeoLocation(10,10), 5.0);
    wineDao.add(testWine);

    Note note1 = wineNotesDao.get(user, wine);
    Note note2 = wineNotesDao.get(user, testWine);

    note1.setNote("Testnote1");
    note2.setNote("Testnote2");

    ObservableList<Note> result =  wineNotesDao.getAll(user);

    assertEquals(2, result.size());
    assertEquals(note1.getNote(), result.get(0).getNote());
    assertEquals(note2.getNote(), result.get(1).getNote());

  }

}
