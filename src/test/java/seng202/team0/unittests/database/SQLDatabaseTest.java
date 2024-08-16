package seng202.team0.unittests.database;

import java.sql.SQLException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import seng202.team0.database.SQLDatabase;

public class SQLDatabaseTest {

  @BeforeEach
  public void setUp() {

  }

  @Test
  public void testSetupDb() {
    SQLDatabase db = new SQLDatabase();
    db.setupDb();

    try {
      Assertions.assertTrue(db.getConnection().isValid(2));

    } catch (SQLException e) {
      e.printStackTrace();
      Assertions.fail(); // Something has gone wrong so test should fail

    }

    db.disconnectDb();

    //TODO More testing needed here
  }
}
