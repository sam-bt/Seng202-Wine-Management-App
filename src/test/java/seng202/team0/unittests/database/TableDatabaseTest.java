package seng202.team0.unittests.database;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import seng202.team0.database.DataTable;
import seng202.team0.database.TableDatabase;
import seng202.team0.database.Value;
import seng202.team0.exceptions.DuplicateTableException;
import seng202.team0.exceptions.TableNotFoundException;

/**
 * Tests the table database
 *
 * @author Angus McDougall
 */
class TableDatabaseTest {

  TableDatabase database;

  /**
   * Makes a table to test with
   *
   * @return a table
   */
  DataTable makeTable() {
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

    return new DataTable(
        new String[]{"foo", "baz", "bar"},
        columns
    );
  }

  /**
   * Initializes the database
   */
  @BeforeEach
  void setup() {
    database = new TableDatabase();
  }

  /**
   * Checks adding tables to the database
   */
  @Test
  void addTable() {
    assertThrows(NullPointerException.class, () -> {
      database.addTable("Joe", null);
    });
    assertDoesNotThrow(() -> {
      database.addTable("Joe", makeTable());
    });
    assertThrows(DuplicateTableException.class, () -> {
      database.addTable("Joe", makeTable());
    });

  }

  /**
   * Tests removing tables from the database
   */
  @Test
  void removeTable() {

    assertThrows(TableNotFoundException.class, () -> {
      database.removeTable("Joe");
    });
    assertDoesNotThrow(() -> {
      database.addTable("Joe", makeTable());
    });
    assertDoesNotThrow(() -> {
      database.removeTable("Joe");
    });
  }

  /**
   * Tests querying if tables exist
   */
  @Test
  void tableExists() {

    assertDoesNotThrow(() -> {
      database.addTable("Joe", makeTable());
    });
    assertTrue(database.tableExists("Joe"));
    assertFalse(database.tableExists("Bob"));
  }

  /**
   * Tests getting references to the table
   */
  @Test
  void tryGetTable() {

    assertDoesNotThrow(() -> {
      database.addTable("Joe", makeTable());
    });
    assertNotEquals(database.tryGetTable("Joe"), null);
    assertNull(database.tryGetTable("Bob"));
  }
}