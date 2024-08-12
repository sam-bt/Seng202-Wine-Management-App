package seng202.team0.database;

import java.util.ArrayList;
import java.util.List;

/**
 * DataTable encapsulates a singular table of data. Each row represents an entity
 *
 * @author Angus McDougall
 */
public class DataTable {

  /**
   * List of columns
   */
  private final ArrayList<ArrayList<Value>> columns;
  /**
   * List of column names
   */
  private final ArrayList<String> columnsNames;

  /**
   * Constructs a DataTable from a list of columns and names
   *
   * @param columnsNames Name of each column
   * @param columns      List of columns
   */
  public DataTable(
      String[] columnsNames,
      ArrayList<ArrayList<Value>> columns
  ) {
    this.columns = columns;
    this.columnsNames = new ArrayList<>(List.of(columnsNames));
    if (!isSizeConsistent()) {
      throw new IllegalStateException("Inconsistent sizes in table");
    }
    if (!isTypeConsistent()) {
      throw new IllegalStateException("Inconsistent types in table");
    }
  }

  /**
   * Checks if all array lengths for columns are the same
   *
   * @return Whether the state of columns is consistent
   */
  private boolean isSizeConsistent() {
    if (columns.isEmpty() && columnsNames.isEmpty()) {
      return true;
    }
    if (columns.size() != columnsNames.size()) {
      return false;
    }
    int firstSize = columns.getFirst().size();
    return columns.stream().allMatch(column -> column.size() == firstSize);
  }

  /**
   * Checks if all types in a column are the same
   *
   * @return If all types in a column are the same
   */
  private boolean isTypeConsistent() {

    for (ArrayList<Value> column : columns) {
      for (Value value : column) {
        if (value.getTypeIndex() != column.getFirst().getTypeIndex()) {
          return false;
        }
      }
    }
    return true;
  }

  /**
   * Gets the number of columns in the table
   *
   * @return The number of columns
   */
  public int columnSize() {
    return columns.size();
  }

  /**
   * Gets the number of rows in the table
   *
   * @return The number of rows
   */
  public int rowSize() {
    if (columnSize() == 0) {
      return 0;
    }
    return columns.getFirst().size();
  }

  /**
   * Gets the value at a given index
   *
   * @param column Column index
   * @param row    Row index
   * @return Value
   */
  public Value get(int column, int row) {
    return columns.get(column).get(row);
  }

  /**
   * Gets the name of a column
   *
   * @param column Column index
   * @return Name of the column
   */
  public String getColumnName(int column) {
    return columnsNames.get(column);
  }

  /**
   * Gets an iterable to iterate a column
   *
   * @param column Column index
   * @return An iterable to the column
   */
  public Iterable<Value> getIterableForColumn(int column) {
    return columns.get(column);
  }

  /**
   * Gets the index of a given named column
   *
   * @param columnName Column index
   * @return Index of a named column, -1 if not valid
   */
  public int getColumnIndexFromName(String columnName) {
    return columnsNames.indexOf(columnName);
  }

  /**
   * Gets a Record as a handle to a given row
   *
   * @param row Row
   * @return Record for a given row
   */
  public Record getRecordForIndex(int row) {
    if (row >= rowSize()) {
      throw new IndexOutOfBoundsException();
    }
    return new Record(this, row);
  }

}
