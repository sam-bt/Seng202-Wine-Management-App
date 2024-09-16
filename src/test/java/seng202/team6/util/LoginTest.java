package seng202.team6.util;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.SQLException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import seng202.team6.managers.DatabaseManager;

/**
 * Tests login service
 */
class LoginTest {

  DatabaseManager database;

  /**
   * Creates a test database to test
   *
   * @throws SQLException if error
   */
  @BeforeEach
  void setUp() throws SQLException {
    database = new DatabaseManager();
  }

  /**
   * @throws SQLException if error
   */
  @Test
  void addUser() throws SQLException {
    String username = "testName";
    String password = "testPassword";
    String salt = "SALT";
    database.addUser(username,password,salt);
    assertEquals(username,database.getUser(username).getUsername());
  }

  /**
   * Frees the database
   */
  @AfterEach
  void tearDown() {
    database.close();
  }

  }