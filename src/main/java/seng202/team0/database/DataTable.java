package seng202.team0.database;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Objects;
import seng202.team0.exceptions.InvalidRecordException;

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
  private final String[] columnNames;

  private final int[] columnTypes;

  /**
   * Constructs a DataTable from a list of columns and names
   *
   * @param columnNames Name of each column
   * @param columns      List of columns
   */
  public DataTable(
      String[] columnNames,
      ArrayList<ArrayList<Value>> columns
  ) {
    this.columns = columns;
    this.columnTypes = new int[this.columns.size()];
    int i = 0;
    for (ArrayList<Value> column : columns) {
      if (column.isEmpty()) {
        throw new IllegalStateException("Must have one entry to infer types");
      }
      columnTypes[i++] = column.getFirst().getTypeIndex();
    }
    this.columnNames = columnNames;
    if (!isSizeConsistent()) {
      throw new IllegalStateException("Inconsistent sizes in table");
    }
    if (!isTypeConsistent()) {
      throw new IllegalStateException("Inconsistent types in table");
    }
  }

  /**
   * Constructs a datatable with an infered schema from a class
   *
   * @param clazz schema
   */
  public DataTable(Class<?> clazz) {
    Field[] fields = clazz.getFields();
    this.columnTypes = new int[fields.length];
    this.columnNames = new String[fields.length];
    this.columns = new ArrayList<>();
    int i = 0;
    for (Field field : fields) {
      this.columns.add(new ArrayList<>());
      this.columnNames[i] = field.getName();
      int typeIndex = Value.getTypeIndex(field.getType());
      if (Value.isInvalidTypeIndex(typeIndex)) {
        throw new IllegalStateException("Constructing a table with an invalid type in schema");
      }
      columnTypes[i] = typeIndex;
      i++;
    }

  }

  /**
   * Checks if all array lengths for columns are the same
   *
   * @return Whether the state of columns is consistent
   */
  private boolean isSizeConsistent() {
    if (columns.isEmpty() && columnNames.length == 0) {
      return true;
    }
    if (columns.size() != columnNames.length) {
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
    int i = 0;
    for (ArrayList<Value> column : columns) {
      for (Value value : column) {
        if (value.getTypeIndex() != columnTypes[i]) {
          return false;
        }
      }
      i++;
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

  public void set(int column, int row, Value value) {
    columns.get(column).set(row, value);
  }


  /**
   * Gets the name of a column
   *
   * @param column Column index
   * @return Name of the column
   */
  public String getColumnName(int column) {
    return columnNames[column];
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
    for (int i = 0; i < columnNames.length; i++) {
      if (Objects.equals(columnName, columnNames[i])) {
        return i;
      }
    }
    return -1;
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

  /**
   * Adds a new null filled record to the table
   */
  private void pushRecord() {
    for (ArrayList<Value> column : columns) {
      column.add(null);
    }
  }

  /**
   * Pops the most recent record from the table
   */
  private void popRecord() {
    for (ArrayList<Value> column : columns) {
      column.removeLast();
    }
  }

  /**
   * Inserts a row into the table from an object
   *
   * @param record trivial object to insert
   */
  public void addRecordFromClass(ToRecord record) {
    pushRecord();
    try {
      record.toRecord(getRecordForIndex(rowSize() - 1));
    } catch (Exception exception) {
      throw new InvalidRecordException(exception);
    }
    // Verify no nulls and type
    int i = 0;
    for (ArrayList<Value> column : columns) {
      Value attribute = column.getLast();
      if (attribute == null || attribute.getTypeIndex() != columnTypes[i++]) {
        popRecord();
        return;
      }
    }
  }
}
