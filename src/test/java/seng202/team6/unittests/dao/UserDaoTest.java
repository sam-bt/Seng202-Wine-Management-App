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

/**
 * Unit tests for the UserDao class, which handles database operations related to user
 * management. These tests verify the correctness of user creation, retrieval, deletion,
 * and updates, including password, role, and salt updates in the database.
 *
 */
public class UserDaoTest {

  private DatabaseManager databaseManager;
  private UserDao userDao;

  /**
   * Initializes the database manager and UserDao before each test.
   * Sets up the database for testing by initializing the connection and loading the UserDao.
   *
   * @throws SQLException if there is an error initializing the database.
   */
  @BeforeEach
  void setup() throws SQLException {
    databaseManager = new DatabaseManager();
    databaseManager.init();
    userDao = databaseManager.getUserDao();
  }

  /**
   * Tears down the database after each test, cleaning up any added test data.
   *
   * @throws SQLException if there is an error during database teardown.
   */
  @AfterEach
  void teardown() throws SQLException {
    databaseManager.teardown();
  }

  /**
   * Tests that the admin account is created and exists in the database.
   */
  @Test
  void testAdminAccountCreated() {
    User user = userDao.get("admin");
    assertNotNull(user);
  }

  /**
   * Tests the addition of a user to the database.
   * Verifies that the added user can be retrieved and has the correct username.
   */
  @Test
  void testAddUser() {
    User initialUser = new User("user", "password", "user", "salt");
    userDao.add(initialUser);

    User retrievedUser = userDao.get(initialUser.getUsername());
    assertNotNull(retrievedUser);
    assertEquals(initialUser.getUsername(), retrievedUser.getUsername());
  }

  /**
   * Tests the addition of multiple users to the database.
   * Verifies that the correct number of users (including admin) exists in the database.
   */
  @Test
  void testAddMultipleUsers() {
    User initialUser1 = new User("user1", "password", "user", "salt");
    userDao.add(initialUser1);

    User initialUser2 = new User("user2", "password", "user", "salt");
    userDao.add(initialUser2);

    // two users plus admin
    assertEquals(3, userDao.getCount());
  }

  /**
   * Tests the removal of a user from the database.
   * Verifies that the user is no longer retrievable after deletion.
   */
  @Test
  void testRemoveUser() {
    User initialUser = new User("user", "password", "user", "salt");
    userDao.add(initialUser);
    userDao.delete(initialUser);

    User retrievedUser = userDao.get(initialUser.getUsername());
    assertNull(retrievedUser);
  }

  /**
   * Tests the removal of all users except the admin user.
   * Verifies that the admin account is never removed when deleting all users.
   */
  @Test
  void testRemoveAllUsersDoesNotRemoveAdmin() {
    userDao.deleteAll();

    User user = userDao.get("admin");
    assertNotNull(user);
  }

  /**
   * Tests the removal of all users from the database, including multiple added users.
   * Verifies that only the admin account remains after deletion.
   */
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

  /**
   * Tests retrieving all users from the database.
   * Verifies that the users retrieved match the users added, excluding the admin user.
   */
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

  /**
   * Tests retrieving users from the database based on a search query.
   * Verifies that the correct users matching the search criteria are returned.
   */
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

  /**
   * Tests updating the password of a user in the database.
   * Verifies that the password is correctly updated and can be retrieved.
   */
  @Test
  void testPasswordUpdatesInDatabase() {
    String initial = "initialPassword123";
    String changed = "changedPassword456";
    User initialUser = createUser("testUser", initial, "USER", "salt123");
    initialUser.setPassword(changed);

    User updatedUser = userDao.get("testUser");
    assertEquals(changed, updatedUser.getPassword());
  }

  /**
   * Tests updating the role of a user in the database.
   * Verifies that the role is correctly updated and can be retrieved.
   */
  @Test
  void testRoleUpdatesInDatabase() {
    String initial = "USER";
    String changed = "ADMIN";
    User initialUser = createUser("testUser", "password123", initial, "salt123");
    initialUser.setRole(changed);

    User updatedUser = userDao.get("testUser");
    assertEquals(changed, updatedUser.getRole());
  }

  /**
   * Tests updating the salt value of a user in the database.
   * Verifies that the salt is correctly updated and can be retrieved.
   */
  @Test
  void testSaltUpdatesInDatabase() {
    String initial = "initialSalt123";
    String changed = "changedSalt456";
    User initialUser = createUser("testUser", "password123", "USER", initial);
    initialUser.setSalt(changed);

    User updatedUser = userDao.get("testUser");
    assertEquals(changed, updatedUser.getSalt());
  }

  /**
   * Helper method for creating a user with the specified username, password, role, and salt.
   * Adds the user to the database and returns the created User object.
   *
   * @param username the username of the user to create
   * @param password the password of the user to create
   * @param role the role of the user (e.g., USER or ADMIN)
   * @param salt the salt value associated with the user's password
   * @return the created User object
   */
  private User createUser(String username, String password, String role, String salt) {
    User user = new User(username, password, role, salt);
    userDao.add(user);
    return user;
  }
}
