package seng202.team0.unittests.database;



import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import seng202.team0.database.DataTable;
import seng202.team0.database.Value;

public class DataTableTest {

  private DataTable table;

  @BeforeEach
  public void setUp() {
    ArrayList<ArrayList<Value>> columns = new ArrayList<>();
    columns.add(new ArrayList<>());
    columns.add(new ArrayList<>());
    columns.add(new ArrayList<>());
    columns.get(0).add(Value.make(0.0));
    columns.get(0).add(Value.make(-1.0));

    columns.get(1).add(Value.make("fing"));
    columns.get(1).add(Value.make("ding"));

    columns.get(2).add(Value.make(0.0));
    columns.get(2).add(Value.make(-1.0));


    this.table = new DataTable(
        new String[]{"foo", "baz", "bar"},
        columns
    );
  }

  @Test
  public void columnSize() {
    assertEquals(table.columnSize(), 3);
  }

  @Test
  void rowSize() {
    assertEquals(table.rowSize(), 2);
  }

  @Test
  void getValue() {
  }

  @Test
  void getColumnsType() {
  }

  @Test
  void getColumnName() {
  }

  @Test
  void getIterableForColumn() {
  }

  @Test
  void getColumnIndexFromString() {
  }

  @Test
  void getRecordForIndex() {
  }
}