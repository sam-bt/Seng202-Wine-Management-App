package seng202.team0.unittests.database;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import seng202.team0.database.DataTable;
import seng202.team0.database.Value;

/**
 * Tests DataTable
 *
 * @author Angus McDougall
 */
public class DataTableTest {

  private DataTable table;

  /**
   * Constructs a table to use for testing
   */
  @BeforeEach
  public void setUp() {
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

    this.table = new DataTable(
        new String[]{"foo", "baz", "bar"},
        columns
    );
  }

  /**
   * Tests if the table fails to construct on invalid dimensions
   */
  @Test
  public void invalidTableDimensions() {
    assertThrows(IllegalStateException.class, () -> {
      ArrayList<ArrayList<Value>> columns = new ArrayList<>();
      columns.add(new ArrayList<>());
      columns.add(new ArrayList<>());
      columns.get(0).add(Value.make(0.0));
      columns.get(0).add(Value.make(1.0));

      columns.get(1).add(Value.make("fing"));

      new DataTable(new String[]{"foo", "bar"}, columns);
    });
    assertThrows(IllegalStateException.class, () -> {
      ArrayList<ArrayList<Value>> columns = new ArrayList<>();
      columns.add(new ArrayList<>());
      columns.add(new ArrayList<>());
      new DataTable(new String[]{"foo"}, columns);
    });
    new DataTable(new String[]{}, new ArrayList<>());
  }

  /**
   * Tests if the tale fails to construct on invalid types
   */
  @Test
  public void invalidTableType() {
    assertThrows(IllegalStateException.class, () -> {
      ArrayList<ArrayList<Value>> columns = new ArrayList<>();
      columns.add(new ArrayList<>());
      columns.get(0).add(Value.make(0.0));
      columns.get(0).add(Value.make("fing"));

      new DataTable(new String[]{"foo"}, columns);
    });
  }


  /**
   * Tests columnSize
   */
  @Test
  public void columnSize() {
    assertEquals(table.columnSize(), 3);
  }

  /**
   * Tests rowSize
   */
  @Test
  void rowSize() {
    assertEquals(table.rowSize(), 2);
  }

  /**
   * Tests value access
   */
  @Test
  void getValue() {
    assertEquals(table.get(1, 0), Value.make("fing"));

  }


  /**
   * Tests column naming
   */
  @Test
  void getColumnName() {
    assertEquals(table.getColumnName(0), "foo");
    assertEquals(table.getColumnName(1), "baz");
    assertEquals(table.getColumnName(2), "bar");
  }

  /**
   * Tests column iteration and order.
   */
  @Test
  void getIterableForColumn() {
    int idx = 0;
    double[] expected = new double[]{0.0, 1.0};
    for (Value value : table.getIterableForColumn(0)) {
      assertEquals(value.getAsReal(), expected[idx++]);
    }
  }

  /**
   * Tests getting a column index from name.
   */
  @Test
  void getColumnIndexFromName() {
    assertEquals(table.getColumnIndexFromName("foo"), 0);
    assertEquals(table.getColumnIndexFromName("baz"), 1);
    assertEquals(table.getColumnIndexFromName("bar"), 2);
    assertEquals(table.getColumnIndexFromName("dingus"), -1);
  }

  /**
   * Tests getting a record to a row.
   */
  @Test
  void createRecord() {
    table.getRecordForIndex(0);
    assertThrows(IndexOutOfBoundsException.class, () -> {
      table.getRecordForIndex(3);
    });
  }

}