package seng202.team0.database;


import java.util.HashMap;
import seng202.team0.exceptions.DuplicateRecordException;
import seng202.team0.exceptions.DuplicateTableException;
import seng202.team0.exceptions.RecordNotFoundException;
import seng202.team0.exceptions.TableNotFoundException;

/**
 * Table based implementation of a database. Intended to store a set of tables cheaply
 * @author Angus McDougall
 */
public class TableDatabase extends Database {

  HashMap<String, DataTable> tables = new HashMap<>();

  /**
   * Adds a table to the database
   *
   * @param tableName name of table to add
   * @param source    table to add
   * @throws DuplicateTableException thrown if the table already exists
   */
  @Override
  public void addTable(String tableName, DataTable source) throws DuplicateTableException {
    // Ensure null reference can never be stored
    if(source == null){
      throw new NullPointerException("Non-null value expected");
    }
    if(tables.containsKey(tableName)){
      throw new DuplicateTableException("Attempted to add a duplicate table to database");
    }
    tables.put(tableName, source);

  }

  /**
   * Removes a table from the database
   *
   * @param tableName name of table to remove
   * @throws TableNotFoundException thrown if the table does not exist
   */
  @Override
  public void removeTable(String tableName) throws TableNotFoundException {
    if(tables.remove(tableName) == null){
      throw new TableNotFoundException("Attempted to remove a non-existent table from database");
    }
  }

  /**
   * Checks if a table exists
   *
   * @param tableName name of table
   * @return if the table exists
   */
  @Override
  public boolean tableExists(String tableName) {
    return tables.containsKey(tableName);
  }

  /**
   * Maybe gets a reference to a table
   *
   * @param tableName name of table
   * @return a reference to the table if it exists, else null
   */
  @Override
  public DataTable tryGetTable(String tableName) {
    return tables.get(tableName);
  }
}
