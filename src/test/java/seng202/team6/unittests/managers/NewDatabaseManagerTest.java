package seng202.team6.unittests.managers;

import java.sql.SQLException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import seng202.team6.managers.NewDatabaseManager;

public class NewDatabaseManagerTest {
  private NewDatabaseManager databaseManager;

  @BeforeEach
  void setup() throws SQLException {
    databaseManager = new NewDatabaseManager();
    databaseManager.init();
  }

  @AfterEach
  void teardown() throws SQLException {
    databaseManager.teardown();
  }

  @Test
  void testTableCreation() {

  }
}
