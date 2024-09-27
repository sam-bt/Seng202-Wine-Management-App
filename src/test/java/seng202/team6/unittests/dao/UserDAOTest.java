package seng202.team6.unittests.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.sql.SQLException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import seng202.team6.dao.UserDAO;
import seng202.team6.managers.DatabaseManager;
import seng202.team6.model.User;

public class UserDAOTest {
  private DatabaseManager databaseManager;
  private UserDAO userDAO;

  @BeforeEach
  void setup() throws SQLException {
    databaseManager = new DatabaseManager();
    databaseManager.init();
    userDAO = databaseManager.getUserDAO();
  }

  @AfterEach
  void teardown() throws SQLException {
    databaseManager.teardown();
  }

  @Test
  void testAdminAccountCreated() {
    User user = userDAO.get("admin");
    assertNotNull(user);
  }

  @Test
  void testAddUser() {
    User initialUser = new User("user", "password", "user", "salt");
    userDAO.add(initialUser);

    User retrievedUser = userDAO.get(initialUser.getUsername());
    assertNotNull(retrievedUser);
    assertEquals(initialUser.getUsername(), retrievedUser.getUsername());
  }

  @Test
  void testAddMultipleUsers() {
    User initialUser1 = new User("user1", "password", "user", "salt");
    userDAO.add(initialUser1);

    User initialUser2 = new User("user2", "password", "user", "salt");
    userDAO.add(initialUser2);

    // two users plus admin
    assertEquals(3, userDAO.getCount());
  }

  @Test
  void testRemoveUser() {
    User initialUser = new User("user", "password", "user", "salt");
    userDAO.add(initialUser);
    userDAO.delete(initialUser);

    User retrievedUser = userDAO.get(initialUser.getUsername());
    assertNull(retrievedUser);
  }

  @Test
  void testRemoveAllUsersDoesNotRemoveAdmin() {
    userDAO.deleteAll();

    User user = userDAO.get("admin");
    assertNotNull(user);
  }

  @Test
  void testRemoveAllUsers() {
    User initialUser1 = new User("user1", "password", "user", "salt");
    userDAO.add(initialUser1);

    User initialUser2 = new User("user2", "password", "user", "salt");
    userDAO.add(initialUser2);

    userDAO.deleteAll();
    // deletes all users apart from admin
    assertEquals(1, userDAO.getCount());
  }

  public static void main(String[] args) throws SQLException {
    UserDAOTest userDAOTest = new UserDAOTest();
    userDAOTest.setup();
    userDAOTest.testAddMultipleUsers();
    userDAOTest.teardown();
  }
}
