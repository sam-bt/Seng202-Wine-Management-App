package seng202.team6.unittests.managers;

import java.sql.SQLException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import seng202.team6.managers.DatabaseManager;

/**
 * Test class for the DatabaseManager. This class contains unit tests to verify the
 * functionality of the databaseManager.
 */
public class DatabaseManagerTest {

  private DatabaseManager databaseManager;

  /**
   * Sets up the test environment before each test method. Initializes the DatabaseManager.
   *
   * @throws SQLException if there's an error setting up the database connection
   */
  @BeforeEach
  void setup() throws SQLException {
    databaseManager = new DatabaseManager();
    databaseManager.init();
  }

  /**
   * Closes the database manager connection after each test method
   */
  @AfterEach
  void teardown() throws SQLException {
    databaseManager.teardown();
  }

  /**
   * Test to verify that the sql tables are created
   */
  @Test
  void testTableCreation() {

  }
}
