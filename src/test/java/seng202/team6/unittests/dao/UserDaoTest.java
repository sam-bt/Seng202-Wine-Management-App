package seng202.team6.unittests.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.sql.SQLException;
import javafx.collections.ObservableList;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import seng202.team6.dao.UserDao;
import seng202.team6.managers.DatabaseManager;
import seng202.team6.model.User;

public class UserDaoTest {

  private DatabaseManager databaseManager;
  private UserDao userDao;

  @BeforeEach
  void setup() throws SQLException {
    databaseManager = new DatabaseManager();
    databaseManager.init();
    userDao = databaseManager.getUserDao();
  }

  @AfterEach
  void teardown() throws SQLException {
    databaseManager.teardown();
  }

  @Test
  void testAdminAccountCreated() {
    User user = userDao.get("admin");
    assertNotNull(user);
  }

  @Test
  void testAddUser() {
    User initialUser = new User("user", "password", "user", "salt");
    userDao.add(initialUser);

    User retrievedUser = userDao.get(initialUser.getUsername());
    assertNotNull(retrievedUser);
    assertEquals(initialUser.getUsername(), retrievedUser.getUsername());
  }

  @Test
  void testAddMultipleUsers() {
    User initialUser1 = new User("user1", "password", "user", "salt");
    userDao.add(initialUser1);

    User initialUser2 = new User("user2", "password", "user", "salt");
    userDao.add(initialUser2);

    // two users plus admin
    assertEquals(3, userDao.getCount());
  }

  @Test
  void testRemoveUser() {
    User initialUser = new User("user", "password", "user", "salt");
    userDao.add(initialUser);
    userDao.delete(initialUser);

    User retrievedUser = userDao.get(initialUser.getUsername());
    assertNull(retrievedUser);
  }

  @Test
  void testRemoveAllUsersDoesNotRemoveAdmin() {
    userDao.deleteAll();

    User user = userDao.get("admin");
    assertNotNull(user);
  }

  @Test
  void testRemoveAllUsers() {
    User initialUser1 = new User("user1", "password", "user", "salt");
    userDao.add(initialUser1);

    User initialUser2 = new User("user2", "password", "user", "salt");
    userDao.add(initialUser2);

    userDao.deleteAll();
    // deletes all users apart from admin
    assertEquals(1, userDao.getCount());
  }

  @Test
  void testGetAllUsers() {
    User test1 = new User("user", "password", "user", "salt");
    User test2 = new User("us", "password", "user", "salt");
    User test3 = new User("userr", "password", "user", "salt");
    User testAdmin = new User("admin", "password", "user", "salt");
    userDao.add(test1);
    userDao.add(test2);
    userDao.add(test3);
    userDao.add(testAdmin);

    ObservableList<User> result = userDao.getAll();

    assertEquals(3, result.size());
    assertEquals(test1.getUsername(), result.get(0).getUsername());
    assertEquals(test2.getUsername(), result.get(1).getUsername());
    assertEquals(test3.getUsername(), result.get(2).getUsername());
  }

  @Test
  void testGetAllUsersFromSearch() {
    User test1 = new User("user", "password", "user", "salt");
    User test2 = new User("us", "password", "user", "salt");
    User test3 = new User("userr", "password", "user", "salt");
    userDao.add(test1);
    userDao.add(test2);
    userDao.add(test3);

    String search = "user";

    ObservableList<User> result = userDao.getAllFromSearch(search);

    assertEquals(2, result.size());
    assertEquals(test1.getUsername(), result.get(0).getUsername());
    assertEquals(test3.getUsername(), result.get(1).getUsername());
  }

  @Test
  void testPasswordUpdatesInDatabase() {
    String initial = "initialPassword123";
    String changed = "changedPassword456";
    User initialUser = createUser("testUser", initial, "USER", "salt123");
    initialUser.setPassword(changed);

    User updatedUser = userDao.get("testUser");
    assertEquals(changed, updatedUser.getPassword());
  }

  @Test
  void testRoleUpdatesInDatabase() {
    String initial = "USER";
    String changed = "ADMIN";
    User initialUser = createUser("testUser", "password123", initial, "salt123");
    initialUser.setRole(changed);

    User updatedUser = userDao.get("testUser");
    assertEquals(changed, updatedUser.getRole());
  }

  @Test
  void testSaltUpdatesInDatabase() {
    String initial = "initialSalt123";
    String changed = "changedSalt456";
    User initialUser = createUser("testUser", "password123", "USER", initial);
    initialUser.setSalt(changed);

    User updatedUser = userDao.get("testUser");
    assertEquals(changed, updatedUser.getSalt());
  }

  private User createUser(String username, String password, String role, String salt) {
    User user = new User(username, password, role, salt);
    userDao.add(user);
    return user;
  }
}
