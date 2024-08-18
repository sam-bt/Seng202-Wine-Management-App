package seng202.team0.database;

/**
 * Record represents a named tuple. This class acts as a handle to a row in a DataTable
 *
 * @author Angus McDougall
 */
public class Record {

  /**
   * Reference to owning table
   */
  private final DataTable dataTable;

  /**
   * Index of row
   */
  private final int rowIndex;

  /**
   * Constructs a Record from a table and index
   *
   * @param dataTable Owner of data
   * @param rowIndex  Index of this row
   */
  public Record(DataTable dataTable, int rowIndex) {
    this.dataTable = dataTable;
    this.rowIndex = rowIndex;
  }

  /**
   * Gets the number of attributes of the Record
   *
   * @return Returns the size of the tuple
   */
  public int size() {
    return dataTable.columnSize();
  }

  /**
   * Gets the attribute of a given index in the Record
   *
   * @param index index of attribute
   * @return value
   */
  public Value getItem(int index) {
    return dataTable.get(index, rowIndex);
  }

  /**
   * Sets an attribute from an index
   *
   * @param index index of attribute
   * @param value value
   */
  public void setItem(int index, Value value) {
    dataTable.set(index, rowIndex, value);
  }

  /**
   * Sets an attribute from a name
   *
   * @param string name of attribute
   * @param value  value
   */
  public void setItem(String string, Value value) {
    dataTable.set(dataTable.getColumnIndexFromName(string), rowIndex, value);
  }


}
