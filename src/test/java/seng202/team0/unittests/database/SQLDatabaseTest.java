package seng202.team0.unittests.database;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.ArrayList;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import seng202.team0.database.DataTable;
import seng202.team0.database.SQLDatabase;
import seng202.team0.database.Value;
import seng202.team0.database.exceptions.DuplicateTableException;

public class SQLDatabaseTest {
  SQLDatabase db = new SQLDatabase();

  @BeforeEach
  public void setUp() {
    try {
      Files.deleteIfExists(Paths.get("sqlDatabase/SQLDatabase.db"));
    } catch (IOException ignored) {
    }
    db.setupDb();
  }

  @Test
  public void testSetupDb() {

    try {
      Assertions.assertTrue(db.getConnection().isValid(2));

    } catch (SQLException e) {
      Assertions.fail(); // Something has gone wrong so test should fail

    }

    db.disconnectDb();

    //TODO More testing needed here
  }

  @Test
  public void testDisconnectDb() {
    db.disconnectDb();
    Assertions.assertNull(db.getConnection());
  }

  @Test
  public void testAddTable() {
    ArrayList<ArrayList<Value>> columns = new ArrayList<>();
    columns.add(new ArrayList<>());
    columns.add(new ArrayList<>());
    columns.add(new ArrayList<>());
    columns.get(0).add(Value.make(0.0));
    columns.get(0).add(Value.make(1.0));

    columns.get(1).add(Value.make("fing"));
    columns.get(1).add(Value.make("ding"));

    columns.get(2).add(Value.make(0.0));
    columns.get(2).add(Value.make(-1.0));

    DataTable testTable = new DataTable(
        new String[]{"foo", "baz", "bar"},
        columns
    );

    try {
      db.addTable("test_table", testTable);

    } catch (DuplicateTableException e) {
      Assertions.fail();
    }

    Assertions.assertTrue(db.tableExists("test_table"));
  }
}
