package seng202.team0.util;

import seng202.team0.database.DataTable;
import seng202.team0.database.Record;

/**
 * Simple view over all records in a table
 */
public class PrimitiveView extends View {

  int index;
  DataTable table;

  /**
   * Constructs a view from a table
   * @param index index of row
   * @param table table
   */
  public PrimitiveView(int index, DataTable table) {
    this.index = index;
    this.table = table;
  }

  /**
   * Constructs a view from a table starting at the beginning
   * @param table table
   */
  public PrimitiveView(DataTable table) {
    this.index = 0;
    this.table = table;
  }

  /**
   * Gets the current element and increments
   *
   * @return current record
   */
  @Override
  public Record next() {
    Record record = table.getRecordForIndex(index);
    index++;
    return record;
  }

  /**
   * Resets the view to the starting element
   */
  @Override
  public void reset() {
    index = 0;
  }

  /**
   * Copies the view and all sub views
   *
   * @return copy of this
   */
  @Override
  public View deepCopy() {
    return new PrimitiveView(index, table);
  }

}
