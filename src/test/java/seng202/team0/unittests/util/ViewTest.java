package seng202.team0.unittests.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import seng202.team0.database.DataTable;
import seng202.team0.database.Record;
import seng202.team0.database.ToRecord;
import seng202.team0.database.Value;
import seng202.team0.util.FilterView;
import seng202.team0.util.PrimitiveView;
import seng202.team0.util.SortedView;
import seng202.team0.util.UnionView;
import seng202.team0.util.View;

/**
 * Tests views
 * <p>
 *   Multiple views are testing in one place to reduce boilerplate
 * </p>
 * @author Angus McDougall
 */
class ViewTest {
  DataTable table;
  View view;

  /**
   * Schema for tests
   * @author Angus McDougall
   */
  private static class RecordClass implements ToRecord {
    public String string;
    public double num;
    RecordClass(String string, double num){
      this.string = string;
      this.num = num;
    }

    /**
     * This method must fill all the attributes of the record
     * <p>
     * Each attribute should be set on the given record
     * </p>
     *
     * @param record record to set
     */
    @Override
    public void toRecord(Record record) {
      record.setItem("num", Value.make(num));
      record.setItem("string", Value.make(string));
    }
  }


  /**
   * Constructs a DataTable to use for testing
   */
  @BeforeEach
  void setUp() {

    DataTable table = new DataTable(RecordClass.class);
    table.addRecordFromClass(new RecordClass("doo", -1));
    table.addRecordFromClass(new RecordClass("coo", 0));
    table.addRecordFromClass(new RecordClass("boo", 1));
    table.addRecordFromClass(new RecordClass("aoo", 2));

    this.table = table;
    this.view = new PrimitiveView(table);
  }

  /**
   * Tests SortedView
   */
  @Test
  void sortedView(){
    {
      View sorted = new SortedView(view.deepCopy(), Record.getComparator("num", table));
      assertEquals(sorted.next().getItem("num"), Value.make(-1));
      assertEquals(sorted.next().getItem("num"), Value.make(0));
      assertEquals(sorted.next().getItem("num"), Value.make(1));
      assertEquals(sorted.next().getItem("num"), Value.make(2));
      assertNull(sorted.next());

    }
    {
      View sorted = new SortedView(view.deepCopy(), Record.getComparator("string", table));
      assertEquals(sorted.next().getItem("num"), Value.make(2));
      assertEquals(sorted.next().getItem("num"), Value.make(1));
      assertEquals(sorted.next().getItem("num"), Value.make(0));
      assertEquals(sorted.next().getItem("num"), Value.make(-1));
      assertNull(sorted.next());
    }
  }

  /**
   * Tests FilterView
   */
  @Test
  void filterView(){
    View filter = new FilterView(view.deepCopy(), record -> record.getItem("num").getAsReal() > 0);
    View sorted = new SortedView(filter, Record.getComparator("num", table));
    assertEquals(sorted.next().getItem("num"), Value.make(1));
    assertEquals(sorted.next().getItem("num"), Value.make(2));
    assertNull(sorted.next());

  }

  /**
   * Tests UnionView
   */
  @Test
  void unionView(){
    View filter1 = new FilterView(view.deepCopy(), record -> record.getItem("num").getAsReal() > 0);
    View filter2 = new FilterView(view.deepCopy(), record -> record.getItem("num").getAsReal() <= 0);
    View sorted = new SortedView(new UnionView(filter1, filter2), Record.getComparator("num", table));
    assertEquals(sorted.next().getItem("num"), Value.make(-1));
    assertEquals(sorted.next().getItem("num"), Value.make(0));
    assertEquals(sorted.next().getItem("num"), Value.make(1));
    assertEquals(sorted.next().getItem("num"), Value.make(2));
    assertNull(sorted.next());
  }

}