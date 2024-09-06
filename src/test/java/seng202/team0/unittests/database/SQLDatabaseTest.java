package seng202.team0.unittests.database;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import seng202.team0.database.DataTable;
import seng202.team0.database.SQLDatabase;
import seng202.team0.database.Value;
import seng202.team0.database.exceptions.DuplicateTableException;
import seng202.team0.database.exceptions.TableNotFoundException;

public class SQLDatabaseTest {

  SQLDatabase db;

  @BeforeEach
  public void setUp() {
    try {
      Files.deleteIfExists(Paths.get("sqlDatabase/SQLDatabase.db"));
    } catch (IOException e) {
      System.out.println("Error deleting SQLDatabase: " + e.getMessage());
    }
    db = new SQLDatabase();
  }

  @AfterEach
  public void tearDown() {
    if (db.getConnection() != null) {
      db.disconnectDb();
    }
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

    // Table exists
    Assertions.assertTrue(db.tableExists("test_table"));

    // Table has correct cols
    try (Statement statement = db.getConnection().createStatement()) {
      ResultSet resultSet = statement.executeQuery("SELECT * FROM test_table");
      ResultSetMetaData metaData = resultSet.getMetaData();

      // Col count is equal (+1 is for auto increment id)
      Assertions.assertEquals(metaData.getColumnCount(), testTable.columnSize() + 1);

      // First col (Should be id / primary key)
      String firstColName = metaData.getColumnName(1);

      // Get database meta data
      DatabaseMetaData dbmd = db.getConnection().getMetaData();
      ResultSet pkRs = dbmd.getPrimaryKeys(null, null, "test_table");

      // Get primary key (Should only be one)
      pkRs.next();
      Assertions.assertEquals(firstColName, pkRs.getString("COLUMN_NAME"));
    } catch (SQLException e) {
      Assertions.fail();
    }
  }

  @Test
  public void testRemoveTable() {
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
      db.addTable("test_table2", testTable);
      db.removeTable("test_table");
      Assertions.assertFalse(db.tableExists("test_table"));
      Assertions.assertTrue(db.tableExists("test_table2"));

    } catch (DuplicateTableException | TableNotFoundException b) {
      Assertions.fail();
    }

  }

  @Test
  public void testInsertTable() {
    // TODO Ensure this test passes
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

    ArrayList<ArrayList<Value>> singleRow = new ArrayList<>();
    singleRow.add(new ArrayList<>());
    singleRow.add(new ArrayList<>());
    singleRow.add(new ArrayList<>());
    singleRow.get(0).add(Value.make(0.0));
    singleRow.get(1).add(Value.make("1.0"));
    singleRow.get(2).add(Value.make(1.0));

    // Initial empty table
    DataTable testTable = new DataTable(
        new String[]{"foo", "baz", "bar"},
        singleRow
    );

    // Table with data
    DataTable testTable2 = new DataTable(
        new String[]{"foo", "baz", "bar"},
        columns
    );

    try {
      db.addTable("test_table", testTable);
      db.insertIntoTable("test_table", testTable2);
    } catch (DuplicateTableException e) {
      throw new RuntimeException(e);
    }

    printSQLTable("test_table");

    // Check everything was inserted correctly
    try (Statement statement = db.getConnection().createStatement()) {
      ResultSet resultSet = statement.executeQuery("SELECT * FROM test_table ORDER BY id");

      // Check inserted row 1
      resultSet.next();
      Assertions.assertEquals(0.0, resultSet.getDouble("foo"));
      Assertions.assertEquals("fing", resultSet.getString("baz"));
      Assertions.assertEquals(0.0, resultSet.getDouble("bar"));

      // Check inserted row 2
      resultSet.next();
      Assertions.assertEquals(1.0, resultSet.getDouble("foo"));
      Assertions.assertEquals("ding", resultSet.getString("baz"));
      Assertions.assertEquals(-1.0, resultSet.getDouble("bar"));

    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Prints an sql table with each row as a line
   *
   * @param tableName name of sql table in database
   */
  public void printSQLTable(String tableName) {
    try {
      Connection conn = db.getConnection();
      Statement stmt = conn.createStatement();
      ResultSet rs = stmt.executeQuery("SELECT * FROM " + tableName + " ORDER BY id");
      ResultSetMetaData metaData = rs.getMetaData();

      // Iterate through data and create a string to print on each line
      while (rs.next()) {
        StringBuilder row = new StringBuilder();

        // Construct string from row
        for (int i = 1; i <= metaData.getColumnCount(); i++) {
          String colValue = rs.getString(i);
          row.append(metaData.getColumnName(i)).append(": ").append(colValue);

          // Add separator
          if (i < metaData.getColumnCount()) {
            row.append(", ");
          }
        }

        System.out.println(row);
      }

      // Close resources
      rs.close();
      stmt.close();

    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }
}
